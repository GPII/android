 /*
 * GPII Android Personalization Framework Node.js Index
 *
 * Copyright 2012 OCAD University
 * Copyright 2014 Lucendo Development Ltd.
 *
 * Licensed under the New BSD license. You may not use this file except in
 * compliance with this License.
 *
 * The research leading to these results has received funding from the European Union's
 * Seventh Framework Programme (FP7/2007-2013)
 * under grant agreement no. 289016.
 *
 * You may obtain a copy of the License at
 * https://github.com/GPII/universal/blob/master/LICENSE.txt
 */

var fluid = require("universal");

fluid.module.register("gpii-android", __dirname, require);

// Native modules for settings handlers
//
fluid.require("activitymanager", require);
fluid.require("androidSettings", require);
fluid.require("audioManager", require);
fluid.require("persistentConfiguration", require);

// Native module for sensors
//
fluid.require("sensors", require);
