package com.dataBase.automate;
 
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
 
 
 
 
import com.dataBase.automate.dto.AppointmentDto;
import com.dataBase.automate.dto.AvailabilityDto;
import com.dataBase.automate.exception.AppointmentNotFoundException;
import com.dataBase.automate.exception.AvailabilityConflictException;
import com.dataBase.automate.exception.AvailabilityNotFoundException;
import com.dataBase.automate.feignClients.AvailabilityFeignClient;
import com.dataBase.automate.feignClients.NotificationFeignClient;
import com.dataBase.automate.model.Appointment;
import com.dataBase.automate.model.Specialization;
import com.dataBase.automate.model.Status;
import com.dataBase.automate.model.TimeSlots;
import com.dataBase.automate.repository.AppointmentRepository;
import com.dataBase.automate.service.AppointmentService;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
 
    @Mock
    private AppointmentRepository appointmentRepository;
 
    @Mock
    private AvailabilityFeignClient availabilityFeignClient;
 
    @Mock
    private NotificationFeignClient notificationFeignClient;
 
    @InjectMocks
    private AppointmentService appointmentService;
 
    private Appointment appointment1;
    private Appointment appointment2;
    private AvailabilityDto availabilityDto1;
    private AvailabilityDto availabilityDto2;
    private AppointmentDto patientDetailsDto;
 
    @BeforeEach
    void setUp() {
        // Sample Availability Data
        availabilityDto1 = new AvailabilityDto();
        availabilityDto1.setAvailabilityId("avail-123");
        availabilityDto1.setDoctorId("doc-101");
        availabilityDto1.setDate(LocalDate.of(2025, 4, 20));
        availabilityDto1.setTimeSlots(TimeSlots.NINE_TO_ELEVEN);
 
        availabilityDto2 = new AvailabilityDto();
        availabilityDto2.setAvailabilityId("avail-456");
        availabilityDto2.setDoctorId("doc-102");
        availabilityDto2.setDate(LocalDate.of(2025, 4, 21));
        availabilityDto2.setTimeSlots(TimeSlots.TWO_TO_FOUR);
 
        // Sample Appointment Data
        appointment1 = new Appointment();
        appointment1.setAppointmentId("appt-1");
        appointment1.setAvailabilityId("avail-123");
        appointment1.setPatientId("pat-1");
        appointment1.setDoctorId("doc-101");
        appointment1.setDate(LocalDate.of(2025, 4, 20));
        appointment1.setTimeSlot(TimeSlots.NINE_TO_ELEVEN);
        appointment1.setStatus(Status.Booked);
        appointment1.setPatientName("Alice");
        appointment1.setDoctorName("Dr. Smith");
 
        appointment2 = new Appointment();
        appointment2.setAppointmentId("appt-2");
        appointment2.setAvailabilityId("avail-456");
        appointment2.setPatientId("pat-2");
        appointment2.setDoctorId("doc-102");
        appointment2.setDate(LocalDate.of(2025, 4, 21));
        appointment2.setTimeSlot(TimeSlots.TWO_TO_FOUR);
        appointment2.setStatus(Status.Booked);
        appointment2.setPatientName("Bob");
        appointment2.setDoctorName("Dr. Jones");
 
        // Sample Patient Details DTO
        patientDetailsDto = new AppointmentDto();
        patientDetailsDto.setPatientId("pat-1");
        patientDetailsDto.setPatientName("Alice");
        patientDetailsDto.setDoctorName("Dr. Smith");
    }
 
    @Test
    void createAppointment_validAvailabilityId_returnsSavedAppointment() {
        when(availabilityFeignClient.viewById("avail-123")).thenReturn(new ResponseEntity<>(availabilityDto1, HttpStatus.OK));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment1);
        when(availabilityFeignClient.bookTimeSlot("avail-123")).thenReturn(new ResponseEntity<>(availabilityDto1, HttpStatus.OK));
        when(notificationFeignClient.createNotification(any(AppointmentDto.class))).thenReturn(new ResponseEntity<>("Notification sent", HttpStatus.OK));
 
        Appointment createdAppointment = appointmentService.createAppointment("avail-123", patientDetailsDto);
 
        assertNotNull(createdAppointment);
        assertEquals("avail-123", createdAppointment.getAvailabilityId());
        assertEquals(Status.Booked, createdAppointment.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(availabilityFeignClient, times(1)).bookTimeSlot("avail-123");
        verify(notificationFeignClient, times(1)).createNotification(any(AppointmentDto.class));
    }
 
    @Test
    void createAppointment_nullAvailabilityId_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(null, patientDetailsDto);
        });
        assertEquals("Error :Availability ID cannot be null", exception.getMessage());
        verifyNoInteractions(availabilityFeignClient);
        verifyNoInteractions(appointmentRepository);
        verifyNoInteractions(notificationFeignClient);
    }
 
    @Test
    void createAppointment_availabilityNotFound_throwsAvailabilityNotFoundException() {
        when(availabilityFeignClient.viewById("avail-123")).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
 
        AvailabilityNotFoundException exception = assertThrows(AvailabilityNotFoundException.class, () -> {
            appointmentService.createAppointment("avail-123", patientDetailsDto);
        });
        assertEquals("Error :Availability is not fetched from client", exception.getMessage());
        verify(availabilityFeignClient, times(1)).viewById("avail-123");
        verifyNoInteractions(appointmentRepository);
        verifyNoInteractions(notificationFeignClient);
    }
 
    @Test
    void viewAppointments_appointmentsExist_returnsListOfAppointments() {
        List<Appointment> appointments = List.of(appointment1, appointment2);
        when(appointmentRepository.findAll()).thenReturn(appointments);
 
        List<Appointment> retrievedAppointments = appointmentService.viewAppointments();
 
        assertNotNull(retrievedAppointments);
        assertEquals(2, retrievedAppointments.size());
        assertEquals("appt-1", retrievedAppointments.get(0).getAppointmentId());
        assertEquals("appt-2", retrievedAppointments.get(1).getAppointmentId());
        verify(appointmentRepository, times(1)).findAll();
    }
 
    @Test
    void viewAppointments_noAppointmentsExist_throwsAppointmentNotFoundException() {
        when(appointmentRepository.findAll()).thenReturn(null);
 
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.viewAppointments();
        });
        assertEquals("Error : List of appointments couldn't be fetched", exception.getMessage());
        verify(appointmentRepository, times(1)).findAll();
    }
 
    @Test
    void fetchAppointmentById_validId_returnsOptionalOfAppointment() {
        when(appointmentRepository.findById("appt-1")).thenReturn(Optional.of(appointment1));
 
        Optional<Appointment> fetchedAppointment = appointmentService.fetchAppointmentById("appt-1");
 
        assertTrue(fetchedAppointment.isPresent());
        assertEquals("appt-1", fetchedAppointment.get().getAppointmentId());
        verify(appointmentRepository, times(1)).findById("appt-1");
    }
 
    @Test
    void fetchAppointmentById_invalidId_throwsAppointmentNotFoundException() {
        when(appointmentRepository.findById("non-existent")).thenReturn(Optional.empty());
 
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.fetchAppointmentById("non-existent");
        });
        assertEquals("Error :Appointment not found for ID: non-existent", exception.getMessage());
        verify(appointmentRepository, times(1)).findById("non-existent");
    }
 
   
    @Test
    void updateAppointment_nullIds_throwsIllegalArgumentException() {
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.updateAppointment(null, "avail-789");
        });
        assertEquals("Appointment ID and New Availability ID cannot be null", exception1.getMessage());
 
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.updateAppointment("appt-1", null);
        });
        assertEquals("Appointment ID and New Availability ID cannot be null", exception2.getMessage());
 
        verifyNoInteractions(appointmentRepository);
        verifyNoInteractions(availabilityFeignClient);
        verifyNoInteractions(notificationFeignClient);
    }
 
    @Test
    void updateAppointment_appointmentNotFound_throwsAppointmentNotFoundException() {
        when(appointmentRepository.findById("non-existent")).thenReturn(Optional.empty());
 
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.updateAppointment("non-existent", "avail-789");
        });
        assertEquals("Appointment not found for ID: non-existent", exception.getMessage());
        verify(appointmentRepository, times(1)).findById("non-existent");
        verifyNoInteractions(availabilityFeignClient);
        verifyNoInteractions(notificationFeignClient);
    }
 
   
 
   
 
    @Test
    void cancelAppointment_validId_updatesStatusAndNotifies() {
        when(appointmentRepository.findById("appt-1")).thenReturn(Optional.of(appointment1));
        when(availabilityFeignClient.cancelAvailability("avail-123")).thenReturn(new ResponseEntity<>("Availability updated", HttpStatus.OK));
        when(notificationFeignClient.createNotification(any(AppointmentDto.class))).thenReturn(new ResponseEntity<>("Cancellation notification sent", HttpStatus.OK));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment1);
 
        appointmentService.cancelAppointment("appt-1");
 
        assertEquals(Status.Cancelled, appointment1.getStatus());
        verify(appointmentRepository, times(1)).findById("appt-1");
        verify(availabilityFeignClient, times(1)).cancelAvailability("avail-123");
        verify(notificationFeignClient, times(1)).createNotification(any(AppointmentDto.class));
        verify(appointmentRepository, times(1)).save(appointment1);
    }
 
    @Test
    void cancelAppointment_nullId_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.cancelAppointment(null);
        });
        assertEquals("Appointment ID cannot be null", exception.getMessage());
        verifyNoInteractions(appointmentRepository);
        verifyNoInteractions(availabilityFeignClient);
        verifyNoInteractions(notificationFeignClient);
    }
 
    @Test
    void cancelAppointment_appointmentNotFound_throwsAppointmentNotFoundException() {
        when(appointmentRepository.findById("non-existent")).thenReturn(Optional.empty());
 
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.cancelAppointment("non-existent");
        });
        assertEquals("Appointment not found for ID: non-existent", exception.getMessage());
        verify(appointmentRepository, times(1)).findById("non-existent");
        verifyNoInteractions(availabilityFeignClient);
        verifyNoInteractions(notificationFeignClient);
    }
 
    @Test
    void cancelAppointment_availabilityNotDeleted_throwsAvailabilityNotFoundException() {
        when(appointmentRepository.findById("appt-1")).thenReturn(Optional.of(appointment1));
        when(availabilityFeignClient.cancelAvailability("avail-123")).thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
 
        AvailabilityNotFoundException exception = assertThrows(AvailabilityNotFoundException.class, () -> {
            appointmentService.cancelAppointment("appt-1");
        });
        assertEquals("Error :Availability is not deleted in client", exception.getMessage());
        verify(appointmentRepository, times(1)).findById("appt-1");
        verify(availabilityFeignClient, times(1)).cancelAvailability("avail-123");
        verify(notificationFeignClient, times(0)).createNotification(any(AppointmentDto.class));
        verify(appointmentRepository, times(0)).save(any(Appointment.class));
    }
 
    @Test
    void fetchAppointmentsByDoctorId_validDoctorId_returnsListOfAppointments() {
        List<Appointment> doctorAppointments = List.of(appointment1);
        when(appointmentRepository.findByDoctorId("doc-101")).thenReturn(doctorAppointments);
 
        List<Appointment> fetchedAppointments = appointmentService.fetchAppointmentsByDoctorId("doc-101");
 
        assertNotNull(fetchedAppointments);
        assertEquals(1, fetchedAppointments.size());
        assertEquals("appt-1", fetchedAppointments.get(0).getAppointmentId());
        verify(appointmentRepository, times(1)).findByDoctorId("doc-101");
    }
 
    @Test
    void fetchAppointmentsByDoctorId_invalidDoctorId_returnsEmptyList() {
        List<Appointment> emptyList = List.of();
        when(appointmentRepository.findByDoctorId("non-existent-doc")).thenReturn(emptyList);
 
        List<Appointment> fetchedAppointments = appointmentService.fetchAppointmentsByDoctorId("non-existent-doc");
 
        assertNotNull(fetchedAppointments);
        assertTrue(fetchedAppointments.isEmpty());
        verify(appointmentRepository, times(1)).findByDoctorId("non-existent-doc");
    }
 
  
 
    @Test
    void fetchAppointmentsByPatientId_validPatientId_returnsListOfAppointments() {
        List<Appointment> patientAppointments = List.of(appointment1);
        when(appointmentRepository.findByPatientId("pat-1")).thenReturn(patientAppointments);
 
        List<Appointment> fetchedAppointments = appointmentService.fetchAppointmentsByPatientId("pat-1");
 
        assertNotNull(fetchedAppointments);
        assertEquals(1, fetchedAppointments.size());
        assertEquals("appt-1", fetchedAppointments.get(0).getAppointmentId());
        verify(appointmentRepository, times(1)).findByPatientId("pat-1");
    }
 
    @Test
    void fetchAppointmentsByPatientId_invalidPatientId_returnsEmptyList() {
        List<Appointment> emptyList = List.of();
        when(appointmentRepository.findByPatientId("non-existent-pat")).thenReturn(emptyList);
 
        List<Appointment> fetchedAppointments = appointmentService.fetchAppointmentsByPatientId("non-existent-pat");
 
        assertNotNull(fetchedAppointments);
        assertTrue(fetchedAppointments.isEmpty());
        verify(appointmentRepository, times(1)).findByPatientId("non-existent-pat");
    }
 
}
 
 