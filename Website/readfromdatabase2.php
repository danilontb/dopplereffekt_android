<?php

header('Content-type: application/json');

 mysql_connect("sql105.freehostingking.com","fking_16296513","dopplereffekt");
mysql_select_db("fking_16296513_dopplereffekt");
 
$q=mysql_query("SELECT * FROM user ORDER BY id");



while($e=mysql_fetch_assoc($q)){
    
 $output[]=$e;
    
}
 
mysql_close();
print(json_encode($output));
 


?>