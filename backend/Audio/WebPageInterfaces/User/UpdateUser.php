<?php
    include_once("../../connect.php");
    $id=$_POST['userid'];
    $username=$_POST['username'];
    $password=$_POST['password'];
    $type=$_POST['type'];
    $query="UPDATE user set type=$type, username='$username', password='$password' where id=$id";
    $result=$db->query($query);
    if($result){
        echo 'True';
    }else{
        echo $query;
    }
?>
