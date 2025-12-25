package org.com.delivery.consumer;

import org.com.delivery.event.OrderEvent;
import org.com.delivery.event.OrderItemEvent;
import org.com.delivery.model.Delivery;
import org.com.delivery.model.Product;
import org.com.delivery.repository.DeliveryRepository;
import org.com.delivery.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeliveryConsumer {
    private final DeliveryRepository deliveryRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    @Autowired
    public DeliveryConsumer(DeliveryRepository deliveryRepository, EmailService emailService, RestTemplate restTemplate) {
        this.deliveryRepository = deliveryRepository;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "order-created", groupId = "delivery-group")
    public void handleOrderConfirmed(OrderEvent orderEvent) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(Long.valueOf(orderEvent.getOrderId()));
        delivery.setTrackingId("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        delivery.setStatus("CONFIRMED");
        delivery.setEstimatedArrival(LocalDateTime.now().plusDays(3));

        deliveryRepository.save(delivery);

        StringBuilder details = new StringBuilder();
        for (OrderItemEvent item : orderEvent.getItems()) {
            String productName = fetchProductName(item.getProductId());
            details.append("- ").append(productName)
                    .append(" (Qty: ").append(item.getQuantity()).append(")\n");
        }

        emailService.sendOrderConfirmation(
                "asifbabashaik3@gmail.com",
                orderEvent.getOrderId(),
                details.toString(),
                delivery.getEstimatedArrival().toString()
        );
    }

    private String fetchProductName(String productId) {
        String url = "http://product-service:8083/api/products/" + productId;
        Product product = restTemplate.getForObject(url, Product.class);
        return product != null ? product.getName() : "Unknown Product";
    }
}
