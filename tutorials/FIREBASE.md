Deploying your static website to Firebase with Git.Watch on push to master
#

Firebase comes with free HTTPS/SSL and CDN, all for free. However they don't support Git.

Here's how you deploy straight from your GitHub repository to Firebase on push:

1. Set up a Firebase Hosting account as in <https://firebase.google.com/docs/hosting/quickstart>.
2. Install `git-watch` with `npm install -g gitwatch-client`.
3. Run `firebase login:ci` to get a CI token.
4. Create a 'Makefile' containing the following:

```
deploy:
	firebase deploy --non-interactive --token "$$FIREBASE_TOKEN" --project "projectid"
serve:
	firebase serve
watch:
    git-watch --url=https://git.watch/github/Your/repo --push-execute='make push-%ref% || true'
push-refs/heads/master:
	git pull
	make deploy
```

Commit & push this to your repository.

Then run:
```
FIREBASE_TOKEN="<token>" make watch
```

Now make another commit and watch what happens.

## Troubleshooting

Run `curl -i https://git.watch/github/Your/repo` to ensure your GitHub WebHook is working with git.watch
