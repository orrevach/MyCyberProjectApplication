package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3000);
                        Intent intent = new Intent (MainActivity.this,LogIn.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}