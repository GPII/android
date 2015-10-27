/*
* GPII Android Personalization Framework
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
package net.gpii.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class GpiiActivity extends Activity {

    private boolean isSystemApp;
    private static String TAG = "Cloud4all";
    private static String filepathgpii = Environment
        .getExternalStorageDirectory() + "/";

    private boolean higherVersionKitKat = false;

    private static String uriTar;

    private static String gpiiCompatibleAndroidDevicesUrl;
    
    private static String gpiiRootDevicesUrl;

    private static String gpiiJS;
    private static String gpiiAPK;

    private static String privSystemDir = "/system/priv-app";
    private static String systemDir = "/system/app";

    private View progressView;

    private WindowManager.LayoutParams progressparams;

    private WindowManager wm;

    public static final int GPII_STATE_RUNNING = 0;
    public static final int GPII_STATE_NOT_RUNNING = 1;
    private static final String ACTION_GPII_UNZIP_COMPLETE = "net.gpii.app.ACTION_GPII_UNZIP_COMPLETE";

    private TextView gpiiStatus;
    private EditText gpiiScriptUri;
    private Button gpiiStartButton;
    private Button gpiiStopButton;
    private Button gpiiKillButton;
    private Button gpiiUpdateStatusButton;
    private Button installationButton;
    private Button downloadButton;
    private RelativeLayout gpiiInfo;
    private File file;

    private long enqueue;
    private DownloadManager dm;
    private boolean tarIncluded = false;
    private boolean userListenersInstalled = true;
    private boolean installationUserListenersEnabled = false;

    private AlertDialog.Builder alertDialogBuilder;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            isSystemApp = isSystemPackage(this.getPackageManager()
                .getApplicationInfo("net.gpii.app", 0));
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }

        ctx = this;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        getAndroidVersion();
        executeShellCommand("su");
        appInstalled("stericson.busybox");

        progressparams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | 
            WindowManager.LayoutParams.TYPE_PHONE |
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        progressView = inflater
            .inflate(R.layout.progress_download_layout, null);

        installationButton = (Button)findViewById(R.id.installButton);
        downloadButton = (Button) findViewById(R.id.downloadButton);

        gpiiInfo = (RelativeLayout) findViewById(R.id.gpii_Info);

        file = new File(Environment.getExternalStorageDirectory(), "gpii");

        AssetManager assetManager = getResources().getAssets();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            XmlPullParser myxml = factory.newPullParser();

            InputStream raw = assetManager.open("GpiiDataResources.xml");
            myxml.setInput(raw, null);
            int eventType = myxml.getEventType();
        
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.TEXT) {
                
                    switch(myxml.getLineNumber()){
                        case 2:
                            uriTar = myxml.getText();
                            break;
                        case 3:
                            uriUserListeners = myxml.getText();
                            break;
                        case 4:
                            gpiiCompatibleAndroidDevicesUrl = myxml.getText();
                            break;
                        case 5:
                            gpiiRootDevicesUrl = myxml.getText();
                            break;
                        case 6:
                            gpiiJS = myxml.getText();
                            break;
                        case 7:
                            gpiiAPK = myxml.getText();
                            break;
                        case 8:
                            gpiiUserListenersAPK = myxml.getText();
                            break;
                        default:
                            Log.i(TAG,"Not suported XML entry");
                            break;
                    }
                }
                eventType = myxml.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    
        try {
            tarIncluded=true;
            Arrays.asList(getResources().getAssets().list("")).contains("gpii-android.tar.gz");
        } catch (IOException e1) {

            tarIncluded=false;
            e1.printStackTrace();
        }
    
        if(!gpiiApkInstalled("cloud4all.UserListener.NFC")){
            installGPIIUserListeners();
            userListenersInstalled = false;
            installationUserListenersEnabled=true;
        
        } else {

            if (file.exists()) {
                downloadButton.setVisibility(View.GONE);
                installationButton.setVisibility(View.VISIBLE);
            }

            if (isSystemApp) {

                downloadButton.setVisibility(View.GONE);
                installationButton.setVisibility(View.GONE);
                gpiiInfo.setVisibility(View.VISIBLE);
            }

            if (!gpiiApkInstalled("net.gpii.app")) {
                Toast.makeText(getApplicationContext(), "GPII NOT INSTALLED",
                    Toast.LENGTH_LONG).show();
                downloadButton.setVisibility(View.VISIBLE);
                installationButton.setVisibility(View.GONE);
            }

        }

        final BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Query query = new Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                            if(!userListenersInstalled){

                                userListenersInstalled=true;
                                userListenersNotInstalledDialog(gpiiUserListenersAPK);
                            
                            } else {
                                new ExtractGpiiZipFileSystem().execute();
                            }
 
                        }
                    }

                } else if (action.equals(ACTION_GPII_UNZIP_COMPLETE)) {

                    downloadButton.setVisibility(View.GONE);
                    installationButton.setVisibility(View.VISIBLE);
                    wm.removeView(progressView);

                } 
            }
        };

        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        registerReceiver(receiver, new IntentFilter(ACTION_GPII_UNZIP_COMPLETE));

        installationButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new InstallationProccessTask().execute();

            }
        });

        downloadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                wm.addView(progressView, progressparams);
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Request request = new Request(Uri.parse(uriTar));
                request.setDestinationUri(Uri.fromFile(new File(filepathgpii + gpiiJS)));
                enqueue = dm.enqueue(request);

            }
        });

        gpiiStatus = (TextView) findViewById(R.id.gpii_status);
        gpiiScriptUri = (EditText) findViewById(R.id.gpii_script_uri);
        gpiiStartButton = (Button) findViewById(R.id.gpii_start_button);
        gpiiStopButton = (Button) findViewById(R.id.gpii_stop_button);
        gpiiKillButton = (Button) findViewById(R.id.gpii_kill_button);
        gpiiUpdateStatusButton = (Button) findViewById(R.id.gpii_update_status_button);

        gpiiStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("org.meshpoint.anode.START");
                intent.putExtra("cmdline", gpiiScriptUri.getText().toString());
                sendBroadcast(intent);
            }
        });

        gpiiStopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("org.meshpoint.anode.STOPALL");
                sendBroadcast(intent);
            }
        });

        gpiiKillButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActivityManager manager = 
                        (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<RunningAppProcessInfo> services = manager
                        .getRunningAppProcesses();
                    RunningAppProcessInfo process = null;

                    for (int i = 0; i < services.size(); i++) {
                        process = services.get(i);
                        String name = process.processName;

                        if (name.equals("net.gpii.app")) {
                            break;
                        }
                    }

                    Process.killProcess(process.pid);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        });

        gpiiUpdateStatusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGPIIServer();
            }
        });

        checkGPIIServer();
        gpiiStatus.requestFocus();

    }

    @Override
    public void onResume(){
        super.onResume();
        if(installationUserListenersEnabled){
            if(tarIncluded){
            
                wm.addView(progressView, progressparams);
                downloadButton.setVisibility(View.GONE);
                installationButton.setVisibility(View.VISIBLE);
   
                new ExtractGpiiZipFileSystem().execute();
            }

            if (file.exists()) {
                downloadButton.setVisibility(View.GONE);
                installationButton.setVisibility(View.VISIBLE);
            } 

            if (isSystemApp) {

                downloadButton.setVisibility(View.GONE);
                installationButton.setVisibility(View.GONE);
                gpiiInfo.setVisibility(View.VISIBLE);

            }

            if (!gpiiApkInstalled("net.gpii.app")&&!tarIncluded) {
                Toast.makeText(getApplicationContext(), "GPII NOT INSTALLED AND NOT INCLUDED",
                    Toast.LENGTH_LONG).show();
                downloadButton.setVisibility(View.VISIBLE);
                installationButton.setVisibility(View.GONE);
            }
            installationUserListenersEnabled=false;
        }
    }

    protected void checkGPIIServer() {
        SocketClient sc = new SocketClient();
        sc.execute(GpiiActivity.this);
    }

    public void updateStatus(Object status) {
        if (status == (Object) GpiiActivity.GPII_STATE_RUNNING) {
            gpiiStatus.setText(R.string.gpii_status_running);
            gpiiStatus.setBackgroundColor(Color.GREEN);
            gpiiStatus.setTypeface(null, Typeface.BOLD);
        } else {
            gpiiStatus.setText(R.string.gpii_status_not_running);
            gpiiStatus.setBackgroundColor(Color.RED);
            gpiiStatus.setTypeface(null, Typeface.BOLD);
        }
    }

    class SocketClient extends AsyncTask {
        private static final String GPII_SERVER_HOST = "0.0.0.0";
        private static final int GPII_SERVER_PORT = 8081;
        private static final int CONNECTION_TIMEOUT = 3000;

        @Override
        protected Object doInBackground(Object... arg0) {
            GpiiActivity activity = (GpiiActivity) arg0[0];
            Object result = null;

            try {
                InetAddress address = InetAddress.getByName(GPII_SERVER_HOST);
                Socket socket = new Socket();

                socket.connect(new InetSocketAddress(address, GPII_SERVER_PORT),
                    CONNECTION_TIMEOUT);

                if (socket.isConnected()) {
                    result = GpiiActivity.GPII_STATE_RUNNING;
                }

                socket.close();
            } catch (Exception ex) {
                result = GpiiActivity.GPII_STATE_NOT_RUNNING;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            GpiiActivity.this.updateStatus(result);
        }
    }

    // Check Android version
    public void getAndroidVersion() {

        int sdkVersion = Build.VERSION.SDK_INT;
        String release = Build.VERSION.RELEASE;

        if (sdkVersion < 14 || sdkVersion > 21) {

            launchWebPage(gpiiCompatibleAndroidDevicesUrl);

        }

        if (sdkVersion > 18) {
            higherVersionKitKat = true;
        }

    }

    // Check rooted device
    private void executeShellCommand(String command) {

        java.lang.Process process;

        try {
            process = Runtime.getRuntime().exec(command);
            Log.i(TAG, "Rooted device");
        } catch (Exception e) {
            Log.i(TAG, "Not rooted device");
            launchWebPage(gpiiRootDevicesUrl);
        }
    }

    // Check BusyBox installed
    private void appInstalled(String app) {

        PackageManager pm = getPackageManager();

        try {

            pm.getPackageInfo(app, PackageManager.GET_ACTIVITIES);

        } catch (PackageManager.NameNotFoundException e) {

            busyBoxNotInstalledDialog(app);

        }
    }

    private boolean gpiiApkInstalled(String app) {

        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(app, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            Log.i(TAG, "Gpii APK not installed");
            return false;

        }
    }

    protected void gunzip(File tarFile, File dest) {

        try {
            dest.mkdir();
            TarArchiveInputStream tarIn = null;

            tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(
                new BufferedInputStream(new FileInputStream(tarFile))));

            TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
            // tarIn is a TarArchiveInputStream
            while (tarEntry != null) {
                // create a file with the same name as the
                // tarEntry
                File destPath = new File(dest, tarEntry.getName());
                System.out.println("working: " + destPath.getCanonicalPath());
                if (tarEntry.isDirectory()) {
                    destPath.mkdirs();
                } else {
                    destPath.createNewFile();
                    // byte [] btoRead = new byte[(int)tarEntry.getSize()];
                    byte[] btoRead = new byte[1024];
                    // FileInputStream fin
                    // = new FileInputStream(destPath.getCanonicalPath());
                    BufferedOutputStream bout = 
                    new BufferedOutputStream(new FileOutputStream(destPath));
                    int len = 0;

                    while ((len = tarIn.read(btoRead)) != -1) {
                        bout.write(btoRead, 0, len);
                    }

                    bout.close();
                    btoRead = null;

                }
                tarEntry = tarIn.getNextTarEntry();
            }

            tarIn.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void installGpiiApkIntoSystem(String apkname,
        String privilegedDir) {

        java.lang.Process process;

        try {

            if (new File("/data/app", apkname).exists()) {

                process = Runtime.getRuntime().exec("su");
                DataOutputStream out = new DataOutputStream(
                    process.getOutputStream());
                out.writeBytes("mount -o rw,remount yaffs2 /system\n");
                out.writeBytes("chmod 777 " + privilegedDir + "\n");
                out.writeBytes("chmod 777 /data/app/" + apkname + "\n");
                out.writeBytes("cat /data/app/" + apkname + " > "
                    + privilegedDir + "/" + apkname + "\n");
                out.writeBytes("chmod 644 " + privilegedDir + "/" + apkname
                    + "\n");
                out.writeBytes("mount -o remount,ro -t yaffs2 /system\n");
                out.writeBytes("reboot\n");
                out.flush();
                process.waitFor();

            } else {

                Log.e(TAG, "THERE IS NOT APK");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Dialog busyBoxNotInstalledDialog(String app) {

        final String appInstalled = app;

        alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("BusyBox Not Installed");

        // set dialog message
        alertDialogBuilder.setMessage("Click download to download BusyBox")
        .setPositiveButton("Download",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                        + appInstalled)));
                }
            }
        )
        .setNegativeButton("Exit",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                    GpiiActivity.this.finish();

                }
            }
        );

        alertDialogBuilder.create();

        return alertDialogBuilder.show();

    }

    private Dialog userListenersNotInstalledDialog(String app) {

        final String appInstalled = app;

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("GPII User Listeners Not Installed");

        alertDialogBuilder.setMessage("Do you want to install GPII User Listeners?")
            .setPositiveButton("Install",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent installUserListenersIntent = new Intent(Intent.ACTION_VIEW);
                        installUserListenersIntent.setDataAndType(Uri.fromFile(new File(filepathgpii+appInstalled)), "application/vnd.android.package-archive");
                        installUserListenersIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(installUserListenersIntent);
                        
                    }
                })
            .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        installationUserListenersEnabled=true;

                    }
                });

        alertDialogBuilder.create();

        return alertDialogBuilder.show();

    }

    private class ExtractGpiiZipFileSystem extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            Intent nfcDiscoveredIntent = new Intent(ACTION_GPII_UNZIP_COMPLETE);
            sendBroadcast(nfcDiscoveredIntent);

        }

        @Override
        protected Void doInBackground(Void... params) {

            File fileTar = new File(filepathgpii + gpiiJS);
            File fileDest = new File(filepathgpii);

            gunzip(fileTar, fileDest);

            return null;
        }

    }


    private class InstallationProccessTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

        }

        @Override
        protected Void doInBackground(Void... params) {

            if (higherVersionKitKat) {

                installGpiiApkIntoSystem(gpiiAPK, privSystemDir);

            } else {

                installGpiiApkIntoSystem(gpiiAPK, systemDir);
            }

            return null;
        }

    }

    private File createFileFromInputStream(InputStream inputStream, File f) {

        try{
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            } 

            outputStream.close();
            inputStream.close();

            return f;

        }catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {

        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private void launchWebPage(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
            Uri.parse(url));
        startActivity(browserIntent);
    }

    private void installGPIIUserListeners(){

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Request request = new Request(Uri.parse(uriUserListeners));
        request.setDestinationUri(Uri.fromFile(new File(filepathgpii
            + gpiiUserListenersAPK)));
        enqueue = dm.enqueue(request);
    }

}
