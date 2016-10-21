DESTDIR = /home/gw/gitwatch-0.1-SNAPSHOT
TAR_ARCHIVE = target/universal/gitwatch-0.1-SNAPSHOT.tgz
git-watch:
	git-watch 2>/dev/null || npm install -g gitwatch-client
	git-watch --url=https://git.watch/github/ScalaWilliam/git-watch --push-execute='make push-%ref% || true'
deploy-templates:
	rsync -av ./dist/templates/. $(DESTDIR)/templates/.
deploy-app:
	sbt universal:packageZipTarball
	tar -zxvf $(TAR_ARCHIVE) -C $(DESTDIR) --strip-components 1
	sudo -tt systemctl restart gw;
push-refs/heads/master:
    changed_files() { \
        git diff --name-only "master" "refs/remotes/origin/master" \
	    | grep -E '^(app|conf|dist|project|build)' \
	    | grep -v 'dist/templates'; }; \
	changed_files; \
	if [ $$(changed_files | wc -l) = "0" ]; then \
		git pull origin refs/heads/master; \
		make deploy-templates; \
	else \
		git pull origin refs/heads/master; \
		make deploy-app; \
	fi
