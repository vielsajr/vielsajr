package dwes.examen1.data;

import dwes.examen1.model.Monitor;

import java.util.List;

public class MonitorData
{
    public static List<Monitor> getMonitores()
    {
        return List.of(
                new Monitor(1L, "Laura Pérez", "Pádel"),
                new Monitor(2L, "Marcos Díaz", "Natación"),
                new Monitor(3L, "Elena Suárez", "Yoga"),
                new Monitor(4L, "Pablo Fernández", "Fútbol"),
                new Monitor(5L, "Sara Menéndez", "Baloncesto")
        );
    }
}
