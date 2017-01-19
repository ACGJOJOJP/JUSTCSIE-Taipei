<?php
	// 設定文件utf-8編碼
	header("Content-Type:text/html; charset=utf-8");
	// 建立MySQL資料庫連線
	$con=mysqli_connect("localhost","root","j29904345","test") or die("Error " . mysqli_error($con)); 
	 // 檢查連線態狀
	 if (mysqli_connect_errno()) {
	   echo "Failed to connect to MySQL: " . mysqli_connect_error();
	 }
	 // 設定MySQL為utf8編碼
	mysqli_query($con,"SET NAMES 'utf8'");
	
	$data['area'] = $_GET['area'];
	$data['type'] = $_GET['type'];

	// 查詢user_info資料表所有記錄
	 $sql = "SELECT * FROM `test` WHERE Area = '" . $data['area'] . "' AND Type = '" . $data['type'] . "'";

	 $result = mysqli_query($con,$sql) or die("Error in the consult.." . mysqli_error($con));
	while($row = $result->fetch_array(MYSQL_ASSOC)) {
            $myArray[] = $row;
    }
    echo json_encode($myArray);
?>