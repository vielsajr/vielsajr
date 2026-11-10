package dwes.examen1.model.instalaciones;

public interface Mantenible {
    boolean necesitaRevision();

    default boolean puedeReservarse() {
        return !necesitaRevision();
    }
}
