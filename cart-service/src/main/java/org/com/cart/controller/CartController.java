package org.com.cart.controller;


import lombok.RequiredArgsConstructor;
import org.com.cart.model.Cart;
import org.com.cart.model.ProductRequest;
import org.com.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // POST: http://localhost:8081/api/cart/user_123/add
    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addToCart(@PathVariable String userId,
                                          @RequestBody ProductRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    // GET: http://localhost:8081/api/cart/user_123
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
