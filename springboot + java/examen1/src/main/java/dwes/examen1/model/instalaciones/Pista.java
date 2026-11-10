package dwes.examen1.model.instalaciones;

public final class Pista extends Instalacion {
    private String deporte;
    private boolean iluminacion;

    public Pista(Long id, String nombre, double precioHora, String deporte, boolean iluminacion) {
        super(id, nombre, precioHora);
        this.deporte = deporte;
        this.iluminacion = iluminacion;
    }

    public String getDeporte() { return deporte; }
    public boolean isIluminacion() { return iluminacion; }

    @Override
    public double calcularPrecio(int horas) {
        double precio = getPrecioHora() * horas;
        if (iluminacion) precio += 3 * horas;
        return precio;
    }

    @Override
    public String getTipo() { return "Pista"; }

    @Override
    public boolean necesitaRevision() { return false; }
}
