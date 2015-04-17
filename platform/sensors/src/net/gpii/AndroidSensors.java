package net.gpii;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

import java.util.List;

public abstract class AndroidSensors extends Base {
    private static short classId = Env.getInterfaceId(AndroidSensors.class);
    public AndroidSensors() { super(classId); }

    // This is the way we tell the android environmental reporter where the
    // changes on the context should be reported.
    //
    public abstract String getEndPoint();
    public abstract void setEndPoint(String value);

    // Light sensor
    //
    public abstract String getLightSensor();
    public abstract void startLightSensor();
    public abstract void stopLightSensor();

    // Noise sensor
    //
    public abstract void startNoiseSensor();
    public abstract void stopNoiseSensor();
    public abstract double getAmplitudeEMA();
}
