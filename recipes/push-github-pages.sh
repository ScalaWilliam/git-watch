#!/bin/bash

if [ ! -f out/.git/config ]; then
    echo "out/ directory needs to be a separate clone."
    echo "run this: "
    repo=$(git config --get git config --get remote.origin.url)
    echo "cd out && git init && git remote add -t gh-pages $repo && git fetch && cd .."
    exit 1
fi

git fetch

if [[ $(git diff --name-only HEAD origin/master) != "" ]]; then
    git pull origin HEAD
    cd out && git pull origin gh-pages && cd ..

    make clean
    make

    cd out && git add -A . && git commit -a -m "Generating commit $$(cd .. && git rev-parse HEAD)" && git push origin HEAD
    echo DONE
fi
