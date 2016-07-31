[![Throughput Graph](https://graphs.waffle.io/ScalaWilliam/git.watch/throughput.svg)](https://waffle.io/ScalaWilliam/git.watch/metrics/throughput)

# git.watch

Zero-configuration Git push hooks as event streams. For free.

Sign up at: [http://git.watch]

## Problem space

Git push hooks are wonderful. They enable continuous deployment and continuous integration.

Unfortunately you need to set up a server to receive them and this creates an ugly coupling.
For my open source projects, I've tried decoupling using AWS. I got a huge bill the next month.

What if you wanted to test push hooks locally, what if you're behind a firewall? It becomes quite complex.

What if you wanted it to be free and quick to set up?

But there's nothing particularly huge in just passing events forward, which is what we'll do for you, for free. Using the most basic tools.
