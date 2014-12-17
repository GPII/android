package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

import java.util.List;

public abstract class AndroidSensors extends Base {
    private static short classId = Env.getInterfaceId(AndroidSensors.class);
    public AndroidSensors() { super(classId); }

    public abstract String getLightSensor();
    public abstract void startLightSensor(String endPoint);
    public abstract void stopLightSensor();

    public abstract List<SensorDict> listSensors();
    //public abstract Integer getVolume(String setting);
    //public abstract void setVolume(String setting, Integer value);
}
