#! /usr/bin/env node
var EventSource = require('eventsource');
var shell = require("shelljs");
var argv = require('yargs')
    .default({
        "url": 'https://git.watch/events/',
        "execute": 'make push'
    })
    .argv;

var es = new EventSource(argv.url);
es.onopen = function(e) {
    console.log(e);
}
es.onerror = function(e) {
    console.error(e);
};
es.addEventListener('push', function (e) {
    console.log(e);
    shell.exec(argv.execute);
});
