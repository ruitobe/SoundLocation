<?php
    include_once("../../connect.php");
    $username=$_POST['user_name'];
    $password=$_POST['password'];
    $query='select * from user where username="'.$username.'" and password="'.$password.'"';
    $result=$db->query($query);
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            echo $row['id'];
        }
    }
?>
