/*
 * GPII Android Personalization Framework - Font handler
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

public abstract class AndroidFontSettings extends Base {
	private static short classId = Env.getInterfaceId(AndroidFontSettings.class);
	public AndroidFontSettings() { super(classId); }

	public abstract void setFontSize(double size);
	public abstract double getFontSize();
}
