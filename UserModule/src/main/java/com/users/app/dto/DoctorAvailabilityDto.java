package com.users.app.dto;
 
import com.users.app.enums.Specialization;
import com.users.app.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityDto {
    private String doctorId;
    private String doctorName;
    private Specialization specialization;
 
    public DoctorAvailabilityDto(String doctorId2, String name, Specialization specialization2) {
		this.doctorId = doctorId2;
		this.doctorName = name;
		this.specialization = specialization2;
	}

	public Doctor toEntity() { 
        Doctor doctor = new Doctor();
        doctor.setDoctorId(this.doctorId);
        doctor.setName(this.doctorName);
        doctor.setSpecialization(this.specialization);
        return doctor;
    }
 
    public static DoctorAvailabilityDto fromEntity(Doctor doctor) {
        return new DoctorAvailabilityDto(doctor.getDoctorId(),doctor.getName(), doctor.getSpecialization());
    }

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Specialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}
}