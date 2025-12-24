
package dev.jlopez.monolitojwt.product.dto.responseDTO;

public record ProductResponseDTO(
    Integer id,
    String name,
    String description,
    Double price,
    Integer stock
) {}