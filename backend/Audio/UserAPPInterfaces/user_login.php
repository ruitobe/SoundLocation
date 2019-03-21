<?php
    //POST data:
    //user_name
    //password

    //return : josn : {id, type}
    include_once("../connect.php");
    $username=$_POST['user_name'];
    $password=$_POST['password'];
    $query='select id, type from user where username="'.$username.'" and password="'.$password.'"';
    $json=array();
    $result=$db->query($query);
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            $json=$row;
        }
    }
    echo json_encode($json);
?>