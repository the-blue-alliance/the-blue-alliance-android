The Blue Alliance - Android App
===============================

[![Build Status](https://travis-ci.org/the-blue-alliance/the-blue-alliance-android.png?branch=master)](https://travis-ci.org/the-blue-alliance/the-blue-alliance-android)

An Android app for accessing information about the FIRST Robotics Competition. This is a native mobile version of [The Blue Alliance](http://www.thebluealliance.com). 

The app is currently in beta state. If you want to check it out, you can join out [beta tester community](https://plus.google.com/communities/108444518980185742549) and opt into the Play Store beta program. Just rememver, there may still be issues. If you find any, please report them on the issue tracker so we can fix them.

Contributing
============
Want to add features, fix bugs, or just poke around the code? No problem!

1. Keep up to date with the [mailing list](https://groups.google.com/forum/#!forum/thebluealliance-developers) and read through the [planning documents](https://drive.google.com/#folders/0B5RO2Yzh2z01MDBOVXYwM1lXdFk) so you know what's going on.
2. Set up your development environment if you haven't used Android Studio before ([see below](#setup))
3. Fork this repository, import the project to your IDE, and create a branch for your changes
4. Make, commit, and push your changes to your branch
5. Submit a pull request here and we'll review it and get it added in!

For more detailed instructions, checkout [GitHub's Guide to Contributing](https://guides.github.com/activities/contributing-to-open-source/)

### <a name="setup"></a>
Environment Setup
-----------------

1. Ensure that you have git installed and that it is added to your system's PATH variable. You should be open you system's shell, navigate to a git repository (like this one), run ```git status``` and get data back.
2. If you haven't already, make sure you have the Android development environment set up. You will need to have [Android Studio](https://developer.android.com/sdk/installing/studio.html) installed (this also required the [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)). 
3. Make sure you read through some [Tips and Tricks](https://developer.android.com/sdk/installing/studio-tips.html) for developing with Android Studio. 
4. Use the [Android SDK Manager](https://developer.android.com/tools/help/sdk-manager.html) to download the correct versions of the Android libraries. You will need to download the Android SDK Tools, Android SDK Platform-Tools, and the SDK Platform for Android version 4.4 (API level 19). If you have already downloaded these, double check and make sure they've been updated to the latest version. 
5. If you have an Android device you want to test on, make sure that you have [enabled USB Debugging](http://stackoverflow.com/questions/16707137/how-to-find-and-turn-on-usb-debugging-mode-on-nexus-4) in your Settings menu. Otherwise, [configure a Virtual Device](https://developer.android.com/tools/devices/managing-avds.html) to debug with (you will have to also download the ARM System image from the SDK manager to use a virtual device). 

