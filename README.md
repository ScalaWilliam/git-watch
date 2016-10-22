# [git.watch](https://git.watch)
[![Build Status](https://travis-ci.org/ScalaWilliam/git-watch.svg?branch=master)](https://travis-ci.org/ScalaWilliam/git-watch)

Git Watch reacts to GitHub Push events to enable Continuous Deployment

# Tutorials

* [Getting started with Git.Watch](https://github.com/ScalaWilliam/git-watch/blob/master/tutorials/GETTINGSTARTED.md)
* [Deploying your static website to Firebase with Git.Watch on push to master](https://github.com/ScalaWilliam/git-watch/blob/master/tutorials/FIREBASE.md)

# Basic Demo

![asciicast](https://cloud.githubusercontent.com/assets/2464813/19617752/478fab50-986b-11e6-8056-231051759f32.gif)

## Changing Frontend and Content

It's all rendered using XSLT.

1. Install `sbt`, `npm` and JDK 8.
2. Run `make develop-frontend`. Wait for a browser window to open.
3. Edit the stuff in `dist/content/`.

## Security
Everything is open to the world. This model is best suited for public projects. Private projects will be for later.

The client-side application has explicit filters to ensure no injection takes place.

If you want to ensure legitimate GitHub pushes, add `--secret=[secret you specified]`

## Licence

* Client: MIT Licence.
* Server: GPLv3 Licence.
* Contributions: copyright transferred to Apt Elements Ltd.
* Copyright (2016) Apt Elements Ltd.

[![Throughput Graph](https://graphs.waffle.io/ScalaWilliam/git-watch/throughput.svg)](https://waffle.io/ScalaWilliam/git-watch/metrics/throughput)
