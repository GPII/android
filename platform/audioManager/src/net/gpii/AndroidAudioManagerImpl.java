package net.gpii;

import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class AndroidAudioManagerImpl extends AndroidAudioManager implements IModule {
    private static final String TAG = "net.gpii.AndroidAudioManagerImpl";

    IModuleContext ctx;
    private Context androidContext;
    private AudioManager audioManager;

    private final Map<String, Integer> STREAM_ID =
        new HashMap<String, Integer>() {{
            put("STREAM_VOICE_CALL", AudioManager.STREAM_VOICE_CALL);
            put("STREAM_SYSTEM", AudioManager.STREAM_SYSTEM);
            put("STREAM_RING", AudioManager.STREAM_RING);
            put("STREAM_MUSIC", AudioManager.STREAM_MUSIC);
            put("STREAM_ALARM", AudioManager.STREAM_ALARM);
            put("STREAM_NOTIFICATION", AudioManager.STREAM_NOTIFICATION);
            put("STREAM_DTMF", AudioManager.STREAM_DTMF);
        }};

    @Override
    public Object startModule(IModuleContext ctx) {
        Log.v(TAG, "AndroidAudioManagerImpl.startModule");
        try {
            this.ctx = ctx;
            androidContext = ((AndroidContext) ctx).getAndroidContext();
            audioManager = (AudioManager) androidContext.getSystemService(Context.AUDIO_SERVICE);
        }
        catch (Exception e) {
            Log.v(TAG, "AndroidAudioManagerImpl error starting module: " + e);
        }
        return this;
    }

    @Override
    public void stopModule() {
        Log.v(TAG, "AndroidAudioManagerImpl.stopModule");
    }

    @Override
    public Integer getVolume(String setting) {
        Log.v(TAG, "AndroidAudioManagerImpl.get: " + setting);
        return audioManager.getStreamVolume(STREAM_ID.get(setting));
    }

    @Override
    public void setVolume(String setting, Integer value) {
        Log.v(TAG, "AndroidAudioManagerImpl.set: " + setting + " to " + value);
        audioManager.setStreamVolume(STREAM_ID.get(setting), value, 0);
    }
}
