# git.watch

Zero-configuration Git push hooks as event streams. For free.

Sign up at: http://git.watch



## Problem space

Git push hooks are wonderful. They enable continuous deployment and continuous integration.

Unfortunately you need to set up a server to receive them and this creates an ugly coupling.

For my open source projects, I've tried decoupling using AWS. I got a huge bill the next month.

What if you wanted to test push hooks locally, what if you're behind a firewall? It becomes quite complex.

What if you wanted it to be free and quick to set up?

But there's nothing particularly huge in just passing events forward, which is what we'll do for you, for free. Using the most basic tools.


## Usage

We need to set up the hook and then listen to it

### Set up the hook

#### GitHub

1. Go to "Settings" in your repository
2. Press "Webhooks & services"
3. Press "Add webhook"
4. In "Payload URL", write "http://git.watch/github/"
5. Press "Add webhook"

#### BitBucket

1. Go to "Settings" in your repository
2. Press "Webhooks"
3. Press "Add webhook"
4. In "URL", write "http://git.watch/bitbucket/"
5. Press "Save"

### Listen for events

Inside the target git repository, run:

```
wget https://raw.githubusercontent.com/ScalaWilliam/git-watch/master/git-watch.py
python3 -m pip install sseclient
chmod +x git-watch.py
./git-watch.py https://github.com/ScalaWilliam/git-watch
```

Create a 'deploy.sh' as well if you like, to execute anything after auto syncing the reposiroty.

I also tried to do event source with curl:
```
curl -f --retry 9999 --retry-delay 10 --retry-max-time 0 $url
```


[![Throughput Graph](https://graphs.waffle.io/ScalaWilliam/git.watch/throughput.svg)](https://waffle.io/ScalaWilliam/git.watch/metrics/throughput)
