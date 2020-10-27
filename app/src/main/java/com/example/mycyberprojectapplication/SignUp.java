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

public class SignUp extends AppCompatActivity {

    private EditText username, password, confirmpassword, emergencyphonenumber;
    String FinalUserName, FinalPassword, FinalEmergencyPhoneNumber, FinalConfirmPassword,message, data;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SetUIViews();
    }
    private void SetUIViews(){
        username= (EditText) findViewById(R.id.usernameid);
        password= (EditText) findViewById(R.id.passwordid);
        confirmpassword=(EditText) findViewById(R.id.confirmpasswordid);
        emergencyphonenumber= (EditText) findViewById(R.id.emergencyphonenumberid);

    }
    public void btn_LogIn(View view) {
        startActivity(new Intent(getApplicationContext(),LogIn.class));

    }

    public void btn_SendDetailsSignUp(View view) {
        FinalUserName= username.getText().toString();
        FinalPassword = password.getText().toString();
        FinalEmergencyPhoneNumber= emergencyphonenumber.getText().toString();
        FinalConfirmPassword = confirmpassword.getText().toString();
        if((FinalPassword.isEmpty()||FinalUserName.isEmpty())||(FinalEmergencyPhoneNumber.isEmpty()||FinalConfirmPassword.isEmpty())) {
            Toast.makeText(this, "please enter all the details", Toast.LENGTH_SHORT).show();
        }

        else{
            if(!FinalPassword.equals(FinalConfirmPassword)){
                Toast.makeText(this, "password and confirm password are different, please change it", Toast.LENGTH_SHORT).show();

            }
            else {
                if(FinalEmergencyPhoneNumber.length()!=10){
                    Toast.makeText(this, "emergency phone is not available", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(FinalUserName.length()>9)
                        Toast.makeText(this, "username is too long, please change it", Toast.LENGTH_SHORT).show();
                    else{
                        if(FinalPassword.length()>9)
                            Toast.makeText(this, "password is too long, please change it", Toast.LENGTH_SHORT).show();
                        else{
                            message = "su"+FinalUserName.length() +FinalUserName+FinalPassword.length()+FinalPassword+FinalEmergencyPhoneNumber;
                            SendToPython(message);
                            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

                        }
                    }

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