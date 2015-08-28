
<?php
 //readfromdatabase.php
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();

 if (isset($_POST['databasename'])){
     

     
     $databasename    = $_POST['databasename'];
 mysql_connect("sql105.freehostingking.com","fking_16296513","dopplereffekt");
        mysql_select_db("fking_16296513_dopplereffekt");


// get all products from products table
$result = mysql_query("SELECT * FROM ".$databasename) or die(mysql_error());
 $anz = mysql_num_rows($result);
// check for empty result
 /*
    if($i !=3){ 
          $ausgabe = "\"".$databasename."\":[{\"anzahl".$databasename."\":\"".$anz."\"}],";
    }else{
     $ausgabe = "\"".$databasename."\":[{\"anzahl".$databasename."\":\"".$anz."\"}]}";
    }


echo $ausgabe;
*/
    
       $response[$databasename] = array();
 
   
        // temp user array
        $product = array();
        $product["anzahlLighter"]   = $anz;
      
        
        // push single product into final response array
        array_push($response[$databasename], $product);
echo json_encode($response);

 }
?>