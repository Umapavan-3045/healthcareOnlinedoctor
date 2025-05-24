package com.healthcare.management.entity;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
	@Id
    @Column(name = "Appointment_id", nullable = false, unique = true)
    private String appointmentId;
	
//	@Column(name = "Patient_id", nullable = false)
//    private String patientId;

    @PrePersist
    public void generateUUID() {
        if (this.appointmentId == null || this.appointmentId.isEmpty()) {
            this.appointmentId = UUID.randomUUID().toString(); // ✅ Generate UUID automatically
        }
    }
	
//	@ManyToOne
//	@JoinColumn(name = "Doctor_id")
//	private Doctor doc;
//	
//	@ManyToOne
//	@JoinColumn(name="Patient_id")
//	private Patient pat;
//	
//	@Enumerated(EnumType.STRING)
//	@Column(name ="TimeSlot")
//	private Timeslots time;
//	
//	@Enumerated(EnumType.STRING)
//	@Column(name ="Status")
//	private Status status;
	
}
