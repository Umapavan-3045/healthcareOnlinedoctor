package com.dataBase.automate.controller;
import com.dataBase.automate.dto.AppointmentDto;
import com.dataBase.automate.dto.AvailabilityDto;
import com.dataBase.automate.dto.Response;
import com.dataBase.automate.exception.AppointmentNotFoundException;
import com.dataBase.automate.model.Appointment;
import com.dataBase.automate.model.Specialization;
import com.dataBase.automate.model.TimeSlots;
import com.dataBase.automate.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;


/**
 * Controller for managing appointments.
 * 
 * @Author Sanjay R
 * @Since 2025-03-18
 */
@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:3000")
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * Creates a new appointment.
     * 
     * @param availabilityId the ID of the availability
     * @return ResponseEntity containing the created appointment
     */
    @PostMapping("/create/{availabilityId}")
    public ResponseEntity<Response<?>> createAppointment(@PathVariable String availabilityId, @RequestBody AppointmentDto patientdetails) {
       
            Appointment appointment = appointmentService.createAppointment(availabilityId,patientdetails);
            Response<Appointment> response = new Response<>(true, HttpStatus.CREATED, appointment, null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        
        }
    

    /**
     * Views all appointments.
     * 
     * @return ResponseEntity containing the list of appointments
     */
    @GetMapping("/view")
    public ResponseEntity<Response<List<?>>> viewAppointments() {
        
            List<Appointment> appointments = appointmentService.viewAppointments();
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, appointments, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Fetches an appointment by ID.
     * 
     * @param id the ID of the appointment
     * @return ResponseEntity containing the appointment
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<Response<?>> fetchAppointmentById(@PathVariable String id) {
        
            Appointment appointment = appointmentService.fetchAppointmentById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + id));
            Response<?> response = new Response<>(true, HttpStatus.OK, appointment, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
        
    }

    /**
     * Updates an appointment by ID and availability ID.
     * 
     * @param appointmentId the ID of the appointment
     * @param availabilityId the new availability ID
     * @return ResponseEntity containing the updated appointment
     */
    @PutMapping("/update/{appointmentId}/{availabilityId}")
    public ResponseEntity<Response<?>> updateAppointment(@PathVariable String appointmentId, @PathVariable String availabilityId) {
       
            Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, availabilityId);
            Response<Appointment> response = new Response<>(true, HttpStatus.OK, updatedAppointment, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Cancels an appointment by ID.
     * 
     * @param id the ID of the appointment
     * @return ResponseEntity indicating the cancellation status
     */
    @DeleteMapping("/cancel/{AppointmentId}")
    public ResponseEntity<Response<?>> cancelAppointment(@PathVariable String AppointmentId) {
        
            appointmentService.fetchAppointmentById(AppointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + AppointmentId));
            appointmentService.cancelAppointment(AppointmentId);
           
            Response<?> response = new Response<>(true, HttpStatus.ACCEPTED, null, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Fetches appointments by patient ID.
     * 
     * @param patientId the ID of the patient
     * @return ResponseEntity containing the list of appointments
     */
    @GetMapping("/viewByPatient/{patientId}")
    public ResponseEntity<Response<List<?>>> fetchAppointmentsByPatientId(@PathVariable String patientId) {
        
            List<Appointment> appointments = appointmentService.fetchAppointmentsByPatientId(patientId);
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, appointments, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Fetches appointments by doctor ID.
     * 
     * @param doctorId the ID of the doctor
     * @return ResponseEntity containing the list of appointments
     */
    @GetMapping("/viewByDoctor/{doctorId}")
    public ResponseEntity<Response<List<?>>> fetchAppointmentsByDoctorId(@PathVariable String doctorId) {
       
            List<Appointment> appointments = appointmentService.fetchAppointmentsByDoctorId(doctorId);
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, appointments, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Gets available doctors by date and specialization.
     * 
     * @param date the date
     * @param specialization the specialization
     * @return ResponseEntity containing the list of available doctors
     */
    @GetMapping("/dateAndSpecialization/{date}/{specialization}")
    public ResponseEntity<Response<List<?>>> getAvailableDoctorsByDateAndSpecialization(@PathVariable LocalDate date, @PathVariable Specialization specialization) {
        
        	List<AvailabilityDto> availableDoctors = appointmentService.getAvailableDoctorsByDateAndSpecialization(date, specialization);
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, availableDoctors, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Gets available doctors by date and doctor ID.
     * 
     * @param date the date
     * @param doctorId the ID of the doctor
     * @return ResponseEntity containing the list of available doctors
     */
    @GetMapping("/dateAndId/{date}/{doctorId}")
    public ResponseEntity<Response<List<?>>> getAvailableDoctorsByDateAndId(@PathVariable LocalDate date, @PathVariable String doctorId) {
        
        	List<AvailabilityDto> availableDoctors = appointmentService.getAvailableDoctorsByDateAndId(date, doctorId);
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, availableDoctors, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    /**
     * Handles appointment notification.
     * 
     * @param appointmentId the ID of the appointment
     * @return ResponseEntity indicating the notification status
     */
    @PutMapping("/CancelledNotification/{appointmentId}")
    public ResponseEntity<Response<?>> AppointmentNotification(@PathVariable String appointmentId) {
        appointmentService.updationAfterNotification(appointmentId);
            Response<?> response = new Response<>(true, HttpStatus.OK, null, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    @GetMapping("/doctorIdAndDateRange/{doctorId}")
    public ResponseEntity<Response<List<?>>> getAvailabilityByDoctorIdAndDateRange(@PathVariable String doctorId,@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
       
        	List<AvailabilityDto> availableDoctors = appointmentService.getAvailabilityByDoctorIdAndDateRange(doctorId, startDate,endDate);
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, availableDoctors, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    @GetMapping("/specializationAndDateRange/{specialization}")
    public ResponseEntity<Response<List<?>>> getAvailabilityBySpecializationAndDateRange(@PathVariable Specialization specialization,@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
       
        	List<AvailabilityDto> availableDoctors = appointmentService.getAvailabilityBySpecializationAndDateRange(specialization, startDate,endDate);
            Response<List<?>> response = new Response<>(true, HttpStatus.OK, availableDoctors, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        
        	
            
           
        
    }
    
    /**
     * Notifies completion of an appointment.
     * 
     * @param appointmentId the ID of the appointment
     * @return ResponseEntity indicating the notification status
     */
    @GetMapping("/notifyCompletion/{appointmentId}")
    public Appointment notifyCompletion(@PathVariable String appointmentId) {
        
           return  appointmentService.notifyAfterCompletion(appointmentId);
           
        
    }
    
    @GetMapping("/checkappointment/{date}/{slot}")
    public ResponseEntity<?> checkAppointment(@PathVariable LocalDate date,@PathVariable TimeSlots slot ){
    	
    	Boolean result = appointmentService.checkAppointment(date,slot);
    	Response<?> response = new Response<>(true, HttpStatus.OK,result, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}