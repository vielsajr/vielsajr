package dwes.examen1.service;

import dwes.examen1.data.MonitorData;
import dwes.examen1.model.Monitor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorServiceImpl implements MonitorService{

    @Override
    public List<Monitor> findAll(){
        return MonitorData.getMonitores();
    }

    @Override
    public Monitor findById(Long id){
        return MonitorData.getMonitores().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }
}
