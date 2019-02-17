<?php
    //return : JSON: every node position within 3 second.
    include_once("../connect.php");
    $query='select deviceid,gpspos1,gpspos2,gpspos3 from devicestate';
    $result=$db->query($query);
    $json=array();
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $json[]=$row;
        }
    }
    echo json_encode($json);
?>