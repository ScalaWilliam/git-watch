<!doctype html>
<html>
<head>
    <title>Git Watch</title>
    <link rel="stylesheet" href="/static/main.css" type="text/css">
</head>
<body>
<h1>Git Watch</h1>
<h2>Install</h2>
<form name="submitter" method="post" enctype="multipart/form-data">
    <button type="submit">Set up git.watch webhooks</button>
    <br/>
    <select name="repo" size="20">
        <?php foreach (@$_GET['repo'] as $repo) { ?>
            <option value="<?php echo htmlspecialchars($repo); ?>"><?php echo htmlspecialchars($repo); ?></option>
        <?php } ?>
    </select>
    <hr/>
    Other repo: <input type="text" name="repo" pattern="[A-Za-z0-9_-]+/[A-Za-z0-9_-]+/"
                       placeholder="full repository name, eg AptElements/git-watch"/>
</form>
</body>
</html>
