<?php
    include_once("../../connect.php");
    $query='select id,gpsts,gpspos1,gpspos2,gpspos3,radiostrength from radiodata';
    $result=$db->query($query);
    $rows=array();
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $rows[]=$row;
        }
    }
    echo json_encode($rows);
?>
