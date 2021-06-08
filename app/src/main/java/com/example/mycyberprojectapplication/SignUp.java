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

        //בדיקת תקינות לשדות
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

                                if (FinalEmail.length() > 99){
                                    Toast.makeText(this, "email is too long, please change it", Toast.LENGTH_SHORT).show();}
                                else {
                                    if(FinalEmail.length()<10)
                                        emaillength="0"+ FinalEmail.length();
                                    else {
                                        if (FinalEmail.length() > 9 && FinalEmail.length() < 100)
                                            emaillength = "" + FinalEmail.length();
                                         }

                                    FinalPassword=Encryption(FinalPassword);
                                    //בניית הודעה לשליחה לשרת
                                    message = "su" + FinalUserName.length() + FinalUserName + FinalPassword.length() + FinalPassword + FinalEmergencyPhoneNumber + emaillength + FinalEmail;
                                    SendToPython(message);

                                    //פעולה בהתאם לתגובת השרת
                                    if (data.equals("username already exists, try again ")) {
                                        Toast.makeText(this, "username already exists, try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(data.equals("the mail is wrong")){
                                            Toast.makeText(this, "the email is wrong", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            if(data.equals("something got wrong"))
                                                Toast.makeText(this, "something got wrong", Toast.LENGTH_SHORT).show();
                                            else{
                                                Toast.makeText(this, "sign up successfully", Toast.LENGTH_SHORT).show();
                                                //עדכון הנתונים בsharepreference
                                                SharedPreferences sharedPreferences =getSharedPreferences(LogIn.PREFS_NAME,0);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("haslogin",true);
                                                editor.putString("name",FinalUserName);
                                                editor.commit();
                                                //מעבר לעמוד הבית
                                                Intent intent = new Intent(SignUp.this,HomePage.class);
                                                intent.putExtra("username", FinalUserName);
                                                startActivity(intent);
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


    public String Encryption(String pass)
    {
        //פעולת הצפנה
        String newpass="";
        byte[] bytes= pass.getBytes();//לא הכנסתי פרמטר ולכן הוא משתמש בפרמטר ברירת המחדל- משנה את הטיפוס של כל אחד מהערכים לבית ומחזיר בסוף מערך של בתים

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