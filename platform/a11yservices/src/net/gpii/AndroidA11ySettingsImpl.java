/*
 * GPII Android Personalization Framework - A11y Settings handler
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

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;

public class AndroidA11ySettingsImpl extends AndroidA11ySettings implements IModule {
	private static final String TAG = "net.gpii.AndroidA11ySettingsImpl";
	private static final String TALKBACK_SETTING_NAME = "com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService";

	IModuleContext ctx;
	private Context androidContext;

	@Override
	public Object startModule(IModuleContext ctx) {
		Log.v(TAG, "AndroidA11ySettingsImpl.startModule()");
		this.ctx = ctx;
		this.androidContext = ((AndroidContext) ctx).getAndroidContext();
		return this;
	}

	@Override
	public void stopModule() {
		Log.v(TAG, "AndroidA11ySettingsImpl.stopModule()");
	}

	@Override
	public void startTalkback() {
		Log.v(TAG, "AndroidA11ySettingsImpl.startTalkback()");
		ContentResolver cr = androidContext.getContentResolver();
		String enableSer=Settings.Secure.getString(cr,Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
		Log.v(TAG, "What services are enabled? : " + enableSer);
		Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1); // Enable accessibility
        Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, TALKBACK_SETTING_NAME);
        Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1); //Enable accessibility
        Log.v(TAG, "Hopefully just enabled the accessibility services!");
	}

	@Override
	public void stopTalkback() {
		Log.v(TAG, "AndroidA11ySettingsImpl.stopTalkback()");
		ContentResolver cr = androidContext.getContentResolver();
		Settings.Secure.putInt(cr,Settings.Secure.ACCESSIBILITY_ENABLED, 0); //Disable Accessibility
        Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "");
        Log.v(TAG, "I think I just turned talkback off!");
	}

}
