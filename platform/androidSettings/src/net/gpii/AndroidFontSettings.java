package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidFontSettings extends Base {
	private static short classId = Env.getInterfaceId(AndroidFontSettings.class);
	public AndroidFontSettings() { super(classId); }
	
	public abstract void setFontSize(double size);
	public abstract double getFontSize();
}
