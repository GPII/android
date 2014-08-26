/*!
GPII Linux Personalization Framework Node.js Bootstrap

Copyright 2014 RTF-US
Copyright 2014 Emergya

Licensed under the New BSD license. You may not use this file except in
compliance with this License.

You may obtain a copy of the License at
https://github.com/gpii/universal/LICENSE.txt
*/

"use strict";

var fs = require("fs");

module.exports = function(grunt) {

    grunt.loadNpmTasks("grunt-shell");
    grunt.loadNpmTasks("grunt-contrib-jshint");
    grunt.loadNpmTasks("grunt-jsonlint");
    grunt.loadNpmTasks("grunt-gpii");
  
    grunt.initConfig({
        jshint: {
            src: ["gpii/**/*.js", "tests/**/*.js"],
            buildScripts: ["Gruntfile.js"],
            options: {
                jshintrc: true
            }
        },
        jsonlint: {
            src: ["gpii/**/*.json", "tests/**/*.json"]
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
                        if (err) {
                            grunt.log.error(stderr);
                            return false;
                        }
                        cb();
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
            installJs: {
                command: [
                    "adb shell 'cd /sdcard; rm gpii-android.tar.gz; rm gpii-android.tar'",
                    "adb push " + __dirname + "/../build/gpii-android.tar.gz /sdcard/gpii-android.tar.gz",
                    "adb shell 'cd /sdcard; gunzip gpii-android.tar.gz; tar xvf gpii-android.tar'"
                ].join("&&"),
                options: {
                    execOptions: {
                        maxBuffer: 5000*1024
                    }
                }
            },
            installApk: {
                command: [
                    "adb install " + __dirname + "/../build/GpiiApp-debug.apk"
                ]
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
        };
    });

    grunt.registerTask("buildApk", "Build the APK", function () {
        var pathToApk = __dirname + "/platform/app/bin/GpiiApp-debug.apk"
        grunt.task.run(["shell:buildApk", "shell:copyToBuildFolder:" + pathToApk]);
    });

    grunt.registerTask("buildTarGz", "Build android-gpii.tar.gz", function () {
        var androidNodeModules = __dirname + "/gpii/node_modules";
        grunt.task.run(["shell:copyToBuildFolder:" + androidNodeModules + ":gpii/android",
                        "shell:copyToBuildFolder:" + __dirname + "/gpii.js" + ":gpii/android",
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

    grunt.registerTask("installPrivilegedApk", "Install the GPII's APK as a system application", function () {
        grunt.task.run("shell:installPrivilegedApk");
    });

    grunt.registerTask("clean", "Clean the GPII binaries and uninstall", function () {
    });

    grunt.registerTask("start", "Start the GPII", function () {
    });
};
