# [git.watch](https://git.watch) [![Build Status](https://travis-ci.org/ScalaWilliam/git-watch.svg?branch=master)](https://travis-ci.org/ScalaWilliam/git-watch)

> Trigger commands when your Git repository is updated

## Usage
Supported: GitHub, BitBucket and GitLab

1. Go to 'WebHooks' section of your GitHub, BitBucket or GitLab repository.
2. Add the WebHook URL `https://git.watch/`.
3. On your target machine, run `npm install -g gitwatch-client`.
4. On your target machine, go to the clone of your repository and run `git-watch -- ./push.sh`.
5. Make a commit and push to your Git repository.
6. You should see `push.sh` executed automatically!

Use `git-watch --help` to get full run options.

## Use cases
I use it for continuous deployment from `master` branch for:

* [ActionFPS](https://actionfps.com/) and [Git Work](https://git.work/): on push to master, there are two options:
  * __Fast deployment__: Build the project with `sbt` and deploy it with `systemd` on a bare metal server.
  * __Instant deployment__: `rsync` the HTML templates if they are the only change in the branch.
  * Example deployment scripts: <a href="https://github.com/ScalaWilliam/ActionFPS/blob/master/push.sh">ActionFPS push.sh</a>,
            <a href="https://github.com/ScalaWilliam/git-watch/blob/master/push.sh">Git Watch push.sh</a>,
                <a href="https://github.com/ScalaWilliam/git-work/blob/master/push">Git Work push</a>.
* [ActionFPS](https://actionfps.com/):
  * Redeploy a simple background Python app on a bare metal server.
  * <a href="https://github.com/ActionFPS/ActionFPS-Game/blob/960794fd4bde6cc56d812e7cb09c0d25685b633c/Makefile.deploy">Build a game server binary and deploy it</a> with `systemd` on a bare metal server.
* [ScalaWilliam.com](https://www.scalawilliam.com/) (previously):
  * Build the static site and deploy it automatically to Firebase.
  * However, now I <a href="https://github.com/ScalaWilliam/ScalaWilliam.com/blob/master/.travis.yml">deploy to Firebase via Travis-CI</a> (<a href="https://www.youtube.com/watch?v=QLVzozWDYAs">See a YouTube tutorial</a>) in order to have fewer servers to manage. Unfortunately, deploy speeds are poorer than git watch.
  * Before Git Watch and Travis, I used a custom PHP WebHook receiver. However there was too much management involved in getting it to work: 
    Setting up PHP-FPM, nginx, etc, just for simple deployments.
  * Git Watch only needs HTTPS access and that's it!
* This would also work very well for deploying changes to your VPS/private server for your Wordpress, PHP and other pages. Super easy and simple - just use `git pull` as the executable command and that's it!

So typical characteristics when you would use Git Watch would be:
* You're experimenting
* Project is lightweight
* You want rapid iterations
* You don't need "The Cloud" yet    
* You have minimal scaling needs
* You want to deploy where you build
* You want instant content redeploys
* You don't need reproducibility right now
* You don't want to invest time in a proper CI right now

# Architecture
1. Your **Git repository** is updated on the **Git host**.
2. The **Git host** sends a webhook to the **Git Watch server**.
3. The **Git Watch server** broadcasts the repository url to the event stream at `https://git.watch/events/`.
4. A **Git Watch client** listens to the stream, receives the event and then triggers your custom command. 


## Technical choices & reasons

### Server side

<dl>

<dt><a href="https://www.w3.org/TR/eventsource/">EventSource</a> for passing events from server to client</dt>
<dd>Easier to use than WebSockets because we can get the result via CURL and basic HTTP.
WebSockets require extra application strength on the client side and server side configuration</dd>
<dd>We only need a one-way flow, which is exactly what EventSource provides.</dd>

<dt><a href="https://www.playframework.com/">Play Framework</a> and <a href="http://www.scala-lang.org">Scala programming language</a></dt>
<dd>I am specialised in Scala and this problem is for me easiest to solve with Play & Scala<./dd>
<dd>Play provides us with native EventSource support.</dd>
<dd>We use ScalaTest as the de facto Scala testing framework.</dd>
<dd>See: <a href="https://www.youtube.com/watch?v=N11rdUvcp7g&feature=youtu.be">Scala for 2017 talk</a></dd>
<dd>See: <a href="https://www.youtube.com/watch?v=tsR0zc6kzRk">An Introduction to Scala (2014)</a></dd>

<dt><a href="http://www.scala-sbt.org/">SBT</a> for building the project</dt>
<dd>Natural fit for Scala and Play.</dd>

<dt>Code formatting with <a href="https://olafurpg.github.io/scalafmt/">scalafmt</a> and <a href="https://plugins.jetbrains.com/plugin/8236-scalafmt">IntelliJ scalafmt plugin</a></dt>
<dd>Helps to make code more readable and consistent.</dd>

<dt>Recommended IDE: <a href="https://www.jetbrains.com/idea/">IntelliJ IDEA</a></dt>
<dd>Works very well with SBT and Scala. De facto Scala IDE.</dd>
<dd>Make sure to install the <a href="https://plugins.jetbrains.com/plugin/1347-scala">Scala Plugin</a>.</dd>

</dl>

### Client side

<dl>
<dt><a href="https://nodejs.org/en/">Node.js</a></dt>
<dd>Comes with the most standards-compliant <a href="https://www.npmjs.com/package/eventsource">non-web EventSource client</a>.
I tried Python, Scala, Jersey, Go, Bash clients which proved unsatisfactory. The Node library worked out the best.</dd>
<dd>Lightweight and most common language for web developers</dd>
<dt><a href="https://www.npmjs.com/package/yargs">yargs</a> for argument parsing</dt>
<dd>I tried <a href="https://www.npmjs.com/package/argv">argv</a> but it's a bit unfriendly to use</dd>
<dd>Allows to pass a full command after <code>--</code></dd>
<dt><a href="https://www.npmjs.com/package/console-stamp">console-stamp</a></dt>
<dd>Realised it's troublesome to look at the logs and not see when an event happened</dd>
<dt><a href="https://mochajs.org/">Mocha</a> and <a href="http://chaijs.com/">Chai</a></dt>
<dd>Documented well enough for me to use without difficulty.</dd>
<dd>Appears to be the most standard way of testing in Node.js</dd>
</dl>
   
## Why it doesn't pass on the commit hash
A Git push event is a bit more than just the latest hash. There's a lot of this information
and it requires a lot of custom parsing that can't be re-used. Instead of this approach,
I realised all we need is just a trigger, and then the user can decide how to deal with the new
information available in Git. 

Extracting too much information became a scope creep that introduced significant complexity
in both the client and the server.

## Security
URLs of updated repositories are sent to the public event stream.

Source IP addresses are checked for BitBucket and GitHub to prevent DOS.

# Recipes
## For a service

`push.sh` could be:

```bash
#!/bin/bash
git fetch
if [[ $(git diff --name-only HEAD origin/master) != "" ]]; then
    pkill -F .pid || true
    git pull origin master
    pip install --user -r requirements.txt || true
    python -m app &
    echo "$!" > .pid
fi
```

Now run `git-watch` with this inside a `tmux` or `screen` session and you have
an automated process restart.

## Licence
* Client: MIT, Server: GPLv3.
* Copyright (2016) Apt Elements Ltd. [William Narmontas](https://www.scalawilliam.com/)
