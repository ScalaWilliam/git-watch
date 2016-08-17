#! /usr/bin/env node
var EventSource = require('eventsource');
var shell = require("shelljs");

var argv = require('argv');
var args = argv
    .option({
            'name': 'url',
            'short': 'u',
            'type': 'string',
            'description': 'Defines a target URL',
            'example': '--url=http://git.watch/github/AptElements/git-watch'
        }
    ).option({
            'name': 'event',
            'short': 'e',
            'type': 'string',
            'description': 'Event type to query',
            'example': '--event=* or --event=push'
        }
    ).option({
            'name': 'secret',
            'short': 's',
            'type': 'string',
            'description': 'push secret to filter by',
            'example': '--secret=abcdef'
        }
    ).option({
            'name': 'push-execute',
            'short': 'p',
            'type': 'string',
            'description': 'Execute the command on push',
            'example': "--push-execute='echo %sha% %ref%'"
        }
    ).run();
console.log(args);
if (!('url' in args.options)) {
    console.error("Require a URL");
} else {
    var url = args.options['url'];
    if ('secret' in args.options) {
        url = url + '?secret=' + encodeURIComponent(args.options['secret']);
    }
    var es = new EventSource(url);
    if ('event' in args.options) {
        var listenType = args.options['event'] == '*' ? 'message' : args.options['event'];
        es.addEventListener(listenType, function (e) {
            console.log("Received an event: ", JSON.stringify(e), "data =", e.data);
        });
    } else if ('push-execute' in args.options) {
        // es.addEventListener('ref-push'], function(e) {
        //   console.log("Received an event: ", JSON.stringify(e));
        // });
    }
}
