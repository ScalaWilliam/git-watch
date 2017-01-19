<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="2.0">

    <xsl:template match="repos">
        <html>
            <head>
                <title>Git Watch</title>
                <link rel="stylesheet" href="/static/main.css" type="text/css"/>
            </head>
            <body>
                <header>
                    <h1>Git Watch</h1>
                    <nav>
                        <ol>
                            <li>
                                <a href="https://github.com/ScalaWilliam/git-watch">GitHub Repository</a>
                            </li>
                            <li>
                                <a href="https://github.com/ScalaWilliam/git-watch/issues">Issues</a>
                            </li>
                            <li>
                                <a href="https://github.com/ScalaWilliam/git-watch/#security">Security</a>
                            </li>
                        </ol>
                    </nav>
                </header>

                <h2>Install</h2>
                <form name="submitter" method="post" enctype="multipart/form-data">
                    <select name="repo" size="20">
                        <xsl:for-each select="repo">
                            <option value="{normalize-space()}">
                                <xsl:value-of select="normalize-space()"/>
                            </option>
                        </xsl:for-each>
                    </select>
                    <br/>
                    <p>Or another repo: <input type="text" name="repo" pattern="[A-Za-z0-9_-]+/[A-Za-z0-9_-]+"
                                               placeholder="full repository name, eg AptElements/git-watch"/></p>
                    <button type="submit">Set up git.watch webhooks</button>
                </form>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="repo-setup">
        <html>
            <head>
                <title>Git Watch</title>
                <link rel="stylesheet" href="/static/main.css" type="text/css"/>
            </head>
            <body>
                <header>
                    <h1><a href="/">Git Watch</a></h1>
                    <nav>
                        <ol>
                            <li>
                                <a href="https://github.com/ScalaWilliam/git-watch">GitHub Repository</a>
                            </li>
                            <li>
                                <a href="https://github.com/ScalaWilliam/git-watch/issues">Issues</a>
                            </li>
                            <li>
                                <a href="https://github.com/ScalaWilliam/git-watch/#security">Security</a>
                            </li>
                        </ol>
                    </nav>
                </header>
                <p>Repo
                    <code>
                        <xsl:value-of select="."/>
                    </code>
                    was set up!
                    <a href="/">Go back</a>
                </p>
                <p><strong>Usage</strong></p>
                <p>Inside a Git directory:</p>
                <textarea rows="2" class="usage">git-watch --url=https://git.watch/github/<xsl:value-of select="normalize-space()"/> \
    --push-execute='echo "Received an event for: %sha% %ref%" &amp;&amp; git pull &amp;&amp; ls'</textarea>
                <p>Windows:</p>
                <textarea rows="2" class="usage">git-watch --url=https://git.watch/github/<xsl:value-of select="normalize-space()"/> ^
    "--push-execute=echo Received an event for: %sha% %ref% &amp;&amp; git pull &amp;&amp; dir"</textarea>
                <p>Alternatively:</p>
                <textarea rows="2" class="usage">git-watch --push-execute='echo "Received an event for: %sha% %ref%" &amp;&amp; git pull &amp;&amp; ls'</textarea>
                <p>Windows:</p>
                <textarea rows="2" class="usage">git-watch "--push-execute=echo Received an event for: %sha% %ref% &amp;&amp; git pull &amp;&amp; dir"</textarea>
                <p>Debugging:</p>
                <textarea rows="1" class="usage">curl -i https://git.watch/github/<xsl:value-of select="normalize-space()"/></textarea>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
