package org.com.order.service;

import lombok.RequiredArgsConstructor;
import org.com.order.event.OrderEvent;
import org.com.order.event.OrderItemEvent;
import org.com.order.exception.EmptyCartException;
import org.com.order.model.cart.CartDTO;
import org.com.order.model.cart.CartItemDTO;
import org.com.order.model.OrderItem;
import org.com.order.model.OrderResponse;
import org.com.order.model.OrderRequest;
import org.com.order.model.Order;
import org.com.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // 1. Fetch Cart from Cart Service
        CartDTO cart = webClientBuilder.build()
                .get()
                .uri("http://cart-service/api/cart/{userId}", request.getUserId())
                .retrieve()
                .bodyToMono(CartDTO.class)
                .block();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot place order: Cart is empty");
        }

        // 2. Business Logic Calculations
        BigDecimal subTotal = calculateSubtotal(cart.getItems());
        Double carbonFootprint = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * 0.5).sum();

        BigDecimal discountAmount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalPrice = subTotal.subtract(discountAmount);

        // 3. Create and Link Order Entities
        // Status is set to PENDING_PAYMENT initially
        Order order = Order.builder()
                .userId(request.getUserId())
                .orderNumber(UUID.randomUUID().toString())
                .totalPrice(finalPrice)
                .carbonFootprint(carbonFootprint)
                .status("PENDING_PAYMENT")
                .build();

        List<OrderItem> items = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .productId(cartItem.getProductId())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getPrice())
                        .order(order)
                        .build())
                .toList();

        order.setOrderItems(items);

        // 4. Save to order_db
        Order savedOrder = orderRepository.save(order);

        // 5. ASYNC INTEGRATION: Publish event to Kafka for Payment Service
        List<OrderItemEvent> itemEvents = cart.getItems().stream()
                .map(item -> OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(savedOrder.getId().toString())
                .amount(savedOrder.getTotalPrice())
                .userId(savedOrder.getUserId())
                .status("INITIATED")
                .items(itemEvents)
                .build();

        kafkaTemplate.send("order-created", orderEvent);

        // 6. Cleanup: Clear the Cart
        clearUserCart(request.getUserId());

        return new OrderResponse(savedOrder.getId(), finalPrice, "PAYMENT_INITIATED");
    }

    private void clearUserCart(String userId) {
        webClientBuilder.build()
                .delete()
                .uri("http://cart-service/api/cart/{userId}/clear", userId)
                .retrieve()
                .toBodilessEntity()
                .subscribe(); // Non-blocking
    }

    private BigDecimal calculateSubtotal(List<CartItemDTO> items) {
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, String> getOrderStatus(Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("orderId", order.getId().toString());
                    response.put("status", order.getStatus());
                    return response;
                })
                .orElseGet(() -> {
                    Map<String, String> emptyResponse = new HashMap<>();
                    emptyResponse.put("error", "Order not found");
                    return emptyResponse;
                });
    }
}