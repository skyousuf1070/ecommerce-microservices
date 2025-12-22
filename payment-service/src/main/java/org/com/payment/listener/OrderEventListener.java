package org.com.payment.listener;

import org.com.payment.event.OrderEvent;
import org.com.payment.event.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class OrderEventListener {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    // Manual Constructor (Replaces @RequiredArgsConstructor)
    public OrderEventListener(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(OrderEvent event) {
        System.out.println("Payment Service received order: " + event.getOrderId());

        // Simulate logic
        String resultStatus = "SUCCESS";
        if (event.getAmount() != null && event.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            resultStatus = "FAILED";
        }

        // Create the result event using the constructor (Replaces .builder())
        PaymentEvent paymentEvent = new PaymentEvent(
                event.getOrderId(),
                "PAY_MOCK_" + UUID.randomUUID().toString().substring(0, 8),
                resultStatus,
                event.getAmount()
        );

        // Send the result back to Order Service
        kafkaTemplate.send("payment-results", paymentEvent);
        System.out.println("Payment " + resultStatus + " sent back for Order: " + event.getOrderId());
    }
}