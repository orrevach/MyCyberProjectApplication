package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AddMoreTime extends AppCompatActivity {
    private EditText hour, minutes, seconds;
    String username, Finalhour, Finalminutes, Finalseconds, currentLocation, endLocation, message, time, data;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    private boolean correctdetails = true;
    private long TimeLeftInMillySecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_time);
        username = getIntent().getStringExtra("username");
        currentLocation = getIntent().getStringExtra("currentLocation");
        endLocation = getIntent().getStringExtra("endLocation");
        SetUIViews();
    }

    private void SetUIViews() {
        minutes = (EditText) findViewById(R.id.minutes);
        seconds = (EditText) findViewById(R.id.seconds);
        hour = (EditText) findViewById(R.id.hour);

    }

    public void btn_SendRouteDetails(View view) {
        //פעולה לבדיקת תקינות השדות ועדכונם
        correctdetails = true;
        Finalminutes = minutes.getText().toString();

        Finalhour = hour.getText().toString();
        Finalseconds = seconds.getText().toString();

        if (Finalhour.isEmpty() && Finalminutes.isEmpty() && Finalseconds.isEmpty()) {//בודק אם השדות ריקים
            Toast.makeText(this, "please enter time", Toast.LENGTH_SHORT).show();
        } else {
            //בדיקת תקינות לשדות הזמן
            if (Finalhour.isEmpty()) {
                Finalhour = "0";
            } else {
                if (Finalhour.length() > 2) {
                    Toast.makeText(this, "hours are too long", Toast.LENGTH_SHORT).show();
                    correctdetails = false;
                }

            }
            if (Finalminutes.isEmpty()) {
                Finalminutes = "0";
            } else {
                if (Finalminutes.length() > 2) {
                    Toast.makeText(this, "minutes are too long", Toast.LENGTH_SHORT).show();
                    correctdetails = false;

                }

            }
            if (Finalseconds.isEmpty()) {
                Finalseconds = "0";
            } else {
                if (Finalseconds.length() > 2) {
                    Toast.makeText(this, "seconds are too long", Toast.LENGTH_SHORT).show();
                    correctdetails = false;

                }
            }


            if (correctdetails) {
                //בניית את ההודעה שתשלח לפייתון
                TimeLeftInMillySecond = Integer.parseInt(Finalhour) * 3600 + Integer.parseInt(Finalminutes) * 60 + Integer.parseInt(Finalseconds);
                TimeLeftInMillySecond = TimeLeftInMillySecond * 1000;
                message = "sr";
                if (Finalhour.length() > 9)
                    message += Finalhour.length() + Finalhour;
                else {
                    message += "0" + Finalhour.length() + Finalhour;
                }
                if (Finalminutes.length() > 9)
                    message += Finalminutes.length() + Finalminutes;
                else {
                    message += "0" + Finalminutes.length() + Finalminutes;
                }
                if (Finalseconds.length() > 9)
                    message += Finalseconds.length() + Finalseconds;
                else {
                    message += "0" + Finalseconds.length() + Finalseconds;
                }
                message += username.length() + username;
                if (currentLocation.length() < 10) {
                    message += "0" + currentLocation.length();
                } else {
                    message += currentLocation.length();
                }
                message += currentLocation;
                if (endLocation.length() < 10) {
                    message += "0" + endLocation.length();
                } else {
                    message += endLocation.length();
                }
                message += endLocation;
                //שליחת הפרטים ומעבר לעמוד onRoute
                Intent intent = new Intent(this, onRoute.class);
                intent.putExtra("username", username);
                intent.putExtra("currentLocation", currentLocation);
                intent.putExtra("endLocation", endLocation);
                time = String.valueOf(TimeLeftInMillySecond);
                intent.putExtra("time", time);
                intent.putExtra("message", message);
                SendToPython("dr" + username.length() + username);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void SendToPython(String message)
    {
        //פעולה לשליחת מידע לשרת פייתון
        String ipY="172.20.10.5";
        String ipO="10.0.2.2";
        int PORT= 7800;
        try {
            //פתיחת סוקט
            socket = new Socket(ipY, PORT);
            //שליחת המידע
            dos = new DataOutputStream(socket.getOutputStream());//משתנה בעזרתו נעביר את המידע לסוקט
            dos.writeBytes((message));//העברת המידע לבתים ושליחתו לשרת
            //קבלת המידע
            dis = new DataInputStream(socket.getInputStream());//משתנה בעזרתו נקבל מידע מהסוקט
            int len = dis.readInt();//משתנה לאורך המידע המוחזר
            byte[] buffer = new byte[len];//מערך של בתים באורך המשתנה
            dis.readFully(buffer);//מוודאת שקיבלתי את כל הבתים ושומר אותם במערך
            data = new String(buffer, StandardCharsets.UTF_8);//המרת המידע מbytes לstring ושמירתו במשתנה data
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}




