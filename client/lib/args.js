var argv = require('argv');

module.exports = {
  args: argv
    .option({
            'name': 'url',
            'short': 'u',
            'type': 'string',
            'description': 'Defines a target URL',
            'example': '--url=http://git.watch/github/AptElements/git-watch'
        }
    ).option({
            'name': 'secret',
            'short': 's',
            'type': 'string',
            'description': 'push secret to filter by',
            'example': '--secret=abcdef'
        }
    ).option({
        'name': 'debug', 'short': 'd', 'type': 'boolean',
        'description': 'enable debugging'
    })
    .option({
            'name': 'push-execute',
            'short': 'x',
            'type': 'string',
            'description': 'Execute the command on push',
            'example': "--push-execute='echo %sha% %ref%'"
        }
    )
};
