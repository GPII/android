android
=======

GPII on Android

Build Steps
===========

'''shell
# clone this module
export ANDROID_HOME=/path/to/your/adt-bundle-linux/sdk/
cd platform
./prebuild.sh
cd app
ant debug
cd ../..
./install-to-device.sh
./android-gpii.sh start
'''

What happens during the build
=============================

What follows is a general list of what is happening when we build this 
nproject.

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
