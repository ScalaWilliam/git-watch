#!/bin/bash
git-watch:
	git-watch 2>/dev/null || npm install -g gitwatch-client
	git-watch --url=https://git.watch/github/ScalaWilliam/git-watch --push-execute='make push-%ref% || true'
push-refs/heads/master:
	cd /home/gw/git-watch
	git fetch
	if [ $$(git diff --name-only "master" "refs/remotes/origin/master" |grep -E '^(app|conf|dist|project|build)' |grep -v 'dist/templates'|wc -l) = "0" ]; then \
		git pull origin refs/heads/master \
		rsync -av /home/gw/git-watch/dist/templates/. /home/gw/gitwatch-0.1-SNAPSHOT/templates/. \
	else \
		git pull origin refs/heads/master \
		sbt dist \
		cd /home/gw \
		unzip -o /home/gw/git-watch/target/universal/gitwatch-0.1-SNAPSHOT.zip \
		sudo -tt systemctl restart gw \
	fi
