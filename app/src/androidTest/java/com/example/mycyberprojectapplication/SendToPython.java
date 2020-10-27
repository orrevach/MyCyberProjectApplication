package com.example.mycyberprojectapplication;

import android.os.AsyncTask;
import android.os.StrictMode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class SendToPython extends AsyncTask<String , Void , Void>
{
    Socket socket;
    DataOutputStream dos;

    @Override
    protected Void doInBackground(String... voids) {
        String message = voids[0];
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            socket = new Socket("10.0.2.2", 7800);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBytes((message));


        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
