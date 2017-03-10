# [git.watch](https://git.watch)
[![Build Status](https://travis-ci.org/ScalaWilliam/git-watch.svg?branch=master)](https://travis-ci.org/ScalaWilliam/git-watch)

Trigger custom commands when your Git repository on the Git host (GitHub, BitBucket or GitLab) is updated.

Git Watch enables lightweight automated deployment onto your own VPS, where Jenkins and Travis are cumbersome.

It's ideal for a microservice that needs automated deployment on a VPS that doesn't need a reproducible environment.

# Architecture
1. Your **Git repository** is updated on the **Git host**.
2. The **Git host** sends a webhook to the **Git Watch server**.
3. The **Git Watch server** broadcasts the repository url to the event stream at `https://git.watch/events/`.
4. A **Git Watch client** listens to the stream, receives the event and then triggers your custom command. 

# Usage

## Prerequsites
* Webhook in your repository or organization pointing to: `https://git.watch/`

### On target machine
* Git repository at: `/path/to/git/repo`.
* Client installation: `npm install-g gitwatch-client`
* Script to run in `/path/to/git/repo/push.sh`.

## Running on target machine
```bash
cd /path/to/git/repo
git-watch -i -- ./push.sh
```

It'll run `push.sh` immediately and also run it on every push event received.

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

# Why it doesn't pass on the commit hash
A Git push event is a bit more than just the latest hash. There's a lot of this information
and it requires a lot of custom parsing that can't be re-used. Instead of this approach,
I realised all we need is just a trigger, and then the user can decide how to deal with the new
information available in Git. 

Extracting too much information became a scope creep that introduced significant complexity
in both the client and the server.

# Security
URLs of updated repositories are sent to the public event stream.

Source IP addresses are checked for BitBucket and GitHub to prevent DOS.

## Licence
* Client: MIT, Server: GPLv3.
* Copyright (2016) Apt Elements Ltd. [William Narmontas](https://www.scalawilliam.com/)
