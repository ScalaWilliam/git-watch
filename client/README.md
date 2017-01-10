# Git Watch (CLI)

Git Watch enables continuous deployment. Simplest way to get started is:

    npm install -g gitwatch-client
    cd /your/github/repo
    github-watch-push --push-execute='git pull'

Currently we support GitHub only.

However we'd recommend using more advanced approaches, documented at <https://git.watch/>

## Local set up

    npm install -g mocha
    npm install
    # To do TDD
    npm run watch-test      
    
