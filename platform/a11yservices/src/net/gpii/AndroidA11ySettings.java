package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidA11ySettings extends Base {
	private static short classId = Env.getInterfaceId(AndroidA11ySettings.class);
	public AndroidA11ySettings() { super(classId); }
	
	public abstract void startTalkback();
	public abstract void stopTalkback();
}
