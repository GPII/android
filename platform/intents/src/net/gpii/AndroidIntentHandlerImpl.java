package net.gpii;

import org.meshpoint.anode.AndroidContext;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;
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

        @Override
        public void startActivityByPackageName(String packageName)
        {
            Log.v(TAG, "AndroidIntentHanlderImpl.startActivityByPackageName: " + packageName);

            Intent intent = androidContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null)
            {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                androidContext.getApplicationContext().startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id="+packageName));
                androidContext.getApplicationContext().startActivity(intent);
            }
        }

        @Override
        public void stopActivityByPackageName(String packageName)
        {
            Log.v(TAG, "AndroidIntentHanlderImpl.stopActivityByPackageName: " + packageName);
            ActivityManager manager = (ActivityManager)androidContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> services = manager.getRunningAppProcesses();
            RunningAppProcessInfo process = null;

            for (int i = 0; i < services.size(); i++) {
                process = services.get(i);
                String name = process.processName;
                if (name.equals(packageName)) {
                    break;
                }
            }

            Process.killProcess(process.pid);
        }

        @Override
        public void goToHomeScreen()
        {
            Log.v(TAG, "AndroidIntentHanlderImpl.goToHomeScreen");

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            androidContext.getApplicationContext().startActivity(intent);
        }
}
