<?php
    include_once("../connect.php");
    if ('get' == $_SERVER['REQUEST_METHOD']) {
        $query='select deviceid,power,state from devicestate';
        $result=$db->query($query);
        $json=array();
        if($result->num_rows>0){
         while($row=$result->fetch_array()){
                $json[]=$row;
            }
        }
        echo json_encode($json);
    }
    //input: json {device_id , state}
    //return : true/false
    else if ('put' == $_SERVER['REQUEST_METHOD']) {
        $_PUT = array();
        parse_str(file_get_contents('php://input'), $_PUT);
        $device_id=$_PUT['device_id'];
        $state=$_PUT['state'];
        $query="update devicestate set state=$state where device_id=$device_id";
        $result=$db->query($query);
        if($result){
            echo "true";
        }
        else{
            echo "false";
        }
    }
?>