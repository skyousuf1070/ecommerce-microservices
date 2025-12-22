package org.com.payment.event;

import java.math.BigDecimal;

public class OrderEvent {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String status;

    // Default Constructor for JSON
    public OrderEvent() {}

    // All-args Constructor
    public OrderEvent(String orderId, String userId, BigDecimal amount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}