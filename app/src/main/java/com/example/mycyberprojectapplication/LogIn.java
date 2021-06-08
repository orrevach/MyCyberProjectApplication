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
        //פעולה לבדיקת תקינות השדות ושמירתם
        FinalUserName= username.getText().toString();
        FinalPassword = password.getText().toString();

        if(FinalPassword.isEmpty()||FinalUserName.isEmpty()) {//בדיקה אם השדות ריקים
            Toast.makeText(this, "please enter all the details", Toast.LENGTH_SHORT).show();
        }
        else{
            //בדיקת תקינות השדות
            if(FinalUserName.length()>9)
                Toast.makeText(this, "username is too long", Toast.LENGTH_SHORT).show();
            else{
                if(FinalPassword.length()>9)
                    Toast.makeText(this, "password is too long", Toast.LENGTH_SHORT).show();
                else{
                    FinalPassword=Encryption(FinalPassword);//הצפנת הסיסמא

                    message = "lg"+FinalUserName.length() +FinalUserName+FinalPassword.length()+FinalPassword;//בניית הודעה לשליחה לשרת
                    SendToPython(message);
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
                    //עדכון המשתמש בהמשך לתגובת השרת להודעה
                    if(data.equals("username is not exist")){
                        Toast.makeText(this, "username is not exist", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(data.equals("username or password are wrong"))
                            Toast.makeText(this, "username or password are wrong", Toast.LENGTH_SHORT).show();
                        else{
                            //עדכון הנתונים בsharepreference
                            SharedPreferences sharedPreferences =getSharedPreferences(LogIn.PREFS_NAME,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("haslogin",true);
                            editor.putString("name",FinalUserName);
                            editor.commit();

                            //מעבר לעמוד הבית
                            Intent intent = new Intent(LogIn.this,HomePage.class);
                            intent.putExtra("username",FinalUserName);
                            startActivity(intent);
                        }


                    }

                }
            }


        }


    }
    public String Encryption(String pass)
    {
        //פעולת הצפנה
        String newpass="";
        byte[] bytes= pass.getBytes();//לא הכנסתי פרמטר ולכן הוא משתמש בפרמטר ברירת המחדל- משנה את הטיפוס של כל אחד מהערכים לבית ומjזיר בסוף מערך של בתים

        for( int i=0;i<bytes.length;i++){
            bytes[i]=(byte)(~bytes[i]);// על כל בית מבצע את הפעולה NOT
            newpass=newpass+(char)bytes[i];//ממיר את המספר החדש לchar ובונה סיסמא חדשה
        }
        return newpass;
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