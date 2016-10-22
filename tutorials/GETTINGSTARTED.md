# Getting Started

1. Set up a hook via <https://git.watch/install/>
2. Install the client: `npm install -g gitwatch-client` ([npm package](https://www.npmjs.com/package/gitwatch-client))
3. Listen for push events:

```bash
git-watch --url=https://git.watch/github/ScalaWilliam/git-watch \
          --push-execute='echo "Received an event for: %sha% %ref%" && git pull && ls'
```

![asciicast](https://cloud.githubusercontent.com/assets/2464813/19617752/478fab50-986b-11e6-8056-231051759f32.gif)
