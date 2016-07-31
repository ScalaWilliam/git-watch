#!/bin/bash
path="$1"

get_from_git() {
    git remote -v | sed -E -n -e 's/^.*https:\/\/(github|bitbucket)[^/]+\/([^/]+)\/([^/]+) \(fetch\)/\1\/\2\/\3/p' -e 's/^.*git@(github|bitbucket)[^:]+:([^/]+)\/(.*)\.git \(fetch\)/\1\/\2\/\3/p' | head -n 1
}

if [ -z "$path" ]; then
    path=$(get_from_git)
fi

if [ -z "$path" ]; then
    echo "Usage: git-watch [github/username/project]"
    echo "URL is not required when you run this inside a git repository"
else
    url="http://git.watch/$path"
    echo "watching url $url ..."
    while [ 1 ]; do
        curl -f --retry 9999 --retry-delay 10 --retry-max-time 0 $url;
    done
fi
