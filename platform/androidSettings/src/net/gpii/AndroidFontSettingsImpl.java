/*
 * GPII Android Personalization Framework - Font handler
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
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
//import android.app.ActivityManagerNative;
import android.os.RemoteException;
import android.content.res.Configuration;
import java.lang.reflect.Method;

public class AndroidFontSettingsImpl extends AndroidFontSettings implements IModule {
    private static final String TAG = "net.gpii.AndroidFontSettingsImpl";
    private final Configuration mFontDemoConfig = new Configuration();
    private Object nativeActivityManager;

    @Override
    public Object startModule(IModuleContext ctx) {
        Log.v(TAG, "AndroidFontSettingsImpl.startModule");
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
        Log.v(TAG, "AndroidFontSettingsImpl.stopModule");
    }

    @Override
    public void setFontSize(double size) {
        mFontDemoConfig.fontScale = (float) size;
        Log.v(TAG, "AndroidFontSettingsImpl.setFontSize: " + size);
        updatePersistentConfiguration(mFontDemoConfig);
        // try {
        // 	ActivityManagerNative.getDefault().updatePersistentConfiguration(mFontDemoConfig);
        // } catch (RemoteException e) {
        // 	Log.e(TAG, "Unable to set fontScale with the ActivityManagerNative.", e);
        // }
    }

    @Override
    public double getFontSize() {
        Log.v(TAG, "AndroidFontSettingsImpl.getFontSize");
        return 0;
    }

}
