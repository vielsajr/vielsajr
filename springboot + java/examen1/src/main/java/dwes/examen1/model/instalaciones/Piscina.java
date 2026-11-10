package dwes.examen1.model.instalaciones;

public final class Piscina extends Instalacion {
    private int calles;
    private int mesesSinRevision;

    public Piscina(Long id, String nombre, double precioHora, int calles, int mesesSinRevision) {
        super(id, nombre, precioHora);
        this.calles = calles;
        this.mesesSinRevision = mesesSinRevision;
    }

    public int getCalles() { return calles; }
    public int getMesesSinRevision() { return mesesSinRevision; }

    @Override
    public double calcularPrecio(int horas) {
        return getPrecioHora() * horas * calles;
    }

    @Override
    public String getTipo() { return "Piscina"; }

    @Override
    public boolean necesitaRevision() { return mesesSinRevision > 6; }
}
