<?php
    //POST data:
    //device_id
    //gpspos1
    //gpspos2
    //gpspos3
    //gpsts
    //frequency
    //samplenumber
    //file

    //okreturn : string:true
    include_once("../connect.php");
    $device_id=$_POST['device_id'];
    $gpspos1=$_POST['gpspos1'];
    $gpspos2=$_POST['gpspos2'];
    $gpspos3=$_POST['gpspos3'];
    $gpsts=$_POST['gpsts'];
    $systemts=$_POST['systemts'];
    $frequency=$_POST['frequency'];
    $samplenumber=$_POST['samplenumber'];
    date_default_timezone_set("Asia/Shanghai");
    $filepath = "../audiofile/".date("YmdHis").$_FILES["file"]["name"]; 
    move_uploaded_file($_FILES["file"]["tmp_name"],$filepath);          
    

    $query="insert into devicedata(deviceid,gpspos1,gpspos2,gpspos3,gpsts,frequency,samplenumber,path,systemts) values ($device_id , $gpspos1 , $gpspos2 , $gpspos3 , $gpsts , $frequency , $samplenumber , '$filepath',$systemts)" ;
    $result=$db->query($query);
    if($result){
        echo "true";
    }   
    else{
        echo $query;
    }
?>