var argv = require('argv');

module.exports = {
  args: argv
    .option({
            'name': 'poll',
            'short': 'p',
            'type': 'int',
            'description': 'Poll interval in minutes',
            'example': '--poll=5'
        }
    ).option({
            'name': 'execute',
            'short': 'x',
            'type': 'string',
            'description': 'Command to execute on trigger',
            'example': "--execute='echo got it!'"
        }
    ).option({
      'name': 'server',
      'short': 's',
      'type': 'boolean',
      'description': "Whether to use nodejs server"
    }).option({
      'name': 'ws-server',
      'short': 'w',
      'type': 'boolean',
      'description': "Whether to provide a websocket server to receive events from"
    })
};
// todo server mode + secret if in server mode
