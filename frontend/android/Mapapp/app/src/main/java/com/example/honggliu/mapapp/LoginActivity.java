package com.example.honggliu.mapapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        new Thread(new Runnable(){
            @Override
            public void run(){
                EditText device_name=findViewById(R.id.user_name);
                EditText password=findViewById(R.id.password);
                TextView message=findViewById(R.id.message);
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try{
                    URL url=new URL("http://139.196.126.51/ding/audio/UserAPPInterfaces/user_login.php" );
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    String data="user_name="+device_name.getText()+"&password="+password.getText();
                    connection.getOutputStream().write(data.getBytes());
                    connection.connect();
                    int code= connection.getResponseCode();//not use here
                    InputStream in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder result=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        result.append(line);
                    }
                    JSONObject obj=new JSONObject(result.toString());
                    int type=obj.getInt("type");

                    if(type==1){//Admin
                        Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                        intent.putExtra("v_device_name", device_name.getText().toString());
                        startActivity(intent);
                    }
                    else if(type==2){//Normal
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("v_device_name", device_name.getText().toString());
                        startActivity(intent);
                    }
                    message.setText(obj.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    message.setText("User name or password wrong!");
                }
            }
        }).start();

    }
}
