package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Skip extends AppCompatActivity {
    public static boolean HasLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip);
        SharedPreferences sharedPreferences = getSharedPreferences(LogIn.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();
        HasLoggedIn= sharedPreferences.getBoolean("haslogin", false);
        if(HasLoggedIn){
            Intent intent = new Intent(Skip.this, HomePage.class);
            intent.putExtra("username", sharedPreferences.getString("name", ""));

            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(Skip.this, LogIn.class);
            startActivity(intent);
            finish();}
    }
}