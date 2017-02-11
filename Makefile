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
