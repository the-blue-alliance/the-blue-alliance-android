#! /usr/bin/env python3

import argparse
import configparser
import json
import time
import requests
import subprocess
import sys
import re

from subprocess import CalledProcessError

"""
A script to push releases to Google Play
See samples: https://github.com/googlesamples/android-play-publisher-api

Requires Google API python client
Insall with: pip install google-api-python-client

Travis support requires the official client: https://github.com/travis-ci/travis.rb
"""

PACKAGE = 'com.thebluealliance.androidclient'
CHANGELOG_PATH = 'android/src/release/play/release-notes/en-US/default.txt'
INAPP_CHANGELOG = 'android/src/main/res/raw/changelog.txt'
APK_PATH_FORMAT = 'android/build/outputs/apk/release/tba-android-v{}-release.apk'
SHORTLOG_PATH = 'RELEASE_SHORTLOG'
GH_TOKEN = 'scripts/github_token'

parser = argparse.ArgumentParser(add_help=True)
parser.add_argument("tag", help="New version number (e.g. 3.1.4)")
parser.add_argument("--message", "-m", help="Tag message. Defaults to 'Version v<tag>'")
parser.add_argument("--base-tag", "-b", help="Initial tag to compare against", default=None)
parser.add_argument("--skip-tag", action="store_true", default=False,
                    help="Do not make a new git tag. Instead, push existing release identified by <tag>")
parser.add_argument("--skip-changelog", action="store_true", default=False,
                    help="Do not prompt for an updated changelog")
parser.add_argument("--dirty-repo", action="store_true", default=False,
                    help="Allow untracked changes in the repo")
parser.add_argument("--skip-validate", action="store_true", default=False,
                    help="Do not build an install the prod apk to test on a real device")
parser.add_argument("--skip-gh", action="store_true", default=False,
                    help="Do not create a new release on GitHub")
parser.add_argument("--dry-run", action="store_true", default=False,
                    help="Don't run any permanent commands, just print what's happening")
parser.add_argument("--skip-local-tests", action="store_true", default=False,
                    help="Don't run the test suite on the local machine")
parser.add_argument("--skip-travis", action="store_true", default=True,
                    help="Don't wait for travis build to complete")
parser.add_argument("--skip-slack-update", action="store_true", default=False,
                    help="Don't post an update to the TBA slack")


def check_clean_repo():
    try:
        subprocess.check_output(["git", "diff-files", "--quiet", "--ignore-submodules"]).decode("utf-8")
    except CalledProcessError:
        print("You have uncommitted changes in the repository. Commit them and try again")
        sys.exit(1)


def check_travis_tests(args):
    tag_name = "v{}".format(args.tag)
    print("Checking travis build status at tag {}".format(tag_name))

    status = "created"
    duration = ""
    while status == "created" or status == "started":
        try:
            info = subprocess.check_output(["travis", "show", tag_name]).decode("utf-8")
        except CalledProcessError:
            try:
                input("Error getting travis status. Press Enter to continue...")
            except SyntaxError:
                pass
        regex = re.search(".*State:[ \t]+((\w)*)\n", info)
        status = regex.group(1)
        regex = re.search(".*Duration:[ \t]+(([\w\d ])*)", info)
        duration = regex.group(1) if regex else None
        print("Build Status: {}, duration: {}".format(status, duration))
        if status == "passed" or status == "failed" or status == "errored":
            break
        time.sleep(30)

    if status == "failed" or status == "errored":
        print("Errors with the travis build")
        print(info)
        if not args.dry_run:
            sys.exit(-1)


def check_unittest_local(args):
    print("Running project unit tests...")
    time.sleep(2)
    try:
        script_args = ["./gradlew", "testReleaseUnitTest"]
        if args.dry_run:
            script_args.append("-m")
        subprocess.check_call(script_args)
        print("Unit tests passed!")
    except CalledProcessError:
        print("Unit tests failed. Fix them before releasing")
        sys.exit(1)


def update_whatsnew(args):
    print("Updating whatsnew file ({}). Limit 500 characters".format(CHANGELOG_PATH))
    time.sleep(2)
    base_tag = subprocess.check_output(["git", "describe", "--tags", "--abbrev=0"]).split()[0].decode("utf-8") if not args.base_tag else args.base_tag
    commitlog = subprocess.check_output(["git", "shortlog", "{}..HEAD".format(base_tag), "--oneline", "--no-merges"]).decode("utf-8")
    commitlog = '# '.join(('\n' + commitlog.lstrip()).splitlines(True))

    # Append commented commitlog to whatsnew file for ease of writing
    if not args.dry_run:
        with open(CHANGELOG_PATH, "a") as whatsnew:
            whatsnew.write(commitlog)

    if args.dry_run:
        print("Would edit changelog file: {}".format(CHANGELOG_PATH))
    else:
        subprocess.call(["vim", CHANGELOG_PATH])

    # Remove "commented" commitlog lines
    if not args.dry_run:
        subprocess.call(["sed", "-i", "/^#/d", CHANGELOG_PATH])

    # Check character count
    chars = subprocess.check_output(["wc", "-m", CHANGELOG_PATH]).decode("utf-8")
    if int(chars.split()[0]) > 500:
        print("Changelog too long. Must be limited to 500 characters")
        sys.exit(1)

    # Copy to in-app changelog
    if not args.dry_run:
        subprocess.call(["cp", CHANGELOG_PATH, INAPP_CHANGELOG])
    else:
        print("Would move {} to {}".format(CHANGELOG_PATH, INAPP_CHANGELOG))

    # Fix line breaks
    if not args.dry_run:
        subprocess.call(["sed", "-i", 's/$/<br>/', INAPP_CHANGELOG])
        subprocess.call(["rm", "-f", "{}{}".format(INAPP_CHANGELOG, "bak")])


def commit_whatsnew(dry_run):
    print("Committing new changelog")
    time.sleep(2)
    if not dry_run:
        subprocess.call(["git", "add", INAPP_CHANGELOG, CHANGELOG_PATH])

        try:
            subprocess.check_output(["git", "commit", "-m", "Version {} Whatsnew".format(args.tag)]).decode("utf-8")
        except CalledProcessError:
            print("Unable to commit new changelog")
            sys.exit(1)
    else:
        print("Would commit changelog")

    # Write shortlog to file
    base_tag = subprocess.check_output(["git", "describe", "--tags", "--abbrev=0"]).split()[0].decode("utf-8")
    shortlog = subprocess.check_output(["git", "shortlog", "{}..HEAD".format(base_tag), "--no-merges", "--oneline"]).decode("utf-8")

    if not dry_run:
        with open(SHORTLOG_PATH, "w") as logfile:
            logfile.write(shortlog)
    else:
        print("Would write shortlog to file: {}".format(SHORTLOG_PATH))
        print("Shortlog contents:\n{}".format(shortlog))


def create_tag(args):
    # Add the git tag
    name = args.message if args.message else "Version {}".format(args.tag)
    print("Creating new git tag for release v{}: {}".format(args.tag, name))
    print("To skip creating a new tag, run with --skip_tag")
    time.sleep(2)
    if not args.dry_run:
        subprocess.call(["git", "tag", "-s", "v{}".format(args.tag), "-m", name])


def validate_build(dry_run):
    print("Installing build, ensure a device is plugged in with USB Debugging enabled")
    subprocess.call(["adb", "devices"])
    try:
        input("Press Enter to continue...")
    except SyntaxError:
        pass
    time.sleep(5)
    script_args = ["./gradlew", "installRelease"]
    if dry_run:
        script_args.append("-m")
    subprocess.check_call(script_args)

    if not dry_run:
        subprocess.call(["adb", "shell", "am", "start", "-n", "com.thebluealliance.androidclient/com.thebluealliance.androidclient.activities.LaunchActivity"])
    else:
        print("Would start launch activity")
    try:
        input("Press Enter to continue the release, or ^C to quit")
    except SyntaxError:
        pass


def build_apk(args):
    # Check out repo at specified tag and build
    old_branch = subprocess.check_output(["git", "rev-parse", "--abbrev-ref", "HEAD"]).decode("utf-8").strip()
    print("Leaving {}, checking out tag v{}".format(old_branch, args.tag))
    time.sleep(2)
    if not args.dry_run:
        subprocess.call(["git", "checkout", "v{}".format(args.tag)])

    print("Uploading the app to Google Play...")
    time.sleep(2)

    # Don't rebuild the app, because we've built it already
    script_args = ["./gradlew", "publishApk", "publishListing"]
    if args.dry_run:
        script_args.append("-m")
    if not args.skip_validate:
        script_args.append("-x")
        script_args.append("assembleRelease")
    subprocess.call(script_args)
    print("Returning to {}".format(old_branch))
    if not args.dry_run:
        subprocess.call(["git", "checkout", old_branch])


def push_repo(dry_run):
    print("Pushing updates to GitHub")
    time.sleep(2)

    if not dry_run:
        subprocess.call(["git", "push", "upstream"])
        subprocess.call(["git", "push", "--tags", "upstream"])
    else:
        print("Would push repo and tags to upstream")


def create_release(args):
    apk_path = APK_PATH_FORMAT.format(args.tag)
    title = "Version {}".format(args.tag)
    tag = "v{}".format(args.tag)
    script_args = ["scripts/github_release.sh", tag, title, CHANGELOG_PATH, SHORTLOG_PATH, apk_path]

    if not args.dry_run:
        subprocess.call(script_args)
    else:
        print("Would call {}".format(subprocess.list2cmdline(script_args)))

def post_to_slack(args):
    with open('local.properties', 'r') as f:
        config_string = '[main]\n' + f.read()
    config = configparser.ConfigParser()
    config.read_string(config_string)
    slack_url = config['main']['slack_update_url']
    if not slack_url:
        print("Can't find slack url to update, bailing")
        return

    base_tag = subprocess.check_output(["git", "describe", "HEAD~1", "--tags", "--abbrev=0"]).split()[0].decode("utf-8") if not args.base_tag else args.base_tag
    print(f"Base tag: {base_tag}")
    commitlog = subprocess.check_output(["git", "shortlog", f"{base_tag}..HEAD", "--oneline", "--no-merges"]).decode("utf-8").strip()
    message_body = """
Shipping android v{version} to Google Play.
```
{shortlog}
```
https://github.com/the-blue-alliance/the-blue-alliance-android/releases/tag/v{version}
""".format(version=args.tag, shortlog=commitlog)
    slack_data = {
        "text": message_body,
        "username": "release-bot",
        "icon_emoji": ":tba:",
    }
    if not args.dry_run:
        response = requests.post(
    	    slack_url, data=json.dumps(slack_data),
            headers={'Content-Type': 'application/json'}
        )
        if response.status_code != 200:
            raise ValueError(
                'Request to slack returned an error %s, the response is:\n%s'
                % (response.status_code, response.text)
            )
    else:
        print("Would have posted {} to slack".format(json.dumps(slack_data)))


if __name__ == "__main__":
    args = parser.parse_args()
    if not args.dirty_repo:
        check_clean_repo()
    if not args.skip_local_tests:
        check_unittest_local(args)
    if not args.skip_changelog:
        update_whatsnew(args)
        commit_whatsnew(args.dry_run)
    if not args.skip_tag:
        create_tag(args)
    if not args.skip_validate:
        validate_build(args.dry_run)
    push_repo(args.dry_run)
    if not args.skip_travis:
        check_travis_tests(args)
    build_apk(args)
    create_release(args)
    if not args.skip_slack_update:
        post_to_slack(args)
