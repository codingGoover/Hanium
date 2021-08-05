package com.example.hanium;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
   protected void onCreate(Bundle savedInstanceStare) {

       super.onCreate(savedInstanceStare);
       setContentView(R.layout.splash);

       Handler handler = new Handler();
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
               finish();
           }
       },3000); //3초 딜레이
   }
 protected void onPause() {

     super.onPause();
     finish();
 }
}
