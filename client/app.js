var EventSource = require('eventsource');
var es = new EventSource('http://git.watch/github/AptElements/git-watch');
es.addEventListener('ref-push', function (e) {
  console.log(e.data);
});

