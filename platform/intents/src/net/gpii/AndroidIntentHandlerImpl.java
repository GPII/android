package net.gpii;

import org.meshpoint.anode.AndroidContext;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;

public class AndroidIntentHandlerImpl extends AndroidIntentHandler implements IModule {
	private static final String TAG = "net.gpii.AndroidIntentHandlerImpl";
	IModuleContext ctx;
	private Context androidContext;
	
	@Override
	public Object startModule(IModuleContext ctx) {
		Log.v(TAG, "AndroidIntentHanlderImpl.startModule()");
		try {
		this.ctx = ctx;
		Log.v(TAG, "AndroidIntentHanlderImpl.startModule Made regular context");
		androidContext = ((AndroidContext) ctx).getAndroidContext();
		Log.v(TAG, "AndroidIntentHanlderImpl.startModule Got android context");
		} catch (Exception e) {
			Log.e(TAG, "AndroidIntentHanlderImpl error starting: " + e);
		}
		return this;
	}

	@Override
	public void stopModule() {
		Log.v(TAG, "AndroidIntentHanlderImpl.stopModule");
	}

	@Override
	public void startActivity(String action, String data) {
		Log.v(TAG, "AndroidIntentHanlderImpl.startActivity a: " + action + " d: " + data);		
		Intent intent = new Intent(action, Uri.parse(data));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		androidContext.getApplicationContext().startActivity(intent);
	}
	
	@Override
	public void startMainLauncherActivity(String action, String comp) {
		Intent intent = new Intent("android.intent.action.MAIN");
		//intent.setComponent(ComponentName.unflattenFromString("com.blogspot.tonyatkins.freespeech/com.blogspot.tonyatkins.freespeech.activity.StartupActivity"));
		intent.setComponent(ComponentName.unflattenFromString(comp));
		intent.addCategory("android.intent.category.LAUNCHER");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    androidContext.getApplicationContext().startActivity(intent);
	}

}
