# Git Watch (CLI) v2

Git Watch enables continuous deployment. Simplest way to get started is:

    npm install -g gitwatch-client
    cd /your/github/repo
    git-watch --execute='date && git pull'

Git Watch supports three levels of operation:

* Poll-only mode: poll every 5 minutes.
This is the minimum set up option, literally a timer and nothing else.
But easy to get started with and upgrade to the next options.
* TODO Hook+Poll mode: receive webhooks from a GitHub client.
This is ideal if you want no delay but have open ports and don't want anyone else to receive your data.
* TODO Event+Poll mode: receive events from a remote server who receives webhooks. This is ideal if you want no delay and are behind a firewall (cannot open ports).
