<!doctype html>
<html>
<head>
    <title>Git Watch</title>
    <link rel="stylesheet" href="static/main.css" type="text/css">
</head>
<body>
<h1>Git Watch</h1>
<p><a target="_blank" href="https://github.com/ScalaWilliam/git-watch">github.com/ScalaWilliam/git-watch</a></p>
<h2>Getting Started</h2>
<ol>
    <li>Set up a GitHub WebHook to <code>https://git.watch/github/</code> with an optional secret. You can also <a
            href="/install/">Do it automatically</a></a></li>
    <li>Install the client <code>npm install -g <a href="https://www.npmjs.com/package/gitwatch-client" target="_blank">gitwatch-client</a></code>
        (use the latest Node.js)
    </li>
    <li>Listen for push events: <code>git-watch --url=<a href="http://git.watch/github/ScalaWilliam/git-watch"
                                                         target="_blank">http://git.watch/github/ScalaWilliam/git-watch</a>
        --push-execute='echo deploying %sha% %ref%'</code></li>
</ol>
<h2>Notes</h2>
<h3>Security</h3>
<ul>
    <li>The client-side application has explicit filters to ensure no injection takes place.</li>
    <li>Everything is open to the world (client: MIT, server: GPLv3).</li>
    <li>If you want to ensure legitimate github requests, add the <code>--secret=[secret you specified]</code> option to
        <code>git-watch</code></li>
</ul>
<h3>Deploying only <code>master</code></h3>
<pre><code>git-watch \
--url=<a href="https://git.watch/github/ScalaWilliam/git-watch" target="_blank">https://git.watch/github/ScalaWilliam/git-watch</a> \
--push-execute='
ref="%ref%"
if [ "$ref" = "refs/heads/master" ]; then
cd /path/to/repo &amp;&amp;
git pull origin $ref &amp;&amp;
echo "Updated $ref"
fi'</code></pre>
</body>

</html>
