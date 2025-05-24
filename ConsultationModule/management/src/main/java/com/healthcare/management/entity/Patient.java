package com.healthcare.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
	@Id
	private String Patient_id;

	
	@Column(name ="Name",nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "Gender",nullable = false)
	private Gender gender;
	
	@Column(name="Age",nullable = false)
	private int age;
	
	@Column(name="Address",nullable = false)
	private String address; 
	
}
