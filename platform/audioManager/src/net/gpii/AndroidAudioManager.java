package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidAudioManager extends Base {
	private static short classId = Env.getInterfaceId(AndroidAudioManager.class);
        public AndroidAudioManager() { super(classId); }
	
	public abstract Integer getVolume(String setting);
	public abstract void setVolume(String setting, Integer value);
}
