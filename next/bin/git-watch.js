#! /usr/bin/env node
var shell = require("shelljs");
var argLib = require("../lib/args.js");
var AsyncPolling = require('async-polling');
var githubhook = require('githubhook');
var args = argLib.args.run();
if ( !args.options['execute'] ) {
  console.info("Error: --execute not set.");
  process.exit(1);
}
var pollIntervalMinutes = args.options['poll-interval'] || 5;
if ( pollIntervalMinutes < 1 ) {
  console.info("Error: poll interval must be positive.");
  process.exit(1);
}
var millisInSecond = 1000;
var secondsInMinute = 60;
var pollIntervalMillis = pollIntervalMinutes * secondsInMinute * millisInSecond;
var executeCommand = args.options['execute'];

var githubhook = require('githubhook');

// https://github.com/nlf/node-github-hook
if ( args.options['server'] ) {
var github = githubhook({'path':"/", 'wildcard': true});
github.listen();
github.on('push', function (repo, ref, data) {
  // console.log('received event', arguments);
  run();
});}

function run() {
  console.log("Executing:", executeCommand);
  shell.exec(executeCommand);
};

AsyncPolling(function (end) {
  run();
  end();
}, pollIntervalMillis).run();
