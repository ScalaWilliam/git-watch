DESTDIR = /home/gw/gitwatch-0.1-SNAPSHOT
TAR_ARCHIVE = target/universal/gitwatch-0.1-SNAPSHOT.tgz

### LOCAL DEVELOPMENT ###

default:
	sbt test universal:packageZipTarball

# Let SBT warm up first, get all the necessary dependencies
# Then start Play and 15 seconds later start browser-sync
develop-frontend:
	sbt test:compile dist
	bash -c 'sleep 15 && make browser-sync' &
	make play-run
browser-sync:
	browser-sync 2>/dev/null || npm install browser-sync
	browser-sync start --proxy "localhost:9000" --files "dist/**/*"
play-run:
	sbt run

### CONTINUOUS DEPLOYMENT ###

# Call back into this Makefile for each push.
git-watch:
	git-watch -i

# Only execute deploy for master
# Pass in the current SHA so we can do a diff
push-refs/heads/master:
	git rev-parse --verify HEAD; \
	SHA=$$(git rev-parse --verify HEAD); \
	git merge; \
	SHA=$$SHA make deploy

# We do a diff to determine whether to redeploy the whole app
# Or just the static resources. Static resource deploy is immediate.
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
deploy-content:
	rsync -av ./dist/content/. $(DESTDIR)/content/.
deploy-app:
	sbt universal:packageZipTarball
	tar -zxvf $(TAR_ARCHIVE) -C $(DESTDIR) --strip-components 1
	sudo -tt systemctl restart gw
push:
	git remote update
	if [[ $$(git diff origin/master) != "" ]]; then make push-refs/heads/master; fi
