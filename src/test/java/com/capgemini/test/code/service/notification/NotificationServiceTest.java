package com.capgemini.test.code.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capgemini.test.code.clients.NotificationClient;
import com.capgemini.test.code.clients.NotificationRequest;

@ExtendWith (MockitoExtension.class)
public class NotificationServiceTest {
    
    @Mock 
    private NotificationClient notificationClient;

    @InjectMocks 
    private NotificationService notificationService;

    @Test 
    void shouldSendEmailWhenRoleIsAdmin() {

        String role = "admin";
        String email = "daniel@email.com";
        String phone = "677998899";

        notificationService.notifyUser(role, email, phone);

        ArgumentCaptor<NotificationRequest> captor = 
            ArgumentCaptor.forClass(NotificationRequest.class);

        verify(notificationClient).sendEmail(captor.capture());

        NotificationRequest request = captor.getValue();

        assertEquals(email, request.getRecipient());
        assertEquals("usuario guardado", request.getMessage());

        verify(notificationClient, never()).sendSms(any());
    }

    @Test 
    void shouldSendSmsWhenRoleIsSuperadmin() {

        String role = "superadmin";
        String email = "daniel@email.com";
        String phone = "677998899";

        notificationService.notifyUser(role, email, phone);

        ArgumentCaptor<NotificationRequest> captor = 
            ArgumentCaptor.forClass(NotificationRequest.class);

        verify(notificationClient).sendSms(captor.capture());

        NotificationRequest request = captor.getValue();

        assertEquals(phone, request.getRecipient());
        assertEquals("usuario guardado", request.getMessage());
        
        verify(notificationClient, never()).sendEmail(any());
    }

    @Test 
    void shouldNotSendNotificationWhenRoleIsUnknown() {

        String role = "user";
        String email = "daniel@email.com";
        String phone = "677998899";

        notificationService.notifyUser(role, email, phone);

        verifyNoInteractions(notificationClient);
    }
}
