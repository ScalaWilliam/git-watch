# [git.watch](https://git.watch)
[![Build Status](https://travis-ci.org/ScalaWilliam/git-watch.svg?branch=master)](https://travis-ci.org/ScalaWilliam/git-watch)

Trigger custom commands when your Git repository on the Git host (GitHub, BitBucket or GitLab) is updated.

Git Watch enables automation for Firebase Hosting, GitHub Pages, Play Framework, simple Python applications.
It can act as a minimalist alternative to Jenkins Continuous Integration.

# Architecture

1. Your **Git repository** is updated on the **Git host**.
2. **Git host** sends a webhook to the **Git Watch server**.
3. **Git Watch server** broadcasts the repository url to the event stream at `https://git.watch/events/`.
4. A **Git Watch client** receives the event and triggers your custom command. 

# Security
URLs of updated repositories are sent to the public event stream. 

## Licence

* Client: MIT, Server: GPLv3.
* Copyright (2016) Apt Elements Ltd. [William Narmontas](https://www.scalawilliam.com/)
