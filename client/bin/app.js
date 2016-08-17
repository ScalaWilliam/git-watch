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
    start();
    function start() {
        var url = args.options['url'];
        if ('secret' in args.options) {
            url = url + '?secret=' + encodeURIComponent(args.options['secret']);
        }
        var es = new EventSource(url);
        es.reconnectInterval = 1000;
        es.onerror = function (err) {
            if ("status" in err && (err.status == 502 || err.status == 503)) {
                es.close();
                setTimeout(function() {
                    start();
                }, 5000);
            }
        };
        if ('event' in args.options) {
            var listenType = args.options['event'] == '*' ? 'message' : args.options['event'];
            es.addEventListener(listenType, function (e) {
                console.log("Received an event: ", JSON.stringify(e), "data =", e.data);
            });
        } else if ('push-execute' in args.options) {
            es.addEventListener('ref-push', function (e) {
                var jd = JSON.parse(e.data);
                if (/^\w+$/.test(jd.commit) && /^[\w\/]+$/.test(jd.ref)) {
                    var command = args.options['push-execute']
                        .replace("%sha%", jd.commit)
                        .replace("%ref%", jd.ref);
                    console.log("Will execute: ", command);
                    shell.exec(command);
                }
            });
        }
    }
}
