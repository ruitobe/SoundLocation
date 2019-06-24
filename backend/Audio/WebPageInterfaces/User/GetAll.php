<?php
    include_once("../../connect.php");
    $query='select id,username,password,type from user';
    $result=$db->query($query);
    $rows=array();
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $rows[]=$row;
        }
    }
    echo json_encode($rows);
?>
