package com.example.hanium;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParkingActivity extends AppCompatActivity implements Serializable {

    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothHelper mBluetooth = new BluetoothHelper();
    private String DEVICE_NAME = "blueblue";
    private Button checkBT;
    private TextView mTextViewStatus;
    Intent intent;
    private static String TAG = "phptest_LoadActivity";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "userID";
    private static final String TAG_NUM = "carNum";



    User user;
    ArrayList<String> cars = new ArrayList<String>();
    private String mJsonString;
    private Spinner spinner;

    int count = -1;

    static private String selectNum;


    public static  String UPDATE = "http://auddms.ivyro.net/updateCar.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_car);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        spinner = (Spinner) findViewById(R.id.spinner);

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

        GetData task = new GetData();

        task.execute("http://auddms.ivyro.net/LoadCar.php");

    }






    public void updateCar() {
        String userid = user.getUserID().toString();
        String carNum = selectNum;


        HashMap<String, String> requestedParams = new HashMap<>();
        requestedParams.put("userid", userid);
        requestedParams.put("carNum", carNum);



        PostRequestHandler postRequestHandler = new PostRequestHandler(UPDATE, requestedParams);
        postRequestHandler.execute();



    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ParkingActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "response  - " + result);



            if (result == null){


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

                if(id.equals(user.getUserID())) // 현재 사용자 ID와 서버에있는 정보중 ID가 같은것들의 차량번호만 가져옴.
                    cars.add(num);
                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NUM, num);



            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    ParkingActivity.this, android.R.layout.simple_spinner_item, cars
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            //adapter.add("차량번호를 선택해주세요");

            spinner.setAdapter(adapter);
            //spinner.setSelection(adapter.count);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectNum = adapter.getItem(i);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

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
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 성공한 경우



                                Toast.makeText(getApplicationContext(), "등록이 성공?.", Toast.LENGTH_SHORT).show();


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

                UpdateRequest CarUpdateRequest = new UpdateRequest(user.getUserID(), selectNum,responseListener);
                RequestQueue queue = Volley.newRequestQueue(ParkingActivity.this);
                queue.add(CarUpdateRequest);





                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림").setMessage("주차를 시작하겠습니다."); //
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                if (mBluetooth.isConnected()) {
                    mBluetooth.SendMessage("parking");
                }
                break;
        }
    }



}

class PostRequestHandler extends AsyncTask<Void, Void, String> {
    // php URL 주소
    String url;
    // Key, Value 값
    HashMap<String, String> requestedParams;
    PostRequestHandler(String url, HashMap<String, String> params){
        this.url = url; this.requestedParams = params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override protected String doInBackground(Void... voids) {
        // post request 보냄

        try { String s = postRequestHandler(url, requestedParams);
            return s.toString(); }
        catch (UnsupportedEncodingException e)
        { e.printStackTrace();
        }
        return null;
    }

    @Override protected void onPostExecute(String s) {
        super.onPostExecute(s); }

    public String postRequestHandler(String requestUrl, HashMap<String, String> requestedDataParams) throws UnsupportedEncodingException { // Set an Empty URL obj in system
        URL url;
        // Set a String Builder to store result as string
        StringBuilder stringBuilder = new StringBuilder();
        // Now Initialize URL
        try {
            url = new URL(requestUrl);
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                // Make a HTTP url connection

                // Set Method Type

                // Set Connection Time
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("POST");
                // set Input output ok
                connection.setDoInput(true);
                connection.setDoOutput(true);

                // Remove Caches
                // connection.setUseCaches(false);
                // connection.setDefaultUseCaches(false);


                // Creating a url as String with params
                StringBuilder url_string = new StringBuilder();
                boolean ampersand = true;
                for (Map.Entry<String, String> params : requestedDataParams.entrySet()) {
                    if (ampersand)
                        ampersand = false;
                    else
                        url_string.append("&");

                    url_string.append(URLEncoder.encode(params.getKey(), "UTF-8"));
                    url_string.append("=");
                    url_string.append(URLEncoder.encode(params.getValue(), "UTF-8"));
                }
                Log.d("Final Url===", url_string.toString());

                //Creating an output stream
                OutputStream outputStream = connection.getOutputStream();

                // Write Output Steam
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(url_string.toString());




                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //Log.d("Response===", connection.getResponseMessage());

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    // Local String
                    String result;
                    while ((result = bufferedReader.readLine()) != null) {
                        stringBuilder.append(result);
                    }

                    // Log.d("Result===", result);

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString(); }

    // Get Request Handler
    public String getRequestHandler(String requestUrl){
        // To Store response
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(requestUrl);
            // Open Connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Local
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }





}


