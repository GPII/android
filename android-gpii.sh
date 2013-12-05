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
    adb shell am start -W -c android.intent.category.LAUNCHER -a android.intent.action.MAIN -c android.intent.category.LAUNCHER 'net.gpii.app/net.gpii.app.GpiiActivity'
    adb shell am broadcast -a org.meshpoint.anode.START -e cmdline '/sdcard/gpii/android/gpii.js'
}

function gpii-stopall {
    adb shell am broadcast -a org.meshpoint.anode.STOPALL
}

function gpii-help {
    echo "android-gpii utilities

Commands:
start - Starting gpii on the device
stop - Stop all node.js instances on device
help - Show this help
get-universal - clone the universal code and install it's dependencies
install-js - Install the gpii node.js code to the sdcard
install-privileged-apk - Installs the GpiiActivity in /system/app and then restarts the Android device
"
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
    mkdir -p build/gpii/android
    mkdir -p build/gpii/node_modules
    cd build
    cp -R $android_gpii_dir/node_modules ./gpii/android/
    cp -R $universal ./gpii/node_modules/
    cp $android_gpii_dir/gpii.js ./gpii/android/
    tar czf gpii-android.tar.gz gpii
    adb shell 'cd /sdcard; rm gpii-android.tar.gz; rm gpii-android.tar'
    adb push gpii-android.tar.gz /sdcard/gpii-android.tar.gz
    cd $android_gpii_dir
    adb shell 'cd /sdcard; gunzip gpii-android.tar.gz; tar xvf gpii-android.tar'
}

function gpii-install-privileged-apk {
    # Remount the /system filesystem as read/write. It is usually read only.
    system_dev=$(adb shell 'mount | grep /system | awk '"'"'BEGIN{FS=" "} {print $1}'"'")
    adb shell 'su -c "mount -o rw,remount -t yaffs2 $system_dev /system; chmod 777 /system/app"'
    
    # Push the apk.
    adb push platform/app/bin/GpiiApp-debug.apk /sdcard/
    adb shell 'su -c "cp /sdcard/GpiiApp-debug.apk /system/app/; chmod 644 /system/app/GpiiApp-debug.apk"; rm /sdcard/GpiiApp-debug.apk'
    
    # Remount again as read only.
    adb shell 'su -c "chmod 755 /system/app; mount -o ro,remount -t yaffs2 $system_dev /system"'
    
    echo "Restarting the Android device..."
    adb reboot
}

if [ $1 = stop ]; then
    gpii-stopall
elif [ $1 = start ]; then
    gpii-start
elif [ $1 = get-universal ]; then
    gpii-get-universal-code
elif [ $1 = install-js ]; then
    gpii-install-gpii
elif [ $1 = install-privileged-apk ]; then
    gpii-install-privileged-apk
else
    gpii-help
fi


