#! /usr/bin/env python

import subprocess
import os.path

from subprocess import CalledProcessError

"""
A script to automatically run tests for only changed files
If the repo is clean, run on files changed in HEAD
If not, run on currently changed files
"""

SRC_PATH = 'android/src/main/java/'
TEST_PATH = 'android/src/test/java/'
SRC_SUFFIX = '.java'
TEST_SUFFIX = 'Test.java'


def check_clean_repo():
    try:
        subprocess.check_output(["git", "diff-files", "--quiet", "--ignore-submodules"])
        return True
    except CalledProcessError:
        return False


def current_changed_files():
    res = subprocess.check_output(['git', 'status', '--short', '--untracked-files']).split('\n')
    changed = []
    for f in res:
        if len(f) > 0:
            changed.append(f.strip().split(' ')[1])
    return changed


def head_changed_files():
    res = subprocess.check_output(['git', 'diff', '--name-only', 'HEAD~1..HEAD'])
    return res.split('\n')


def gen_to_test(changed):
    res = []
    for f in changed:
        if f.startswith(SRC_PATH) and f.endswith('.java'):
            test = f.replace(SRC_PATH, TEST_PATH)
            test = test.replace(SRC_SUFFIX, TEST_SUFFIX)
            if os.path.isfile(test):
                base = os.path.basename(test)
                base = base.replace('.java', '')
                base = "*.{}".format(base)
                res.append(base)
    return res


def run_tests(classes):
    p = subprocess.Popen(["./gradlew", "assembleAndroidTest"], shell=False)
    p.communicate()
    for cls in classes:
        proc = subprocess.Popen(["./gradlew", "testDebugUnitTest", "-a", "--quiet", "--tests={}".format(cls)], shell=False)
        proc.communicate()


if __name__ == "__main__":
    clean = check_clean_repo()
    if clean:
        changed = head_changed_files()
    else:
        changed = current_changed_files()

    test = gen_to_test(changed)
    run_tests(test)
