package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidIntentHandler extends Base {
	private static short classId = Env.getInterfaceId(AndroidIntentHandler.class);
	public AndroidIntentHandler() { super(classId); }
	
	//public abstract void startActivity(String action);
	public abstract void startActivity(String action, String data);
	public abstract void startMainLauncherActivity(String action, String comp);
}
