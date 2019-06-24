<?php
    include_once("../../connect.php");
    $devicename=$_POST['devicename'];
    $password=$_POST['password'];
    $state=$_POST['state'];
    $query="INSERT INTO device(devicename, password) values ('$devicename', '$password')";
    $result=$db->query($query);
    $query="SELECT id from device where devicename='$devicename' and password='$password'";
    $result=$db->query($query);
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $id=$row['id'];
        }
    }
    $query="INSERT INTO devicestate(deviceid,gpspos1,gpspos2,gpspos3,power,state) values ($id,0,0,0,0,$state)";
    $result=$db->query($query);
    if($result){
        echo "true";
    }else{
        echo "false";
    }
?>
