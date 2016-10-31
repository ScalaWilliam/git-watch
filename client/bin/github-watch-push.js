#! /usr/bin/env node
var EventSource = require('eventsource');
var shell = require("shelljs");
var argLib = require("../lib/args.js");
var execLib = require("../lib/exec.js");
var urlLib = require("../lib/url.js");
var foreverLib = require("../lib/forever-es.js");
var args = argLib.args.run();
var debug = 'debug' in args.options && args.options['debug'];
if (debug) {
    console.log(args);
}
if (!('url' in args.options)) {
  var findUrl = urlLib.get();
  if ( !findUrl ) {
    console.error("Require a URL and cannot retrieve one");
  } else {
    args.options.url = findUrl;
  }
}
if ( debug ) {
  console.log("Using URL: " + args.options.url);
}
if (!('push-execute' in args.options)) {
  console.error("Push execute option not specified");
}
foreverLib.forever(createEventSource, handleEventSource);

function handlePush(jd) {
  var command = args.options['push-execute']
      .replace("%sha%", jd.sha)
      .replace("%ref%", jd.ref);
  console.log("Will execute: ", command);
  shell.exec(command);
}

function handleEventSource(eventSource) {
  if ( debug ) {
    execLib.listenAny(eventSource, printEvent);
  }
  execLib.listenPush(eventSource, handlePush);
}

function createEventSource() {
  var url = args.options['url'];
  if ('secret' in args.options) {
    url = url + '?secret=' + encodeURIComponent(args.options['secret']);
  }
  return new EventSource(url);
}

function printEvent(event) {
  console.log("Received an event: ", JSON.stringify(event.data), "data =", event.data);
}
