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
    private AlertDialog dialog;
    private boolean validate = false , checkPW = false;

    protected void onCreate(Bundle savedInstanceState) { //액티비티 시작시 처음으로 실행되는 생명주기!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        //ID값 찾아주기
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        check_pw = findViewById(R.id.check_pw);
        btn_signup = findViewById(R.id.btn_signup);
        checkBT = findViewById(R.id.checkBT);


        checkBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                if (validate) {
                    return; //검증 완료
                }

                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                et_id.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                queue.add(validateRequest);
            }
        });

        //회원가입 버튼 클릭시 수행행
        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check pw 랑 pw입력이랑 같을 경우


                //EditText에 현재 입력되어 있는 값을 get해옴옴
                String userID = et_id.getText().toString();
                String userPassword = et_pw.getText().toString();
String checkPassword = check_pw.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        if(checkPassword.equals(userPassword)) {
                            checkPW = true;
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
                        else if (userID.equals("") || userPassword.equals("") || checkPassword.equals("")) {
                            Toast.makeText(getApplicationContext(), "모두 기입하였는지 한번 더 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하는지 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            checkPW = false;
                        }


                    }
                };


                if(userID.equals("") || userPassword.equals("") || checkPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "모두 기입하였는지 한번 더 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(validate == false){
                    Toast.makeText(getApplicationContext(), "ID중복확인을 진행해주세요.", Toast.LENGTH_SHORT).show();
                }
else if( checkPassword.equals(userPassword)) {
    //서버로 Volley를 이용해서 요청을 함.
    RegisterRequest registerRequest = new RegisterRequest(userID, checkPassword, responseListener);
    RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
    queue.add(registerRequest);
}
else{
    Toast.makeText(getApplicationContext(), "비밀번호가 일치하는지 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
}
            }
        });
    }
}
