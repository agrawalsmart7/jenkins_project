<html>
  <h2>Hello this code is vulnerable to XSS</h2>
  
  <form action="" method="GET">
    <input type="text" name="xss" value="<?php echo $aaa ;?>">
  
  </html>



<?php
  
  $newvar = $aaaa;

echo $newvar;

?>
