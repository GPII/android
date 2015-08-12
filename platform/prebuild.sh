#!/bin/bash

# Clone anode
if [ -d 'anode' ]; then
    echo 'Anode already checked out'
else
    git clone git://github.com/paddybyers/anode.git
fi


# Issue a warning if you don't have ANDROID_HOME set
if [ -z "$ANDROID_HOME" ]; then
    echo "Warning: ANDROID_HOME environment variable not set, compilation will fail."
fi

# Set ANODE_ROOT env variable
# TODO Reminder that script has to be sourced for these to be set. 
export ANODE_ROOT=$(pwd)/anode

# Create libs directory if it doesn't exist yet
if [ -d 'app/libs' ]; then
    echo 'libs directory already created'
else
    mkdir app/libs
fi

curl -o app/libs/jtar-1.0.4.jar https://jtar.googlecode.com/files/jtar-1.0.4.jar
curl -o app/libs/commons-compress-1.5.jar https://docs.google.com/uc?export=download&id=0B9NaK6yZUAngSXlKNTBOMHRIcXM

# Create Assets directory if it doesn't exist yet
if [ -d 'app/assets' ]; then
    echo 'Assets directory already created'
else
    mkdir app/assets
fi

# Fetch node binaries from webinos
curl -o app/assets/bridge.node 'https://gist.githubusercontent.com/javihernandez/b9619af20219417d5a03/raw/1a59be257b2162b2b991b9ace01e992a82df6616/bridge.node'
curl -o app/assets/libjninode.so 'https://gist.githubusercontent.com/javihernandez/b9619af20219417d5a03/raw/611ed2a158ace788bb75fdfaa3987768a8924a75/libjninode.so'
