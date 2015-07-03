/*
 * GPII Android Personalization Framework - Intent handler
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

public abstract class AndroidIntentHandler extends Base {
	private static short classId = Env.getInterfaceId(AndroidIntentHandler.class);
	public AndroidIntentHandler() { super(classId); }

	//public abstract void startActivity(String action);
	public abstract void startActivity(String action, String data);
	public abstract void startActivityByPackageName(String packageName);
	public abstract void startMainLauncherActivity(String action, String comp);
	public abstract void stopActivityByPackageName(String packageName);

	public abstract void goToHomeScreen();
}
