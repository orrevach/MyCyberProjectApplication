package com.example.mycyberprojectapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChangePhoneNumber extends AppCompatActivity {
    private EditText newphonenumber;
    String message,phonenumber,username,data,Finalnewphonenumber;
    private TextView phone;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number);

        username =getIntent().getStringExtra("username");
        phone=findViewById(R.id.currentphone);
        message = "pn"+username.length()+username;
        SendToPython(message);
        phonenumber = data;
        phone.setText("your current phone number "+phonenumber);
        data="your phone did not change";
        SetUIViews();
    }
    private void SetUIViews(){
        newphonenumber= (EditText) findViewById(R.id.newphonenumberid);
    }
    public void btn_SendDetailsNewPhone(View view) {
        //פעולה לבדיקת תקינות השדה
        Finalnewphonenumber=newphonenumber.getText().toString();
        if(Finalnewphonenumber.isEmpty())
        {
            Toast.makeText(this, "please enter new emergency phone number", Toast.LENGTH_SHORT).show();
        }
        else{
            if(Finalnewphonenumber.length()!=10){
                Toast.makeText(this, "emergency phone is not available", Toast.LENGTH_SHORT).show();
            }
            else{
                ChangeMessage();


            }

        }

    }
    public void ChangeMessage(){
        //פעולה להקפצת הודעה לאישור השינויים
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePhoneNumber.this);//בניית הדיאלוג
        builder.setCancelable(false);//לחיצה על המסך מחוץ לדיאלוג אינה סוגרת אותו
        builder.setTitle("Are You Sure You Want To Change The Emergency Phone Number?");//בניית הכפתור הראשון
        builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //בניית הודעה לשליחה לשרת
                //בניית הודעה לשליחה לשרת
                message ="cp"+username.length()+username+Finalnewphonenumber;
                SendToPython(message);
                phone.setText("your current phone number "+Finalnewphonenumber);//עדכון המייל החדש
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//בניית הכפתור השני
                dialog.cancel();//סגירת הדיאלוג
            }
        });
        builder.show();//הצגת הדיאלוג
    }


    public void btn_MoveToHomePage(View view) {
        //פעולה למעבר לעמוד הבית
        Intent intent= new Intent(ChangePhoneNumber.this,HomePage.class);
        intent.putExtra("username",username);//השמת פרמטרים שיועברו גם הם לעמוד הבית
        startActivity(intent);
        finish();//סגירת האקטיביטי
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