/*!
GPII Linux Personalization Framework Node.js Bootstrap

Copyright 2012 OCAD University

Licensed under the New BSD license. You may not use this file except in
compliance with this License.

You may obtain a copy of the License at
https://github.com/gpii/universal/LICENSE.txt
*/
thatall = this;

var bridge = require("bridge");

var deviceReporter = bridge.load("net.gpii.AndroidDeviceReporterImpl", this);
var output = deviceReporter.getJSONOutput();

console.log("deviceReporter output: " + output);
