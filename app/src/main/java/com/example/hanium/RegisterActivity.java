package com.example.hanium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements Serializable {

    Intent intent ;
    User user;
    EditText et_carNum;
    private Button checkBT;
    private AlertDialog dialog;
    boolean RegiSucced = false;
    private static String TAG = "phptest_MainActivity";
    private TextView mTextViewResult;

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "userID";
    private static final String TAG_NUM = "carNum";
    private static final String TAG_STATE ="parkingState";

    private String mJsonString;
    private Spinner spinner;
    ArrayList<HashMap<String, String>> mArrayList;


    ArrayList<String> items = new ArrayList<String>();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        // System.out.println("User ID = " + user.getUserID() + " User Password = " + user.getUserPassword());
        setContentView(R.layout.register_car);

        et_carNum = (EditText) findViewById(R.id.et_carNum);
        //mlistView = (ListView) findViewById(R.id.listView_main_list);
        checkBT = (Button)findViewById(R.id.checkBT);

        spinner = (Spinner) findViewById(R.id.spinner);


        mArrayList = new ArrayList<>();

        GetData task = new GetData();

        task.execute("http://auddms.ivyro.net/LoadCar.php");


    }
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegisterActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "response  - " + result);



            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://auddms.ivyro.net/LoadCar.php";


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }

    }



    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String num = item.getString(TAG_NUM);
                String state = item.getString(TAG_STATE);
                if(id.equals(user.getUserID())) // 현재 사용자 ID와 서버에있는 정보중 ID가 같은것들의 차량번호만 가져옴.
                    items.add(num);
                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NUM, num);
                hashMap.put(TAG_STATE, state);

                mArrayList.add(hashMap);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    RegisterActivity.this, android.R.layout.simple_spinner_item, items
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

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
                                //user.addCar(carNum);
                                //intent.putExtra("user",user);
                                intent = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);


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
