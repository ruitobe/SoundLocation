<?php
    include_once("../../connect.php");
    $query='select a.id as id, a.devicename as devicename, a.password as password, b.gpspos1 as gpspos1, b.gpspos2 as gpspos2, b.gpspos3 as gpspos3, b.power as power, b.state as state from device a left join devicestate b on a.id=b.deviceid';
    $result=$db->query($query);
    $rows=array();
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $rows[]=$row;
        }
    }
    echo json_encode($rows);
?>
