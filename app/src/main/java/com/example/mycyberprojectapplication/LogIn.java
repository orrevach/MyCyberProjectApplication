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


public class LogIn extends AppCompatActivity {

    private EditText username, password;
    String FinalUserName, FinalPassword,data,message;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    public static String PREFS_NAME="name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        SetUIViews();
    }
private void SetUIViews(){
        username= (EditText) findViewById(R.id.newphonenumberid);
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
                    if(data.equals("username is not exist")){
                        Toast.makeText(this, "username is not exist", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(data.equals("username or password are false"))
                            Toast.makeText(this, "username or password are false", Toast.LENGTH_SHORT).show();
                        else{
                            SharedPreferences sharedPreferences =getSharedPreferences(LogIn.PREFS_NAME,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("haslogin",true);
                            editor.putString("name",FinalUserName);
                            editor.commit();
                            Intent intent = new Intent(LogIn.this,HomePage.class);
                            intent.putExtra("keyname", FinalUserName);
                            intent.putExtra("boolean", "true");

                            intent.putExtra("username",FinalUserName);
                            startActivity(intent);
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