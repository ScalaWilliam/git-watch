var chai = require('chai');
var expect = chai.expect;
var url = require("../lib/url.js");
var transform_url = url.transform;

describe('URL', function () {
    var target = "https://github.com/ScalaWilliam/git-watch";

    var httpsUrl = "https://github.com/ScalaWilliam/git-watch.git";
    var sshUrl = "git@github.com:ScalaWilliam/git-watch.git";
    var gitUrl = "git://github.com/ScalaWilliam/git-watch.git";

    it("Works for HTTPS URL", function () {
        expect(transform_url(httpsUrl)).to.equal(target);
    });
    it("Works for SSH URL", function () {
        expect(transform_url(sshUrl)).to.equal(target);
    });
    it("Works for GIT URL", function () {
        expect(transform_url(gitUrl)).to.equal(target);
    });
});
