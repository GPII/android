/*!
GPII Android Personalization Framework Node.js Bootstrap

Copyright 2014 RTF-US
Copyright 2014 Emergya

Licensed under the New BSD license. You may not use this file except in
compliance with this License.

You may obtain a copy of the License at
https://github.com/gpii/universal/LICENSE.txt
*/

"use strict";

var fs = require("fs");

module.exports = function (grunt) {

    grunt.loadNpmTasks("grunt-shell");
    grunt.loadNpmTasks("grunt-contrib-jshint");
    grunt.loadNpmTasks("grunt-jsonlint");
    grunt.loadNpmTasks("grunt-gpii");

    grunt.initConfig({
        jshint: {
            src: ["gpii/**/*.js"],
            buildScripts: ["Gruntfile.js"],
            options: {
                jshintrc: true
            }
        },
        jsonlint: {
            src: ["gpii/**/*.json"]
        },
        shell: {
            options: {
                stdout: true,
                stderr: true,
                failOnError: true
            },
            buildApk: {
                command: [
                    "cd " + __dirname + "/platform/app",
                    "ant debug",
                    "cd -"
                ].join("&&")
            },
            cleanApk: {
                command: [
                    "cd " + __dirname + "/platform/app",
                    "ant clean",
                    "cd -"
                ].join("&&")
            },
            copyToBuildFolder: {
                command: function (filePath, outputFolder) {
                    var command = "";
                    var buildPath = __dirname + "/../build/";
                    var fileName = filePath.split("/").reverse()[0];
                    outputFolder = (outputFolder || "");

                    if (!(fs.existsSync(buildPath + outputFolder))) {
                        command += "mkdir -p " + buildPath + outputFolder + "&&";
                    }

                    command += "cp -rf " + filePath +
                               " " + buildPath + outputFolder;


                    command += " && tput bold && echo '" + fileName +
                               " successfully copied to build folder' " +
                               "&& tput sgr0";

                    return command;
                },
                options: {
                    callback: function (err, stdout, stderr, cb) {
                        var returnValue = true;
                        if (err) {
                            grunt.log.error(stderr);
                            returnValue = false;
                        }
                        cb(returnValue);
                    }
                }
            },
            createAndroidTarGz: {
                command: [
                    "cd " + __dirname + "/../build/",
                    "tar czf gpii-android.tar.gz gpii",
                    "cd -",
                    "tput bold",
                    "echo 'Successfully created android-gpii.tar.gz file.'",
                    "tput sgr0"
                ].join("&&")
            },
            removeBuildFolder: {
                command: "rm -rf " + __dirname + "/../build"
            },
            installJs: {
                command: [
                    "adb shell 'cd /sdcard; rm gpii-android.tar.gz; rm gpii-android.tar'",
                    "adb push " + __dirname + "/../build/gpii-android.tar.gz /sdcard/gpii-android.tar.gz",
                    "adb shell 'cd /sdcard; gunzip gpii-android.tar.gz; tar xvf gpii-android.tar'"
                ].join("&&"),
                options: {
                    execOptions: {
                        maxBuffer: 5000 * 1024
                    }
                }
            },
            uninstallJs: {
                command: "adb shell 'rm -r /sdcard/gpii'"
            },
            installApk: {
                command: "adb install " + __dirname + "/../build/GpiiApp-debug.apk",
                options: {
                    callback: function (err, stdout, stderr, cb) {
                        var returnValue = true;
                        if (err) {
                            grunt.log.error(stderr);
                            returnValue = false;
                        }
                        if (stdout.indexOf("INSTALL_FAILED_ALREADY_EXISTS") > -1) {
                            grunt.log.error("Error while trying to install the APK. " +
                                            "Looks like it is already installed\n" +
                                            "Try to uninstall it before by using " +
                                            "the 'shell:uninstallApk' task.");
                            returnValue = false;
                        }
                        cb(returnValue);
                    }
                }
            },
            uninstallApk: {
                command: "adb uninstall net.gpii.app",
                options: {
                    callback: function (err, stdout, stderr, cb) {
                        var returnValue = true;
                        if (err) {
                            grunt.log.error(stderr);
                            returnValue = false;
                        }
                        if (stdout === "Failure") {
                            grunt.log.error("Error while trying to uninstall the APK.\n" +
                                            "Is the APK installed as a privileged " +
                                            "application? Try to uninstall it by " +
                                            "using the 'shell:uninstallPrivilegedApk' task.");
                            returnValue = false;
                        }
                        cb(returnValue);
                    }
                }
            },
            installPrivilegedApk: {
                command: [
                    "system_dev=$(adb shell 'mount | grep /system | awk '\"'\"'BEGIN{FS=\" \"} {print $1}'\"'\")",
                    "adb shell 'su -c \"mount -o rw,remount -t yaffs2 $system_dev /system; chmod 777 /system/app\"'",
                    "adb push " + __dirname + "/../build/GpiiApp-debug.apk /sdcard/",
                    "adb shell 'su -c \"cp /sdcard/GpiiApp-debug.apk /system/app/; chmod 644 /system/app/GpiiApp-debug.apk\"; rm /sdcard/GpiiApp-debug.apk'",
                    "adb shell 'su -c \"chmod 755 /system/app; mount -o ro,remount -t yaffs2 $system_dev /system\"'",
                    "echo 'Restarting the Android device...'",
                    "adb reboot"
                ].join("&&")
            },
            uninstallPrivilegedApk: {
                command: [
                    "system_dev=$(adb shell 'mount | grep /system | awk '\"'\"'BEGIN{FS=\" \"} {print $1}'\"'\")",
                    "adb shell 'su -c \"mount -o rw,remount -t yaffs2 $system_dev /system; chmod 777 /system/app\"'",
                    "adb shell 'su -c \"rm /system/app/GpiiApp-debug.apk\"'",
                    "adb shell 'su -c \"chmod 755 /system/app; mount -o ro,remount -t yaffs2 $system_dev /system\"'",
                    "echo 'Restarting the Android device...'",
                    "adb reboot"
                ].join("&&")
            },
            distClean: {
                command: [
                    "cd " + __dirname,
                    "rm -rf ../node_modules",
                    "rm -rf node_modules",
                    "rm -rf platform/anode",
                    "rm -rf platform/app/assets",
                    "rm -rf platform/app/libs",
                    "cd -"
                ].join("&&")
            },
            startGpii: {
                command: [
                    "adb shell am start -W -c android.intent.category.LAUNCHER -a android.intent.action.MAIN " +
                    "-c android.intent.category.LAUNCHER 'net.gpii.app/net.gpii.app.GpiiActivity'",
                    "adb shell am broadcast -a org.meshpoint.anode.START -e cmdline '/sdcard/gpii/android/gpii.js'"
                ].join("&&")
            }
        }
    });

    grunt.registerTask("checkBuildEnv", "Check if the build environment is appropriately set up", function () {
        if ((!(process.env.ANDROID_HOME)) || (!(process.env.ANODE_ROOT))) {
            grunt.log.error("ANDROID_HOME and/or ANODE_ROOT not found.\n" +
                            "Please, be sure to execute 'source ./prebuild.sh' " +
                            "under 'platform' to set up your build environment " +
                            "before running this Grunt task again.");
            return false;
        }
    });

    grunt.registerTask("buildApk", "Build the APK", function () {
        var pathToApk = __dirname + "/platform/app/bin/GpiiApp-debug.apk";
        grunt.task.run(["shell:buildApk", "shell:copyToBuildFolder:" + pathToApk]);
    });

    grunt.registerTask("buildTarGz", "Build android-gpii.tar.gz", function () {
        var androidNodeModules = __dirname + "/gpii/node_modules";
        grunt.task.run(["shell:copyToBuildFolder:" + androidNodeModules + ":gpii/android",
                        "shell:copyToBuildFolder:" + __dirname + "/gpii.js" + ":gpii/android",
                        "shell:copyToBuildFolder:" + __dirname + "/index.js" + ":gpii/android",
                        "shell:copyToBuildFolder:" + __dirname + "/package.json" + ":gpii/android",
                        "shell:copyToBuildFolder:" + __dirname + "/../node_modules" + ":gpii",
                        "shell:createAndroidTarGz"]);
    });

    grunt.registerTask("build", "Build the entire GPII", function () {
        grunt.task.run("gpii-universal");
        grunt.task.run("checkBuildEnv");
        grunt.task.run("buildApk");
        grunt.task.run("buildTarGz");
    });

    grunt.registerTask("install", "Install the GPII on your Android device", function () {
        grunt.task.run("shell:installJs");
        grunt.task.run("shell:installApk");
    });

    grunt.registerTask("uninstall", "Remove the GPII from your Android device", function () {
        grunt.task.run("shell:uninstallJs");
        grunt.task.run("shell:uninstallApk");
    });

    grunt.registerTask("installPrivileged", "Install the GPII as a system application (requires root access)", function () {
        grunt.task.run("shell:installJs");
        grunt.task.run("shell:installPrivilegedApk");
    });

    grunt.registerTask("uninstallPrivileged", "Remove the GPII from your Android device (privileged installation)", function () {
        grunt.task.run("shell:uninstallJs");
        grunt.task.run("shell:uninstallPrivilegedApk");
    });

    grunt.registerTask("clean", "Clean the GPII binaries", function () {
        grunt.task.run("shell:cleanApk");
        grunt.task.run("shell:removeBuildFolder");
    });

    grunt.registerTask("distClean", "Clean the full GPII build environment, including " +
                       "build results, downloaded dependencies, universal repo, etc.\n" +
                       "You can use this in order to start building again from scratch", function () {
        grunt.task.run("clean");
        grunt.task.run("shell:distClean");
    });

    grunt.registerTask("start", "Start the GPII", function () {
        grunt.task.run("shell:startGpii");
    });
};
