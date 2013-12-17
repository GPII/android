package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

public abstract class AndroidPersistentConfiguration extends Base {
    private static short classId = Env.getInterfaceId(AndroidPersistentConfiguration.class);
    public AndroidPersistentConfiguration() { super(classId); }

    public abstract String get(String setting);
    public abstract Boolean set(String setting, String value);
}
