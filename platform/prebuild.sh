#!/bin/bash

# Clone anode
git clone git://github.com/paddybyers/anode.git

# Issue a warning if you don't have ANDROID_HOME set

# Set ANODE_ROOT env variable
export ANODE_ROOT=$(pwd)/anode

# Fetch node binaries from webinos
curl -o app/assets/bridge.node https://raw.github.com/webinos/Webinos-Platform/master/webinos/platform/android/app/assets/bridge.node
curl -o app/assets/libjninode.so https://raw.github.com/webinos/Webinos-Platform/master/webinos/platform/android/app/assets/libjninode.so





