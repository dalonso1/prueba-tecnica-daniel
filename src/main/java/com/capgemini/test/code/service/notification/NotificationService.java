package com.capgemini.test.code.service.notification;

import org.springframework.stereotype.Service;

import com.capgemini.test.code.clients.NotificationClient;
import com.capgemini.test.code.clients.NotificationRequest;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class NotificationService {

    private static final String USER_SAVED_MESSAGE = "usuario guardado";

    private final NotificationClient notificationClient;
    
    public void notifyUser(String role, String email, String phone) {

        if ("admin".equals(role)) {
            sendEmail(email);
        } else if ("superadmin".equals(role)) {
            sendSms(phone);
        }
    }

    private void sendEmail(String email) {
        
        NotificationRequest request = new NotificationRequest(
            email, 
            null,
            USER_SAVED_MESSAGE
        );

        notificationClient.sendEmail(request);
    }

    private void sendSms(String phone) {
        
        NotificationRequest request = new NotificationRequest(
            null,
            phone, 
            USER_SAVED_MESSAGE
        );

        notificationClient.sendSms(request);
    }
}
