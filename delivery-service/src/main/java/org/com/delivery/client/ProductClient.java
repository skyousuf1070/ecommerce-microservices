package org.com.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://product-service:8083")
public interface ProductClient {
    @GetMapping("/api/products/{productId}")
    Product getProduct(@PathVariable String productId);
}
