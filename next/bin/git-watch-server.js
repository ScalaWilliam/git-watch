#! /usr/bin/env node
var argLib = require("../lib/args-server.js");
var AsyncPolling = require('async-polling');
var githubhook = require('githubhook');
var args = argLib.args.run();

var github = githubhook({'path':"/", 'wildcard': true});
github.listen();
var ws = require("nodejs-websocket")

var server = ws.createServer(function (conn) {
    console.log("New connection", arguments);
    conn.on("close", function (code, reason) {
        console.log("Connection closed", arguments);
    })
}).listen(8001);

github.on('push', function (repo, ref, data) {
  console.log('received event', arguments);
  server.connections.forEach(function (conn) {
      conn.sendText(repo);
  });
});
