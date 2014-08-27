GPII for Android
================

GPII on Android is currently in development release. You can follow
the steps below to download, build, and install the necessary components
to develop against. For future production releases, everything will
be bundled into an apk(s) build.

In development you can keep the gpii javascript on the sd card,
and make changes to them there while testing.

This repository contains all the platform-specific code required to run the
GPII Personalization Framework on Android.

The following components can be found in the reposoitory:

* Native modules. These modules are written in Java language and built using
Anode. These are required to allow the GPII to access to Android's internals,
and they can be found under the _platform_ folder.
	* __a11yservices__
	* __androidSettings__
	* __audioManager__
	* __intents__
	* __persistentconfig__
* In order to use these native modules from Node.js we have included several
JavaScript modules. They are under _gpii/node_modules_ folder and are strictly
needed to run the GPII on Android. These are:
	* __activityManager__: Required for launching/stopping applications/services
	* __androidSettings__: Used for setting conf items on the Android's Settings API
	* __audioManager__: Used for setting diverse volumes in the Android platform
	* __persistentConfiguration__: Used for setting conf items on the Android Preferences API

Building, installing and running
===========

We use the grunt task system to perform our build operations. If you don't have
grunt installed yet you can do so with:

	npm install -g grunt-cli

To fetch our core universal dependencies run:

	npm install

Before starting the build, you have to make two things by hand:

* Tell the system where your Android SDK is located, you can do this by running

<pre>export ANDROID_HOME=/path/to/your/android/sdk</pre>

* Pre-configure the build nevironment. To do that, just go to _platform_ folder
and run:

<pre>source ./prebuild.sh</pre>

After doing that you are able to perform the build by running:

	grunt build

Once the _build_ task has finished, you are more than ready to install the GPII
Personalization Framework into your device.

The installation can be done in two different ways:

* Normal installation. By using this method, the GPII won't be able to change
some settings or to perform certain operations, such as starting Talkback or
changing the speech rate, font size, etc. To perform this installation run:

<pre>grunt install</pre>

* Privileged installation. By using this method, the GPII will be installed as
a system application so it will have permissions to perform _privileged_ tasks.
Note that you need __root__ permissions in your device to perform this
installation.

<pre>grunt installPrivileged</pre>

Note that the install scripts make use of the Android's _adb_ command and also
they need some _powerful_ commands in your device in order to perform the
installation. Those _powerful_ commands can be provided by some applications
that are available in the [Google's PlayStore](https://play.google.com/), such as [BusyBox](https://play.google.com/store/apps/details?id=stericson.busybox) or
[BusyBox(for non-rooted devices)](https://play.google.com/store/apps/details?id=burrows.apps.busybox). In any case, we recommend the first one.

The GPII can be run either:
* From the Android User Interface. Search for _GpiiActivity_ and click on _Start_
* By using the _start_ task by running:

	grunt start

There are some more grunt tasks available, the full list of available tasks is:

* __checkBuildEnv__: Check if the build environment is appropriately set up
* __buildApk__: Build the APK
* __buildTarGz__: Build android-gpii.tar.gz
* __build__: Build the entire GPII
* __install__: Install the GPII on your Android device
* __uninstall__: Remove the GPII from your Android device
* __installPrivileged__: Install the GPII as a system application (requires root
access)
* __uninstallPrivileged__: Remove the GPII from your Android device (privileged
installation)
* __clean__: Clean the GPII binaries
* __distClean__: Clean the full GPII build environment, including build
results, downloaded dependencies, universal repo, etc.
You can use this in order to start building again from scratch.
* __start__: Start the GPII
