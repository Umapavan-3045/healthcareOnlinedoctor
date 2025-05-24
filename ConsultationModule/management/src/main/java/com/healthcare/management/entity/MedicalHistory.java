package com.healthcare.management.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="MedicalHistory")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name = "getMedicalHistoryByPatientId",
query = "SELECT h FROM MedicalHistory h WHERE h.patientId = :patientId")
public class MedicalHistory {
	
	public String getHistoryId() {
		return historyId;
	}

	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getHealthHistory() {
		return healthHistory;
	}

	public void setHealthHistory(String healthHistory) {
		this.healthHistory = healthHistory;
	}

	@Id
	@Column(name="History_id")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String historyId;	
	
	
	
	@Column(name = "Patient_id")
	private String patientId;
	
	
	
	@Column(name="DiseaseHistory")
	private String healthHistory;
	
	@PrePersist
    public void generateUUID() {
        if (this.historyId == null || this.historyId.isEmpty()) {
            this.historyId = UUID.randomUUID().toString();
        }
	}
	
}
