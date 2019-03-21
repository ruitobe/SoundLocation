package com.example.honggliu.userapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    URL url=new URL("http://139.196.126.51/ding/audio/AndroidInterfaces/device_login.php" );
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    String data="device_name="+device_name.getText()+"&password="+password.getText();
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
                    int id=Integer.parseInt(result.toString());
                    if(id!=0){ //Normal
                        Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                        intent.putExtra("v_id", id);
                        intent.putExtra("v_device_name", device_name.getText().toString());
                        startActivity(intent);
                    }
                    else{
                        message.setText("Device name or password wrong.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
