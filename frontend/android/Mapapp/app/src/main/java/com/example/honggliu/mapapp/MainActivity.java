package com.example.honggliu.mapapp;

import android.Manifest;
import android.bluetooth.BluetoothClass;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity {
    private List<DeviceItem> list=new ArrayList<>(); //store the device infomation
    public String user_name="";
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient locationClient;
    int flag=0;
    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};
    Timer timer2=new Timer();
    DeviceItemAdapter deviceItemAdapter;
    final TimerTask task2=new TimerTask(){
        @Override
        public void run(){
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            try{
                //Get the audios
                URL url=new URL("http://139.196.126.51/ding/audio/UserAPPInterfaces/audio_position.php");
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(in));
                StringBuilder result=new StringBuilder();
                String line;
                while((line=reader.readLine())!=null){
                    result.append(line);
                }
                JSONArray jsonArray=new JSONArray(result.toString());
                mBaiduMap.clear();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    double lat=jsonObject.getDouble("gpspos1");
                    double lot=jsonObject.getDouble("gpspos2");
                    int audio_id=jsonObject.getInt("audiodataid");
                    LatLng point=new LatLng(lat,lot);
                    OverlayOptions option1=new TextOptions().text(String.valueOf(audio_id)).bgColor(0xFF0000FF).fontSize(16).fontColor(0xFF000000).position(point);
                    mBaiduMap.addOverlay(option1);
                    if (flag == 0) { //设定地图中心点
                        LatLng GEO = new LatLng(lat, lot);
                        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(GEO);
                        mBaiduMap.setMapStatus(status1);
                        flag = 1;
                    }
                }

                //Get the devices
                url=new URL("http://139.196.126.51/ding/audio/UserAPPInterfaces/device_position.php");
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                in=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(in));
                result=new StringBuilder();
                while((line=reader.readLine())!=null){
                    result.append(line);
                }
                jsonArray=new JSONArray(result.toString());
                list.clear();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    int id=jsonObject.getInt("id");
                    double lat=jsonObject.getDouble("gpspos1");
                    double lot=jsonObject.getDouble("gpspos2");
                    String devicename=jsonObject.getString("devicename");
                    int power=jsonObject.getInt("power");
                    int state=jsonObject.getInt("state");
                    LatLng point=new LatLng(lat,lot);
                    if(state==1){
                        OverlayOptions option1=new TextOptions().text(String.valueOf(devicename)).bgColor(0xFFFF0000).fontSize(16).fontColor(0xFF000000).position(point);
                        mBaiduMap.addOverlay(option1);
                    }
                    else if(state==2){
                        OverlayOptions option1=new TextOptions().text(String.valueOf(devicename)).bgColor(0xFF00FF00).fontSize(16).fontColor(0xFF000000).position(point);
                        mBaiduMap.addOverlay(option1);
                    }
                    else{
                    }
                    if(state!=0&&power!=0){
                        if (flag == 0) { //设定地图中心点
                            LatLng GEO = new LatLng(lat, lot);
                            MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(GEO);
                            mBaiduMap.setMapStatus(status1);
                            flag = 1;
                        }
                    }
                    DeviceItem deviceItem=new DeviceItem(devicename,power,state,id);
                    list.add(deviceItem);
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceItemAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user_name=getIntent().getStringExtra("v_device_name");
        judgePermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        initLocationOption();
        setContentView(R.layout.activity_main);
        mMapView=findViewById(R.id.bmapView2);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        timer2.schedule(task2,1000,1000);
        TextView textView=findViewById(R.id.info2);
        textView.setText("User name:"+user_name+" type: Admin user");
        deviceItemAdapter=new DeviceItemAdapter(MainActivity.this,R.layout.device_item,list);
        ListView listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(deviceItemAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
    }
    protected void judgePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

            // sd卡权限
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
            }

            //手机状态权限
            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
            }

            //定位权限
            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, locationPermission, 300);
            }

            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
            }


            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
            }

            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
            }

            LocationManager Im=(LocationManager)this.getSystemService(this.LOCATION_SERVICE);
            boolean ok=Im.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(ok){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, LOCATIONGPS,100);
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }

        }else{
            //doSdCardResult();
        }
        //LocationClient.reStart();
    }
    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        //yLocationListener = new MyLocationListener();
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        locationOption.setIsNeedAltitude(true);
        locationClient.setLocOption(locationOption);
        //locationClient.registerLocationListener(myLocationListener);
        locationClient.start();
    }
}
