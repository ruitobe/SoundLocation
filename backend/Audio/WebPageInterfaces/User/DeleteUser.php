<?php
    include_once("../../connect.php");
    $id=$_POST['userid'];
    $query="delete from user where id=$id ";
    $result=$db->query($query);
    if($result){
        echo "true";
    }else{
        echo $query;
    }
?>
