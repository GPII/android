package net.gpii.app;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GpiiActivity extends Activity
{

    public static final int GPII_STATE_RUNNING = 0;
    public static final int GPII_STATE_NOT_RUNNING = 1;

    private TextView gpiiStatus;
    private EditText gpiiScriptUri;
    private Button gpiiStartButton;
    private Button gpiiStopButton;
    private Button gpiiKillButton;
    private Button gpiiUpdateStatusButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gpiiStatus = (TextView)findViewById(R.id.gpii_status);
        gpiiScriptUri = (EditText)findViewById(R.id.gpii_script_uri);
        gpiiStartButton = (Button)findViewById(R.id.gpii_start_button);
        gpiiStopButton = (Button)findViewById(R.id.gpii_stop_button);
        gpiiKillButton = (Button)findViewById(R.id.gpii_kill_button);
        gpiiUpdateStatusButton = (Button)findViewById(R.id.gpii_update_status_button);

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
                    ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    List<RunningAppProcessInfo> services = manager.getRunningAppProcesses();
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

    protected void checkGPIIServer (){
        SocketClient sc = new SocketClient();
        sc.execute(GpiiActivity.this);
    }

    public void updateStatus (Object status) {
        if (status == (Object) GpiiActivity.GPII_STATE_RUNNING){
            gpiiStatus.setText(R.string.gpii_status_running);
            gpiiStatus.setBackgroundColor(Color.GREEN);
            gpiiStatus.setTypeface(null,Typeface.BOLD);
        } else {
            gpiiStatus.setText(R.string.gpii_status_not_running);
            gpiiStatus.setBackgroundColor(Color.RED);
            gpiiStatus.setTypeface(null,Typeface.BOLD);
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
 
                socket.connect(new InetSocketAddress(address, GPII_SERVER_PORT), CONNECTION_TIMEOUT);

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
}
