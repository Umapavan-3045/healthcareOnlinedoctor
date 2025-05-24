package com.cts.healthcareappointment.notificationmodule.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.healthcareappointment.notificationmodule.Entity.Notification;

import java.util.List;

@Repository
public interface NotificationDao extends JpaRepository<Notification, String> {
   

	Notification findByAppointmentId(String appointmentId);

	Notification findByAppointmentIdAndPatientId(String appointmentId, String patientId);

	Notification findByAppointmentIdAndDoctorId(String appointmentId, String doctorId);
	
	List<Notification> findByDoctorIdOrPatientId(String doctorId, String patientId);
	 
    List<Notification> findByDoctorId(String doctorId);
 
    List<Notification> findByPatientId(String patientId);
 
}
