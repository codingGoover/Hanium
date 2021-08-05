package com.example.hanium;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    Button signBT;
    Intent intent;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        signBT=(Button)findViewById(R.id.button3);
        signBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
           case R.id.button3:
                //회원가입 버튼 클릭시 일단 로그인 페이지로 이동 (실제: id,pw 가입필요)
                intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                break;
        }
    }


}
