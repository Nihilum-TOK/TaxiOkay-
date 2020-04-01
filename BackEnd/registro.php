<?php

include_once('resources/init.php');
if(isset($_GET['emai']) && isset($_GET['psw']) && isset($_GET['uname']) && isset($_GET['ap_paterno']) && isset($_GET['ap_materno'])){

  $errors = array();
  if(exists($_GET['emai'],"Users","emai")){
        $errors[] = 'Ya existe.';
  }
  if(empty($errors)){
  
          $insertar_datos = array(
                0 => $_GET['emai'],
                1 => $_GET['psw'],
                2 => $_GET['uname'],
                3 => $_GET['ap_paterno'],
                4 => $_GET['ap_materno'],
            );
          $insertar_campos = array(
              0 => "emai",
              1 => "psw",
              2 => "uname",
              3 => "ap_paterno",
              4 => "ap_materno",
          );
          
          add($insertar_datos, $insertar_campos , "Users");
          $id = mysqli_insert_id($mysql_connect);
            
          $arr = array('emai' => $_GET['emai'], 'id' => $id, 'estatus' => "si");
          echo json_encode($arr);

  }
  else{
          $arr = array('emai' => $_GET['emai'], 'id' => "no", 'estatus' => "existe");
          echo json_encode($arr);
  }
  
  

}

?>