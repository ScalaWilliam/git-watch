package model

import play.api.libs.EventSource.Event
import play.api.libs.json.JsValue
import play.api.mvc.Request
import play.core.server.common.SubnetValidate

/**
  * Generic push event with the fully qualified repository URL.
  *
  * This is the most canonical way to match against the Git repository for the client.
  */
case class PushEvent(repositoryUrl: String) {
  def toEventSource: Event =
    Event(
      id = None,
      name = Some("push"),
      data = repositoryUrl
    )
}

object PushEvent {

  /**
    * Turn a JSON request into a PushEvent if possible.
    * Request headers are important here.
    */
  sealed case class AtRequest(request: Request[JsValue]) {
    def githubEvent: Option[PushEvent] =
      for {
        eventType <- request.headers.get("X-GitHub-Event")
        if "push" == eventType
        repositoryUri <- (request.body \ "repository" \ "html_url")
          .asOpt[String]
      } yield PushEvent(repositoryUri)

    def bitbucketEvent: Option[PushEvent] =
      for {
        eventType <- request.headers.get("X-Event-Key")
        if "repo:push" == eventType
        repositoryUri <- (request.body \ "repository" \ "links" \ "html" \ "href")
          .asOpt[String]
      } yield PushEvent(repositoryUri)

    def gitlabEvent: Option[PushEvent] =
      for {
        eventType <- request.headers.get("X-Gitlab-Event")
        if "Push Hook" == eventType
        repositoryUrl <- (request.body \ "repository" \ "homepage")
          .asOpt[String]
      } yield PushEvent(repositoryUrl)

    def anyEvent: Option[PushEvent] =
      githubEvent orElse bitbucketEvent orElse gitlabEvent
  }

  type PushEventBuilder = Request[JsValue] => Option[PushEvent]

  def builder(validateIp: Boolean): PushEventBuilder = {
    if (validateIp) AtIpValidatedRequest.apply _
    else AtRequest.apply _
  }.andThen(_.anyEvent)

  /**
    * Validate source IP addresses.
    *
    * This is done to verify that the request comes from a legit source.
    * If we didn't have this filter the clients could face a DOS if somebody decides to spam us
    * with many many requests and the client does not use a secret.
    *
    * A secret is nice but not easy to make generic.
    *
    * Ideally they send us a signed HMAC (RSA?) that is service-wide and an HMAC with shared secret.
    *
    * GitLab doesn't publish its IPs: https://gitlab.com/gitlab-com/support-forum/issues/847
    *
    */
  class AtIpValidatedRequest(request: Request[JsValue]) extends AtRequest(request) {

    /**
      * @see https://confluence.atlassian.com/bitbucket/manage-webhooks-735643732.html
      * @see https://bitbucket.org/site/master/issues/12195/webhook-hmac-signature-security-issue
      */
    def isBitBucketIp: Boolean =
      SubnetValidate.validate("104.192.143.0/24", request.remoteAddress)

    /**
      * @see https://developer.github.com/webhooks/securing/
      */
    def isGitHubIP: Boolean =
      SubnetValidate.validate("192.30.252.0/22", request.remoteAddress)

    override def bitbucketEvent: Option[PushEvent] =
      if (isBitBucketIp) super.bitbucketEvent else None

    override def githubEvent: Option[PushEvent] =
      if (isGitHubIP) super.githubEvent else None
  }

  object AtIpValidatedRequest {
    def apply(request: Request[JsValue]): AtIpValidatedRequest =
      new AtIpValidatedRequest(request)
  }

}
