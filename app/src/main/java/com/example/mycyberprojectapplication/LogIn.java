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


public class LogIn extends AppCompatActivity {

    private EditText username, password;
    String FinalUserName, FinalPassword,data,message;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        SetUIViews();
    }
private void SetUIViews(){
        username= (EditText) findViewById(R.id.usernameid);
        password= (EditText) findViewById(R.id.passwordid);

}
    public void btn_SignUp(View view) {
        startActivity(new Intent(getApplicationContext(),SignUp.class));
    }

    public void btn_SendDetails(View view) {
        FinalUserName= username.getText().toString();
        FinalPassword = password.getText().toString();
        if(FinalPassword.isEmpty()||FinalUserName.isEmpty()) {
            Toast.makeText(this, "please enter all the details", Toast.LENGTH_SHORT).show();
        }
        else{
            if(FinalUserName.length()>9)
                Toast.makeText(this, "username is too long", Toast.LENGTH_SHORT).show();
            else{
                if(FinalPassword.length()>9)
                    Toast.makeText(this, "password is too long", Toast.LENGTH_SHORT).show();
                else{
                    message = "lg"+FinalUserName.length() +FinalUserName+FinalPassword.length()+FinalPassword;
                    SendToPython(message);
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
                }
            }


        }


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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}