#! /usr/bin/env node
var EventSource = require('eventsource');
var shelljs = require("shelljs");
var foreverLib = require("../lib/forever-es.js");
var argv = require('yargs').boolean('i').default({
    "u": 'https://git.watch/events/',
    "i": false
}).describe('u', "EventSource URL.")
    .describe('i', "Execute on launch.")
    .usage('Usage: $0 [-s shell] [-h] [-i] [-u url] -- [command]')
    .help('h')
    .alias('h', 'help')
    .alias('s', 'shell')
    .alias('u', 'url')
    .alias('i', 'initial-execute')
    .epilog("* https://git.watch/\n* https://www.scalawilliam.com/")
    .argv;

var executeCommand = argv._.join(" ");

var url = require("../lib/url.js");
var exec_params;
if (argv.shell) {
    exec_params = {'shell': argv.shell};
}

var repositoryUrl;
var getUrlResult = shelljs.exec('git config --get remote.origin.url', {silent: true});
if (getUrlResult.code == 0) {
    var cleanUrl = getUrlResult.stdout.replace("\n", "").replace("\r", "");
    repositoryUrl = url.transform(cleanUrl);
}

if (!repositoryUrl) {
    console.error("Repository URL not detected.");
    console.error("Make sure you're inside a GitHub/GitLab/BitBucket repository.");
    process.exit(1);
}

require('console-stamp')(console);

function execute() {
    console.log("Executing: '" + executeCommand + "'.");
    shelljs.exec(executeCommand, exec_params);
}

if (argv['i']) {
    execute();
}

foreverLib.forever(function () {
    var es = new EventSource(argv.url);
    es.onopen = function (e) {
        console.log("Opened connection to '" + argv.url + "'.");
    };
    es.addEventListener('push', function (e) {
        if (e.data == repositoryUrl) {
            console.log("Received an event for '" + repositoryUrl + "'.");
            execute();
        }
    });
    return es;
});
