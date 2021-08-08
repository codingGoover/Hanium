package com.example.hanium;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    Intent intent;

    private EditText et_id, et_pw, check_pw;
    private Button btn_signup, checkBT;

    protected void onCreate(Bundle savedInstanceState) { //액티비티 시작시 처음으로 실행되는 생명주기!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        //ID값 찾아주기
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        check_pw = findViewById(R.id.check_pw);
        btn_signup = findViewById(R.id.btn_signup);
        checkBT = findViewById(R.id.checkBT);


        //회원가입 버튼 클릭시 수행행
        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check pw 랑 pw입력이랑 같을 경우


                //EditText에 현재 입력되어 있는 값을 get해옴옴
                String userID = et_id.getText().toString();
                String userPassword = et_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { //회원가입 성공한 경우
                                Toast.makeText(getApplicationContext(), "회원가입이 성공되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {//회원가입 실패한 경우
                                Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //서버로 Volley를 이용해서 요청을 함.
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                queue.add(registerRequest);

            }
        });
    }
}
