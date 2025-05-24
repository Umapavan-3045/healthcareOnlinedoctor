package com.cts.healthcareappointment.notificationmodule.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.cts.healthcareappointment.notificationmodule.dto.Appointmentdto;
import com.cts.healthcareappointment.notificationmodule.dto.Response;
import com.cts.healthcareappointment.notificationmodule.Entity.Notification;
import com.cts.healthcareappointment.notificationmodule.service.NotificationService;

import jakarta.validation.Valid;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/notifications")
public class NotificationController {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * Notify the user and patient after an appointment is booked.
     * 
     */
    /*
     * i need end point from other appointment module GET /appointments/{appointmentId}GET /users/{userId}GET /doctors/{doctorId}
     */
//    @PostMapping("/afterBooking")
//    public ResponseEntity<Response<?>> notifyAfterBooking(@RequestBody Appointmentdto appointment) {
//       
//        
//        try {
//            notificationService.notifyAfterBooking(notification);
//            Response<String> response = new Response<>(true, HttpStatus.OK, "Notification sent successfully to patient and doctor for booking.", null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error occurred while processing booking notification: {}", ex.getMessage());
//            Response<String> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * Notify the user and patient after an appointment is canceled.
     */
    /* i need end point from sanjay appointment module
     * GET /appointments/{appointmentId}GET /users/{userId}GET /doctors/{doctorId}
     */
//    @PostMapping("/afterCancellation")
//    public ResponseEntity<Response<?>> notifyAfterCancellation(@RequestBody @Valid Notification notification, BindingResult result) {
//        if (result.hasErrors()) {
//            log.error("Validation errors: {}", result.getFieldErrors());
//            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, "Validation failed: " + result.getFieldErrors().toString());
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//        try {
//            notificationService.notifyAfterCancellation(notification);
//            Response<String> response = new Response<>(true, HttpStatus.OK, "Notification sent successfully to patient and doctor for cancellation.", null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error occurred while processing cancellation notification: {}", ex.getMessage());
//            Response<String> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * Trigger a notification for appointment completion based on the time slot.
//     */
//    @PostMapping("/checkCompletion")
//    public ResponseEntity<Response<?>> checkAppointmentsCompletion() {
//        log.info("Received request to manually trigger completion notifications.");
//        try {
//            notificationService.notifyAppointmentCompletion();
//            Response<String> response = new Response<>(true, HttpStatus.OK, "Completion notifications sent successfully to Appointment Module.", null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error occurred while triggering completion notifications: {}", ex.getMessage());
//            Response<String> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * Create a new notification.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createNotification(@RequestBody Appointmentdto appointmentdto) {
       
        try {
            String createdNotification = notificationService.createNotification(appointmentdto);
            //Response<Notification> response = new Response<>(true, HttpStatus.CREATED, createdNotification, null);
            return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Error occurred while creating a new notification: {}", ex.getMessage());
           // Response<Notification> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing notification.
     */
//    @PutMapping("/update/{notificationId}")
//    public ResponseEntity<Response<Notification>> updateNotification(
//            @PathVariable String notificationId, @RequestBody @Valid Notification notification, BindingResult result) {
//        if (result.hasErrors()) {
//            log.error("Validation errors: {}", result.getFieldErrors());
//            Response<Notification> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, "Validation failed: " + result.getFieldErrors().toString());
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//        try {
//            Notification updatedNotification = notificationService.updateNotification(notificationId, notification);
//            Response<Notification> response = new Response<>(true, HttpStatus.OK, updatedNotification, null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error occurred while updating notification: {}", ex.getMessage());
//            Response<Notification> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * Retrieve all notifications.
//     */
//    @GetMapping
//    public ResponseEntity<Response<List<Notification>>> getAllNotifications() {
//        log.info("Received request to fetch all notifications.");
//        try {
//            List<Notification> notifications = notificationService.getAllNotifications();
//            Response<List<Notification>> response = new Response<>(true, HttpStatus.OK, notifications, null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error occurred while fetching notifications: {}", ex.getMessage());
//            Response<List<Notification>> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * Retrieve notifications by userId.
//     */
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<Response<List<Notification>>> getNotificationsByUserId(@PathVariable String userId) {
//        log.info("Received request to fetch notifications for userId: {}", userId);
//        try {
//            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
//            if (notifications.isEmpty()) {
//                log.info("No notifications found for userId: {}", userId);
//                Response<List<Notification>> response = new Response<>(false, HttpStatus.NOT_FOUND, null, "No notifications found for the user.");
//                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//            }
//            Response<List<Notification>> response = new Response<>(true, HttpStatus.OK, notifications, null);
//            log.info("Fetched {} notifications for userId: {}", notifications.size(), userId);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error occurred while fetching notifications for userId: {}", ex.getMessage());
//            Response<List<Notification>> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//}
    
    @PutMapping("/onCompletion")
    public void onCompletion(@RequestBody Appointmentdto appointment) {
    	notificationService.onCompletetion(appointment);
    	}
    

@PutMapping("/onUpdate")
public void onUpdate(@RequestBody Appointmentdto appointment) {
	notificationService.onUpdate(appointment);
	}
/**
* Endpoint to fetch notifications by either doctorId or patientId.
* @param doctorId (Optional) ID of the doctor.
* @param patientId (Optional) ID of the patient.
* @return List of notifications matching the specified doctorId or patientId.
*/
@GetMapping("/fetchByDoctorOrPatient")
public ResponseEntity<List<Notification>> fetchNotificationsByDoctorOrPatient(
        @RequestParam(required = false) String doctorId,
        @RequestParam(required = false) String patientId) {
    try {
        // Validate input: at least one parameter must be provided
        if (doctorId == null && patientId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400: Bad Request
        }
 
        List<Notification> notifications = notificationService.fetchNotificationsByDoctorOrPatient(doctorId, patientId);
        if (notifications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: No Content
        }
 
        return new ResponseEntity<>(notifications, HttpStatus.OK); // 200: Success
    } catch (Exception ex) {
        log.error("Error occurred while fetching notifications: {}", ex.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500: Internal Server Error
    }
}
 
 
}
    
