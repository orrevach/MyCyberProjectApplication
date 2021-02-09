package com.example.mycyberprojectapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class onRoute extends AppCompatActivity {
    String username, currentlocation, endlocation, time, data, message, phonenumber, helpmessage, messagetopython;
    private TextView route;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    private boolean timerrunning = false, ispressed = false;
    private long TimeLeftInMillySecond;
    private CountDownTimer countDownTimer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_route);

        username = getIntent().getStringExtra("username");
        currentlocation = getIntent().getStringExtra("currentLocation");
        endlocation = getIntent().getStringExtra("endLocation");
        time = getIntent().getStringExtra("time");
        messagetopython = getIntent().getStringExtra("message");
        message = "pn" + username.length() + username;
        SendToPython(message);
        phonenumber = data;
        SendToPython(messagetopython);

        TimeLeftInMillySecond = Long.parseLong(time);
        route = findViewById(R.id.onroute);
        route.setText("you are on a route from " + currentlocation + " to " + endlocation);


        startstop();
    }

    public void startstop() {
        if (timerrunning) {
            stoptimer();
        } else {
            starttimer();


        }

    }

    public void stoptimer() {
        countDownTimer.cancel();
        timerrunning = false;

    }

    public void starttimer() {

        countDownTimer = new CountDownTimer(TimeLeftInMillySecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillySecond = millisUntilFinished;

            }

            @Override
            public void onFinish() {
                if (ispressed == false) {
                    isOKmessage();
                }


            }

        }.start();
    }

    public void isOKmessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(onRoute.this);
        builder.setTitle("Have you reached to " + endlocation + " safetly?");

        builder.setItems(new CharSequence[]
                        {"I'm safe", "add more time", Html.fromHtml("<font color='#E91E63'>" + "help!!" + "</font>")},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                Toast.makeText(onRoute.this, "Glad to hear you arrived safely", Toast.LENGTH_SHORT).show();

                                arrived();
                                ispressed = true;
                                break;
                            case 1:
                                Toast.makeText(onRoute.this, "add more time", Toast.LENGTH_SHORT).show();
                                ispressed = true;

                                MoveToAddMoreTime();
                                break;
                            case 2:
                                ispressed = true;
                                SendHelpMessage();
                                Toast.makeText(onRoute.this, "help!!", Toast.LENGTH_SHORT).show();

                                break;

                        }
                    }
                });
        final AlertDialog dialog = builder.create();
        builder.create().show();
        if (timerrunning) {
            stoptimer();
        } else {
            TimeLeftInMillySecond = 90000;
            countDownTimer = new CountDownTimer(TimeLeftInMillySecond, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TimeLeftInMillySecond = millisUntilFinished;

                }

                @Override
                public void onFinish() {
                    Toast.makeText(onRoute.this, "AHAHHAHAHAHAHAHAH", Toast.LENGTH_SHORT).show();
                    if (ispressed == false) {
                        SendHelpMessage();
                    } else {
                        MoveToHomePage();
                    }


                }

            }.start();

        }
    }

    public void DeleteRoute() {
        SendToPython("dr" + username.length() + username);
    }

    public void MoveToHomePage() {
        Intent intent = new Intent(onRoute.this, HomePage.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    public void arrived() {
        DeleteRoute();
        final AlertDialog.Builder builder = new AlertDialog.Builder(onRoute.this);
        builder.setTitle(Html.fromHtml("<font color='#E91E63'>" + "Glad to hear you arrived safely, we are waiting for you're next route \n :)" + "</font>"));
        final AlertDialog dialog = builder.create();
        dialog.show();

        if (timerrunning) {
            stoptimer();
        } else {
            TimeLeftInMillySecond = 90000;
            countDownTimer = new CountDownTimer(TimeLeftInMillySecond, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TimeLeftInMillySecond = millisUntilFinished;

                }

                @Override
                public void onFinish() {
                    dialog.dismiss();

                }

            }.start();

            MoveToHomePage();
        }

    }

    public void btn_arrived(View view) {
        ispressed = true;
        arrived();
    }

    public void SendHelpMessage() {
        SendToPython("nh" + username.length() + username);
        int permissioncheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissioncheck == PackageManager.PERMISSION_GRANTED) {
            MyMessage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);

        }
    }

    private void MyMessage() {
        helpmessage="username "+username+" did not arrive while she was budgeting for herself from  "+currentlocation+" to "+endlocation+", please check if she is okay.";
        SmsManager smsManager=SmsManager.getDefault();
        smsManager.sendTextMessage(phonenumber,null,helpmessage,null,null);

        Toast.makeText(this,"message sent", Toast.LENGTH_SHORT).show();

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
            socket.close();

        }
        catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void btn_helpbutton(View view) {
        ispressed=true;
        SendHelpMessage();
    }
    public void btn_addmoretime(View view) {
        ispressed=true;
        MoveToAddMoreTime();
    }
    public void MoveToAddMoreTime(){

        Intent intent= new Intent(onRoute.this,AddMoreTime.class);
        intent.putExtra("username",username);
        intent.putExtra("currentLocation",currentlocation);
        intent.putExtra("endLocation",endlocation);

        startActivity(intent);
        finish();

    }
    public void btn_EmergencyNumbers(View view) {
        Intent intent= new Intent(onRoute.this,Instructions.class);
        intent.putExtra("username",username);
        startActivity(intent);

    }



}