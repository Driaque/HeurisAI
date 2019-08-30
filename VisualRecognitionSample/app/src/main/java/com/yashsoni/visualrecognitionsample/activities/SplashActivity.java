package com.yashsoni.visualrecognitionsample.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yashsoni.visualrecognitionsample.R;

public class SplashActivity extends AppCompatActivity {
    final Object splashObj = new Object();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       //synchronized(splashObj)
//        {
//            try {
//
//                Intent intent = new Intent(SplashActivity.this, TakePhotoActivity.class);
//                splashObj.wait(3000);
//                startActivity(intent);
//                finish();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }


        Intent intent = new Intent(SplashActivity.this, TakePhotoActivity.class);
        Thread timerThread = new Thread() {
            @Override public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();

    }


//    final Object splashObj = new Object();
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        synchronized(splashObj){
//            try {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_splash);
//                Intent intent = new Intent(SplashActivity.this, TakePhotoActivity.class);
//                splashObj.wait(3000);
//                startActivity(intent);
//                finish();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//    }
}
