# Developing


Install [wscat](https://www.npmjs.com/package/wscat) & then use curl to run a sample request against the app.

## Testing the websocket in command line
[How I found out about mapfile](https://twitter.com/ScalaWilliam/status/803683877499744258)

```
# install wscat
$ npm install -g wscat
# in one window:
$ sbt run
# in another:
$ wscat -c ws://localhost:9000/ws/github/AptElements/git-watch
# in another, with latest bash - on mac need to `brew install bash`
$ mapfile -t curlargs < test/resources/sample-push-curl.txt; curl "${curlargs[@]}" http://host/github/
```
