# git.watch
[![Build Status](https://travis-ci.org/AptElements/git-watch.svg?branch=master)](https://travis-ci.org/AptElements/git-watch)

Full tutorial at: <http://git.watch>

## Frontend changes

You can edit and prototype the front-end templates separately from the Scala backend.

All you need is PHP 7 to render the templates.

The templates are located in 'dist/templates/'.

```bash
cd dist/templates
php -S localhost:8080 router.php
```

'dist/templates/static/' is served directly by nginx.

'dist/templates/*' is consumed by the Play app and rendered in Scala.

## Licence

* Client: MIT Licence.
* Server: GPLv3 Licence.
* Contributions: copyright transferred to Apt Elements Ltd.
* Copyright (2016) Apt Elements Ltd.
