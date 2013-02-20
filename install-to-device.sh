#!/bin/sh

# GPII Android Install Script
#
# Copyright 2012 OCAD University
#
# Licensed under the New BSD license. You may not use this file except in
# compliance with this License.
#
# You may obtain a copy of the License at
# https://github.com/gpii/universal/LICENSE.txt

node_modules="../node_modules"
universal="../node_modules/universal"
repoURL="git://github.com/GPII/universal.git" 

if [ -d $node_modules ]; then
    echo "$node_modules already exists"
else
    echo "$node_modules does not exist"
    echo "creating $node_modules"
    mkdir -p "$node_modules"
fi
if [ -d $universal ]; then
    echo "$universal already exists"
else
    echo "$universal does not exist"
    echo "cloning universal"
    git clone "$repoURL" "$universal"
fi

echo "Going to put the GPII Javascript source on the device"
adb shell 'mkdir -p /sdcard/gpii/node_modules'
adb push $universal '/sdcard/gpii/node_modules/universal'
adb push gpii.js /sdcard/gpii/

echo "Going to fetch and install the node apk"
curl -O https://raw.github.com/sgithens/gpii-android-test/master/AnodeActivity.apk

echo "Installing APK to device"
adb install AnodeActivity.apk
