package io.raza.mockemailproviderservice.controller;

import io.raza.mockemailproviderservice.dto.OrderPlacedEmailRequest;
import io.raza.mockemailproviderservice.service.EmailClaimResult;
import io.raza.mockemailproviderservice.service.EmailIdempotencyService;
import io.raza.mockemailproviderservice.service.EmailProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailProviderService emailProviderService;

    @PostMapping("/order-placed")
    public ResponseEntity<String> sendOrderPlacedEmail(
            @RequestHeader("Idempotency-Key")
            UUID idempotencyKey,

            @RequestBody
            OrderPlacedEmailRequest request
    ) {

        EmailClaimResult result =
                emailProviderService.sendOrderPlacedEmail(
                        idempotencyKey,
                        request
                );

        return switch (result) {

            case CLAIMED ->
                    ResponseEntity.ok("SENT");

            case ALREADY_SUCCESS ->
                    ResponseEntity.ok("ALREADY_SENT");

            case IN_PROGRESS ->
                    ResponseEntity
                            .status(202)
                            .body("IN_PROGRESS");
        };
    }
}