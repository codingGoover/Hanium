package com.example.hanium;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Intent intent;
    ImageButton regiBT;
    ImageButton parkBT;
    ImageButton departBT;
    BluetoothAdapter mBluetoothAdapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Check if Bluetooth is supported by the device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            finish();
        }

        regiBT = (ImageButton) findViewById(R.id.registerBT);
        regiBT.setOnClickListener(this);

        parkBT = (ImageButton) findViewById(R.id.parkingBT);
        parkBT.setOnClickListener(this);

        departBT=(ImageButton)findViewById(R.id.departBT);
        departBT.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.registerBT:
                //등록하기 버튼 클릭시 등록 페이지로 이동
                intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.parkingBT:
                //주차하기 버튼 클릭시 주차 페이지로 이동
                mBluetoothAdapter.enable();
                intent = new Intent(getApplicationContext(),ParkingActivity.class);
                startActivity(intent);
                break;
            case R.id.departBT:
                //출차하기 버튼 클릭시 출차 페이지로 이동
                mBluetoothAdapter.enable();
                intent = new Intent(getApplicationContext(),DepartureActivity.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
