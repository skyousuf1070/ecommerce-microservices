package org.com.payment.event;

import java.math.BigDecimal;

public class PaymentEvent {
    private String orderId;
    private String paymentId;
    private String status;
    private BigDecimal amount;

    // Default Constructor (Required for JSON Deserialization)
    public PaymentEvent() {}

    // All-args Constructor
    public PaymentEvent(String orderId, String paymentId, String status, BigDecimal amount) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}