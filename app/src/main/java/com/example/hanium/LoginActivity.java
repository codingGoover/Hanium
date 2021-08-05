package com.example.hanium;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener{
    TextView joinTxt;
    Button loginBT;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        joinTxt = (TextView)findViewById(R.id.textView9);
        joinTxt.setOnClickListener(this);
        loginBT=(Button)findViewById(R.id.button2);
        loginBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textView9:
                //회원가입 페이지로 이동
                intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.button2:
                //로그인 버튼 클릭시 일단 홈 페이지로 이동 (실제: id,pw 검사 필요)
                intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}