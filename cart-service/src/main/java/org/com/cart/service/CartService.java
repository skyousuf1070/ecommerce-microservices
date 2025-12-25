package org.com.cart.service;

import lombok.RequiredArgsConstructor;
import org.com.cart.exception.InsufficientStockException;
import org.com.cart.exception.ProductNotFoundException;
import org.com.cart.model.Cart;
import org.com.cart.model.CartItem;
import org.com.cart.model.ProductDTO;
import org.com.cart.model.ProductRequest;
import org.com.cart.repository.CartRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder;

    @Transactional
    public Cart addToCart(String userId, ProductRequest request) {
        ProductDTO product = webClientBuilder.build()
                .get()
                .uri("http://product-service/api/products/{id}", request.getProductId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ProductNotFoundException("Product ID " + request.getProductId() + " does not exist!")))
                .bodyToMono(ProductDTO.class)
                .block(); // Synchronous wait

        if (Objects.requireNonNull(product).getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock! Available: " + product.getStockQuantity());
        }

        // 1. Get existing cart or create a new one
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .items(new ArrayList<>())
                        .build());

        // 2. Check if product is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            // Check total quantity vs stock
            if (product.getStockQuantity() < (item.getQuantity() + request.getQuantity())) {
                throw new InsufficientStockException("Cannot add more. Total would exceed available stock!");
            }
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}