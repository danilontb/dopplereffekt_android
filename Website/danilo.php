<?php

header('Content-type:application/json');


define('HOST','sql105.freehostingking.com');
define('USER','fking_16296513');
define('PASS','dopplereffekt');
define('DB','fking_16296513_dopplereffekt');
 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from user";
 
$res = mysqli_query($con,$sql);
 
$result = array();
 
while($row = mysqli_fetch_array($res)){
array_push($result,
array('id'=>$row[0],
'name'=>$row[1],
'email'=>$row[2]
));
}
 
echo json_encode(array("result"=>$result));
 
mysqli_close($con);
 
?>

