<?php
if ( $_SERVER['REQUEST_URI'] == "/") {
    readfile("types.html");
} else if ($_SERVER['SCRIPT_NAME'] == "/index") {
    readfile("index.html");
} else {
    return false;
//    phpinfo();
}
?>
