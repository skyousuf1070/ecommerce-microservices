package org.com.order.controller;

import lombok.RequiredArgsConstructor;
import org.com.order.model.OrderResponse;
import org.com.order.model.OrderRequest;
import org.com.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable Long id) {
        Map<String, String> statusMap = orderService.getOrderStatus(id);
        if (statusMap.isEmpty() || statusMap.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(statusMap);
        }
        return ResponseEntity.ok(statusMap);
    }
}
