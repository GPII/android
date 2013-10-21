This platform directory contains various build parts of the platform
specific pieces of the Android APK that gets built. The directories 
include:

* a11yservices This directory contains the code to start and stop 
  settings such as Talkback. In order to work, the apk must be installed
  in /system/apps for these to work.

* anodeshare This directory contains code that was originally copied from
  git://github.com/paddybyers/anode.git 7e0c90350fbc54689fca6b50d65807b0015fafc5
  Currently, because of the way the code is factored and the packaging mechanisms
  for android builds, these code needs to be duplicated with some minor
  changes for our work. We hope to work with the anode project to 
  remove this necessity.
  
* app This directory contains the raw code for the GPII

* intents This directory contains the module for launching intents from
  node.js
  
* nativesettings Module for setting settings that require root access on
  the device, but do not require the apk to be installed in /system/apps.
  
  
