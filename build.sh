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

currentDir=`pwd`
node_modules="../node_modules"
universal="../node_modules/universal"
repoURL="git://github.com/GPII/universal.git"

# Clone the necessary GPII framework dependencies from Git.
# TODO: Deal with cut and pastage for directory creation logic.
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
    cd $universal
    npm install
    cd $currentDir
fi
