package net.gpii;

/*

DeviceReporterEngine
This class explores hardware and software on the device to make the Device reporter results list.	

	Copyright (c) 2013, Technosite R&D
	All rights reserved.
The research leading to these results has received funding from the European Union's Seventh Framework Programme (FP7/2007-2013) under grant agreement n° 289016

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, 
   this list of conditions and the following disclaimer in the documentation 
   and/or other materials provided with the distribution. 
 * Neither the name of Technosite R&D nor the names of its contributors may 
   be used to endorse or promote products derived from this software without 
   specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.json.JSONObject;

public class AndroidDeviceReporterImpl extends AndroidDeviceReporter implements IModule {
	private static final String TAG = "net.gpii.AndroidDeviceReporterImpl";

	IModuleContext ctx;
	private Context androidContext;

	private HashMap<String,String>results = new HashMap<String,String>();

	public Map<String,String> getResults() {
		return (Map<String,String>) this.results;
	}

	@Override
	public Object startModule(IModuleContext ctx) {
		Log.v(TAG, "AndroidDeviceReporterImpl.startModule()");
		this.ctx = ctx;
		this.androidContext = ((AndroidContext) ctx).getAndroidContext();

		return this;
	}

	@Override
	public void stopModule() {
		Log.v(TAG, "AndroidDeviceReporterImpl.stopModule()");
	}

	@Override
        public String getJSONOutput() {

                getDataAboutDevice();
		getDataAboutOperatingSystem(androidContext);
		getDataAboutHardware(androidContext);
        	getDataAboutSensors(androidContext);
        	//getDataAboutSound(androidContext);
        	//getDataAboutScreen(androidContext);

		JSONObject output = new JSONObject(results);

                return output.toString();
        }

	// *** Device reporter getters

	// ** Software

	private void getDataAboutDevice() {
		results.put("Device" , android.os.Build.DEVICE);
		results.put("Model" , android.os.Build.MODEL);
		results.put("Manufacturer" , android.os.Build.MANUFACTURER);
		results.put("Product" , android.os.Build.PRODUCT);
		results.put("Brand" , android.os.Build.BRAND);
		results.put("CPU" , android.os.Build.CPU_ABI);
		results.put("CPU2" , android.os.Build.CPU_ABI2);
		if (isDeviceRooted()) {
			results.put("rootAccess", "yes");
		} else {
			results.put("rootAccess", "no");
		}
	}

	private void getDataAboutOperatingSystem(Context ct) {
		results.put("OS Version", "Android " + android.os.Build.VERSION.RELEASE );
		results.put("Build name" , android.os.Build.FINGERPRINT);
		results.put("Kernel Version", System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")");
		results.put("OS API Level" , String.valueOf(android.os.Build.VERSION.SDK_INT));

		// Services
		PackageManager packageManager = ct.getPackageManager();
		List<ApplicationInfo> applicationList = packageManager.getInstalledApplications (0);
		if (applicationList.size()>0)
			for (int i=0; i<applicationList.size();i++) {
				ApplicationInfo tmpApp = applicationList.get(i);
				results.put("Application " + (String) packageManager.getApplicationLabel(tmpApp), tmpApp.toString());
			}
		List<PackageInfo> packageList = packageManager.getInstalledPackages (PackageManager.GET_SERVICES);
		for (int i=0; i<packageList.size();i++) {
			PackageInfo tmpServ = packageList.get(i);
			results.put("Service " +tmpServ.packageName , tmpServ.toString());
		}
	}

	// ** Hardware

	@SuppressLint("NewApi")
	private void getDataAboutHardware(Context ct) {
		// External memory
		String estado = Environment.getExternalStorageState();   
		if (estado.equals(Environment.MEDIA_MOUNTED)) {     
			results.put("External storage", "Write and read"); 
		} else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {     
			results.put("External storage", "Read only"); 
		} else{     
			results.put("External storage", "Not available"); 
		}
		results.put("External path", String.valueOf(Environment.getExternalStorageDirectory()));

		// USB
		try {
			UsbManager usb= (UsbManager)ct.getSystemService(Context.USB_SERVICE); 
			if (usb != null) {
				results.put("USB", "Supported");
				if (Build.VERSION.SDK_INT>=12) {
					HashMap<String, UsbDevice> deviceList = usb.getDeviceList();
					Iterator<Map.Entry<String,UsbDevice>> it = deviceList.entrySet().iterator();
					int c = 1;
					if (it.hasNext()) {
						while (it.hasNext()) {
							Map.Entry<String, UsbDevice> e = (Map.Entry<String, UsbDevice>) it.next();
							results.put("USB attached device " + String.valueOf(c), e.getKey());
							c++;
						}
					} else results.put("USB attached device", "None");
				}
			} else results.put("USB", "Not supported");
		} catch (Exception e) {
			results.put("USB", "Not supported\nError:" + e.toString());			
		}

		// Keyboard
		results.put("System board" , android.os.Build.BOARD);
		boolean keyboardPresent = (ct.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);
		results.put("Keyboard hardware" , String.valueOf(keyboardPresent));

		switch (ct.getResources().getConfiguration().keyboard ) {
		case Configuration.KEYBOARD_12KEY : 
			results.put("Keyboard type", "Numeric");
			break;
		case Configuration.KEYBOARD_QWERTY : 
			results.put("Keyboard type", "Qwerty");
			break;
		case Configuration.KEYBOARD_NOKEYS : 
			results.put("Keyboard type", "No keys");
			break;
		default :
			results.put("Keyboard type", "UNKNOWN");
			break;
		}

		// Navigation mode
		switch (ct.getResources().getConfiguration().navigation) {
		case Configuration.NAVIGATION_TRACKBALL :
			results.put("Navigator mode", "Trackball");
			break;
		case Configuration.NAVIGATION_WHEEL :
			results.put("Navigator mode", "Wheel");
			break;
		case Configuration.NAVIGATION_DPAD :
			results.put("Navigator mode", "DPad");
			break;
		case Configuration.NAVIGATION_NONAV :
			results.put("Navigator mode", "None");
			break;
		case Configuration.NAVIGATION_UNDEFINED :
			results.put("Navigator mode", "Unknown");
			break;
		}

		// Battery
		try
		{
			IntentFilter batIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent battery =  ct.registerReceiver(null, batIntentFilter);
			int batteryLevel = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int batteryStatus = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			int chargePlug = battery.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			boolean isCharging = (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING || batteryStatus == BatteryManager.BATTERY_STATUS_FULL);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			results.put("Battery level", String.valueOf(batteryLevel));
			results.put("Battery charging", String.valueOf(isCharging));
			if (isCharging) {
				if (usbCharge ) results.put("Battery charging mode", "USB");
				else if (acCharge) results.put("Battery charging mode", "AC	");
				else results.put("Battery charging mode", "UNKNOWN");
			}
		}
		catch (Exception e)
		{
			results.put("Battery level", "UNKNOWN");
		}
	}

	@SuppressLint("NewApi")
	private void getDataAboutSensors(Context ct) {
		// Wifi
		try {
			WifiManager wifi = (WifiManager) ct.getSystemService(Context.WIFI_SERVICE);
			if (wifi!=null) {
				results.put("Wifi", "Supported");
				if (wifi.isWifiEnabled()) {
					results.put("Wifi state", "Enabled");
					WifiInfo info = wifi.getConnectionInfo();
					results.put("Wifi SSID", info.getSSID());
					results.put("Wifi BSSID", info.getBSSID());
					results.put("Wifi Mac address", info.getMacAddress());
					results.put("Wifi network Id", String.valueOf(info.getNetworkId() )); 
					results.put("Wifi IP address", String.valueOf(info.getIpAddress() ));
					results.put("Wifi link speed", String.valueOf(info.getLinkSpeed() ));
					results.put("Wifi Rssi", String.valueOf(info.getRssi() ));
				} else results.put("Wifi state", "Disabled");
			} else results.put("Wifi", "Not supported");
		} catch (Exception e) {
			results.put("Wifi", "Not supported\nError:" + e.toString());
		}

		// Bluetooth
		try {
			BluetoothAdapter bluetoothLocalAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothLocalAdapter!= null) {
				results.put("Bluetooth", "Supported");
				results.put("Bluetooth address", bluetoothLocalAdapter.getAddress());
				results.put("Bluetooth name", bluetoothLocalAdapter.getName());
				switch (bluetoothLocalAdapter.getState()) {
				case android.bluetooth.BluetoothAdapter.STATE_OFF :
					results.put("Bluetooth state", "Off");
					break;
				case android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF :
					results.put("Bluetooth state", "Turning off");
					break;
				case android.bluetooth.BluetoothAdapter.STATE_ON :
					results.put("Bluetooth state", "On");
					break;
				case android.bluetooth.BluetoothAdapter.STATE_TURNING_ON :
					results.put("Bluetooth state", "Turning on");
					break;
				case android.bluetooth.BluetoothAdapter.STATE_CONNECTED :
					results.put("Bluetooth state", "Connected");
					break;
				case android.bluetooth.BluetoothAdapter.STATE_CONNECTING :
					results.put("Bluetooth state", "Connecting");	
					break;
				case android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED :
					results.put("Bluetooth state", "Disconnected");
					break;
				case android.bluetooth.BluetoothAdapter.STATE_DISCONNECTING :
					results.put("Bluetooth state", "Disconnecting");
					break;
				}
			} else {
				results.put("Bluetooth", "Not supported");
			}
		} catch (Exception e) {
			results.put("Bluetooth", "Not supported\nError:" + e.toString());
		}

		// NFC
		NfcManager nfcManager = (NfcManager) ct.getSystemService(Context.NFC_SERVICE);
		NfcAdapter nfc = nfcManager.getDefaultAdapter();
		if (nfc != null) {
			results.put("NFC", "Supported");
			results.put("NFC enabled", String.valueOf(nfc.isEnabled() ));
			if (Build.VERSION.SDK_INT>=16) results.put("NFC NDefPush enabled", String.valueOf(nfc.isNdefPushEnabled() ));
		} else results.put("NFC", "Not supported"); 

		// Camera
		int numCameras = android.hardware.Camera.getNumberOfCameras(); 		
		if (numCameras > 0 ) {
			results.put("Cameras", String.valueOf(numCameras));
			for (int i=0;i<numCameras;i++) {
				android.hardware.Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(i,info);
				if (info.facing == CameraInfo.CAMERA_FACING_BACK) results.put("Camera " + String.valueOf(i) + " face", "Back");
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) results.put("Camera " + String.valueOf(i) + " face", "Front");
				results.put("Camera " + String.valueOf(i) + " angle orientation", String.valueOf(info.orientation));
				try {
					Camera tmpCamera = Camera.open(i);
					results.put("Camera " + String.valueOf(i) + " available", "true");
					Camera.Parameters tmpParam = tmpCamera.getParameters();
					results.put("Camera " + String.valueOf(i) + " flash mode", tmpParam.getFlashMode());
					results.put("Camera " + String.valueOf(i) + " focal length", String.valueOf(tmpParam.getFocalLength()) + " mm");
					results.put("Camera " + String.valueOf(i) + " focus mode", tmpParam.getFocusMode());
					results.put("Camera " + String.valueOf(i) + " zoom supported", String.valueOf(tmpParam.isZoomSupported()));
					results.put("Camera " + String.valueOf(i) + " video stabilization supported", String.valueOf(tmpParam.isVideoStabilizationSupported()));
					results.put("Camera " + String.valueOf(i) + " video snapshot supported", String.valueOf(tmpParam.isVideoSnapshotSupported()));
					results.put("Camera " + String.valueOf(i) + " smooth zoom supported", String.valueOf(tmpParam.isSmoothZoomSupported()));
					results.put("Camera " + String.valueOf(i) + " auto white balance supported", String.valueOf(tmpParam.isAutoWhiteBalanceLockSupported()));
					results.put("Camera " + String.valueOf(i) + " auto exposure lock supported", String.valueOf(tmpParam.isAutoExposureLockSupported()));
					Camera.Size picturesize = tmpParam.getPictureSize();
					results.put("Camera " + String.valueOf(i) + " picture width", String.valueOf(picturesize.width));
					results.put("Camera " + String.valueOf(i) + " picture height", String.valueOf(picturesize.height));
					picturesize = tmpParam.getPreviewSize();
					results.put("Camera " + String.valueOf(i) + " preview picture width", String.valueOf(picturesize.width));
					results.put("Camera " + String.valueOf(i) + " preview picture height", String.valueOf(picturesize.height));
					picturesize = tmpParam.getPreferredPreviewSizeForVideo (); 
					results.put("Camera " + String.valueOf(i) + " preview video width", String.valueOf(picturesize.width));
					results.put("Camera " + String.valueOf(i) + " preview video height", String.valueOf(picturesize.height));
					tmpCamera.release();
				} catch (Exception e) {
					results.put("Camera " + String.valueOf(i) + " available", "false");
				}
			}
		} else results.put("Cameras", "0");
		// Sensors
		try {
			SensorManager sensorManager = (SensorManager) ct.getSystemService(Context.SENSOR_SERVICE);
			if (sensorManager != null) {
				List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
				for (int i=0;i<sensorList.size();i++) {
					Sensor tmpSensor = (Sensor) sensorList.get(i);
					results.put("Sensor " + String.valueOf(i+1), tmpSensor.getName());
					results.put("Sensor " + String.valueOf(i+1) + " description", tmpSensor.toString());
				}
			} else results.put("Sensors", "Not supported");
		} catch (Exception e) {
			results.put("Sensors", "Unknown");
		}

		// Telephony
		try {
			TelephonyManager telephonyManager = (TelephonyManager) ct.getSystemService(Context.TELEPHONY_SERVICE);

			results.put("Phone" , "Supported");
			results.put("Phone roaming" , String.valueOf(telephonyManager.isNetworkRoaming()));
			switch (telephonyManager.getPhoneType()) {
			case TelephonyManager.PHONE_TYPE_CDMA :
				results.put("Phone type", "CDMA");
				break;
			case TelephonyManager.PHONE_TYPE_GSM :
				results.put("Phone type", "GSM");
				break;
			case TelephonyManager.PHONE_TYPE_NONE :
				results.put("Phone type", "None");
				break;
			case TelephonyManager.PHONE_TYPE_SIP :
				results.put("Phone type", "SIP");
				break;
			}
			int networktype = telephonyManager.getNetworkType(); 
			switch (networktype ) {
			case TelephonyManager.NETWORK_TYPE_UNKNOWN :
				results.put("Phone network type", "Unknown");
				break;
			case TelephonyManager.NETWORK_TYPE_1xRTT :
				results.put("Phone network type", "1XRTT");
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA :
				results.put("Phone network type", "CDMA");
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE :
				results.put("Phone network type", "EDGE");
				break;
			case TelephonyManager.NETWORK_TYPE_EHRPD :
				results.put("Phone network type", "EHrpD");
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0 :
				results.put("Phone network type", "EVDO_0");
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A :
				results.put("Phone network type", "EVDO_A");
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_B :
				results.put("Phone network type", "EVDO_B");
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS :
				results.put("Phone network type", "GPRS");
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA :
				results.put("Phone network type", "HSDPA");
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA :
				results.put("Phone network type", "HSPA");
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP :
				results.put("Phone network type", "HSPAP");
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA :
				results.put("Phone network type", "HSUPA");
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN :
				results.put("Phone network type", "IDEN");
				break;
			case TelephonyManager.NETWORK_TYPE_LTE :
				results.put("Phone network type", "LTE");
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS :
				results.put("Phone network type", "UMTS");
				break;
			}
			switch (telephonyManager.getSimState()) {
			case TelephonyManager.SIM_STATE_ABSENT :
				results.put("Phone SIM state", "ABSENT");
				break;
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
				results.put("Phone SIM state", "Network locked");
				break;
			case TelephonyManager.SIM_STATE_PIN_REQUIRED :
				results.put("Phone SIM state", "PIN required");
				break;
			case TelephonyManager.SIM_STATE_PUK_REQUIRED :
				results.put("Phone SIM state", "PUK required");
				break;
			case TelephonyManager.SIM_STATE_READY :
				results.put("Phone SIM state", "Ready");
				break;
			case TelephonyManager.SIM_STATE_UNKNOWN :
				results.put("Phone SIM state", "Unknown");
				break;
			}
		} catch (Exception e) {
			results.put("Phone" , "Not supported");
		}
	}

	@SuppressWarnings("deprecation")
	private void getDataAboutSound(Context ct) {
		// TTS
		try {
			TextToSpeech mTts = new TextToSpeech(ct, null);
			if (mTts != null) {
				results.put("TTS supported", "Yes");
				results.put("TTS defaults enforced", String.valueOf(mTts.areDefaultsEnforced()));
				results.put("TTS default engine", mTts.getDefaultEngine());
				Locale tmpLocale = mTts.getLanguage();
				results.put("TTS default language", tmpLocale.getLanguage());
				results.put("TTS default country", tmpLocale.getCountry());
				if (Build.VERSION.SDK_INT>=14) {

					mTts.shutdown();
				}
			} 
		} catch (Exception e) {
			results.put("TTS supported", "No:\n Error:" + e.toString());
		}


		// Sound device
		AudioManager audioManager = (AudioManager) ct.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager != null) {
			results.put("Sound system volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)));
			results.put("Sound music volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));
			results.put("Sound alarm volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM)));
			results.put("Sound ring volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_RING)));
			results.put("Sound voice call volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL	)));
			results.put("Sound notification volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)));
			results.put("Sound DTMF volume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_DTMF)));
			results.put("Max sound system volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)));
			results.put("Max sound music volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
			results.put("Max sound alarm volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)));
			results.put("Max sound ring volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)));
			results.put("Max sound voice call volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL	)));
			results.put("Max sound notification volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)));
			results.put("Max sound DTMF volume", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF)));

			int audioStatus = audioManager.getMode();
			switch (audioStatus) {
			case AudioManager.MODE_NORMAL :
				results.put("Audio mode", "Normal");
				break;
			case AudioManager.MODE_RINGTONE :
				results.put("Audio mode", "Ringtone");
				break;
			case AudioManager.MODE_IN_CALL :
				results.put("Audio mode", "In call");
				break;
			case AudioManager.MODE_IN_COMMUNICATION :
				results.put("Audio mode", "In communication");
				break;
			case AudioManager.MODE_INVALID :
				results.put("Audio mode", "Invalid");
				break;
			}
			// Sound flags
			results.put("Wired eadsets", String.valueOf(audioManager.isWiredHeadsetOn()));
			results.put("Bluetooth Sco", String.valueOf(audioManager.isBluetoothScoOn()));
			results.put("Bluetooth Sco available off call", String.valueOf(audioManager.isBluetoothScoAvailableOffCall()));
			results.put("Microphone muted", String.valueOf(audioManager.isMicrophoneMute()));
			results.put("Music active", String.valueOf(audioManager.isMusicActive()));
			results.put("Speaker phone", String.valueOf(audioManager.isSpeakerphoneOn()));
			results.put("Bluetooth A2dp speaker", String.valueOf(audioManager.isBluetoothA2dpOn()));
		} else { // Errors getting info about Sound device
			results.put("Sound volume", "UNKNOWN");
		}
	}

	@SuppressLint("NewApi") 
	private void getDataAboutScreen(Context ct) {
		WindowManager wm = (WindowManager) ct.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		if (Build.VERSION.SDK_INT<=16){ // Android 4.0 and lowers
			if (Build.VERSION.SDK_INT>13){
				Point size = new Point();
				display.getSize(size);
				results.put("Screen width", String.valueOf(size.x));
				results.put("Screen height", String.valueOf(size.y));
			} else {
				try {
					Method mGetRawH = Display.class.getMethod("getRawHeight");
					Method mGetRawW = Display.class.getMethod("getRawWidth");
					results.put("Screen width",String.valueOf((Integer) mGetRawW.invoke(display)));
					results.put("Screen height", String.valueOf((Integer) mGetRawH.invoke(display)));
				} catch (Exception e) {
					Log.d("Error", "\n Error accessing to video data.");
					results.put("Screen width", "UNKNOWN");
					results.put("Screen height", "UNKNOWN");
				}
			}
		} else { // Android 4.1 or highers
			DisplayMetrics displayMetrics = new DisplayMetrics();
			display.getRealMetrics(displayMetrics);
			results.put("Screen width", String.valueOf(displayMetrics.widthPixels));
			results.put("Screen height", String.valueOf(displayMetrics.heightPixels));
		}
		results.put("Density DPI" , String.valueOf(ct.getResources().getConfiguration().densityDpi));
		results.put("Font scale" , String.valueOf(ct.getResources().getConfiguration().fontScale));
		//Screen orientation))
		switch (ct.getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE :
			results.put("Screen orientation", "Landscape");
			break;
		case Configuration.ORIENTATION_PORTRAIT :
			results.put("Screen orientation", "Portrait");
			break;
		}
		// Type of Touch screen
		results.put("Touch screen", String.valueOf((ct.getResources().getConfiguration().touchscreen != Configuration.
				TOUCHSCREEN_NOTOUCH)));
	}

	
	// CheckForRoot management
	
	private static String LOG_TAG = AndroidDeviceReporterImpl.class.getName();

	public static enum SHELL_CMD {
		check_su_binary(new String[] { "su", "-c", "ls /" }), ;

		String[] command;

		SHELL_CMD(String[] command) {
			this.command = command;
		}
	}

	/*
	 * Three method to check the root permissions
	 */
	public boolean isDeviceRooted() {
		if (checkRootMethod1()) {
			Log.d(LOG_TAG, "method 1: true");
			return true;
		}
		if (checkRootMethod2()) {
			Log.d(LOG_TAG, "method 2: true");
			return true;
		}
		if (checkRootMethod3()) {
			Log.d(LOG_TAG, "method 3: true");
			return true;
		}
		return false;
	}

	/*
	 * Method 1, check the SO builds Tags, Usually don't response with the
	 * correct answer
	 */
	public boolean checkRootMethod1() {
		String buildTags = android.os.Build.TAGS;

		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}
		return false;
	}

	/*
	 * Method 2, If exists Superuser.apk, you can access as SuperUser. Usually,
	 * if the device is root, this method return true.
	 */
	public boolean checkRootMethod2() {
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	/*
	 * Method 2, check thought the shell. If you can exec su, the device is
	 * root.
	 */
	public boolean checkRootMethod3() {
		if (executeCommand(SHELL_CMD.check_su_binary) != null) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
		String line = null;
		ArrayList<String> fullResponse = new ArrayList<String>();
		Process localProcess = null;

		try {
			localProcess = Runtime.getRuntime().exec(shellCmd.command);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				localProcess.getOutputStream()));
		BufferedReader in = new BufferedReader(new InputStreamReader(
				localProcess.getInputStream()));

		try {
			while ((line = in.readLine()) != null) {
				Log.d(LOG_TAG, "--> Line received: " + line);
				fullResponse.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "--> Full response was: " + fullResponse);

		return fullResponse;
	}

	
	

}
