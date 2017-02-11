# Git Watch (CLI)

Git Watch runs commands in response to your Git pushes. After setting up the webhook to `https://git.watch/`, you can do:

    npm install -g gitwatch-client
    cd /your/github/repo
    git-watch -- ./push.sh push

We support GitHub, BitBucket and GitLab. For more detail, <https://git.watch/>.

## Local set up

    npm install -g mocha
    npm install
    # To do TDD
    npm run watch-test      
    
