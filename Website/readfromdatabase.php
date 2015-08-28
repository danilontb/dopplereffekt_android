
<?php
 //readfromdatabase.php
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();

if (isset($_POST['databasename'])){
 
    $databasename = $_POST['databasename'];
    
 mysql_connect("sql105.freehostingking.com","fking_16296513","dopplereffekt");
        mysql_select_db("fking_16296513_dopplereffekt");



// get all products from products table
$result = mysql_query("SELECT * FROM ".$databasename) or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response[$databasename] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
        $product["longitude"]   = $row["longitude"];
        $product["latitude"]    = $row["latitude"];
        $product["comment"]     = $row["comment"];
        $product["day"]         = $row["day"];
        $product["month"]       = $row["month"];
        $product["hour"]        = $row["hour"];
        $product["minutes"]     = $row["minutes"];
        
        // push single product into final response array
        array_push($response[$databasename], $product);
    }
    // success
    $response["success"] = 1;
    $response["anzLighter"] = mysql_num_rows($result);
    
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No products found";
 
    // echo no users JSON
    echo json_encode($response);
}
}

?>