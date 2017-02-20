#! /bin/bash

# A script to update the logo with the newest files from github

START=$(pwd)
MAIN=$START/android/src/main
DEBUG=$START/android/src/debug
mkdir -p logo-tmp

cd logo-tmp
wget https://github.com/the-blue-alliance/the-blue-alliance-logo/archive/master.zip
unzip master.zip
cd the-blue-alliance-logo-master/android

echo "Copying image resources..."
for dir in */ ; do
  if [[ -d "$dir" && ! -L "$dir" ]]; then
    cp ${dir}ic_launcher_blue.png $MAIN/res/${dir}ic_launcher.png
    cp ${dir}ic_notification.png $MAIN/res/${dir}ic_notification.png
    cp ${dir}ic_launcher_red.png $DEBUG/res/${dir}ic_launcher.png
  fi;
done

echo "Cleaning up..."
cd $START
rm -rf logo-tmp

echo "Done"
