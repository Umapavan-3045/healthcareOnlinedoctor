package com.healthcare.management.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.management.entity.Appointment;

public interface AppointmentDAO extends JpaRepository<Appointment, String> {
}