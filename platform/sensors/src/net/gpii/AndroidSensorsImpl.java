package net.gpii;

import net.gpii.SensorDict;
import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.meshpoint.anode.idl.Dictionary;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;


public class AndroidSensorsImpl extends AndroidSensors implements IModule {
    private static final String TAG = "net.gpii.AndroidSensorsImpl";

    IModuleContext ctx;
    private Context androidContext;
    private SensorManager sensorManager;
    private float currentLightValue;

    private final Map<String, Integer> SENSOR_TYPE =
        new HashMap<String, Integer>() {{
            put("TYPE_ACCELEROMETER", Sensor.TYPE_ACCELEROMETER);
            put("TYPE_ALL", Sensor.TYPE_ALL);
            put("TYPE_AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE);
            put("TYPE_GAME_ROTATION_VECTOR", Sensor.TYPE_GAME_ROTATION_VECTOR);
            // API Level 19
            //put("TYPE_GEOMAGNETIC_ROTATION_VECTOR", Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            put("TYPE_GRAVITY", Sensor.TYPE_GRAVITY);
            put("TYPE_GYROSCOPE", Sensor.TYPE_GYROSCOPE);
            put("TYPE_GYROSCOPE_UNCALIBRATED", Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
            // API Level 20
            //put("TYPE_HEART_RATE", Sensor.TYPE_HEART_RATE);
            put("TYPE_LIGHT", Sensor.TYPE_LIGHT);
            put("TYPE_LINEAR_ACCELERATION", Sensor.TYPE_LINEAR_ACCELERATION);
            put("TYPE_MAGNETIC_FIELD", Sensor.TYPE_MAGNETIC_FIELD);
            put("TYPE_MAGNETIC_FIELD_UNCALIBRATED", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
            put("TYPE_ORIENTATION", Sensor.TYPE_ORIENTATION);
            put("TYPE_PRESSURE", Sensor.TYPE_PRESSURE);
            put("TYPE_PROXIMITY", Sensor.TYPE_PROXIMITY);
            put("TYPE_RELATIVE_HUMIDITY", Sensor.TYPE_RELATIVE_HUMIDITY);
            put("TYPE_ROTATION_VECTOR", Sensor.TYPE_ROTATION_VECTOR);
            put("TYPE_SIGNIFICANT_MOTION", Sensor.TYPE_SIGNIFICANT_MOTION);
            // API Level 19
            //put("TYPE_STEP_COUNTER", Sensor.TYPE_STEP_COUNTER);
            //put("TYPE_STEP_DETECTOR", Sensor.TYPE_STEP_DETECTOR);
            put("TYPE_TEMPERATURE", Sensor.TYPE_TEMPERATURE);
        }};

    @Override
    public Object startModule(IModuleContext ctx) {
        Log.v(TAG, "AndroidSensorsImpl.startModule");
        try {
            this.ctx = ctx;
            androidContext = ((AndroidContext) ctx).getAndroidContext();
            sensorManager = (SensorManager) androidContext.getSystemService(Context.SENSOR_SERVICE);
        }
        catch (Exception e) {
            Log.v(TAG, "AndroidSensorsImpl error starting module: " + e);
        }
        return this;
    }

    @Override
    public void stopModule() {
        Log.v(TAG, "AndroidSensorsImpl.stopModule");
    }

    @Override
    public List<SensorDict> listSensors () {
        List<SensorDict> sensorsList = new ArrayList<SensorDict>();

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor: sensors) {
            SensorDict s = new SensorDict();

            s.name = sensor.getName();
            s.type = getSensorType(sensor.getType());
            s.maximumRange = sensor.getMaximumRange();
            s.minDelay = sensor.getMinDelay();
            // Commented because this was introduced in API level 21
            //s.maxDelay = sensor.getMaxDelay();
            s.resolution = sensor.getResolution();
            s.version = sensor.getVersion();
            s.vendor = sensor.getVendor();
            s.power = sensor.getPower();

            sensorsList.add(s);
        }

        return sensorsList;
    }

    SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("### ACCURACY CHANGED: ", String.valueOf(accuracy));
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                final float currentReading = event.values[0];
                Log.d("### VALUES: ", String.valueOf(currentReading));
                currentLightValue = currentReading;
            }
        }
    };

    @Override
    public void startLightSensor(String endPoint) {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(lightSensorEventListener,
                                       sensor,
                                       SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stopLightSensor() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.unregisterListener(lightSensorEventListener, sensor);
    }

    @Override
    public String getLightSensor () {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        JSONObject sensorJSON = new JSONObject();
        try {
            sensorJSON.put("name", sensor.getName());
            sensorJSON.put("type", getSensorType(sensor.getType()));
            sensorJSON.put("maximumRange", sensor.getMaximumRange());
            sensorJSON.put("minDelay", sensor.getMinDelay());
            sensorJSON.put("resolution", sensor.getResolution());
            sensorJSON.put("version", sensor.getVersion());
            sensorJSON.put("vendor", sensor.getVendor());
            sensorJSON.put("power", sensor.getPower());
            sensorJSON.put("currentValue", currentLightValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SensorDict s = new SensorDict();

        //s.name = sensor.getName();
        //s.type = getSensorType(sensor.getType());
        //s.maximumRange = sensor.getMaximumRange();
        //s.minDelay = sensor.getMinDelay();
        //// Commented because it's from API level 21
        ////s.maxDelay = sensor.getMaxDelay();
        //s.resolution = sensor.getResolution();
        //s.version = sensor.getVersion();
        //s.vendor = sensor.getVendor();
        //s.power = sensor.getPower();

        return sensorJSON.toString();
    }

    private String getSensorType (int value) {
        for (String key: SENSOR_TYPE.keySet()) {
            if (SENSOR_TYPE.get(key).equals(value)) {
                return key;
            }
        }
        return "TYPE_UNKNOWN";
    }
}
