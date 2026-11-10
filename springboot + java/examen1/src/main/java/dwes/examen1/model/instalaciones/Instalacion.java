package dwes.examen1.model.instalaciones;

public abstract class Instalacion implements Mantenible {
    private Long id;
    private String nombre;
    private double precioHora;

    public Instalacion(Long id, String nombre, double precioHora) {
        this.id = id;
        this.nombre = nombre;
        this.precioHora = precioHora;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioHora() { return precioHora; }

    public abstract double calcularPrecio(int horas);
    public abstract String getTipo();

    @Override
    public String toString() {
        return nombre + " (" + precioHora + " €/h)";   // "Piscina cubierta (8.0 €/h)"
    }
}