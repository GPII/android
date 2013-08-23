#!/bin/bash
# GPII Android Install Script
#
# Copyright 2012 OCAD University
#
# Licensed under the New BSD license. You may not use this file except in
# compliance with this License.
#
# You may obtain a copy of the License at
# https://github.com/gpii/universal/LICENSE.txt

gpii_dir="$(pwd)/.."
node_modules="$(pwd)/../node_modules"
universal="$(pwd)/../node_modules/universal"
repoURL="git://github.com/GPII/universal.git" 
android_gpii_dir=$(pwd)

function gpii-start {
    #adb shell am start -c android.intent.category.LAUNCHER -a android.intent.action.MAIN -c android.intent.category.LAUNCHER 'net.gpii.app/net.gpii.app.GpiiActivity'
    adb shell am broadcast -a org.meshpoint.anode.START -e cmdline '/sdcard/gpii/android/gpii.js'
}

function gpii-stopall {
    adb shell am broadcast -a org.meshpoint.anode.STOPALL
}

function gpii-help {
    echo "Print the help here..."
}

function gpii-get-universal-code {
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
    cd $universal
    npm install
    cd $android_gpii_dir
}

function gpii-install-gpii {
    cd $gpii_dir
    if [ -d $node_modules ]; then
        rm -fR build
    fi
    mkdir -p build/gpii
    cd build
    cp -R $android_gpii_dir ./gpii/
    cp -R $node_modules ./gpii/
    tar czf gpii-android.tar.gz gpii
    adb push gpii-android.tar.gz /sdcard/gpii-android.tar.gz
    cd $android_gpii_dir
    adb shell 'cd /sdcard; tar -xzvf gpii-android.tar.gz'
}

if [ $1 = stop ]; then
    gpii-stopall
elif [ $1 = start ]; then
    gpii-start
elif [ $1 = get-universal ]; then
    gpii-get-universal-code
elif [ $1 = install-js ]; then
    gpii-install-gpii
else
    gpii-help
fi


