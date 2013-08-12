package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidDeviceReporter extends Base {
	private static short classId = Env.getInterfaceId(AndroidDeviceReporter.class
);
	public AndroidDeviceReporter() { super(classId); }
        public abstract String getJSONOutput();
}
