package com.symantec.screamaway;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;

import android.widget.Toast;

import com.example.chandan_yadav.screamaway.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity  {
    Button button1,button2,button3,button4,button5, monitorButton;
    public String selectedDeviceName;
    ListView deviceList;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    Map<String, String> list;
    MediaPlayer player;
    AssetFileDescriptor afd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button4=(Button)findViewById(R.id.button4);
        button5=(Button)findViewById(R.id.button5);
        monitorButton = (Button) findViewById(R.id.button6);
        // final MediaPlayer mp=MediaPlayer.create(this,R.raw.soho.mp3)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
                Toast.makeText(getApplicationContext(), "Device Found",Toast.LENGTH_LONG).show();
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Toast.makeText(getApplicationContext(), "Device is now connected",Toast.LENGTH_LONG).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
                Toast.makeText(getApplicationContext(), "Done searching",Toast.LENGTH_LONG).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                Toast.makeText(getApplicationContext(), "Device is about to disconnect",Toast.LENGTH_LONG).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Toast.makeText(getApplicationContext(), "Device has disconnected",Toast.LENGTH_LONG).show();
            }
        }
    };

    public void on(View v){
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        bluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


    public void list(View v){
        pairedDevices = bluetoothAdapter.getBondedDevices();
        list = new HashMap<String,String>();
        for(BluetoothDevice bt : pairedDevices) list.put(bt.getName(),bt.getAddress());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_checked, list.keySet().toArray());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDeviceName = lv.getItemAtPosition(position).toString();
                monitorButton.setEnabled(true);
                button5.setEnabled(true);
            }
        });
        monitorButton.setEnabled(false);
        button5.setEnabled(false);
    }

    public void monitor(View v){
        BluetoothHeadset mBluetoothHeadset;
        BluetoothSocket mBluetoothSocket;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String address = list.get(selectedDeviceName);
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            mBluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            mBluetoothSocket.connect();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unable to connect to the device", Toast.LENGTH_LONG).show();
        }
    }

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.STATE_CONNECTED) {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.STATE_CONNECTED) {
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
            }
        }
    };
}
