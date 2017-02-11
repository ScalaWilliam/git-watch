#!/bin/bash

[ -z "$FIREBASE_TOKEN" ] && { echo "Need to set FIREBASE_TOKEN"; exit 1; }
[ -z "$FIREBASE_PROJECT" ] && { echo "Need to set FIREBASE_PROJECT"; exit 1; }

git fetch

if [[ $(git diff --name-only HEAD origin/master) != "" ]]; then
    git pull origin master

    make

    firebase deploy --non-interactive --token ${FIREBASE_TOKEN} --project ${FIREBASE_PROJECT}
fi
