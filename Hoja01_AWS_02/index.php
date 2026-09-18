<?php

require_once "conexion.php";

class MenuRepository
{
    private PDO $conexion;

    public function __construct(PDO $conexion)
    {
        $this->conexion = $conexion;
    }

    public function obtenerPlatos(): array
    {
        $sql = "SELECT platos.id, platos.nombre, platos.precio, platos.descripcion,
                       categorias.nombre AS categoria
                FROM platos
                INNER JOIN categorias ON platos.categoria_id = categorias.id
                ORDER BY categorias.id, platos.nombre";

        $stmt = $this->conexion->prepare($sql);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}

try {
    $repositorio = new MenuRepository(Database::getConexion());
    $platos = $repositorio->obtenerPlatos();
} catch (PDOException $e) {
    die("Error al obtener datos: " . $e->getMessage());
}
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Restaurante MOC Naranco | Carta</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f7f3ee;
        }
        .hero {
            background: linear-gradient(135deg, #5c3a21, #8a5a35);
            color: #fff;
            padding: 4rem 1rem;
        }
        .card-plato {
            border: none;
            border-radius: 1rem;
            overflow: hidden;
            box-shadow: 0 0.5rem 1.2rem rgba(0, 0, 0, 0.08);
            transition: transform 0.2s ease-in-out;
        }
        .card-plato:hover {
            transform: translateY(-4px);
        }
        .card-plato img {
            height: 220px;
            object-fit: cover;
        }
        .precio {
            font-size: 1.25rem;
            font-weight: 600;
            color: #8a5a35;
        }
    </style>
</head>
<body>
    <header class="hero text-center">
        <h1 class="display-5 fw-bold">Restaurante MOC Naranco</h1>
        <p class="lead mb-0">Práctica EC2, RDS y S3 de AWS</p>
    </header>

    <div class="container py-5">
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <?php foreach ($platos as $plato): ?>
                <div class="col">
                    <div class="card card-plato h-100">
                        <img src="img/<?= htmlspecialchars($plato['id']) ?>.jpg"
                             class="card-img-top"
                             alt="<?= htmlspecialchars($plato['nombre']) ?>">
                        <div class="card-body d-flex flex-column">
                            <span class="badge bg-secondary align-self-start mb-2"><?= htmlspecialchars($plato['categoria']) ?></span>
                            <h5 class="card-title"><?= htmlspecialchars($plato['nombre']) ?></h5>
                            <p class="card-text text-muted flex-grow-1"><?= htmlspecialchars($plato['descripcion']) ?></p>
                            <p class="precio mb-0"><?= number_format((float) $plato['precio'], 2, ',', '.') ?> €</p>
                        </div>
                    </div>
                </div>
            <?php endforeach; ?>
        </div>
    </div>

    <footer class="text-center py-4 text-muted">
        Restaurante MOC Naranco &middot; Carta sujeta a disponibilidad de mercado
    </footer>
</body>
</html>
