package net.gpii;

import android.util.Log;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import android.app.ActivityManagerNative;
import android.os.RemoteException;
import android.content.res.Configuration;


public class AndroidFontSettingsImpl extends AndroidFontSettings implements IModule {
	private static final String TAG = "net.gpii.AndroidFontSettingsImpl";
	private final Configuration mFontDemoConfig = new Configuration();
	
	@Override
	public Object startModule(IModuleContext ctx) {
		Log.v(TAG, "AndroidFontSettingsImpl.startModule");
		return this;
	}

	@Override
	public void stopModule() {
		Log.v(TAG, "AndroidFontSettingsImpl.stopModule");		
	}

	@Override
	public void setFontSize(double size) {
		mFontDemoConfig.fontScale = (float) size;
		Log.v(TAG, "AndroidFontSettingsImpl.setFontSize: " + size);		
		try {
			ActivityManagerNative.getDefault().updatePersistentConfiguration(mFontDemoConfig);
		} catch (RemoteException e) {
			Log.e(TAG, "Unable to set fontScale with the ActivityManagerNative.", e);
		}
	}

	@Override
	public double getFontSize() {
		Log.v(TAG, "AndroidFontSettingsImpl.getFontSize");
		return 0;
	}

}
