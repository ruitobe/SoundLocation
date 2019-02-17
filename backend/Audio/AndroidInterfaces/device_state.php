<?php
    //POST data:
    //device_id
    //gpspos1
    //gpspos2
    //gpspos3
    //power
    //state

    //ok return : string:true
    include_once("../connect.php");
    $device_id=$_POST['device_id'];
    $gpspos1=$_POST['gpspos1'];
    $gpspos2=$_POST['gpspos2'];
    $gpspos3=$_POST['gpspos3'];
    $power=$_POST['power'];
    $state=$_POST['state'];
    

    $query='update devicestate set gpspos1='.$gpspos1.',gpspos2='.$gpspos2.', gpspos3='.$gpspos3.',power='.$power.',state='.$state.' where deviceid='.$device_id;
    $result=$db->query($query);
    if($result){
        echo "true";
    }    
    else{
        echo $query;
    }
?>
