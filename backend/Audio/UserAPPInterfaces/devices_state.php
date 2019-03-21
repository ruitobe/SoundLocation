<?php
    include_once("../connect.php");
    //input: json {device_name , state}
    //return : true/false
    $deviceid=$_POST['deviceid'];
    $state=$_POST['state'];
    $query="UPDATE devicestate set state=$state where deviceid=$deviceid";
    $result=$db->query($query);
    if($result){
        echo "true";
    }else{
        echo $query;
    }
?>