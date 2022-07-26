<html>
  <h2>Hello this code is vulnerable to XSS</h2>
  
  <form action="" method="GET">
    <input type="text" name="xss" value="<?php echo htmlspecialchars($_GET["testing"]);?>">
  
  </html>



<?php
  
  $newvar = htmlspecialchars($_GET["testing"]);

echo $newvar;

?>
