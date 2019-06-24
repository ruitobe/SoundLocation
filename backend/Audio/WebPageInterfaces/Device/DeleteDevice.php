<?php
    include_once("../../connect.php");
    $id=$_POST['deviceid'];
    $query="DELETE FROM device where id=$id";
    $result=$db->query($query);
    $query="DELETE FROM devicestate where deviceid=$id";
    $result=$db->query($query);
    if($result){
        echo "true";
    }else{
        echo $query;
    }
?>
