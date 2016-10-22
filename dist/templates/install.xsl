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
                <h1>Git Watch</h1>
                <h2>Install</h2>
                <form name="submitter" method="post" enctype="multipart/form-data">
                    <button type="submit">Set up git.watch webhooks</button>
                    <br/>
                    <select name="repo" size="20">
                        <xsl:for-each select="repo">
                            <option value="{string()}">
                                <xsl:value-of select="."/>
                            </option>
                        </xsl:for-each>
                    </select>
                    <hr/>
                    Other repo:
                    <input type="text" name="repo" pattern="[A-Za-z0-9_-]+/[A-Za-z0-9_-]+/"
                           placeholder="full repository name, eg AptElements/git-watch"/>
                </form>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="repo-setup">
        <p>Repo
            <code>
                <xsl:value-of select="."/>
            </code>
            was set up!
            <a href="/">Homepage</a>
        </p>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
