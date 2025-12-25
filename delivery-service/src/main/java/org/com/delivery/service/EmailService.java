package org.com.delivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(String toEmail, String orderId, String details,
                                      String address, String phoneNumber, String arrival) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Order Confirmed! Tracking: " + orderId);
        message.setText("Your order is confirmed!\n\n" +
                "Items:\n" + details + "\n" +
                "Address: " + address + "\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Estimated Arrival: " + arrival + "\n\n" +
                "Thank you for shopping with us!");

        mailSender.send(message);
    }
}
