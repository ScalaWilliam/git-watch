# [git.watch](https://git.watch)
[![Build Status](https://travis-ci.org/ScalaWilliam/git-watch.svg?branch=master)](https://travis-ci.org/ScalaWilliam/git-watch)
[![Join the chat at https://gitter.im/ScalaWilliam/git-watch](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ScalaWilliam/git-watch?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Workflow](https://badge.waffle.io/ScalaWilliam/git-watch.png?label=ready&title=Ready)](https://waffle.io/ScalaWilliam/git-watch)

Git Watch reacts to GitHub Push events to enable Continuous Deployment. And polls too.

# Philosophy

Dogfood is eaten. Git Watch uses Git Watch for its own deploys, see [Makefile](https://github.com/ScalaWilliam/git-watch/blob/master/Makefile). Each commit to master = an immediate deploy if content has been changed, and something like a 10 second deploy otherwise. 

<img src="https://cloud.githubusercontent.com/assets/2464813/19618147/edc87a84-9874-11e6-9e90-ba258005b63d.png" height="80"/>

Continuous Deployment works spectacularly well. It is very fast and fun to use. You put in most of the work upfront, clarify all the assumptions and then pretty much forget about it. Whereas typical projects write a lot of code and then stress about how to deploy it. I'll write an article about this later.

# Tutorials

* [Slideshare](http://www.slideshare.net/WilliamNarmontas/git-watch)
* [Getting started with Git.Watch](https://github.com/ScalaWilliam/git-watch/blob/master/tutorials/GETTINGSTARTED.md)
* [Deploying your static website to Firebase with Git.Watch on push to master](https://github.com/ScalaWilliam/git-watch/blob/master/tutorials/FIREBASE.md)


# Basic Demo

![asciicast](https://cloud.githubusercontent.com/assets/2464813/19849322/8ac6e888-9f8d-11e6-81d5-3137c7fd940d.gif)

# Security
Everything is open to the world. This model is best suited for public projects. Private projects will be for later.

The client-side application has explicit filters to ensure no injection takes place.

If you want to ensure legitimate GitHub pushes, add `--secret=[secret you specified]`. See [https://github.com/ScalaWilliam/git-watch/issues/3](https://github.com/ScalaWilliam/git-watch/issues/3)

# Contributing
See: [DEVELOPING.md](DEVELOPING.md)

Contribution implies transfer of ownership/copyright to Apt Elements Ltd.

## Changing Frontend and Content

1. Install `sbt`, `npm` and JDK 8.
2. Run `make develop-frontend`. Wait for a browser window to open.
3. Edit the stuff in `dist/content/`.

## Coding Standards
Feel free to contribute to any of the tickets, including improving the design and the User Experience. There's a /lot/ of work to be done and I am very very open to new ideas.

## Licence

* Client: MIT Licence.
* Server: GPLv3 Licence.
* Copyright (2016) Apt Elements Ltd. [William Narmontas](https://www.scalawilliam.com/)

[![Throughput Graph](https://graphs.waffle.io/ScalaWilliam/git-watch/throughput.svg)](https://waffle.io/ScalaWilliam/git-watch/metrics/throughput)
