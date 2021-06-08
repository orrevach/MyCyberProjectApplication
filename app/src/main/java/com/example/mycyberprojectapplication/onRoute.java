package com.example.mycyberprojectapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
    private TextView phone;
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

        phone=findViewById(R.id.myphone);
        phone.setText(phonenumber+": your emergency phone number");
        startstop();
    }

    public void startstop() {
        //בודק האם הטיימר רץ
        if (timerrunning) {
            stoptimer();
        } else {
            starttimer();


        }

    }

    public void stoptimer() {
        countDownTimer.cancel();//מכבה את הטיימר
        timerrunning = false;

    }

    public void starttimer() {
        //פעולת ספירה לאחור

        countDownTimer = new CountDownTimer(TimeLeftInMillySecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillySecond = millisUntilFinished;//מעדכן את מספר המילי שניות עד לסוף הטיימר

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
        //הקפצת הודעה לבדיקת מצב המשתמש

        final AlertDialog.Builder builder = new AlertDialog.Builder(onRoute.this);//בניית הדיאלוג
        builder.setTitle("Have you reached to " + endlocation + " safetly?");
        builder.setCancelable(false);//לחיצה על המסך מחוץ לדיאלוג אינה סוגרת אותו
        builder.setItems(new CharSequence[]//בניית שלושת הכפתורים
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
                                SendToPython("nh" + username.length() + username);
                                SendHelpMessage();
                                Toast.makeText(onRoute.this, "help!!", Toast.LENGTH_SHORT).show();

                                break;

                        }
                    }
                });
        final AlertDialog dialog = builder.create();//יצירת הדיאלוג
        dialog.show();//הצגת הדיאלוג

        //טיימר לדיאלוג
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
                public void onFinish() {//בסיום הטיימר
                    Toast.makeText(onRoute.this, "AHAHHAHAHAHAHAHAH", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();//סגירת הדיאלוג
                    if (ispressed == false) {//אם לא בוצעו שינויים (כמו שינוי הזמן או הגעה)
                        SendHelpMessage();//שליחת הודעה לקריאת עזרה
                        MoveToHomePage();//מעבר לעמוד הבית
                    } else {
                        MoveToHomePage();
                    }


                }

            }.start();//התחלת הטיימר

        }
    }

    public void DeleteRoute() {
        //פעולה למחיקת מסלול במקרה של הגעה\ שינוי זמן
        SendToPython("dr" + username.length() + username);
    }

    public void MoveToHomePage() {
        //פעולה למעבר לעמוד הבית

        Intent intent = new Intent(onRoute.this, HomePage.class);
        intent.putExtra("username", username);//השמת פרמטרים שיועברו גם הם לעמוד הבית
        stoptimer();
        startActivity(intent);
        finish();//סגירת האקטיביטי
    }

    public void arrived() {
        DeleteRoute();//מחיקת המסלול
        final AlertDialog.Builder builder = new AlertDialog.Builder(onRoute.this);//בניית דיאלוג
        builder.setTitle(Html.fromHtml("<font color='#E91E63'>" + "Glad to hear you arrived safely, we are waiting for you're next route \n :)" + "</font>"));
        final AlertDialog dialog = builder.create();//יצירת הדיאלוג
        dialog.show();//הצגת הדיאלוג

        //פתיחת טיימר לזמן הדיאלוג
        if (timerrunning) {
            stoptimer();
        } else {
            TimeLeftInMillySecond = 5000;
            countDownTimer = new CountDownTimer(TimeLeftInMillySecond, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TimeLeftInMillySecond = millisUntilFinished;

                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                    MoveToHomePage();
                }

            }.start();//התחלת הטיימר


        }

    }

    public void btn_arrived(View view) {
        //פעולה למקרה של לחיצה על כפתור ההגעה
        ispressed = true;
        arrived();
    }

    public void SendHelpMessage() {
//בדיקת הרשאה לשליחת SMS
        int permissioncheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissioncheck == PackageManager.PERMISSION_GRANTED) {
            MyMessage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);

        }
    }

    private void MyMessage() {
        //שליחת הודעת SMS

        helpmessage="username "+username+" did not arrive while she was budgeting for herself from  "+currentlocation+" to "+endlocation+", please check if she is okay.";
        SmsManager smsManager=SmsManager.getDefault();//קבלת ניהול הודעות הtext של המכשיר
        smsManager.sendTextMessage(phonenumber,null,helpmessage,null,null);

        Toast.makeText(this,"message sent", Toast.LENGTH_SHORT).show();

    }



    public void btn_helpbutton(View view) {
        //פעולה לטיפול במקרה של לחיצה על כפתור העזרה
        ispressed=true;
        SendToPython("nh" + username.length() + username);
        SendHelpMessage();
        MoveToHomePage();
    }
    public void btn_addmoretime(View view) {
        //פעולה לטיפול במקרה של לחיצה על הוספת זמן
        ispressed=true;

        MoveToAddMoreTime();
    }
    public void MoveToAddMoreTime(){


        Intent intent= new Intent(onRoute.this,AddMoreTime.class);
        intent.putExtra("username",username);
        intent.putExtra("currentLocation",currentlocation);
        intent.putExtra("endLocation",endlocation);
        stoptimer();
        DeleteRoute();
        startActivity(intent);
        finish();

    }
    public void btn_Call1202(View view){
        Call("1202");

    }
    public void btn_Callmyphone(View view){
        Call(phonenumber);

    }
    public void btn_Call1203(View view){
        Call("1203");

    }
    public void btn_Call04_6566813(View view){
        Call("046566813");
    }
    public void btn_Call02_6730002(View view){
        Call("026730002");
    }
    public void btn_Call02_5328000(View view){
        Call("025328000");
    }
    public void btn_Call100(View view){
        Call("100");
    }

    public void Call(String phone)
    {
        //בדיקת הרשאות
        int permissioncheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permissioncheck == PackageManager.PERMISSION_GRANTED) {

            String s="tel:"+phone;
            Intent intent=new Intent(Intent.ACTION_CALL);//פתיחת intent למעבר לאפליקציית הטלפון
            intent.setData(Uri.parse(s));//העברת פרטי המידע (המספר אליו נתקשר)
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 0);

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