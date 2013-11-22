package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidSettings extends Base {
	private static short classId = Env.getInterfaceId(AndroidSettings.class);
        public AndroidSettings() { super(classId); }
	
	public abstract String get(String settingType, String setting);
	public abstract Boolean set(String settingType, String setting, String value);

}
