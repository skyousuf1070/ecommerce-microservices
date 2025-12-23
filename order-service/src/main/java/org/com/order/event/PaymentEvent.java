package org.com.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {
    private String orderId;
    private String status;    // "SUCCESS" or "FAILED"
    private String paymentId;
    private BigDecimal amount;
}