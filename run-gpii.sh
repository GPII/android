#!/bin/sh

adb shell am start -a org.meshpoint.anode.MAIN
adb shell am broadcast -a org.meshpoint.anode.START -e cmdline '/sdcard/gpii/gpii.js'
