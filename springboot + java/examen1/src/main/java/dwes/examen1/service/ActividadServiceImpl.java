package dwes.examen1.service;

import dwes.examen1.data.ActividadData;
import dwes.examen1.model.Actividad;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ActividadServiceImpl implements ActividadService{

    private final List<Actividad> actividades = ActividadData.getActividades();
    private Long contador = actividades.stream().mapToLong(Actividad::getId).max().orElse(0) + 1;

    @Override
    public List<Actividad> findAll(){
        return actividades.stream().sorted(Comparator.comparing(Actividad::getFechaInicio)).toList();
    }

    @Override
    public Actividad findById(Long id){
        return actividades.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Actividad> findByMonitor(Long monitorId){
        return actividades.stream().filter(m -> m.getMonitor().getId().equals(monitorId)).toList();
    }

    @Override
    public void guardar(Actividad actividad) {
        actividad.setId(contador++);
        actividades.add(actividad);
    }

    @Override
    public void eliminar(Long id) {
        actividades.removeIf(a -> a.getId().equals(id));
    }
}
