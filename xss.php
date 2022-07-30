<html>
  <h2>Hello this code is vulnerable to XSS</h2>
  
  <form action="" method="GET">
    <input type="text" name="xss" value="<?php echo $_GET["testing"];?>">
  
  </html>





<?php

$username = "Utkarsh_testing";
$password = "Testing_Purpose_88_**";
  
  $newvar = $_GET["testing"];

echo $newvar;

?>
