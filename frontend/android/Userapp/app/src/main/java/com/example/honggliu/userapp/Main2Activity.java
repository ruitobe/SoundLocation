package com.example.honggliu.userapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
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
import com.baidu.mapapi.model.LatLng;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
public class Main2Activity extends AppCompatActivity {
    public int id;
    public String device_name="";
    public int delaytime; //recording time
    public int periodtime;//task1 periodtime
    public int state;
    public int frequency=96000;
    public int sample=44100;
    public BatteryManager batteryManager;
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient locationClient;
    MyLocationListener myLocationListener;
    int flag = 0;
    private int sending = 0;
    public double latitude;
    public double longitude;
    public double altitude;
    public long time;// system time
    //public long time2;//gps time
    public long responsetime;
    //public long timegps;
    public long timeinterval=0;
    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};
    Timer timer=null;//new Timer();
    Timer timer2=new Timer();
    TimerTask task=null;
    //public final static ScheduledThreadPoolExecutor schedual=new ScheduledThreadPoolExecutor(3);
    //public final static ScheduledThreadPoolExecutor schedual1=new ScheduledThreadPoolExecutor(1);
    int flag2=0; //To tell if the timeinterval is first run.

    final TimerTask task2=new TimerTask(){
        @Override
        public void run(){
            SntpClient sntpClient = new SntpClient();
            if(sntpClient.requestTime("1.cn.pool.ntp.org",30000)){
                long time3=sntpClient.getNtpTime();
                long time4=System.currentTimeMillis();
                timeinterval=time3-time4;
            }
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            try{
                //Get the state
                URL url=new URL("http://139.196.126.51/ding/audio/AndroidInterfaces/device_state.php?device_id="+id );
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in=connection.getInputStream();
                /*if(flag2==0) {
                    String responsedate = connection.getHeaderField("Date");
                    if (!TextUtils.isEmpty((responsedate))) {
                        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",
                                Locale.ENGLISH);
                        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                        try {
                            Date serverDate = simpleDateFormat.parse(responsedate);
                            responsetime = serverDate.getTime();
                            timeinterval=responsetime-System.currentTimeMillis();;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                //    flag2=1;
               // }
               */
                reader=new BufferedReader(new InputStreamReader(in));
                StringBuilder result=new StringBuilder();
                String line;
                while((line=reader.readLine())!=null){
                    result.append(line);
                }
                JSONObject jsonObject=new JSONObject(result.toString());
                state=jsonObject.optInt("state");
                //Control Task1
                if(state==0) { //Offline
                    state=1;
                    sending=0;
                    task.cancel();
                    task=null;
                    timer.cancel();
                    //schedual.shutdown();
                    timer=null;
                }
                else if(state==1){ //Turn Off
                    sending=0;
                    task.cancel();
                    task=null;
                    timer.cancel();
                    timer=null;
                    //schedual.shutdown();
                }
                else if(state==2){ //Turn on
                    if(sending==0){
                        task=new TimerTask(){
                        @Override
                        public void run(){
                            sending=1;
                            String filename=String.valueOf(System.currentTimeMillis()+timeinterval)+device_name;
                            String recordfilename=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+filename;
                            recordfilename+=".mp4";
                            MediaRecorder recorder=new MediaRecorder();
                            recorder.setAudioSamplingRate(sample);
                            recorder.setAudioEncodingBitRate(frequency);
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                            recorder.setOutputFile(recordfilename);
                            File file=new File(recordfilename);
                            try {
                                recorder.prepare();
                                //time=System.currentTimeMillis();
                                time=System.currentTimeMillis()+timeinterval;
                                //timegps=time2;
                                recorder.start();
                                Thread.sleep(delaytime);
                                recorder.stop();
                                recorder.release();
                                HttpURLConnection connection=null;
                                BufferedReader reader=null;
                                URL url=new URL("http://139.196.126.51/ding/audio/AndroidInterfaces/device_state.php" );
                                connection=(HttpURLConnection)url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                connection.setDoOutput(true);
                                connection.setReadTimeout(1);
                                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                                String data="device_id="+id+"&power="+battery+"&gpspos1="+latitude+"&gpspos2="+longitude+"&gpspos3="+altitude+"&state="+state;
                                connection.getOutputStream().write(data.getBytes());
                                connection.connect();
                                try{ InputStream in=connection.getInputStream();} 
                                catch(Exception e){
                                }
                                url=new URL("http://139.196.126.51/ding/audio/AndroidInterfaces/device_data.php");
                                String boundary= UUID.randomUUID().toString();
                                connection=(HttpURLConnection)url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setDoOutput(true);
                                connection.setReadTimeout(1);
                                connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
                                DataOutputStream request=new DataOutputStream(connection.getOutputStream());
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"device_id\"\r\n\r\n");
                                request.writeBytes(id+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"gpspos1\"\r\n\r\n");
                                request.writeBytes(latitude+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"gpspos2\"\r\n\r\n");
                                request.writeBytes(longitude+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"gpspos3\"\r\n\r\n");
                                request.writeBytes(altitude+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"systemts\"\r\n\r\n");
                                request.writeBytes(time+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"gpsts\"\r\n\r\n");
                                request.writeBytes(timeinterval+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"frequency\"\r\n\r\n");
                                request.writeBytes(frequency+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"samplenumber\"\r\n\r\n");
                                request.writeBytes(sample/10*9+"\r\n");
                                request.writeBytes("--"+boundary+"\r\n");
                                request.writeBytes("Content-Disposition:form-data;name=\"file\";filename=\""+file.getName()+"\"\r\n\r\n");
                                request.write(FileUtils.readFileToByteArray(file));
                                request.writeBytes("\r\n");
                                request.writeBytes("--"+boundary+"--\r\n");
                                request.flush();

                                try{ InputStream in=connection.getInputStream();} 
                                catch(Exception e){
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    };

                        timer=new Timer();
                        long target=((System.currentTimeMillis()+timeinterval)/1000+2)*1000;
                        long delay=target-System.currentTimeMillis()-timeinterval;
                        timer.schedule(task,delay,periodtime);
                        //schedual.scheduleAtFixedRate(task,delay,periodtime, TimeUnit.MILLISECONDS);
                        sending=1;
                    }
                }
                View view=getWindow().getDecorView();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView=findViewById(R.id.info);
                        long time_now=System.currentTimeMillis()+timeinterval;
                        textView.setText("Name:"+device_name+" state:"+state+" lat:"+latitude+" lot:"+longitude+" alt:"+altitude+" alter:"+timeinterval+" systs:"+time_now);
                    }
                });
               //Refresh power and GPS info
                url=new URL("http://139.196.126.51/ding/audio/AndroidInterfaces/device_state.php" );
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                String data="device_id="+id+"&power="+battery+"&gpspos1="+latitude+"&gpspos2="+longitude+"&gpspos3="+altitude+"&state="+state;
                connection.getOutputStream().write(data.getBytes());
                connection.connect();
                in=connection.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initLocationOption();
        mMapView=findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        id=getIntent().getIntExtra("v_id",0);
        device_name=getIntent().getStringExtra("v_device_name");
        delaytime=getIntent().getIntExtra("v_delaytime",600);
        periodtime=getIntent().getIntExtra("v_periodtime",1000);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        judgePermission();
        batteryManager=(BatteryManager)getSystemService(BATTERY_SERVICE);
        //This is the Android Studio Inside GPS method. Not using for now.
        /*
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider;
        Criteria criteria =new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
        criteria.setAltitudeRequired(true);// 不要求海拔
        criteria.setBearingRequired(true);// 不要求方位
        criteria.setCostAllowed(true);// 允许有花
        criteria.setSpeedRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = lm.getBestProvider(criteria, true);
        LocationListener ll = new mysecondlocationlistener();
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        lm.requestLocationUpdates(provider, 0, 0, ll);*/
        //This is Baidu Map
        mBaiduMap.setMyLocationEnabled(true);
        timer2.schedule(task2,5000,10000);
        //schedual1.scheduleAtFixedRate(task2,5000,10000, TimeUnit.MILLISECONDS);
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
        task.cancel();
        //schedual1.shutdown();
        timer.cancel();
        task2.cancel();
        //schedual.shutdown();
        timer2.cancel();
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


    //This one From Baidu
    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        myLocationListener = new MyLocationListener();
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        //locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        //locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        //locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        //locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        //locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        //locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(true);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        //locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//开始定位
        locationClient.setLocOption(locationOption);
        locationClient.registerLocationListener(myLocationListener);
        locationClient.start();
    }
    //This one From Baidu
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取纬度信息
            latitude = location.getLatitude();
            if(latitude<0.000001) latitude=0.0;
            //获取经度信息
            longitude = location.getLongitude();
            if(longitude<0.000001) longitude=0.0;
            //获取定位精度，默认值为0.0f
            altitude = location.getAltitude();
            if(altitude<0.000001) altitude=0.0;
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (flag == 0) { //设定地图中心点
                LatLng GEO = new LatLng(latitude, longitude);
                MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(GEO);
                mBaiduMap.setMapStatus(status1);
                flag = 1;
            }
        }
    }
    /*
    private class mysecondlocationlistener implements  LocationListener{
        public void onLocationChanged(Location location){
            //location.setTime(System.currentTimeMillis());
            time2=location.getTime();
        }
        public void onStatusChanged(String provider, int status, Bundle extras){

        }
        public void onProviderDisabled(String provider){

        }
        public void onProviderEnabled(String provider){

        }
    }
    */
}

