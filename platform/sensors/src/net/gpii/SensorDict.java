package net.gpii;

import org.meshpoint.anode.idl.Dictionary;

public class SensorDict implements Dictionary {
    public String name;
    public String type; // This is a float const, but we will use a string
    public float maximumRange;
    public int minDelay;
    // Commented because it's from API level 21
    //public int maxDelay;
    public float resolution;
    public float version;
    public String vendor;
    public float power;
}
