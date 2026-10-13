package io.raza.mockemailproviderservice.service;

import io.raza.mockemailproviderservice.entity.EmailRequestEntity;
import io.raza.mockemailproviderservice.entity.EmailRequestStatus;
import io.raza.mockemailproviderservice.repository.EmailRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailIdempotencyService {

    private final EmailRequestRepository emailRequestRepository;

    @Transactional
    public EmailClaimResult claim(
            UUID idempotencyKey,
            Long orderId,
            Long customerId,
            Long productId,
            Integer quantity
    ) {

        int inserted = emailRequestRepository.tryClaim(
                idempotencyKey,
                orderId,
                customerId,
                productId,
                quantity
        );

        if (inserted == 1) {
            return EmailClaimResult.CLAIMED;
        }

        EmailRequestEntity existing =
                emailRequestRepository.findById(idempotencyKey)
                        .orElseThrow();

        if (existing.getStatus()
                == EmailRequestStatus.SUCCESS) {

            return EmailClaimResult.ALREADY_SUCCESS;
        }

        return EmailClaimResult.IN_PROGRESS;
    }

    @Transactional
    public void markSuccess(UUID idempotencyKey) {
        emailRequestRepository.markSuccess(idempotencyKey);
    }
}