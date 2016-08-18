# Git Watch (CLI)

Easy automated deployments with GitHub WebHooks. Example:

    git-watch \
    --url=http://git.watch/github/AptElements/git-watch \
    --push-execute='
    ref="%ref%"
    if [ "$ref" = "refs/heads/master" ]; then
    cd /path/to/repo &&
    git pull origin $ref &&
    echo "Updated $ref"
    fi'


Get more detail at <http://git.watch/>
