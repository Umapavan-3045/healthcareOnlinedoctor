package com.cts.healthcareappointment.notificationmodule.dto;

import java.time.LocalDateTime;

import com.cts.healthcareappointment.notificationmodule.Entity.NotificationStatus;
import com.cts.healthcareappointment.notificationmodule.Entity.NotificationType;
import lombok.Data;

@Data
public class Notificationdto {

    private int notificationId;
    private String appointmentId; // You can remove this field if not used
    private NotificationType type;
    private String message;
   
    private NotificationStatus status;
    private String doctorId;
   private LocalDateTime date;
}