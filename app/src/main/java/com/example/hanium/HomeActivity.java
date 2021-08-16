package com.example.hanium;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , Serializable {

    Intent intent ;
    ImageButton regiBT;
    ImageButton parkBT;
    ImageButton departBT;
    TextView tv_id;
    BluetoothAdapter mBluetoothAdapter = null;
    User user;
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

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

        tv_id = (TextView)findViewById(R.id.tv_id);
        tv_id.setText(user.getUserID()+ " 님");

    }

    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.registerBT:
                //등록하기 버튼 클릭시 등록 페이지로 이동
                intent = new Intent(getApplicationContext(),RegisterActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                break;
            case R.id.parkingBT:
                //주차하기 버튼 클릭시 주차 페이지로 이동
                mBluetoothAdapter.enable();
                intent = new Intent(getApplicationContext(),ParkingActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                break;
            case R.id.departBT:
                //출차하기 버튼 클릭시 출차 페이지로 이동
                mBluetoothAdapter.enable();
                intent = new Intent(getApplicationContext(),DepartureActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
