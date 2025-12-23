package org.com.product.consumer;

import org.com.product.event.OrderEvent;
import org.com.product.event.OrderItemEvent;
import org.com.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryConsumer {

    private final ProductRepository productRepository;

    @Autowired
    public InventoryConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    @Transactional
    public void updateStock(OrderEvent event) {
        for (OrderItemEvent item : event.getItems()) {
            productRepository.findById(item.getProductId())
                    .ifPresent(product -> {
                        int newStock = product.getStockQuantity() - item.getQuantity();
                        product.setStockQuantity(Math.max(0, newStock));
                        productRepository.save(product);
                    });
        }
        System.out.println("Stock updated for Order: " + event.getOrderId());
    }
}
