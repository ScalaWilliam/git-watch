# git.watch
[![Build Status](https://travis-ci.org/AptElements/git-watch.svg?branch=master)](https://travis-ci.org/AptElements/git-watch)

Full tutorial at: <https://git.watch>

## Frontend changes

You can edit and prototype the front-end templates separately from the Scala backend.

All you need is PHP 7 to render the templates.

The templates are located in 'dist/templates/'.

```bash
cd dist/templates
php -S localhost:8848
```

'dist/templates/static/' is served directly by nginx.

'dist/templates/*' is rendered by PHP (called by Scala). We use an IoC (Inversion of Control) approach.

## Licence

* Client: MIT Licence.
* Server: GPLv3 Licence.
* Contributions: copyright transferred to Apt Elements Ltd.
* Copyright (2016) Apt Elements Ltd.
