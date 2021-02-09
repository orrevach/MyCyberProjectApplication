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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePhoneNumber.this);
        builder.setCancelable(false);
        builder.setTitle("Are You Sure You Want To Change The Emergency Phone Number?");
        builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                message ="cp"+username.length()+username+Finalnewphonenumber;
                SendToPython(message);
                phone.setText("your current phone number "+Finalnewphonenumber);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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


    public void btn_MoveToHomePage(View view) {
        Intent intent= new Intent(ChangePhoneNumber.this,HomePage.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }
}