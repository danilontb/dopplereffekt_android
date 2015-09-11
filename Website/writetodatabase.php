
<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

            
// check for required fields
/*
if (isset($_POST['longitude']) && isset($_POST['latitude'])&& isset($_POST['comment'])&& isset($_POST['time'])&& isset($_POST['date'])&& isset($_POST['eventoption'])){
    */
 
if($_POST)
{
    $eventoption    = $_POST['eventoption'];
    $longitude      = $_POST['longitude'];
    $latitude       = $_POST['latitude'];
    $comment        = $_POST['comment'];
    $time           = $_POST['time'];
    $date           = $_POST['date'];
    

     
    switch($eventoption){
        case "fixLighter":
            $databasename = "fixLighter";
        break;
         case "LighterMobile":
            $databasename = "radarfallen";
        break;
         case "laserLighter":
            $databasename = "laserLighter";
        break;
         case "controlPosition":
            $databasename = "controlePosition";
        break;
    }

        mysql_connect("localhost","stuxnet_doppler","tabasco");
        mysql_select_db("stuxnet_doppler");
  /*      $result=mysql_query("SELECT * FROM ".$databasename) or die(mysql_error());
  
  */
    
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO ".$databasename."(longitude, latitude, comment, time, date) VALUES('$longitude', '$latitude','$comment','$time', '$date')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";
 
         $response["longitude"] = $longitude;
    $response["latitude"] = $latitude;
    $response["time"] = $time;
    $response["comment"] = $comment;
    $response["date"] = $date;
    $response["eventoption"] = $eventoption;
        
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
         $response["longitude"] = $longitude;
    $response["latitude"] = $latitude;
    $response["time"] = $time;
    $response["comment"] = $comment;
    $response["date"] = $date;
    $response["eventoption"] = $eventoption;
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    $response["longitude"] = $longitude;
    $response["latitude"] = $latitude;
    $response["time"] = $time;
    $response["comment"] = $comment;
    $response["date"] = $date;
    $response["eventoption"] = $eventoption;
    
    // echoing JSON response
    echo json_encode($response);
    
}

?>