<?php
    include_once("../../connect.php");
    $username=$_POST['username'];
    $password=$_POST['password'];
    $type=$_POST['type'];
    $query="INSERT INTO user(username, password, type) VALUES ( '$username' , '$password' , $type)";
    $result=$db->query($query);
    if($result){
        echo "success";
    }
    else{
        echo $query;
    }
?>
