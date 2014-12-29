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
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpResponse;

import org.json.JSONObject;


public class AndroidSensorsImpl extends AndroidSensors implements IModule {
    private static final String TAG = "net.gpii.AndroidSensorsImpl";

    IModuleContext ctx;
    private Context androidContext;
    private SensorManager sensorManager;
    private float currentLightValue;

    // For the noise sensor
    //
    private MediaRecorder mRecorder;
    private Thread runner = null;

    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    final Runnable updater = new Runnable() {
        public void run() {
           updateNoiseValue();
        }
    };

    private Handler mHandler;

    // Common terms for environmental data
    //
    private static final String LUMINANCE = "http://registry.gpii.net/common/environment/visual.luminance";
    private static final String NOISE = "http://registry.gpii.net/common/environment/auditory.noise";

    // endPoint where sensors must report their values when they change
    //
    private String endPoint = "http://localhost:8081/environmentChanged";

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

    // TODO: Transform this into JSON
    //
    //@Override
    //public List<SensorDict> listSensors () {
    //    List<SensorDict> sensorsList = new ArrayList<SensorDict>();

    //    List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
    //    for (Sensor sensor: sensors) {
    //        SensorDict s = new SensorDict();

    //        s.name = sensor.getName();
    //        s.type = getSensorType(sensor.getType());
    //        s.maximumRange = sensor.getMaximumRange();
    //        s.minDelay = sensor.getMinDelay();
    //        // Commented because this was introduced in API level 21
    //        //s.maxDelay = sensor.getMaxDelay();
    //        s.resolution = sensor.getResolution();
    //        s.version = sensor.getVersion();
    //        s.vendor = sensor.getVendor();
    //        s.power = sensor.getPower();

    //        sensorsList.add(s);
    //    }

    //    return sensorsList;
    //}

    SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Log.d("### ACCURACY CHANGED: ", String.valueOf(accuracy));
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //Log.d("[node] ### VALUE CHANGED: ", String.valueOf(event.values[0]));
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                final float currentReading = event.values[0];
                //Log.d("### VALUES: ", String.valueOf(currentReading));
                currentLightValue = currentReading;

                JSONObject lightValue = new JSONObject();
                try {
                    lightValue.put(LUMINANCE, currentReading);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                reportChanges(lightValue);
            }
        }
    };

    @Override
    public void startLightSensor() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(lightSensorEventListener,
                                       sensor,
                                       99999999);
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

    // This function is where we make a PUT reuest to the flow manager
    // telling the new value for a concrete sensor
    //
    public void reportChanges (JSONObject change) {
        Log.d(TAG, "[node] on reportChanges");

        HttpClient client = new DefaultHttpClient();
        HttpPut put = new HttpPut(endPoint.toString());
        StringEntity se = null;

        try {
            se = new StringEntity(change.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        };

        se.setContentType("application/json;charset=UTF-8");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
        put.setEntity(se);

        // TODO: Improve the handling of exceptions
        //
        try {
            HttpResponse response = client.execute(put);
            Log.d(TAG, "[node] after client.execute");
        } catch (ConnectException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEndPoint(String value) {
        endPoint = value;
    }

    @Override
    public String getEndPoint() {
        return endPoint;
    }

    @Override
    public void startNoiseSensor() {
        Log.d(TAG, "[node] Starting noise sensor ....");
        // This approach didn't work, so we're scheduling reads from the
        // javascript side of the code by using node's setInterval
        //
        //mHandler = new Handler();
        //if (runner == null) {
            // Not working - is AsyncTask the solution?
            //runner = new Thread() {
            //    public void run() {
            //        Log.d("NOISE", "[node] RUNNING the Thread");
            //        while (runner != null) {
            //            try {
            //                Thread.sleep(500);
            //                Log.d("Noise", "Tick");
            //            } catch (InterruptedException e) {
            //                Log.e("Noise", "" + e.getMessage());
            //            }
            //            mHandler.post(updater);
            //        }
            //    }
            //};
            //runner.start();
            //Log.d("Noise", "start runner()");
        //}
        startRecorder();
    }

    @Override
    public void stopNoiseSensor() {
        stopRecorder();
    }

    public void startRecorder() {
        Log.d(TAG, "[node] on startRecorder");
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
        }
    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateNoiseValue() {
        DecimalFormat df = new DecimalFormat("#.##");
        float currentReading = Float.parseFloat(df.format(getAmplitudeEMA()));

        JSONObject noiseValue = new JSONObject();
        try {
            noiseValue.put(NOISE, currentReading);
        } catch (Exception e) {
            e.printStackTrace();
        }

        reportChanges(noiseValue);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;
    }

    @Override
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

}
