var shell = require("shelljs");
module.exports = {
  'get': function() {
    var result;
    var out = shell.exec('git remote -v show', {silent: true});
    out.stdout.split("\n").forEach(function(line) {
      var lr = module.exports.parse(line);
      if ( !result && lr ) {
        result = lr;
      }
    });
    return result;
  },
  'url': function() {

  },
  'parse': function(line) {
    var prefix = "https://git.watch/github/";
    if ( line.indexOf("(fetch)") === -1 ) {
      return;
    }
    var gitUrl = line.substring(line.indexOf("\t") + 1, line.indexOf(' (fetch)'));
    if ( line.indexOf("github") === -1 ) {
      return;
    }
    if ( gitUrl.indexOf("git@github.com:") === 0 ) {
      var name = gitUrl.substring(15, gitUrl.length - 4);
      return prefix+name;
    } else if ( gitUrl.indexOf("https://github.com/") === 0 ) {
      var name = gitUrl.substring(19, gitUrl.length - 4);
      return prefix + name;
    }
  }
}
