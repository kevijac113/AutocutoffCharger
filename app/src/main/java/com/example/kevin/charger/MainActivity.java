package com.example.kevin.charger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
    Button b1, b2;
    TextView textView;
    private OutputStream outStream = null;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "Charger";
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter myBluetoothAdapter = null;
    private ArrayAdapter<String> BTArrayAdapter;
    private BluetoothSocket mBTSocket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "98:D3:31:20:A8:09";

    // #defines for identifying shared types between calling functions
    //private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    //private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    //private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        b1 = (Button) findViewById(R.id.find);
        b2 = (Button) findViewById(R.id.search);
        ListView myListView = (ListView) findViewById(R.id.listView1);
        // create the arrayAdapter that contains the BTDevices, and set it
        // to the ListView
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    pairedDevices = myBluetoothAdapter.getBondedDevices();
                    BTArrayAdapter.clear();
                    for (BluetoothDevice device : pairedDevices)
                        BTArrayAdapter.add(device.getName() + "\n"
                                + device.getAddress());
                    Toast.makeText(getApplicationContext(), "Showing Paired Devices",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myBluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "First Trun On Bluetooth",
                            Toast.LENGTH_SHORT).show();
                } else {

                    BTArrayAdapter.clear();
                    myBluetoothAdapter.startDiscovery();
                    registerReceiver(bReceiver, new IntentFilter(
                            BluetoothDevice.ACTION_FOUND));
                    Toast.makeText(getApplicationContext(), "Showing discovered Devices",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(i);
            }
        });
    };
    //myListView.setOnItemClickListener(new OnItemClickListener()
    //private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
      //      {
        //        @Override
          //      public void onItemClick(AdapterView<?> parent, View view, int position, long id)

                    // Set up a pointer to the remote node using it's address.
                    //BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);
                 //try {
                   //     mBTSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    //} catch (IOException e) {
                      //  Toast.makeText(getApplicationContext(),"creating socket error1",Toast.LENGTH_LONG).show();
                    //}
                    // Discovery is resource intensive.  Make sure it isn't going on
                    // when you attempt to connect and pass your message.
                    //myBluetoothAdapter.cancelDiscovery();
                    // Establish the connection.  This will block until it connects.
                    //Log.d(TAG, "...Connecting to Remote...");
                    //try {
                        //mBTSocket.connect();
                      //  Log.d(TAG, "...Connection established and data link opened...");
                    //} catch (IOException e) {
                        //try {
                          //  mBTSocket.close();
                        //} catch (IOException e2) {
                            //errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                          //  Toast.makeText(getApplicationContext(),"error in array onclick listener",Toast.LENGTH_LONG).show();
                        //}

                   // }
                    // Create a data stream so we can talk to server.
                    //Log.d(TAG, "...Creating Socket...");
                    //try {
                        //outStream = mBTSocket.getOutputStream();

                    //} catch (IOException e) {
                        //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
                       // Toast.makeText(getApplicationContext(),"creating socket error2",Toast.LENGTH_LONG);
                    //}

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the
                // arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

}
