writetodatabase.php
<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['longitude']) && isset($_POST['latitude'])&& isset($_POST['comment'])&& isset($_POST['day'])&& isset($_POST['month'])&& isset($_POST['hour'])&& isset($_POST['minutes'])&& isset($_POST['eventoption'])){
 
    $eventoption    = $_POST['eventoption'];
    $longitude      = $_POST['longitude'];
    $latitude       = $_POST['latitude'];
    $comment        = $_POST['comment'];
    $day            = $_POST['day'];
    $month          = $_POST['month'];
    $hour           = $_POST['hour'];
    $minutes        = $_POST['minutes'];
    
    $databasename   = "noname"; 
    
    switch($eventoption){
        case "fixlighter":
            $databasename = "fixLighter";
        break;
         case "mobilelighter":
            $databasename = "mobileLighter";
        break;
         case "laserlighter":
            $databasename = "laserLighter";
        break;
         case "controlposition":
            $databasename = "controlePosition";
        break;
    }
   
    
        mysql_connect("sql105.freehostingking.com","fking_16296513","dopplereffekt");
        mysql_select_db("fking_16296513_dopplereffekt");
        $result=mysql_query("SELECT * FROM ".$databasename) or die(mysql_error());
    
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO ".$databasename."(longitude, latitude, comment, day, month, hour, minutes) VALUES('$longitude', '$latitude','$comment','$day', '$month','$hour','$minutes')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>