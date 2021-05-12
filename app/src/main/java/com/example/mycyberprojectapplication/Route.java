package com.example.mycyberprojectapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Route extends AppCompatActivity {
    private EditText currentLocation, endLocation, hour,minutes,seconds;
    String username,FinalcurrentLocation, FinalendLocation, Finalhour, Finalminutes,Finalseconds, data,message,time;
    private boolean timerrunning=false, correctdetails=true;
    private long TimeLeftInMillySecond;
    private CountDownTimer countDownTimer;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        username =getIntent().getStringExtra("username");

        SetUIViews();

    }
    private void SetUIViews(){
        minutes= (EditText) findViewById(R.id.minutes);
        seconds= (EditText) findViewById(R.id.seconds);
        currentLocation= (EditText) findViewById(R.id.start);
        endLocation=(EditText) findViewById(R.id.end);
        hour= (EditText) findViewById(R.id.hour);

    }
    public void btn_SendRouteDetails(View view) {
        correctdetails=true;
        Finalminutes = minutes.getText().toString();
        FinalcurrentLocation = currentLocation.getText().toString();
        FinalendLocation = endLocation.getText().toString();
        Finalhour = hour.getText().toString();
        Finalseconds = seconds.getText().toString();
       if(FinalendLocation.isEmpty()||FinalcurrentLocation.isEmpty()){
           Toast.makeText(this, "please enter all the locations", Toast.LENGTH_SHORT).show();
       }
       else {
           if(FinalendLocation.length()>99)
               Toast.makeText(this, "end location is too long", Toast.LENGTH_SHORT).show();
           else{
               if(FinalcurrentLocation.length()>99)
                   Toast.makeText(this, "current location is too long", Toast.LENGTH_SHORT).show();
               else {

                   if (Finalhour.isEmpty() && Finalminutes.isEmpty() && Finalseconds.isEmpty()) {
                       Toast.makeText(this, "please enter time", Toast.LENGTH_SHORT).show();
                   } else {

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
                   }
               }



                   if(correctdetails){
                       TimeLeftInMillySecond = Integer.parseInt(Finalhour)*3600+Integer.parseInt(Finalminutes)*60+Integer.parseInt(Finalseconds);
                       TimeLeftInMillySecond=TimeLeftInMillySecond*1000;
                       message = "sr";
                       if(Finalhour.length()>9)
                           message+= Finalhour.length()+Finalhour;
                       else{
                           message+="0"+Finalhour.length()+Finalhour;
                       }
                       if(Finalminutes.length()>9)
                           message+= Finalminutes.length()+Finalminutes;
                       else{
                           message+="0"+Finalminutes.length()+Finalminutes;
                       }
                       if(Finalseconds.length()>9)
                           message+= Finalseconds.length()+Finalseconds;
                       else{
                           message+="0"+Finalseconds.length()+Finalseconds;
                       }
                       message+=username.length()+username;
                       if(FinalcurrentLocation.length()<10){
                           message+="0"+FinalcurrentLocation.length();
                       }
                       else {
                       message+=FinalcurrentLocation.length();
                       }
                       message+=FinalcurrentLocation;
                       if(FinalendLocation.length()<10){
                           message+="0"+FinalendLocation.length();
                       }

                       else {
                           message+=FinalendLocation.length();
                       }
                       message+=FinalendLocation;

                       Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                       Intent intent= new Intent(Route.this,onRoute.class);
                       intent.putExtra("username",username);
                       intent.putExtra("currentLocation",FinalcurrentLocation);
                       intent.putExtra("endLocation",FinalendLocation);
                       time=String.valueOf(TimeLeftInMillySecond) ;
                       intent.putExtra("time",time);
                       intent.putExtra("message",message);
                       startActivity(intent);
                       finish();


                   }
                   else{
                       Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
                   }

               }

           }

           }

    public void startstop(){
        if(timerrunning){
            stoptimer();
        }
        else{
            starttimer();

        }

    }
    public void stoptimer(){
        countDownTimer.cancel();
        timerrunning=false;

    }
    public void starttimer(){
        countDownTimer = new CountDownTimer(TimeLeftInMillySecond,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillySecond=millisUntilFinished;

            }

            @Override
            public void onFinish() {
                isOKmessage();

            }

        }.start();
    }

    public void isOKmessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Route.this);
        builder.setCancelable(false);
        builder.setTitle("Have you reached to "+FinalendLocation+" safetly?");
        builder.setPositiveButton("I'm safe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Route.this, "Glad to hear you arrived safely", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("add more time", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void btn_MoveToHomePage(View view) {
        Intent intent= new Intent(Route.this,HomePage.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();


    }
    public void SendToPython(String message)
    {
        try {
            socket = new Socket("10.0.2.2", 7800);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBytes((message));
            dis = new DataInputStream(socket.getInputStream());
            int len = dis.readInt();
            byte[] buffer = new byte[len];
            dis.readFully(buffer);
            data = new String(buffer, StandardCharsets.UTF_8);
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
