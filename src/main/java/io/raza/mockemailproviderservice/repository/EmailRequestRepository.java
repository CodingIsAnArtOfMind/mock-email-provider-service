package io.raza.mockemailproviderservice.repository;

import io.raza.mockemailproviderservice.entity.EmailRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EmailRequestRepository
        extends JpaRepository<EmailRequestEntity, UUID> {

    @Modifying
    @Query(
            value = """
                    INSERT INTO email_provider.email_requests
                        (
                            idempotency_key,
                            order_id,
                            customer_id,
                            product_id,
                            quantity,
                            status,
                            created_at
                        )
                    VALUES
                        (
                            :idempotencyKey,
                            :orderId,
                            :customerId,
                            :productId,
                            :quantity,
                            'PROCESSING',
                            NOW()
                        )
                    ON CONFLICT (idempotency_key) DO NOTHING
                    """,
            nativeQuery = true
    )
    int tryClaim(
            @Param("idempotencyKey") UUID idempotencyKey,
            @Param("orderId") Long orderId,
            @Param("customerId") Long customerId,
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Query(
            value = """
                UPDATE email_provider.email_requests
                SET status = 'SUCCESS',
                    sent_at = NOW()
                WHERE idempotency_key = :idempotencyKey
                """,
            nativeQuery = true
    )
    int markSuccess(
            @Param("idempotencyKey") UUID idempotencyKey
    );
}