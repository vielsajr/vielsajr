<?php

class Database
{
    private static ?PDO $instancia = null;

    private string $host = "tu-endpoint-rds.amazonaws.com";
    private string $usuario = "admin";
    private string $password = "tu_password";
    private string $bd = "restaurante";

    public static function getConexion(): PDO
    {
        if (self::$instancia === null) {
            $config = new self();
            $dsn = "mysql:host={$config->host};dbname={$config->bd};charset=utf8mb4";

            try {
                self::$instancia = new PDO($dsn, $config->usuario, $config->password);
                self::$instancia->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            } catch (PDOException $e) {
                die("Error de conexión: " . $e->getMessage());
            }
        }

        return self::$instancia;
    }
}
