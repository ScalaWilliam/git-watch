var chai = require('chai');
var expect = chai.expect;
var url = require("../lib/url.js");
var exec = require("../lib/exec.js");

describe('URL', function() {
  it('For HTTPS it works', function() {
    var line = "origin\thttps://github.com/ScalaWilliam/git-watch.git (fetch)";
    expect(url.parse(line)).to.equal("https://git.watch/github/ScalaWilliam/git-watch");
  });
  it('For SSH it works', function() {
    var line2 = "origin\tgit@github.com:ScalaWilliam/git-watch.git (fetch)";
    expect(url.parse(line2)).to.equal("https://git.watch/github/ScalaWilliam/git-watch");
  });
  it('allows good input', function() {
    var sha, ref;
    exec.handle(function(map) { sha = map.sha; ref = map.ref; })({
      'data': '{"commit": "abc","ref": "abcd"}'
    })
    expect(sha).to.equal("abc");
    expect(ref).to.equal("abcd");
  });
  it('Does not allow bad input', function() {
    var bad = false;
    exec.handle(function(map) { bad = true; })({
      'data': '{"commit": "abc ","ref": "abcd"}'
    })
    expect(bad).to.equal(false);
  })
  it('Gets the git url', function() {
    expect(url.get()).to.equal("https://git.watch/github/ScalaWilliam/git-watch");
  })
});
