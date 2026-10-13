package io.raza.mockemailproviderservice.service;

import io.raza.mockemailproviderservice.dto.OrderPlacedEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendOrderPlacedEmail(
            OrderPlacedEmailRequest request) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("orders@provider.local");

        message.setTo(
                "customer-"
                        + request.customerId()
                        + "@poc.local"
        );

        message.setSubject(
                "Order "
                        + request.orderId()
                        + " placed successfully"
        );

        message.setText(
                """
                Your order has been placed successfully.

                Order ID: %d
                Customer ID: %d
                Product ID: %d
                Quantity: %d
                """.formatted(
                        request.orderId(),
                        request.customerId(),
                        request.productId(),
                        request.quantity()
                )
        );

        mailSender.send(message);
    }
}