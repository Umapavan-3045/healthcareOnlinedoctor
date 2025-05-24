package com.cts.healthcareappointment.notificationmodule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cts.healthcareappointment.notificationmodule.Dao.NotificationDao;
import com.cts.healthcareappointment.notificationmodule.Entity.Notification;
import com.cts.healthcareappointment.notificationmodule.Entity.NotificationStatus;
import com.cts.healthcareappointment.notificationmodule.Entity.NotificationType;
import com.cts.healthcareappointment.notificationmodule.Entity.Status;
import com.cts.healthcareappointment.notificationmodule.Entity.TimeSlots;
import com.cts.healthcareappointment.notificationmodule.client.Appointmentclient;
import com.cts.healthcareappointment.notificationmodule.dto.Appointmentdto;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificationService {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private NotificationDao notificationDao;
    
    @Autowired
    private Appointmentclient appointmentClient;

   

    // Other methods...

//    public void scheduleNotification(Appointmentdto appointmentDto) {
//        LocalDateTime appointmentTime = getAppointmentTime(appointmentDto.getTimeSlot());
//        LocalDateTime notificationTime = appointmentTime.minusMinutes(10);
//
//        long delay = calculateDelay(notificationTime);
//
//        scheduler.schedule(() -> createNotification(appointmentDto), delay, TimeUnit.MILLISECONDS);
//    }

   

//    private long calculateDelay(LocalDateTime notificationTime) {
//        LocalDateTime now = LocalDateTime.now();
//        return notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
//               now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//    }

//    
//    public List<Notification> getNotificationsByUserId(String userId) {
//        log.info("Fetching notifications for userId: {}", userId);
//        return notificationDao.findByUserId(userId);
//    }
    
    /**
     * Notify users and patients when an appointment is booked.
     */
//    public void notifyAfterBooking(Notification bookingNotification) {
//        log.info("Processing booking notification: {}", bookingNotification);
//        try {
//            bookingNotification.setType(NotificationType.APPOINTMENT);
//            bookingNotification.setStatus(NotificationStatus.Confirmed);
//          //  bookingNotification.setSeenStatus(false);
//            notificationDao.save(bookingNotification);
//
//            log.info("Booking notification saved successfully.");
//            System.out.println("Notification sent successfully to patient and doctor for booking.");
//        } catch (Exception ex) {
//            log.error("Error while sending booking notification: {}", ex.getMessage());
//            throw new RuntimeException("Error in sending notification for appointment booking", ex);
//        }
//    }

    /**
     * Update an existing notification by its ID.
     */
//    public Notification updateNotification(String notificationId, Notification notificationDetails) {
//        log.info("Updating notification with ID: {}", notificationId);
//
//        // Fetch the existing notification from the database
//        Notification existingNotification = notificationDao.findById(notificationId)
//                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
//
//        // Update the relevant fields
//        existingNotification.setType(notificationDetails.getType());
//        existingNotification.setStatus(notificationDetails.getStatus());
//        existingNotification.setMessage(notificationDetails.getMessage());
//        existingNotification.setDoctorId(notificationDetails.getDoctorId());
//       // existingNotification.setSeenStatus(notificationDetails.isSeenStatus());
//        existingNotification.setPatientId(notificationDetails.getPatientId());
//
//        // Save the updated notification
//        Notification updatedNotification = notificationDao.save(existingNotification);
//        log.info("Updated notification with ID: {}", notificationId);
//        return updatedNotification;
//    }
//
//    
    
    /**
     * Notify users and patients when an appointment is canceled.
     */
//    public void notifyAfterCancellation(Notification cancellationNotification) {
//        log.info("Processing cancellation notification: {}", cancellationNotification);
//        try {
//            cancellationNotification.setType(NotificationType.APPOINTMENT);
//            cancellationNotification.setStatus(NotificationStatus.Cancelled);
//           // cancellationNotification.setSeenStatus(false);
//            notificationDao.save(cancellationNotification);
//
//            log.info("Cancellation notification saved successfully.");
//            System.out.println("Notification sent successfully to patient and doctor for cancellation.");
//        } catch (Exception ex) {
//            log.error("Error while sending cancellation notification: {}", ex.getMessage());
//            throw new RuntimeException("Error in sending notification for appointment cancellation", ex);
//        }
//    }

    /**
     * Notify Appointment Module when an appointment is completed based on the time slot.
     */
  /*  @Scheduled(fixedRate = 60000)
    public void notifyAppointmentCompletion() {
        log.info("Checking for appointments to mark as completed...");
        try {
            List<Notification> notifications = notificationDao.findAll();
            LocalDateTime now = LocalDateTime.now();

            for (Notification notification : notifications) {
                if (notification.getStatus() == NotificationStatus.Confirmed && notification.getType() == NotificationType.APPOINTMENT) {
                    log.info("Marking appointment as completed: {}", notification.getNotification_id());

                    notification.setStatus(NotificationStatus.Completed);
                   // notification.setSeenStatus(true);
                    notificationDao.save(notification);

                    log.info("Appointment completion notification processed successfully.");
                    System.out.println("Notification sent successfully to Appointment Module for completion.");
                }
            }
        } catch (Exception ex) {
            log.error("Error while notifying appointment completion: {}", ex.getMessage());
        }
    }
    */


    public String createNotification(Appointmentdto appointmentDto) {
    	
        try {
            log.info("Creating a new notification for appointmentId: {}", appointmentDto.getAppointmentId());

            // Create and populate the notification object
            Notification patientNotification = new Notification();
            Notification doctorNotification = new Notification();
            if (appointmentDto.getStatus() == Status.Booked) {
            	
                 patientNotification.setPatientId(appointmentDto.getPatientId());
                 patientNotification.setDoctorId(null);
                 patientNotification.setType(NotificationType.APPOINTMENT);
                 patientNotification.setAppointmentId(appointmentDto.getAppointmentId());
                 
                patientNotification.setMessage("Appointment is Booked  with doctor: "+ appointmentDto.getDoctorName()+" on "+appointmentDto.getDate()+" at "+appointmentDto.getTimeSlot());
                patientNotification.setStatus(NotificationStatus.Booked);
                patientNotification.setDate( LocalDateTime.now());
                
                doctorNotification.setPatientId(null);
                doctorNotification.setDoctorId(appointmentDto.getDoctorId());
                doctorNotification.setType(NotificationType.APPOINTMENT);
                doctorNotification.setAppointmentId(appointmentDto.getAppointmentId());

               doctorNotification.setMessage("Appointment is Booked  with Patient: "+ appointmentDto.getPatientName()+" on "+appointmentDto.getDate()+" at "+appointmentDto.getTimeSlot());
               doctorNotification.setStatus(NotificationStatus.Booked);
               doctorNotification.setDate( LocalDateTime.now());
               
               
            } else if (appointmentDto.getStatus() == Status.Cancelled) {
            	
            	

                patientNotification.setMessage("Appointment is Cancelled with Doctor: "+ appointmentDto.getDoctorName()+" on "+appointmentDto.getDate()+" at "+appointmentDto.getTimeSlot());
                patientNotification.setPatientId(appointmentDto.getPatientId());
                patientNotification.setDoctorId(null);
                patientNotification.setType(NotificationType.APPOINTMENT);
                patientNotification.setAppointmentId(appointmentDto.getAppointmentId());
                patientNotification.setStatus(NotificationStatus.Cancelled);
                patientNotification.setDate( LocalDateTime.now());
                
                
                
                doctorNotification.setPatientId(null);
                doctorNotification.setDoctorId(appointmentDto.getDoctorId());
                doctorNotification.setType(NotificationType.APPOINTMENT);
                doctorNotification.setAppointmentId(appointmentDto.getAppointmentId());
                
                doctorNotification.setMessage("Appointment is Cancelled with Patient: "+ appointmentDto.getPatientName()+" on "+appointmentDto.getDate()+" at "+appointmentDto.getTimeSlot());
                doctorNotification.setStatus(NotificationStatus.Cancelled);
                doctorNotification.setDate( LocalDateTime.now());
            }
            
            notificationDao.save(patientNotification);
            notificationDao.save(doctorNotification);
            log.info("Notification created successfully with ID: {}", patientNotification.getNotification_id());
            log.info("Notification created successfully with ID: {}", doctorNotification.getNotification_id());

           
            return "Inserted Notification successfully";
        } catch (Exception ex) {
        	ex.printStackTrace();
            log.error("Error while creating notification: {}", ex.getMessage());
            throw new RuntimeException("Unable to create notification", ex);
        }
    }

//    public Notification createNotification(Appointmentdto appointmentDto) {
//            log.info("Creating a new notification for appointmentId: {}", appointmentDto.getAppointmentId());
//
//            // Create and populate the notification object
//            Notification notification = new Notification();
//            notification.setStatus(NotificationStatus.Booked); // Set status as 'Booked'
//                // Save the notification using the DAO
//            Notification savedNotification = notificationDao.save(notification);
//            log.info("Notification created successfully with ID: {}", savedNotification.getNotification_id());
//
//            // Return the saved notification object
//            return savedNotification;
//    }


    /**
     * Fetch all notifications for logging or monitoring purposes.
     */
    public List<Notification> getAllNotifications() {
        log.info("Fetching all notifications...");
        return notificationDao.findAll();
    }



	public void onCompletetion(Appointmentdto appointment) {
		log.info("Appointment completed succesfully and consultaion records are inserted");
		Notification notification =new Notification();
		notification.setStatus(NotificationStatus.Completed);
		notification.setMessage("Appointment has been completed and the consulation records are provided from "+appointment.getDoctorName());
		notification.setPatientId(appointment.getPatientId());
        notification.setDoctorId(null);
        notification.setType(NotificationType.APPOINTMENT);
        notification.setAppointmentId(appointment.getAppointmentId());
        notification.setDate( LocalDateTime.now());
		notificationDao.save(notification);
		
	}

	public void onUpdate(Appointmentdto appointment) {
		// TODO Auto-generated method stub
		log.info("Appointment has been rescheduled");
		Notification PatientNotification = new Notification();
		Notification DoctorNotification = new Notification();
		
		
		PatientNotification.setMessage("Appointment has been Rescheduled with Doctor "+ appointment.getDoctorName()+" to "+appointment.getDate()+"("+appointment.getTimeSlot()+")");
		 PatientNotification.setPatientId(appointment.getPatientId());
         PatientNotification.setDoctorId(null);
         PatientNotification.setType(NotificationType.APPOINTMENT);
         PatientNotification.setAppointmentId(appointment.getAppointmentId());
         PatientNotification.setStatus(NotificationStatus.Booked);
        
         
		 PatientNotification.setDate( LocalDateTime.now());
		 
		 
		 DoctorNotification.setDate( LocalDateTime.now());
		 DoctorNotification.setMessage("Appointment has been Rescheduled with Patient "+ appointment.getPatientName()+" to "+appointment.getDate()+"("+appointment.getTimeSlot()+")");

		 DoctorNotification.setPatientId(null);
         DoctorNotification.setDoctorId(appointment.getDoctorId());
         DoctorNotification.setType(NotificationType.APPOINTMENT);
         DoctorNotification.setAppointmentId(appointment.getAppointmentId());
         
        
         DoctorNotification.setStatus(NotificationStatus.Booked);
		notificationDao.save(PatientNotification);
		
		notificationDao.save(DoctorNotification);

		
	}
	public List<Notification> fetchNotificationsByDoctorOrPatient(String doctorId, String patientId) {
        try {
            log.info("Fetching notifications for doctorId: {} or patientId: {}", doctorId, patientId);
 
            // Use appropriate repository methods depending on the provided parameters
            if (doctorId != null && patientId != null) {
                return notificationDao.findByDoctorIdOrPatientId(doctorId, patientId);
            } else if (doctorId != null) {
                return notificationDao.findByDoctorId(doctorId);
            } else if (patientId != null) {
                return notificationDao.findByPatientId(patientId);
            } else {
                return List.of(); // Return an empty list if no valid input
            }
        } catch (Exception ex) {
            log.error("Error occurred while fetching notifications: {}", ex.getMessage());
            throw new RuntimeException("Unable to fetch notifications", ex);
        }
    }
 
 
}
