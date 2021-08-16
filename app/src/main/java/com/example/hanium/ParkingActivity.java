package com.example.hanium;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ParkingActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothHelper mBluetooth = new BluetoothHelper();
    private String DEVICE_NAME = "blueblue";
    private Button checkBT;
    private TextView mTextViewStatus;
    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_car);

        mTextViewStatus = (TextView) findViewById(R.id.ID_STATUSTEXT);
        checkBT=(Button)findViewById(R.id.checkBT);

        // Check if Bluetooth is supported by the device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            finish();
        }

        // Start Bluetooth connection with the paired "RNBT-729D" device (BlueSMIRF Gold)
        mTextViewStatus.setText("Connecting to " + DEVICE_NAME);

        mBluetooth.Connect(DEVICE_NAME);

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

    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.homeBT:
              //  mBluetooth.Disconnect(false);
                //홈 버튼 클릭시 홈 페이지로 이동
                intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                if (mBluetooth.isConnected()) {
                    mBluetooth.SendMessage("disconnect");
                    mBluetoothAdapter.disable();
                }
                break;

            case R.id.checkBT:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알람").setMessage("주차를 시작합니다");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (mBluetooth.isConnected()) {
                    mBluetooth.SendMessage("parking");
                }
                break;
        }
    }
}
