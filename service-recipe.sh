#!/bin/bash
git fetch
if [[ $(git diff --name-only HEAD origin/master) != "" ]]; then
    pkill -F .pid || true
    git pull origin master
    pip install --user -r requirements.txt || true
    python -m app &
    echo "$!" > .pid
fi
