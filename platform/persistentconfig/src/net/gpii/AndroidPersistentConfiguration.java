/*
 * GPII Android Personalization Framework - Persistent configuration
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

package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidPersistentConfiguration extends Base {
    private static short classId = Env.getInterfaceId(AndroidPersistentConfiguration.class);
    public AndroidPersistentConfiguration() { super(classId); }

    public abstract String get(String setting);
    public abstract Boolean set(String setting, String value);
}
