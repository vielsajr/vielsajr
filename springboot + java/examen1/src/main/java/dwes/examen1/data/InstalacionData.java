package dwes.examen1.data;

import dwes.examen1.model.instalaciones.Instalacion;
import dwes.examen1.model.instalaciones.Piscina;
import dwes.examen1.model.instalaciones.Pista;

import java.util.List;

public class InstalacionData
{
    // Lista inmutable: no se puede modificar, solo consultar con streams
    public static List<Instalacion> getInstalaciones()
    {
        return List.of(
                new Pista(1L, "Pista de pádel 1", 12.0, "Pádel", true),
                new Pista(2L, "Pista de pádel 2", 10.0, "Pádel", false),
                new Pista(3L, "Pista de tenis 1", 15.0, "Tenis", true),
                new Piscina(4L, "Piscina cubierta", 8.0, 6, 2),
                new Piscina(5L, "Piscina exterior", 6.0, 4, 11),
                new Pista(6L, "Pista de tenis 2", 13.0, "Tenis", false)
        );
    }
}
