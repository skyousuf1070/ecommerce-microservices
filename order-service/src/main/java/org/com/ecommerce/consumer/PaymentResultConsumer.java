package org.com.ecommerce.consumer;

import lombok.RequiredArgsConstructor;
import org.com.ecommerce.event.PaymentEvent;
import org.com.ecommerce.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-results", groupId = "order-group")
    public void updateOrderStatus(PaymentEvent event) {
        System.out.println("Order Service received payment result for: " + event.getOrderId());

        orderRepository.findById(Long.parseLong(event.getOrderId())).ifPresent(order -> {
            if ("SUCCESS".equals(event.getStatus())) {
                order.setStatus("CONFIRMED");
            } else {
                order.setStatus("FAILED");
            }
            orderRepository.save(order);
            System.out.println("Order " + event.getOrderId() + " is now: " + order.getStatus());
        });
    }
}