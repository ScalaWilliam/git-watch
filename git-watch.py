#!/usr/bin/python
from sseclient import SSEClient
import requests
import time
import os
import sys
import subprocess

def get_messages(url):
    while True:
        try:
            messages = SSEClient(url)
            for msg in messages:
                yield msg
        except requests.exceptions.HTTPError, e:
            if e.response.status_code / 100 == 5:
                time.sleep(10)
            else:
                raise e

def master_deploy(message):
    if hasattr(message, 'event') and hasattr(message, 'id'):
        if message.event == 'push' and message.id == 'refs/heads/master' and message.data.isalnum():
            subprocess.call(["git", "fetch"])
            subprocess.call(["git", "checkout", message.data])
            if os.path.isfile("./deploy.sh"):
                subprocess.call(["./deploy.sh", message.data])

if __name__ == "__main__":
    if len(sys.argv) < 2:
        raise Exception("Not enough arguments. Please include a URL.")
    else:
        url = sys.argv[1]
        for message in get_messages(url):
            master_deploy(message)
