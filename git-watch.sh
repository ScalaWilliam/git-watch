#!/bin/bash

path="$2"

deploy() {
    git fetch
    git checkout "$1"
}

stream_extract_master_push () {
sed -n -e '
/^event: push$/ {
n
/^id: refs\/heads\/master/ {
n
s/^data: \([0-9a-f][0-9a-f]*\)$/\1/p
}
}
'
}
get_from_git() {
    git remote -v | git remote -v | sed -E -n -e 's/^.*https:\/\/(github|bitbucket)[^/]+\/([^/]+)\/([^/]+)\.git \(fetch\)/\1\/\2\/\3/p' -e 's/^.*git@(github|bitbucket)[^:]+:([^/]+)\/(.*)\.git \(fetch\)/\1\/\2\/\3/p' | head -n 1
}

if [ -z "$path" ]; then
    path=$(get_from_git)
fi

read_lines() {
    url="http://git.watch/$path"
    echo "watching url $url ..."
    while [ 1 ]; do
        curl -f --retry 9999 --retry-delay 10 --retry-max-time 0 $url;
    done
};

case "$1" in
    deploy-master)
        read_lines | stream_extract_master_push | xargs deploy
    ;;
    master-commit)
        read_lines | stream_extract_master_push
    ;;
    dump)
        read_lines
    ;;
    *)
        echo "Usage: git-watch [deploy-master|master-commit|dump] [github/username/project]"
        echo "URL is not required when you run this inside a git repository"
    ;;
esac
