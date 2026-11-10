package dwes.examen1.service;

import dwes.examen1.data.InstalacionData;
import dwes.examen1.model.instalaciones.Instalacion;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InstalacionServiceImpl implements InstalacionService {

    private final List<Instalacion> instalaciones = InstalacionData.getInstalaciones();

    @Override
    public Deque<Instalacion> historialReservas() {
        Deque<Instalacion> pila = new ArrayDeque<>();
        Stream.of(instalaciones.get(1), instalaciones.get(3))  // segunda y cuarta
                .forEach(pila::push);                            // push = añadir a la cima
        return pila;
    }

    @Override
    public List<Instalacion> findMasCarasQue(double precioHora) {
        return instalaciones.stream()
                .filter(i -> i.getPrecioHora() > precioHora)
                .sorted(Comparator.comparingDouble(Instalacion::getPrecioHora))
                .toList();
    }

    @Override
    public double calcularPrecioTotal(int horas) {
        return instalaciones.stream()
                .mapToDouble(i -> i.calcularPrecio(horas))
                .sum();
    }

    @Override
    public long contarReservables() {
        return instalaciones.stream()
                .filter(Instalacion::puedeReservarse)
                .count();
    }

    @Override
    public Instalacion obtenerMasCara() {
        return instalaciones.stream()
                .max(Comparator.comparingDouble(Instalacion::getPrecioHora))
                .orElse(null);
    }
}
