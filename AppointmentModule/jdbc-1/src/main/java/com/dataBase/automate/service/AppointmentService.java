package com.dataBase.automate.service;


import com.dataBase.automate.dto.AppointmentDto;
import com.dataBase.automate.dto.AvailabilityDto;
import com.dataBase.automate.dto.Response;
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
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing appointments.
 * 
 * @Author Sanjay R
 * @Since 2025-03-18
 */
@Slf4j
@Service
public class AppointmentService {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);
	
	@Autowired
	private AvailabilityFeignClient availabilityFeignClient; 

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationFeignClient notificationFeignClient;
    
   

    /**
     * Creates a new appointment.
     * 
     * @param availabilityId the ID of the availability
     * @return the created appointment
     */
    public Appointment createAppointment(String availabilityId , AppointmentDto patientdetails) {
        if (availabilityId == null) {
            throw new IllegalArgumentException("Error :Availability ID cannot be null");
        }
        
        AvailabilityDto availability = null;
       
            availability = availabilityFeignClient.viewById(availabilityId).getBody();
            if(availability == null) {
            	throw new AvailabilityNotFoundException("Error :Availability is not fetched from client");
            
        }Appointment appointment = new Appointment();
        appointment.setTimeSlot(availability.getTimeSlots());
        
        appointment.setStatus(Status.Booked);
        appointment.setDoctorName(patientdetails.getDoctorName());
        appointment.setDoctorId(availability.getDoctorId());
        appointment.setPatientId(patientdetails.getPatientId()); // Manual Set
        appointment.setDate(availability.getDate());
        appointment.setAvailabilityId(availabilityId);
        appointment.setPatientName(patientdetails.getPatientName());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        notifyAfterBooking(appointment,patientdetails.getPatientName());
        return savedAppointment;
    }

    /**
     * Views all appointments.
     * 
     * @return the list of appointments
     */
    public List<Appointment> viewAppointments() {
    	List<Appointment> appointmentList =appointmentRepository.findAll();
    	if(appointmentList == null) {
    		throw new AppointmentNotFoundException("Error : List of appointments couldn't be fetched");}
    		return appointmentList;
    	
    }

    /**
     * Fetches an appointment by ID.
     * 
     * @param appointmentId the ID of the appointment
     * @return the appointment
     */
    public Optional<Appointment> fetchAppointmentById(String appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Error :Appointment ID cannot be null");
        }

        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            log.info("Exiting fetchAppointmentById method");
            return optionalAppointment;
        } else {
            log.error("Appointment not found for ID: {}", appointmentId);
            throw new AppointmentNotFoundException("Error :Appointment not found for ID: " + appointmentId);
        }
    }

    /**
     * Updates an appointment by ID and new availability ID.
     * 
     * @param appointmentId the ID of the appointment
     * @param newAvailabilityId the new availability ID
     * @return the updated appointment
     */
    public Appointment updateAppointment(String appointmentId, String newAvailabilityId) {
        if (appointmentId == null || newAvailabilityId == null) {
            throw new IllegalArgumentException("Appointment ID and New Availability ID cannot be null");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + appointmentId));

        Optional<Appointment> conflictingAppointment = appointmentRepository.findByAvailabilityId(newAvailabilityId);
        if (conflictingAppointment.isPresent() && !conflictingAppointment.get().getAppointmentId().equals(appointmentId)) {
            throw new AvailabilityConflictException("Pick another availability");
        }
       String oldAvailabilityId =  appointment.getAvailabilityId();
        AvailabilityDto availability = availabilityFeignClient.viewById(newAvailabilityId).getBody();
        if(availability == null) {
        	throw new AvailabilityNotFoundException("Error :Availability is not fetched from client");
        }
                

        appointment.setAvailabilityId(newAvailabilityId);
        appointment.setTimeSlot(availability.getTimeSlots());
        appointment.setDate(availability.getDate());
        appointment.setDoctorId(availability.getDoctorId());
        notifyAfterUpdate(oldAvailabilityId, newAvailabilityId,appointment);

        return appointmentRepository.save(appointment);
    }

    /**
     * Cancels an appointment by ID.
     * 
     * @param id the ID of the appointment
     */
    public void cancelAppointment(String AppointmentId) {
        if (AppointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }

        Appointment appointment = appointmentRepository.findById(AppointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + AppointmentId));
        appointment.setStatus(Status.Cancelled);
        //logic for notification module-got
        String availability = availabilityFeignClient.cancelAvailability(appointment.getAvailabilityId()).getBody();
        if(availability == null) {
        	throw new AvailabilityNotFoundException("Error :Availability is not deleted in client");
        }
        
        AppointmentDto appointmentDto = new AppointmentDto();
        
        appointmentDto.setAppointmentId(appointment.getAppointmentId());
        appointmentDto.setTimeSlot(appointment.getTimeSlot());
        appointmentDto.setStatus(appointment.getStatus());
        appointmentDto.setAvailabilityId(appointment.getAvailabilityId());
        appointmentDto.setPatientId(appointment.getPatientId());
        appointmentDto.setDoctorId(appointment.getDoctorId());
        appointmentDto.setDate(appointment.getDate());
        appointmentDto.setPatientName(appointment.getPatientName());
        appointmentDto.setDoctorName(appointment.getDoctorName());
        
        String notification = notificationFeignClient.createNotification(appointmentDto).getBody(); 
        appointmentRepository.save(appointment);
    }

    /**
     * Fetches appointments by patient ID.
     * 
     * @param patientId the ID of the patient
     * @return the list of appointments
     */
    public List<Appointment> fetchAppointmentsByPatientId(String patientId) {
        
            List<Appointment> appointmentList= appointmentRepository.findByPatientId(patientId);
            if(appointmentList == null) {
            	throw new AppointmentNotFoundException("Error : Availability for PatientId is not found");
            	
            }
       return appointmentList;
    }

    /**
     * Fetches appointments by doctor ID.
     * 
     * @param doctorId the ID of the doctor
     * @return the list of appointments
     */
    public List<Appointment> fetchAppointmentsByDoctorId(String doctorId) {
    	 List<Appointment> appointmentList= appointmentRepository.findByDoctorId(doctorId);
         if(appointmentList == null) {
         	throw new AppointmentNotFoundException("Error : Availability for PatientId is not found");
         	
         }
    return appointmentList;
    }

    /**
     * Notifies the doctor for removal of an appointment.
     * 
     * @param doctorId the ID of the doctor
     * @param patientId the ID of the patient
     * @param id the ID of the appointment
     */
//    public void notifyDoctorForRemoval(String doctorId, String patientId, String id) {
//        if (doctorId == null || patientId == null || id == null) {
//            throw new IllegalArgumentException("Doctor ID or Patient ID and Appointment ID cannot be null");
//        }
//
//        Appointment appointment = appointmentRepository.findById(id)
//                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + id));
//        appointment.setStatus(Status.Cancelled);
//        appointmentRepository.save(appointment);
//        log.info("Doctor {} notified for schedule cancellation for patient {}", doctorId, patientId);
//    }

    /**
     * Notifies after booking an appointment.
     * 
     * @param appointment the appointment
     */
    public void notifyAfterBooking(Appointment appointment,String patientName) {
        if (appointment == null) {
            throw new AppointmentNotFoundException("Appointment not found");
        }

        // logic for Notification module, done
        AvailabilityDto availabilityDto = availabilityFeignClient.bookTimeSlot(appointment.getAvailabilityId()).getBody();
        if(availabilityDto == null) {
        	throw new AvailabilityNotFoundException("Error :Couldn't block the time slot");
        }
        log.info("Booked succesfully");
        AppointmentDto appointmentDto = new AppointmentDto();
        
        appointmentDto.setAppointmentId(appointment.getAppointmentId());
        appointmentDto.setTimeSlot(appointment.getTimeSlot());
        appointmentDto.setStatus(appointment.getStatus());
        appointmentDto.setAvailabilityId(appointment.getAvailabilityId());
        appointmentDto.setPatientId(appointment.getPatientId());
        appointmentDto.setDoctorId(appointment.getDoctorId());
        appointmentDto.setDate(appointment.getDate());
        appointmentDto.setPatientName(appointment.getPatientName());
        appointmentDto.setDoctorName(appointment.getDoctorName());
        
        
        String appointmentNotification = notificationFeignClient.createNotification(appointmentDto).getBody();
if(appointmentNotification == null) {
	throw new AppointmentNotFoundException("Error :Couldn't create notification for the client");
}
log.info(appointmentNotification);
    }

    /**
     * Notifies after updating an appointment.
     * 
     * @param availabilityId the old availability ID
     * @param newAvailabilityId the new availability ID
     */
    public void notifyAfterUpdate(String availabilityId, String newAvailabilityId,Appointment appointment) {
        if (availabilityId == null || newAvailabilityId == null) {
            throw new IllegalArgumentException("Availability ID and New Availability ID cannot be null");    
        }
        String availability = availabilityFeignClient.updateAvailability(availabilityId, newAvailabilityId).getBody();
        if(availability == null) {
        	throw new AvailabilityConflictException("Error :Couldn't update the availability with client");
        }
        log.info("updated succesfully");
      
        
        notificationFeignClient.onUpdate(appointment);
    }

    /**
     * Deletes an appointment by ID.
     * 
     * @param appointmentId the ID of the appointment
     */
    public void deleteAppointment(String appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }

        if (appointmentRepository.existsById(appointmentId)) {
            appointmentRepository.deleteById(appointmentId);
        } else {
            throw new AppointmentNotFoundException("Appointment not found for ID: " + appointmentId);
        }
    }

    /**
     * Notifies after completing an appointment.
     * 
     * @param appointmentId the ID of the appointment
     */
    public Appointment notifyAfterCompletion(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + appointmentId));
        appointment.setStatus(Status.Completed);
        appointmentRepository.save(appointment);
        
        AppointmentDto appointmentDto =new AppointmentDto();
        appointmentDto.setAppointmentId(appointment.getAppointmentId());
        appointmentDto.setTimeSlot(appointment.getTimeSlot());
        appointmentDto.setStatus(appointment.getStatus());
        appointmentDto.setAvailabilityId(appointment.getAvailabilityId());
        appointmentDto.setPatientId(appointment.getPatientId());
        appointmentDto.setDoctorId(null);
        appointmentDto.setDate(appointment.getDate());
        appointmentDto.setPatientName(appointment.getPatientName());
        appointmentDto.setDoctorName(appointment.getDoctorName());
        
        notificationFeignClient.onCompletion(appointmentDto);
        // logic for Consultation module
        log.info("Appointment completed Successfully");
        return appointment;
    }

    
    
    
    public List<AvailabilityDto> getAvailabilityByDoctorIdAndDateRange(String doctorId, LocalDate startDate,LocalDate endDate) {
        if (endDate == null || startDate == null) {
            throw new IllegalArgumentException("Date  cannot be null");
        }
        
        	List<AvailabilityDto> availabilityList = availabilityFeignClient.getAvailabilityByDoctorIdAndDateRange(doctorId,startDate,endDate);
        	if(availabilityList == null) {
        		throw new AvailabilityNotFoundException("Error :Couldn't get availability list from client");
        	}
        	
        		return availabilityList;
    }
    
    public List<AvailabilityDto> getAvailabilityBySpecializationAndDateRange(Specialization specialization, LocalDate startDate,LocalDate endDate) {
        if (endDate == null || startDate == null) {
            throw new IllegalArgumentException("Date  cannot be null");
        }
        
        	List<AvailabilityDto> availabilityList = availabilityFeignClient.getAvailabilityBySpecializationAndDateRange(specialization,startDate,endDate);
        	if(availabilityList == null) {
        		throw new AvailabilityNotFoundException("Error :Couldn't get availability list from client");
        	}
        		
        	return availabilityList;
    }
    /**
     * Gets available doctors by date and specialization.
     * 
     * @param date the date
     * @param specialization the specialization
     * @return the list of available doctors
     */
    public List<AvailabilityDto> getAvailableDoctorsByDateAndSpecialization(LocalDate date, Specialization specialization) {
        if (date == null || specialization == null) {
            throw new IllegalArgumentException("Date and Specialization cannot be null");
        }
        
        List<AvailabilityDto> availabilityList = availabilityFeignClient.getAvailabilityBySpecializationAndDate(specialization,date);
        if(availabilityList == null) {
        	throw new AvailabilityNotFoundException("Error :Couldn't get availability list from client");
}
        
        	return availabilityList;
    }

    /**
     * Gets available doctors by date and doctor ID.
     * 
     * @param date the date
     * @param doctorId the ID of the doctor
     * @return the list of available doctors
     */
    public List<AvailabilityDto> getAvailableDoctorsByDateAndId(LocalDate date, String doctorId) {
        if (date == null || doctorId == null) {
            throw new IllegalArgumentException("Date and Doctor ID cannot be null");
        }
        List<AvailabilityDto> availabilityList = availabilityFeignClient.getAvailabilityByDoctorIdAndDate(doctorId, date);
        
        return availabilityList;
    }
    /**
     * Updates appointment after doctor cancels appointment.
     * 
     * @param appointmentId the ID of the appointment
     */
    public void updationAfterNotification(String appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }

        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStatus(Status.Cancelled);
            appointmentRepository.save(appointment);
            
            AppointmentDto appointmentDto = new AppointmentDto();
            
            appointmentDto.setAppointmentId(appointment.getAppointmentId());
            appointmentDto.setTimeSlot(appointment.getTimeSlot());
            appointmentDto.setStatus(appointment.getStatus());
            appointmentDto.setAvailabilityId(appointment.getAvailabilityId());
            appointmentDto.setPatientId(appointment.getPatientId());
            appointmentDto.setDoctorId(appointment.getDoctorId());
            appointmentDto.setDate(appointment.getDate());
            appointmentDto.setPatientName(appointment.getPatientName());
            appointmentDto.setDoctorName(appointment.getDoctorName());


            String appointmentNotification = notificationFeignClient.createNotification(appointmentDto).getBody();
            
    if(appointmentNotification == null) {
    	throw new AppointmentNotFoundException("Error :Couldn't create notification for the client");
    }
    log.info(appointmentNotification);
        } else {
            throw new AppointmentNotFoundException("Appointment for this availability is not found");
        }
    }

	public Boolean checkAppointment(LocalDate date, TimeSlots slot) {
		
		Optional<Appointment> appointment = appointmentRepository.findByDateAndTimeSlot(date,slot);
		if(appointment.isPresent() && !appointment.get().getStatus().toString().equals("Cancelled"))
		return true;
		
		return false;
	}
}