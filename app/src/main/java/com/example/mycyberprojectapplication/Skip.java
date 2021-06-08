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
        //sharedpreference- אחסון כמויות קטנות של מידע במכשיר
        SharedPreferences sharedPreferences = getSharedPreferences(LogIn.PREFS_NAME, 0);
        //SharedPreferences.Editor- SharedPreferences ממשק לשינוי ערכים ב
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();//הפעלה
        HasLoggedIn= sharedPreferences.getBoolean(/*מפתח*/"haslogin", /*ברירת מחדל*/false);//מכניס למשתנה האם המשתמש התחבר בעבר
        if(HasLoggedIn){

            Intent intent = new Intent(Skip.this, HomePage.class);
            intent.putExtra("username", sharedPreferences.getString("name", ""));//הוספת פרמטק שם משתמש
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(Skip.this, LogIn.class);
            startActivity(intent);
            finish();}
    }
}