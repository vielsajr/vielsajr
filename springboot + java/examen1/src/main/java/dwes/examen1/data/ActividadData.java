package dwes.examen1.data;

import dwes.examen1.model.Actividad;
import dwes.examen1.model.Monitor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActividadData
{
    // Los monitores se toman de MonitorData para que sean los mismos objetos
    private static final List<Monitor> monitores = MonitorData.getMonitores();
    private static final Monitor laura = monitores.get(0);
    private static final Monitor marcos = monitores.get(1);
    private static final Monitor elena = monitores.get(2);
    private static final Monitor pablo = monitores.get(3);
    private static final Monitor sara = monitores.get(4);

    // Se crea una sola vez: los cambios se mantienen mientras la aplicación esté arrancada.
    // new ArrayList<>(List.of(...)): List.of() es inmutable; el ArrayList permite add() y remove()
    private static final List<Actividad> actividades = new ArrayList<>(List.of(
            new Actividad(1L, "Pádel iniciación", "Clases para aprender los golpes básicos y las reglas del juego.",
                    35.0, 8, LocalDate.of(2026, 9, 15), "padel_iniciacion.jpg", true, laura),
            new Actividad(2L, "Pádel perfeccionamiento", "Táctica, juego en pared y partidos dirigidos.",
                    42.5, 8, LocalDate.of(2026, 10, 1), "padel_perfeccionamiento.jpg", true, laura),
            new Actividad(3L, "Natación infantil", "Aprendizaje de los cuatro estilos para niños de 6 a 12 años.",
                    28.0, 15, LocalDate.of(2026, 9, 20), "natacion_infantil.jpg", true, marcos),
            new Actividad(4L, "Aquagym", "Ejercicio de bajo impacto en el agua con música.",
                    25.0, 20, LocalDate.of(2026, 10, 5), "aquagym.jpg", false, marcos),
            new Actividad(5L, "Yoga", "Posturas, respiración y relajación para todos los niveles.",
                    30.0, 18, LocalDate.of(2026, 9, 10), "yoga.jpg", true, elena),
            new Actividad(6L, "Pilates", "Fortalecimiento del core y mejora de la postura.",
                    32.0, 12, LocalDate.of(2027, 1, 12), "pilates.jpg", false, elena),
            new Actividad(7L, "Escuela de fútbol", "Entrenamientos para categorías alevín e infantil.",
                    20.0, 25, LocalDate.of(2026, 9, 1), "futbol.jpg", true, pablo),
            new Actividad(8L, "Baloncesto adultos", "Liga interna y entrenamientos dos días por semana.",
                    22.0, 24, LocalDate.of(2026, 10, 15), "baloncesto.jpg", true, sara)
    ));

    public static List<Actividad> getActividades()
    {
        return actividades;
    }
}
