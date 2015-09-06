
<?php

 mysql_connect("sql105.freehostingking.com","fking_16296513","dopplereffekt") or  die(mysql_error());
 mysql_select_db("fking_16296513_dopplereffekt");

$sql = mysql_query("select * from user");




    $output["user"] = array();
    
while($row = mysql_fetch_assoc($sql)){
   
    $product = array();
    
    $product["id"]   = $row["id"];
     $product["name"]   = $row["name"];
     $product["email"]   = $row["email"];
     $product["password"]   = $row["password"];
   
    
    array_push($output["user"], $product);
}

echo json_encode($output);


mysql_close();


?>