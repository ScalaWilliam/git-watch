#!/bin/bash

PID_FILE=${PID_FILE:-.pid}

git fetch

if [[ $(git diff --name-only HEAD origin/master) != "" ]]; then
    pkill -F ${PID_FILE} || true
    git pull origin master
    npm install || true
    npm run app &
    echo "$!" > ${PID_FILE}
fi
