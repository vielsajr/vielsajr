package dwes.examen1.service;

import dwes.examen1.model.Actividad;

import java.util.List;

public interface ActividadService {
    List<Actividad> findAll();

    Actividad findById(Long id);

    List<Actividad> findByMonitor(Long monitorId);

    void guardar(Actividad actividad);

    void eliminar(Long id);
}
