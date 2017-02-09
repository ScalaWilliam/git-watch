#! /usr/bin/env node
var EventSource = require('eventsource');
var shelljs = require("shelljs");
var argv = require('yargs').default({
    "url": 'https://git.watch/events/',
    "execute": 'make push'
}).argv;

var url = require("../lib/url.js");

var repositoryUrl;
if (!repositoryUrl) {
    var r = shelljs.exec('git config --get remote.origin.url', {silent: true});
    if (r.code == 0) {
        repositoryUrl = url.transform(r.stdout.replace("\n", "").replace("\r", ""));
    }
}

console.log("got... ", repositoryUrl);
if (!repositoryUrl) {
    console.warn("Repository URL not detected. Will trigger for every push event.");
    console.warn("We support GitHub, BitBucket and GitLab repositories only.");
} else {
    console.log("Matching all events for '" + repositoryUrl + "'");
}

var es = new EventSource(argv.url);
es.onopen = function (e) {
    console.log(e);
};
es.onerror = function (e) {
    console.error(e);
};
es.addEventListener('push', function (e) {
    if (!repositoryUrl || e.data == repositoryUrl) {
        console.log("Received event: ", e);
        console.log("Repository URL: ", repositoryUrl);
        console.log("Executing: " + argv.execute);
        shell.exec(argv.execute);
    }
});
