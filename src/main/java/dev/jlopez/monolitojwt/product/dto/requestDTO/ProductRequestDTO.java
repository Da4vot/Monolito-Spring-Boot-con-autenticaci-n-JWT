package dev.jlopez.monolitojwt.product.dto.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequestDTO(
        @NotBlank(message = "El nombre del producto es obligatorio") @Size(min = 2, max = 50) String name,
        @Size(max = 300, message = "La descripcion no debe superar 300 caracteres") String description,

        @NotNull(message = "El precio es obligatorio") @Positive(message = "El precio debe ser mayor a 0") Double price,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) {
}