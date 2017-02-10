#!/bin/bash
set -x

DESTDIR=/home/gw/gitwatch-0.1-SNAPSHOT
TAR_ARCHIVE=target/universal/gitwatch-0.1-SNAPSHOT.tgz

deploy_app() {
  sbt universal:packageZipTarball
  tar -zxvf $TAR_ARCHIVE -C $DESTDIR --strip-components 1
  sudo -tt systemctl restart gw
}

deploy_content() {
  rsync -av ./dist/content/. $DESTDIR/content/.
}

push() {
  git rev-parse --verify HEAD
  SHA=$(git rev-parse --verify HEAD)
  git pull origin refs/heads/master
  SHA=$SHA deploy
}

# We do a diff to determine whether to redeploy the whole app
# Or just the static resources. Static resource deploy is immediate.
deploy() {
  echo Changed files from "$SHA" to master:
  changed_files;
  if [[ $(changed_files) == "" ]]; then
   make deploy-content;
  else
   make deploy-app;
  fi
}

changed_files() {
  git diff --name-only "$SHA" "master"
    | grep -E '^(app|conf|dist|project|build)' \
    | grep -v 'dist/content'
}

push
