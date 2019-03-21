<?php
    //return : JSON: every node position within 3 second.
    include_once("../connect.php");
    //$query='select audiodataid,gpspos1,gpspos2,gpspos3 from audioposition where CURRENT_TIMESTAMP-ts<181';
    $query='select a.audiodataid,gpspos1,gpspos2,gpspos3 from audioposition a RIGHT JOIN (select audiodataid,MAX(ts) as ts from audioposition where CURRENT_TIMESTAMP-ts<181 GROUP BY audiodataid) tmp ON a.ts=tmp.ts and a.audiodataid=tmp.audiodataid ';
    $result=$db->query($query);
    $json=array();
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $json[]=$row;
        }
    }
    echo json_encode($json);
?>