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
var executeCommand = args.options['execute'];

var githubhook = require('githubhook');

// https://github.com/nlf/node-github-hook
if ( args.options['server'] ) {
var github = githubhook({'path':"/", 'wildcard': true});
github.listen();
github.on('push', function (repo, ref, data) {
  // console.log('received event', arguments);
  run();
});} else {
  var WebSocket = require('ws');
  function conn_ws() {
    console.log("Connecting as a client to another ws server.");
    var ws;
    try {
      ws = new WebSocket("ws://localhost:8001", function() {
        console.log("WAT", arguments);
      })
    } catch (e) {
      console.log("Could not connect for WS, will do in 5 seconds.");
      setTimeout(conn_ws, 5000);
      return;
    }
    ws.on('error', function(data) {
      console.log("Err", data);
    })
  ws.on('message', function(data, flags) {
    run();
  });
  ws.on('close', function close() {
    console.log('disconnected, attempting reconnect');
    conn_ws();
  });
}
conn_ws();

}

function run() {
  console.log("Executing:", executeCommand);
  shell.exec(executeCommand);
};

var pollIntervalMinutes = args.options['poll'];
if ( pollIntervalMinutes ) {
if ( pollIntervalMinutes < 1 ) {
  console.info("Error: poll interval must be positive.");
  process.exit(1);
}
var millisInSecond = 1000;
var secondsInMinute = 60;
var pollIntervalMillis = pollIntervalMinutes * secondsInMinute * millisInSecond;

AsyncPolling(function (end) {
  run();
  end();
}, pollIntervalMillis).run();

}
