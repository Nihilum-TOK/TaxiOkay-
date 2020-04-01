<?php
session_start();


##json decode para el JSON de entrada


$user=$_POST{'name'};
$pass=$_POST{'pass'};

//CONEXION DE LA BASE DE DATOS

$domain="nestor160300116.atwebpages.com";
$id="3188003";
$serverpass="6pfVRnt_0byX)C{Q";
$database="3188003_hackaton";

$connect=mysqli_connect('$domain',
                        '$id',
                        '$serverpass',
                        '$database');

$query="SELECT * FROM Users WHERE emai='$user' and psw='$pass'";

$response=mysqli_query($connect, $query);

$row=mysqli_num_rows($response);

if ($row>0) {
    header("location:###NOMBRE DE PAGINA REDIRECCINADA###.html");
}
else {
    echo "Usuario y/o contraseña incorrectos";
}

mysqli_free_result($response)
mysqli_close($connect);