package com.example.honggliu.mapapp;

import android.content.Context;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DeviceItemAdapter extends ArrayAdapter<DeviceItem> {
    private int layoutId;
    public DeviceItemAdapter(Context context, int layoutId, List<DeviceItem> list){
        super(context,layoutId,list);
        this.layoutId=layoutId;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final DeviceItem deviceItem=getItem(position);
        View view=LayoutInflater.from(getContext()).inflate(layoutId,parent,false);
        TextView device_name=(TextView) view.findViewById(R.id.device_name);
        TextView device_power=(TextView)view.findViewById(R.id.device_power);
        TextView device_state=(TextView)view.findViewById(R.id.device_state);
        Button start=(Button)view.findViewById(R.id.start);
        Button stop=(Button)view.findViewById(R.id.stop);
        final Button offline=(Button)view.findViewById(R.id.offline);
        device_name.setText(deviceItem.getDevice_name());
        device_power.setText(String.valueOf(deviceItem.getDevice_power()));
        device_state.setText(String.valueOf(deviceItem.getDevice_state()));
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        HttpURLConnection connection=null;
                        BufferedReader reader=null;
                        try {
                            URL url=new URL("http://139.196.126.51/ding/audio/UserAPPInterfaces/devices_state.php" );
                            connection=(HttpURLConnection)url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                            connection.setDoOutput(true);
                            String data="deviceid="+deviceItem.getId()+"&state=2";
                            connection.getOutputStream().write(data.getBytes());
                            connection.connect();
                            InputStream in=connection.getInputStream();
                            reader=new BufferedReader(new InputStreamReader(in));
                            StringBuilder result=new StringBuilder();
                            String line;
                            while((line=reader.readLine())!=null){
                                result.append(line);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new Thread(new Runnable() {
                    public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try {
                    URL url=new URL("http://139.196.126.51/ding/audio/UserAPPInterfaces/devices_state.php" );
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    String data="deviceid="+deviceItem.getId()+"&state=1";
                    connection.getOutputStream().write(data.getBytes());
                    connection.connect();
                    InputStream in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder result=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        result.append(line);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
                }).start();
            }
        });
        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        HttpURLConnection connection=null;
                        BufferedReader reader=null;
                        try {
                            URL url=new URL("http://139.196.126.51/ding/audio/UserAPPInterfaces/devices_state.php" );
                            connection=(HttpURLConnection)url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                            connection.setDoOutput(true);
                            String data="deviceid="+deviceItem.getId()+"&state=0";
                            connection.getOutputStream().write(data.getBytes());
                            connection.connect();
                            InputStream in=connection.getInputStream();
                            reader=new BufferedReader(new InputStreamReader(in));
                            StringBuilder result=new StringBuilder();
                            String line;
                            while((line=reader.readLine())!=null){
                                result.append(line);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        return view;
    }

}
