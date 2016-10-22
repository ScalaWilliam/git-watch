DESTDIR = /home/gw/gitwatch-0.1-SNAPSHOT
TAR_ARCHIVE = target/universal/gitwatch-0.1-SNAPSHOT.tgz
git-watch:
	git-watch 2>/dev/null || npm install -g gitwatch-client
	git-watch --url=https://git.watch/github/ScalaWilliam/git-watch --push-execute='make push-%ref% || true'
deploy-content:
	rsync -av ./dist/content/. $(DESTDIR)/content/.
deploy-app:
	sbt universal:packageZipTarball
	tar -zxvf $(TAR_ARCHIVE) -C $(DESTDIR) --strip-components 1
	sudo -tt systemctl restart gw;
push-refs/heads/master:
    git rev-parse --verify HEAD; \
	SHA=$$(git rev-parse --verify HEAD); \
	git pull origin refs/heads/master; \
	SHA=$$SHA make deploy
deploy:
	changed_files() { \
	    git diff --name-only "$$SHA" "master" \
	    | grep -E '^(app|conf|dist|project|build)' \
	    | grep -v 'dist/content'; }; \
	echo Changed files from "$$SHA" to master:; \
	changed_files; \
	if [ $$(changed_files | wc -l) = "0" ]; then \
		make deploy-content; \
	else \
		make deploy-app; \
	fi
browser-sync:
	browser-sync 2>/dev/null || npm install browser-sync
	browser-sync start --proxy "localhost:9000" --files "dist/**/*"
play-run:
	sbt run
develop-frontend:
	make browser-sync &
	make play-run
