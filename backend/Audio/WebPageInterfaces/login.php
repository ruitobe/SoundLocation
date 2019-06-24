<?php
    include_once("../connect.php");
    $username=$_POST['username'];
    $password=$_POST['password'];
    $query='select * from user where username="'.$username.'" and password="'.$password.'"';
    $result=$db->query($query);
    if($result->num_rows>0){
        while($row=$result->fetch_array()){
            if($row['type']==2){
                $url = "Admin.html";
            }else if($row['type']==1) {
                $url="Normal.html";
            }else{
                echo("Type is wrong, please contant database manager.");
            }
        }
    }
    else{
        echo ("User name of password wrong");
        return;
    }
?>
<!DOCTYPE html>
<html>
<head>	
</head>
<body>
    <script type='text/javascript'>
        window.location.href='<?php echo $url?>';
    </script>
</body>