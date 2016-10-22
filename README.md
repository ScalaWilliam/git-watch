# git.watch
[![Build Status](https://travis-ci.org/AptElements/git-watch.svg?branch=master)](https://travis-ci.org/AptElements/git-watch)

# Tutorials

* [Raw tutorial](https://git.watch)
* [Deploying your static website to Firebase with Git.Watch on push to master](https://github.com/ScalaWilliam/git-watch/blob/master/tutorials/FIREBASE.md)

## Changing Frontend and Content

It's all rendered using XSLT.

1. Install `sbt`, `npm` and JDK 8.
2. Run `make develop-frontend`.
3. Edit the stuff in `dist/content/`.

As a bonus you can run it behind browser-sync.

## Licence

* Client: MIT Licence.
* Server: GPLv3 Licence.
* Contributions: copyright transferred to Apt Elements Ltd.
* Copyright (2016) Apt Elements Ltd.

[![Throughput Graph](https://graphs.waffle.io/ScalaWilliam/git-watch/throughput.svg)](https://waffle.io/ScalaWilliam/git-watch/metrics/throughput)
