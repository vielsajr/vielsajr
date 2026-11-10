package dwes.examen1.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actividad {
    private Long id;
    @NotBlank(message = "El nombre debe ser obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String nombre;
    private String descripcion;
    @Positive(message = "El precio mensual debe ser superior a 0")
    private double precioMensual;
    @Min(value = 1, message = "La plaza debe estar entre 1 y 30")
    @Max(value = 30, message = "La plaza debe estar entre 1 y 30")
    private int plazas;
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaInicio;
    private String imagen;
    private boolean activa;
    @Valid
    @NotNull(message = "El monitor es obligatorio")
    private Monitor monitor;
}
