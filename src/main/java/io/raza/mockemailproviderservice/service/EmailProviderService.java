package io.raza.mockemailproviderservice.service;

import io.raza.mockemailproviderservice.dto.OrderPlacedEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailProviderService {

    private final EmailIdempotencyService idempotencyService;
    private final EmailSenderService emailSenderService;

    public EmailClaimResult sendOrderPlacedEmail(
            UUID idempotencyKey,
            OrderPlacedEmailRequest request
    ) {

        EmailClaimResult result =
                idempotencyService.claim(
                        idempotencyKey,
                        request.orderId(),
                        request.customerId(),
                        request.productId(),
                        request.quantity()
                );

        if (result == EmailClaimResult.ALREADY_SUCCESS) {

            System.out.println(
                    "Email already successfully sent"
                            + " | key=" + idempotencyKey
            );

            return result;
        }

        if (result == EmailClaimResult.IN_PROGRESS) {

            System.out.println(
                    "Email request is already processing"
                            + " | key=" + idempotencyKey
            );

            return result;
        }

        // Only CLAIMED reaches here.
        emailSenderService.sendOrderPlacedEmail(request);

        System.out.println(
                "PROVIDER EMAIL SENT"
                        + " | key=" + idempotencyKey
        );

        // NEW FAILURE SIMULATION
        if (request.productId().equals(4444L)) {

            System.out.println(
                    "PROVIDER CRASH AFTER SMTP, BEFORE SUCCESS"
                            + " | key=" + idempotencyKey
            );

            throw new RuntimeException(
                    "Simulated provider crash after SMTP send"
            );
        }


        idempotencyService.markSuccess(idempotencyKey);

        return EmailClaimResult.CLAIMED;
    }
}