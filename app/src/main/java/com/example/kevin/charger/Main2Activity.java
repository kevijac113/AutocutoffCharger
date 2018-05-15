package com.example.kevin.charger;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
public class Main2Activity extends Activity {
    private static final String TAG = "Charger";
    Button btnReadBattery;
    TextView textBatteryStatus;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "98:D3:31:20:A8:09";
    private boolean started = true;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            btnReadBattery.performClick();
            if (started) {
                start1();
            } else {
                stop();
            }
        }
    };
    private void start1() {
        started = true;
        btnReadBattery.postDelayed(runnable,9000);
    }
    public void stop() {
        started = false;
        btnReadBattery.removeCallbacks(runnable);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnReadBattery = (Button) findViewById(R.id.img);
        textBatteryStatus = (TextView) findViewById(R.id.t1);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        runnable.run();
        btnReadBattery.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"in on click",Toast.LENGTH_SHORT).show();
                textBatteryStatus.setText(readBattery());
                IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryIntent = registerReceiver(null, batteryIntentFilter);
                assert batteryIntent != null;
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                if (level>=50) {
                    sendData("0");
                    Toast msg = Toast.makeText(getBaseContext(),
                            "Battery Completely charged Disconnecting Charger", Toast.LENGTH_SHORT);
                    msg.show();
                } else {
                    Toast msg = Toast.makeText(getBaseContext(),
                            "Battery Still needs  to Charge", Toast.LENGTH_SHORT);
                    sendData("1");
                    msg.show();
                }
            }
        });
        Log.d(TAG, "In onCreate()");

        //checkBTState();
    }
    protected String readBattery() {
        StringBuilder sb = new StringBuilder();
        IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = registerReceiver(null, batteryIntentFilter);
        assert batteryIntent != null;
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            sb.append("BATTERY_STATUS_CHARGING\n");
        }
        if (status == BatteryManager.BATTERY_STATUS_FULL) {
            sb.append("BATTERY_STATUS_FULL\n");
        }
        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if (plugged == BatteryManager.BATTERY_PLUGGED_USB) sb.append("BATTERY_PLUGGED_USB\n");
        if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
            sb.append("BATTERY_PLUGGED_AC\n");
        }
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        sb.append("level: ").append(level).append("\n");
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        sb.append("scale: ").append(scale).append("\n");
        return sb.toString();
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "...In onResume - Attempting client connect...");
        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();
        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
            Toast.makeText(getApplicationContext(),"Connection established and data link opened",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }
        public void onPause() {
        super.onPause();
        Log.d(TAG, "...In onPause()...");
        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }
        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }
   // private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
     //   if (btAdapter == null) {
       //     errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        //} else {
          //  if (btAdapter.isEnabled()) {
            //    Log.d(TAG, "...Bluetooth is enabled...");
            //} else {
                //Prompt user to turn on Bluetooth
              //  Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //}
        //}
    //}
    private void errorExit(String title, String message) {
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        Log.d(TAG, "...Sending data: " + message + "...");
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
            errorExit("Fatal Error", msg);
        }
    }
}
