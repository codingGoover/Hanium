package com.example.hanium;


/**
 * BluetoothHelper Java Helper Class for Android - Example app
 * Created by G.Capelli (BasicAirData) on 06/02/16
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 **/

import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hanium.BluetoothHelper;

public class BluetoothHelperExample extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothHelper mBluetooth = new BluetoothHelper();
    private SeekBar mSeekBar;
    private TextView mTextViewStatus;
    boolean led = false;                                // The status of the ON/OFF led
    private String DEVICE_NAME = "blueblue";           // The name of the remote device (BlueSMIRF Gold)
    // private String DEVICE_NAME = "HC-05";            // The name of the remote device (HC-05)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // force portrait screen
        setContentView(R.layout.activity_bluetooth_helper_example);

        mTextViewStatus = (TextView) findViewById(R.id.ID_STATUSTEXT);
        mSeekBar = (SeekBar) findViewById(R.id.ID_SEEKBAR);

        // Check if Bluetooth is supported by the device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            finish();
        }

        // Start Bluetooth connection with the paired "RNBT-729D" device (BlueSMIRF Gold)
        mTextViewStatus.setText("Connecting to " + DEVICE_NAME);
        mBluetooth.Connect(DEVICE_NAME);

        // Setup listener for SeekBar:
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mBluetooth.isConnected()) {
                    // Write the new value to Bluetooth (The String is something like "$PWM,128")
                    mBluetooth.SendMessage("$PWM," + seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used in this demo app
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mBluetooth.isConnected()) {
                    // Write the new value to Bluetooth
                    mBluetooth.SendMessage("$PWM," + seekBar.getProgress());
                }
            }
        });

        // Setup listener for Bluetooth helper;
        mBluetooth.setBluetoothHelperListener(new BluetoothHelper.BluetoothHelperListener() {
            @Override
            public void onBluetoothHelperMessageReceived(BluetoothHelper bluetoothhelper, String message) {
                // Do your stuff with the message received !!!
                // runOnUiThread(new Runnable() {
                //     @Override
                //     public void run() {
                //         // Update here your User Interface
                //     }
                // });
            }

            @Override
            public void onBluetoothHelperConnectionStateChanged(BluetoothHelper bluetoothhelper, boolean isConnected) {
                if (isConnected) {
                    mTextViewStatus.setText("Connected");
                } else {
                    mTextViewStatus.setText("Disconnected");
                    // Auto reconnect!
                    mBluetooth.Connect(DEVICE_NAME);
                }
            }
        });
    }

    // The event fired when you click the button
    public void onButtonClick(View view) {
        if (mBluetooth.isConnected()) {
            mBluetooth.SendMessage("주차하기");

        }
    }
}