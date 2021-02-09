package com.example.mycyberprojectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private EditText username, password, confirmpassword, emergencyphonenumber,email;
    String FinalUserName, FinalPassword, FinalEmergencyPhoneNumber, FinalConfirmPassword,message, data,FinalEmail,emaillength;

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
        username= (EditText) findViewById(R.id.newphonenumberid);
        password= (EditText) findViewById(R.id.passwordid);
        confirmpassword=(EditText) findViewById(R.id.confirmpasswordid);
        emergencyphonenumber= (EditText) findViewById(R.id.emergencyphonenumberid);
        email= (EditText) findViewById(R.id.emailid);

    }
    public void btn_LogIn(View view) {
        startActivity(new Intent(getApplicationContext(),LogIn.class));

    }

    public void btn_SendDetailsSignUp(View view) {
        FinalUserName= username.getText().toString();
        FinalPassword = password.getText().toString();
        FinalEmail = email.getText().toString();
        FinalEmergencyPhoneNumber= emergencyphonenumber.getText().toString();
        FinalConfirmPassword = confirmpassword.getText().toString();
        SharedPreferences sharedPreferences =getSharedPreferences(LogIn.PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("haslogin",true);
        editor.putString("name",FinalUserName);
        editor.commit();
        Intent intent = new Intent(SignUp.this,HomePage.class);
        intent.putExtra("keyname", FinalUserName);
        intent.putExtra("boolean", "true");
        if(((FinalPassword.isEmpty()||FinalUserName.isEmpty())||(FinalEmergencyPhoneNumber.isEmpty()||FinalConfirmPassword.isEmpty())||FinalEmail.isEmpty())) {
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
                            if(FinalEmail.length()<10)
                                emaillength="0"+ FinalEmail.length();
                            else {
                                if (FinalEmail.length() > 9 && FinalEmail.length() < 100)
                                    emaillength = "" + FinalEmail.length();

                                if (FinalEmail.length() > 99)
                                    Toast.makeText(this, "email is too long, please change it", Toast.LENGTH_SHORT).show();
                                else {
                                    message = "su" + FinalUserName.length() + FinalUserName + FinalPassword.length() + FinalPassword + FinalEmergencyPhoneNumber + emaillength + FinalEmail;
                                    SendToPython(message);
                                    if (data.equals("user already exists, try again ")) {
                                        Toast.makeText(this, "user already exists, try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(data.equals("the mail is wrong")){
                                            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();}
                                        else{
                                            Intent intent1 = new Intent(SignUp.this, HomePage.class);
                                            intent1.putExtra("username", FinalUserName);
                                            startActivity(intent1);
                                            finish();
                                        }

                                    }
                                }
                            }
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
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}