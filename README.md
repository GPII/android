android
=======

GPII on Android is currently in development release. You can follow
the steps below to download, build, and install the necessary components
to develop against. For future production releases, everything will
be bundled into an apk(s) build.

In development you can keep the gpii javascript on the sd card,
and make changes to them there while testing.

Build Steps
===========

`shell
# clone this android module and cd in to it.

export ANDROID_HOME=/path/to/your/adt-bundle-linux/sdk/
cd platform
# It's important that you source rather than execute prebuild.sh
# because it needs to export some env variables.
source ./prebuild.sh
cd app
ant debug
adb install ./bin/GpiiApp-debug.apk
cd ../..

# You can skip the get-universal step if you're planning on working
# with a universal branch and set it up manually. However, if you
# use this command and are working on a branch, be sure to go to
# ../node_modules/universal and checkout the correct branch.
./android-gpii.sh get-universal

# Tar gzips the android and universal javascript and moves it to the
# android SD card and unzips it.
./android-gpii.sh install-js

# Starts gpii on port 8081 (by default)
./android-gpii.sh start
`

What happens during the build
=============================

What follows is a general list of what is happening when we build this 
project.

- The gpii android module containing this readme file is clone with git.
- Initial work is performed in the platform directory, starting with fetching
  the anode project. Then we make create an assets directory in /app, and 
  download the prebuilt node.js binaries to put in that directory.
- We set the anode root env variable and check to make sure the android home
  variable is set.
- We copy the jtar jar dependency from anode to our app directory.
- Then we go into /app and use 'ant debug' to build the Gpii activity apk.
- Then we create the node_modules directory and clone the universal repository
  into it.
- Inside the universal repository 'npm install' is run to fetch all the 
  dependencies.
- The android and universal gpii javascript is copied to the device, to the
  sdcard for development. ( For production builds we will bundle it in the apk )
- The gpii activity apk is 'adb installed' to the device.
- The first time running, you need to manually start the Gpii activity. ( This
  will be fixed in the future )
- Then we can start the GPII via intent.

Notes
=====

- Currently, the main activity screen must be launched for the broadcast 
  intent that starts GPII to work.
