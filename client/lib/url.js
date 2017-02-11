module.exports = {
    'transform': function (url) {
        return "https://" + url.replace(".git", "").replace("git@", "").replace("git://", "").replace("https://", "").replace(":", "/");
    }
};
