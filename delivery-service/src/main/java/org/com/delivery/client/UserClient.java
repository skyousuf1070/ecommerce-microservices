package org.com.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name must match the spring.application.name of user-service
@FeignClient(name = "user-service", url = "http://user-service:8085")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    User getUser(@PathVariable String id);
}
