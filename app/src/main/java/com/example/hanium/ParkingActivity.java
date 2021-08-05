package com.example.hanium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ParkingActivity extends AppCompatActivity {

    Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_car);
    }

    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.homeBT:
                //홈 버튼 클릭시 홈 페이지로 이동
                intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                break;

            case R.id.checkBT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알람").setMessage("주차를 시작합니다");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }
}
