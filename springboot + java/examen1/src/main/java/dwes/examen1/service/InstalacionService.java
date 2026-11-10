package dwes.examen1.service;

import dwes.examen1.model.instalaciones.Instalacion;

import java.util.Deque;
import java.util.List;

public interface InstalacionService {
    Deque<Instalacion> historialReservas();
    List<Instalacion> findMasCarasQue(double precioHora);
    double calcularPrecioTotal(int horas);
    long contarReservables();
    Instalacion obtenerMasCara();
}
