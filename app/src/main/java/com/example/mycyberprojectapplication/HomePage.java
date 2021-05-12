package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity  {
    private TextView name;
    String username;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        username =getIntent().getStringExtra("username");
        name=findViewById(R.id.hellousername);
        name.setText("Hello "+username);

       Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

    }


    public void btn_MoveToChangePhone(View view)
    {
        Intent intent= new Intent(HomePage.this,ChangePhoneNumber.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }
    public void btn_MoveToChangeEmail(View view)
    {
        Intent intent= new Intent(HomePage.this,ChangeEmail.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }

    public void LogOut(View view) {
        SharedPreferences sharedPreferences =getSharedPreferences(LogIn.PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("haslogin",false);
        editor.putString("name","");
        editor.commit();
        username ="";
        startActivity(new Intent(getApplicationContext(),LogIn.class));
        finish();
    }

    public void btn_MoveToRoute(View view) {
        Intent intent= new Intent(HomePage.this,Route.class);
        intent.putExtra("username",username);

        startActivity(intent);
    }
    public void btn_MoveToInstructions(View view) {
        Intent intent= new Intent(HomePage.this,Instructions.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }


}