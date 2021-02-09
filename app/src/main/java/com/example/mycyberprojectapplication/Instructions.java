package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Instructions extends AppCompatActivity {
    String username,phonenumber,message,data;
    private TextView phone;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username =getIntent().getStringExtra("username");
        setContentView(R.layout.activity_instructions);
        phone=findViewById(R.id.myphone);
        message = "pn"+username.length()+username;
        SendToPython(message);
        phonenumber = data;
        phone.setText(phonenumber+": your emergency phone number");
    }
    public void btn_MoveToHomePage(View view) {
        Intent intent= new Intent(Instructions.this,HomePage.class);
        intent.putExtra("username",username);
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

        String s="tel:"+phone;
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(s));
        Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        startActivity(intent);

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
}