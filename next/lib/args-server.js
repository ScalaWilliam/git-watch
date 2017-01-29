var argv = require('argv');

module.exports = {
  args: argv.option({
            'name': 'execute',
            'short': 'x',
            'type': 'string',
            'description': 'Command to execute on trigger',
            'example': "--execute='echo got it!'"
        }
    )
};
// todo server mode + secret if in server mode
