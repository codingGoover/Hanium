package com.example.hanium;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DepartureActivity extends AppCompatActivity {
    private static String TAG = "phptest_LoadActivity";
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothHelper mBluetooth = new BluetoothHelper();
    private String DEVICE_NAME = "blueblue";
    private Button checkBT;
    private TextView mTextViewStatus;
    Intent intent;


    User user;

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "userID";
    private static final String TAG_NUM = "carNum";
    private static final String TAG_STATE ="parkingState";

    private String mJsonString;
    private Spinner spinner;
    static private String selectNum;
    ArrayList<String> cars = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.departure_car);

        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        spinner = (Spinner) findViewById(R.id.spinner);

       DepartureActivity.GetData task = new DepartureActivity.GetData();

        task.execute("http://auddms.ivyro.net/LoadCar.php");

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
    }
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DepartureActivity.this,
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
String state = item.getString(TAG_STATE);
                if(id.equals(user.getUserID()) && state.equals("1") ) // 현재 사용자 ID와 서버에있는 정보중 ID가 같은것들의 차량번호만 가져옴.&& 주차된 차량만 (state = 1)
                    cars.add(num);
                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NUM, num);



            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    DepartureActivity.this, android.R.layout.simple_spinner_item, cars
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
                //홈 버튼 클릭시 홈 페이지로 이동
                intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                if (mBluetooth.isConnected()) {
                    mBluetooth.SendMessage("disconnect");
                    mBluetoothAdapter.disable();
                }
                break;

            case R.id.checkBT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림").setMessage("출차를 시작합니다");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (mBluetooth.isConnected()) {
                    mBluetooth.SendMessage("departure");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
