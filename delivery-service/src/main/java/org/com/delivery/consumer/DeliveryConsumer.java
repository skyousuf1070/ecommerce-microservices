package org.com.delivery.consumer;

import org.com.delivery.event.OrderEvent;
import org.com.delivery.model.Delivery;
import org.com.delivery.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeliveryConsumer {
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryConsumer(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "delivery-group")
    public void handleOrderConfirmed(OrderEvent orderEvent) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(Long.valueOf(orderEvent.getOrderId()));
        delivery.setTrackingId("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        delivery.setStatus("PREPARING");
        delivery.setEstimatedArrival(LocalDateTime.now().plusDays(3));

        deliveryRepository.save(delivery);
        System.out.println("Delivery initiated for Order: " + orderEvent.getOrderId());
    }
}
