<?php
    //POST data:
    //device_name
    //password

    //return : device_id
    include_once("../connect.php");
    $device_name=$_POST['device_name'];
    $password=$_POST['password'];
    $query='select * from device where devicename="'.$device_name.'" and password="'.$password.'"';
    $result=$db->query($query);
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            echo $row['id'];
            return;
        }
    }
    else
        echo 0;
?>
