package com.cts.healthcareappointment.notificationmodule.test;
 
import com.cts.healthcareappointment.notificationmodule.Controller.NotificationController;
import com.cts.healthcareappointment.notificationmodule.Dao.NotificationDao;
import com.cts.healthcareappointment.notificationmodule.Entity.Notification;
import com.cts.healthcareappointment.notificationmodule.dto.Appointmentdto;
import com.cts.healthcareappointment.notificationmodule.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
 
import java.time.LocalDate;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
class NotificationUnitTest {
 
    // For NotificationController
    @InjectMocks
    private NotificationController notificationController;
 
    @Mock
    private NotificationService notificationService;
 
    // For NotificationService
    @InjectMocks
    private NotificationService notificationServiceUnderTest;
 
    @Mock
    private NotificationDao notificationDao;
 
    private Appointmentdto appointmentDto;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        appointmentDto = new Appointmentdto();
        appointmentDto.setAppointmentId("A123");
        appointmentDto.setDoctorId("D001");
        appointmentDto.setDoctorName("Dr. Smith");
        appointmentDto.setPatientName("John Doe");
        appointmentDto.setPatientId("P001");
        appointmentDto.setDate(LocalDate.of(2025, 4, 25));
    }
 
    // Unit tests for NotificationController
 
    @Test
    void testCreateNotification_Controller() {
        when(notificationService.createNotification(any(Appointmentdto.class)))
                .thenReturn("Inserted Notification successfully");
 
        ResponseEntity<String> response = notificationController.createNotification(appointmentDto);
 
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Inserted Notification successfully", response.getBody());
    }
 
    @Test
    void testFetchNotificationsByDoctorOrPatient_Controller() {
        String doctorId = "D002";
        Notification notification = new Notification();
        notification.setDoctorId(doctorId);
 
        when(notificationService.fetchNotificationsByDoctorOrPatient(eq(doctorId), isNull()))
                .thenReturn(List.of(notification));
 
        ResponseEntity<List<Notification>> response =
                notificationController.fetchNotificationsByDoctorOrPatient(doctorId, null);
 
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(doctorId, response.getBody().get(0).getDoctorId());
    }
 
    @Test
    void testOnCompletion_Controller() {
        doNothing().when(notificationService).onCompletetion(any(Appointmentdto.class));
 
        notificationController.onCompletion(appointmentDto);
 
        verify(notificationService, times(1)).onCompletetion(appointmentDto);
    }
 
    @Test
    void testOnUpdate_Controller() {
        doNothing().when(notificationService).onUpdate(any(Appointmentdto.class));
 
        notificationController.onUpdate(appointmentDto);
 
        verify(notificationService, times(1)).onUpdate(appointmentDto);
    }
 
    // Unit tests for NotificationService
 
    @Test
    void testCreateNotification_Service() {
        when(notificationDao.save(any(Notification.class))).thenReturn(new Notification());
 
        String result = notificationServiceUnderTest.createNotification(appointmentDto);
 
        assertEquals("Inserted Notification successfully", result);
        verify(notificationDao, times(2)).save(any(Notification.class)); // Verify saving both patient and doctor notifications
    }
 
    @Test
    void testOnCompletion_Service() {
        when(notificationDao.save(any(Notification.class))).thenReturn(new Notification());
 
        notificationServiceUnderTest.onCompletetion(appointmentDto);
 
        verify(notificationDao, times(1)).save(any(Notification.class));
    }
 
    @Test
    void testOnUpdate_Service() {
        when(notificationDao.save(any(Notification.class))).thenReturn(new Notification());
 
        notificationServiceUnderTest.onUpdate(appointmentDto);
 
        verify(notificationDao, times(2)).save(any(Notification.class)); // Verify saving both patient and doctor notifications
    }
 
    @Test
    void testFetchNotificationsByDoctorOrPatient_Service() {
        String doctorId = "D002";
        Notification notification = new Notification();
        notification.setDoctorId(doctorId);
 
        // Mock the DAO method
        when(notificationDao.findByDoctorId(doctorId)).thenReturn(List.of(notification));
 
        List<Notification> result = notificationServiceUnderTest.fetchNotificationsByDoctorOrPatient(doctorId, null);
 
        assertEquals(1, result.size());
        assertEquals(doctorId, result.get(0).getDoctorId());
        verify(notificationDao, times(1)).findByDoctorId(doctorId);
    }
}