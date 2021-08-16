package com.example.hanium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class RegisterActivity extends AppCompatActivity implements Serializable {

    Intent intent ;
    User user;
    EditText et_carNum;
    private Button checkBT;
    private AlertDialog dialog;
boolean RegiSucced = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
       // System.out.println("User ID = " + user.getUserID() + " User Password = " + user.getUserPassword());
        setContentView(R.layout.register_car);

        et_carNum = (EditText) findViewById(R.id.et_carNum);

        checkBT = (Button)findViewById(R.id.checkBT);





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

                String carNum = et_carNum.getText().toString();
int parkingState = 0;
                if (carNum.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("차량번호를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }



                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) { // 성공한 경우


                                    Toast.makeText(getApplicationContext(), "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                    RegiSucced = true;
                                    user.addCar(carNum);
                                    intent.putExtra("user",user);
                                   // startActivity(intent);

                                } else {// 실패한 경우
                                    Toast.makeText(getApplicationContext(), "등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                    }
                };

                CarRegisterRequest CarRegisterRequest = new CarRegisterRequest(user.getUserID(), carNum,parkingState, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(CarRegisterRequest);

                break;
        }
    }
}
