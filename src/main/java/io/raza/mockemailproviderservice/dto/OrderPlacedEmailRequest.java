package io.raza.mockemailproviderservice.dto;

public record OrderPlacedEmailRequest(
        Long orderId,
        Long customerId,
        Long productId,
        Integer quantity
) {
}