<?php
    include_once("../../connect.php");
    $id=$_POST['deviceid'];
    $devicename=$_POST['devicename'];
    $password=$_POST['password'];
    $state=$_POST['state'];
    $query="UPDATE device set devicename='$devicename', password='$password' where id=$id";
    $result=$db->query($query);
    $query="UPDATE devicestate set state=$state where deviceid=$id";
    $result=$db->query($query);
    if($result){
        echo "true";
    }else{
        echo "false";
    }
?>
