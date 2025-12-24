package org.com.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Long orderId;
    private String trackingId;
    private String status;
    private LocalDateTime estimatedArrival;

    public Delivery(Long orderId, String trackingId, String status, LocalDateTime estimatedArrival) {
        this.orderId = orderId;
        this.trackingId = trackingId;
        this.status = status;
        this.estimatedArrival = estimatedArrival;
    }

    public Delivery() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(id, delivery.id) && Objects.equals(orderId, delivery.orderId) && Objects.equals(trackingId, delivery.trackingId) && Objects.equals(status, delivery.status) && Objects.equals(estimatedArrival, delivery.estimatedArrival);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, trackingId, status, estimatedArrival);
    }
}
