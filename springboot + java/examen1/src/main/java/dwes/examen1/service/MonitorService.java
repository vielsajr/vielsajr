package dwes.examen1.service;

import dwes.examen1.model.Monitor;

import java.util.List;

public interface MonitorService {
    List<Monitor> findAll();
    Monitor findById(Long id);
}
