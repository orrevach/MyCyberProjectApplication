package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Instructions extends AppCompatActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username =getIntent().getStringExtra("username");
        setContentView(R.layout.activity_instructions);

    }
    public void btn_MoveToHomePage(View view) {
        Intent intent= new Intent(Instructions.this,HomePage.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }

}