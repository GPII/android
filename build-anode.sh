#!/bin/sh

# GPII Linux Build Script
#
# Copyright 2012 OCAD University
#
# Licensed under the New BSD license. You may not use this file except in
# compliance with this License.
#
# You may obtain a copy of the License at
# https://github.com/gpii/universal/LICENSE.txt

anodePlatformDir="anode_platform"

# We need to create the anode space and checkout all 
# the source
if [ -d $anodePlatformDir ]; then
    echo "$anodePlatformDir already exists"
else
    echo "$anodePlatformDir does not exist"
    echo "Creating $anodePlatformDir directory"
    mkdir -p "$anodePlatformDir"
fi

cd $anodePlatformDir

# Clone openssl-android
if [ -d 'openssl-android' ]; then
    echo "Already cloned openssl-android"
else
    git clone git@github.com:sgithens/openssl-android.git
    cd openssl-android
    git checkout gpii-work
    cd ..
fi

# Build openssl-android
cd openssl-android
ndk-build
cd ..

# Clone anode, pty, and node
if [ -d 'anode' ]; then
    echo "Already cloned anode"
else
    git clone git@github.com:sgithens/anode.git
    cd anode
    git checkout gpii-work
    cd ..
fi

if [ -d 'node' ]; then
    echo "Already cloned node"
else
    git clone git@github.com:sgithens/node.git
    cd node
    git checkout gpii-work
    cd ..
fi

if [ -d 'pty' ]; then
    echo "Already cloned pty"
else 
    git clone git://github.com/paddybyers/pty.git
fi

# Run the build
cd anode
ndk-build NDK_PROJECT_PATH=. NDK_APPLICATION_MK=Application.mk

# Copy the resulting binaries into the Android Dalvik App
cp libs/armeabi/libjninode.so ./app/assets/
cp libs/armeabi/bridge.node ./app/assets/



    
