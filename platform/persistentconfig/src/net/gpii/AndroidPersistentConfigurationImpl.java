/*
 * GPII Android Personalization Framework - Persistent configuration
 *
 * Licensed under the New BSD license. You may not use this file except in
 * compliance with this License.
 *
 * The research leading to these results has received funding from the European Union's
 * Seventh Framework Programme (FP7/2007-2013)
 * under grant agreement no. 289016.
 *
 * You may obtain a copy of the License at
 * https://github.com/GPII/universal/blob/master/LICENSE.txt
 */

package net.gpii;

import android.util.Log;
import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;

import android.os.RemoteException;
import android.content.Context;
import android.content.res.Configuration;
import java.lang.reflect.Method;
import java.util.Locale;

public class AndroidPersistentConfigurationImpl extends AndroidPersistentConfiguration implements IModule {
    private static final String TAG = "net.gpii.AndroidPersistentConfiguration";
    private Configuration mConfig;
    private Object nativeActivityManager;
    IModuleContext ctx;
    private Context androidContext;

    @Override
    public Object startModule(IModuleContext ctx) {
        Log.v(TAG, "AndroidPersistentConfiguration.startModule");

        try {
            this.ctx = ctx;
            androidContext = ((AndroidContext) ctx).getAndroidContext();
            mConfig = androidContext.getResources().getConfiguration();
        } catch (Exception e) {
            Log.v(TAG, "Issues fetching configuration instance from context", e);
        }

        try {
            nativeActivityManager = Class.forName("android.app.ActivityManagerNative")
                .getMethod("getDefault", new Class[0])
                .invoke(null, new Object[0]);
        }
        catch (Exception e) {
            Log.v(TAG, "Issues fetching nativeActivityManager class.", e);
        }
        return this;
    }

    private void updatePersistentConfiguration(Configuration config) {
        try {
            Method upc = nativeActivityManager.getClass()
                .getMethod("updatePersistentConfiguration", new Class[] { Configuration.class });
            upc.invoke(nativeActivityManager, config);
        }
        catch (Exception e) {
            Log.v(TAG, "Issues calling updatePersistentConfiguration", e);
        }
    }

    @Override
    public void stopModule() {
        Log.v(TAG, "AndroidPersistentConfigurationImpl.stopModule");
    }

    @Override
    public String get(String setting) {
        String value;

        if (setting.equals("fontScale")) {
            value = String.valueOf(mConfig.fontScale);
        } else if (setting.equals("locale")) {
            value = mConfig.locale.toString();
        } else {
            value = null;
        }

        return value;
    }

    @Override
    public Boolean set(String setting, String value) {

        if (setting.equals("fontScale")) {
            mConfig.fontScale = Float.parseFloat(value);
        } else if (setting.equals("locale")) {
            if (value.contains("_")) {
                String language [] = value.split("_");
                Locale locale = new Locale(language[0],
                                           language[1]);
                mConfig.locale = locale;
            } else {
                Locale locale = new Locale(value);
                mConfig.locale = locale;
            }
        } else {
            value = null;
        }

        updatePersistentConfiguration(mConfig);

        return true;
    }
}
