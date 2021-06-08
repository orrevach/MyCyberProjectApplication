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

public class ChangeEmail extends AppCompatActivity {
    private EditText newemail;
    String message,email,username,data,FinalEmail;
    private TextView currentemail;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        username =getIntent().getStringExtra("username");
        currentemail=findViewById(R.id.currentemail);
        message = "em"+username.length()+username;
        SendToPython(message);
        email = data;
        currentemail.setText("your current email "+email);
        data="your email did not change";
        SetUIViews();
    }
    private void SetUIViews(){
        newemail= (EditText) findViewById(R.id.newemail);
    }
    public void btn_SendDetailsNewEmail(View view) {
        //פעולה לבדיקת תקינות השדה ועדכון המייל
        FinalEmail=newemail.getText().toString();
        if(FinalEmail.isEmpty())
        {
            Toast.makeText(this, "please enter new emergency email ", Toast.LENGTH_SHORT).show();
        }
        else{
            //בדיקת תקינות בסיסית למייל (בדיקה נוספת מתבצעת בשרת)
            if(FinalEmail.length()>99)
                Toast.makeText(this, "the mail is too long ", Toast.LENGTH_SHORT).show();
            else
                ChangeMessage();
            if(data.equals("the mail is wrong")){
                Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            }

        }

    }
    public void ChangeMessage(){
        //פעולה להקפצת הודעה לאישור השינויים
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmail.this);//בניית הדיאלוג
        builder.setCancelable(false);//לחיצה על המסך מחוץ לדיאלוג אינה סוגרת אותו
        builder.setTitle("Are You Sure You Want To Change The Emergency Email?");
        builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//בניית הכפתור הראשון
                //בניית הודעה לשליחה לשרת
                message ="ce"+username.length()+username;
                if(FinalEmail.length()<10)
                    message+="0"+FinalEmail.length()+FinalEmail;
                else{
                    message+=FinalEmail.length()+FinalEmail;
                }

                SendToPython(message);
                currentemail.setText("your current email "+FinalEmail);//עדכון המייל החדש
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
        Intent intent= new Intent(ChangeEmail.this,HomePage.class);
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