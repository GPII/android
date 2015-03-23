/*
 * GPII Android Personalization Framework - Seettings Handler
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

import org.meshpoint.anode.AndroidContext;

import android.util.Log;
import android.content.Context;
import android.provider.Settings;

import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;

public class AndroidSettingsImpl extends AndroidSettings implements IModule {
    private static final String TAG = "net.gpii.AndroidSettingsImpl";

    IModuleContext ctx;
    private Context androidContext;

    @Override
    public Object startModule(IModuleContext ctx) {
        Log.v(TAG, "AndroidSettingsImpl.startModule");
        try {
            this.ctx = ctx;
            androidContext = ((AndroidContext) ctx).getAndroidContext();
        }
        catch (Exception e) {
            Log.v(TAG, "AndroidSettingsImpl error starting module: " + e);
        }
        return this;
    }

    @Override
    public void stopModule() {
        Log.v(TAG, "AndroidSettingsImpl.stopModule");
    }

    @Override
    public String get(String settingType, String setting) {
        Log.v(TAG, "AndroidSettingsImpl.get: " + setting);

        String value = null;
        if (settingType.contains("Secure")) {
            value = Settings.Secure.getString(androidContext.getContentResolver(), setting);
        } else if (settingType.contains("System")) {
            value = Settings.System.getString(androidContext.getContentResolver(), setting);
        } else if (settingType.contains("Global")) {
            value = Settings.Global.getString(androidContext.getContentResolver(), setting);
        } else {
            Log.e(TAG, "We haven't implemented this type yet: " + settingType);
        }

        return value;
    }

    @Override
    public Boolean set(String settingType, String setting, String value) {
        Log.v(TAG, "AndroidSettingsImpl.set: " + setting + " to " + value);

        Boolean result = false;
        if (settingType.contains("Secure")) {
            result = Settings.Secure.putString(androidContext.getContentResolver(), setting, value);
        } else if (settingType.contains("System")) {
            result = Settings.System.putString(androidContext.getContentResolver(), setting, value);
        } else if (settingType.contains("Global")) {
            result = Settings.Global.putString(androidContext.getContentResolver(), setting, value);
        } else {
            Log.e(TAG, "We haven't implemented this type yet: " + settingType);
        }

        return result;
    }
}
