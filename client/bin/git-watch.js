#! /usr/bin/env node
var EventSource = require('eventsource');
var shelljs = require("shelljs");
var argv = require('yargs').boolean('i').default({
    "u": 'https://git.watch/events/',
    "x": 'make push',
    "i": true
}).argv;
require('console-stamp')(console);

var executeCommand = argv.x;

var url = require("../lib/url.js");

var repositoryUrl;
var getUrlResult = shelljs.exec('git config --get remote.origin.url', {silent: true});
if (getUrlResult.code == 0) {
    var cleanUrl = getUrlResult.stdout.replace("\n", "").replace("\r", "");
    repositoryUrl = url.transform(cleanUrl);
}

if (!repositoryUrl) {
    throw new Error("Repository URL not detected.");
}

function execute() {
    console.log("Executing: '" + executeCommand + "'");
    shelljs.exec(executeCommand);
}

if (argv['initial-execute']) {
    console.log("Executing initial command. '-i' to disable.");
    execute();
}

var es = new EventSource(argv.u);
es.onopen = function (e) {
    console.log("Opened connection to '" + argv.u + "'");
};
es.onerror = function (e) {
    console.error("Connection error", e);
};
es.addEventListener('push', function (e) {
    if (e.data == repositoryUrl) {
        console.log("Received an event for '" + repositoryUrl + "'");
        execute();
    }
});
