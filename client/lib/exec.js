var shell = require("shelljs");
module.exports = {
  'handle': function(callback) {
    return function(event) {
      if ( 'data' in event ) {
      var jd = JSON.parse(event.data);
        if ('commit' in jd && 'ref' in jd && /^\w+$/.test(jd.commit) && /^[\w\/]+$/.test(jd.ref)) {
          callback({'sha': jd.commit, 'ref': jd.ref});
        }
      }
    }
  },
  'listenPush': function(eventSource, callback) {
    eventSource.addEventListener('ref-push', module.exports.handle(callback));
  },
  'listenAny': function(eventSource, callback) {
    eventSource.addEventListener('message', callback);
  }
}
