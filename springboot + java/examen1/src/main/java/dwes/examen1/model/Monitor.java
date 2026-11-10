package dwes.examen1.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Monitor {
    @NotNull(message = "El monitor es obligatorio")
    private Long id;
    private String nombre;
    private String especialidad;
}
