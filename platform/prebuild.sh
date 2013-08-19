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

cp anode/app/contrib/jtar-1.0.4.jar app/libs/

# Create Assets directory if it doesn't exist yet
if [ -d 'app/assets' ]; then
    echo 'Assets directory already created'
else
    mkdir app/assets
fi

# Fetch node binaries from webinos
curl -o app/assets/bridge.node https://raw.github.com/webinos/Webinos-Platform/master/webinos/platform/android/app/assets/bridge.node
curl -o app/assets/libjninode.so https://raw.github.com/webinos/Webinos-Platform/master/webinos/platform/android/app/assets/libjninode.so

# java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./intents/src --classpath ./app/bin/classes net.gpii.AndroidIntentHandler

# java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./a11yservices/src --classpath ./app/bin/classes net.gpii.AndroidA11ySettings

# java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./nativesettings/src --classpath ./app/bin/classes net.gpii.AndroidFontSettings
