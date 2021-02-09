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
        FinalEmail=newemail.getText().toString();
        if(FinalEmail.isEmpty())
        {
            Toast.makeText(this, "please enter new emergency email ", Toast.LENGTH_SHORT).show();
        }
        else{
            if(FinalEmail.length()>99)
                Toast.makeText(this, "email is too long ", Toast.LENGTH_SHORT).show();
            else
                ChangeMessage();
            if(data.equals("the mail is wrong")){
                Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            }

        }

    }
    public void ChangeMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmail.this);
        builder.setCancelable(false);
        builder.setTitle("Are You Sure You Want To Change The Emergency Email?");
        builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                message ="ce"+username.length()+username;
                if(FinalEmail.length()<10)
                    message+="0"+FinalEmail.length()+FinalEmail;
                else{
                    message+=FinalEmail.length()+FinalEmail;
                }

                SendToPython(message);
                currentemail.setText("your current email "+FinalEmail);
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
        Intent intent= new Intent(ChangeEmail.this,HomePage.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }
}