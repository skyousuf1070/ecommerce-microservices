package org.com.delivery.consumer;

import org.com.delivery.client.ProductClient;
import org.com.delivery.model.Product;
import org.com.delivery.model.User;
import org.com.delivery.client.UserClient;
import org.com.delivery.event.OrderEvent;
import org.com.delivery.event.OrderItemEvent;
import org.com.delivery.model.Delivery;
import org.com.delivery.repository.DeliveryRepository;
import org.com.delivery.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeliveryConsumer {
    private final DeliveryRepository deliveryRepository;
    private final EmailService emailService;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Autowired
    public DeliveryConsumer(DeliveryRepository deliveryRepository, EmailService emailService,
                            ProductClient productClient, UserClient userClient) {
        this.deliveryRepository = deliveryRepository;
        this.emailService = emailService;
        this.productClient = productClient;
        this.userClient = userClient;
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

        String userEmail = "skyousuf1070@gmail.com";
        String address = "defaultAddress";
        String phoneNumber = "defaultPhoneNumber";
        try {
            User user = userClient.getUser(orderEvent.getUserId());
            userEmail = user.getEmail();
            address = user.getAddress();
            phoneNumber = user.getPhone();
        } catch (Exception exception) {
            System.err.println("Could not fetch user email: " + exception.getMessage());
        }
        emailService.sendOrderConfirmation(
                userEmail,
                orderEvent.getOrderId(),
                details.toString(),
                address,
                phoneNumber,
                delivery.getEstimatedArrival().toString()
        );
    }

    private String fetchProductName(String productId) {
        Product product = productClient.getProduct(productId);
        return product != null ? product.getName() : "Unknown Product";
    }
}
