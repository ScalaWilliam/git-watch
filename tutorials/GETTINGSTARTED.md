# Getting Started

1. Set up a hook via <https://git.watch/install/>
2. Install the client: `npm install -g gitwatch-client` ([npm package](https://www.npmjs.com/package/gitwatch-client))
3. `cd your/git/repo`
4. Listen for push events:

```bash
git-watch --push-execute='echo "Received an event for: %sha% %ref%" && git pull && ls'
```

![asciicast](https://cloud.githubusercontent.com/assets/2464813/19849322/8ac6e888-9f8d-11e6-81d5-3137c7fd940d.gif)
