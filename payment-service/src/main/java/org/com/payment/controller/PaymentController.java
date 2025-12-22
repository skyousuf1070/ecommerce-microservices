package org.com.payment.controller;

import org.com.payment.event.PaymentEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    // Manual Constructor Injection
    public PaymentController(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/simulate")
    public ResponseEntity<String> simulatePayment(@RequestBody PaymentEvent simulationRequest) {

        // Manual object creation instead of Builder
        PaymentEvent event = new PaymentEvent(
                simulationRequest.getOrderId(),
                "SIM_PAY_" + UUID.randomUUID().toString().substring(0,8),
                simulationRequest.getStatus(),
                simulationRequest.getAmount()
        );

        kafkaTemplate.send("payment-results", event);

        return ResponseEntity.ok("Simulated Payment Event sent for Order: " + event.getOrderId());
    }
}