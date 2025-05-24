package com.example.demo;

import com.users.app.dto.DoctorAvailabilityDto;
import com.users.app.dto.DoctorDto;
import com.users.app.enums.Specialization;
import com.users.app.exceptions.DoctorNotFoundException;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.model.Doctor;
import com.users.app.model.User;
import com.users.app.repository.DoctorRepository;
import com.users.app.service.DoctorService;
import com.users.app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Doctor doctor;
    private User user;
    private DoctorDto doctorDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String doctorId = UUID.randomUUID().toString();
        doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setName("Test Doctor");
        doctor.setSpecialization(Specialization.Cardiology);

        user = new User();
        user.setId(doctorId);
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setPassword("encodedPassword");

        doctorDto = new DoctorDto();
        doctorDto.setDoctor_id(doctorId);
        doctorDto.setName("Updated Doctor");
        doctorDto.setSpecialization(Specialization.Dermatology);
        doctorDto.setEmail("updated@example.com");
        doctorDto.setPhoneNumber("0987654321");
        doctorDto.setPassword("newPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
    }

    @Test
    void getDoctorById_success() {
        when(doctorRepository.findBydoctorId(doctor.getDoctorId())).thenReturn(Optional.of(doctor));
        when(userService.getUserById(doctor.getDoctorId())).thenReturn(Optional.of(user));

        DoctorDto result = doctorService.getDoctorById(doctor.getDoctorId());

        assertNotNull(result);
        assertEquals(doctor.getName(), result.getName());
        assertEquals(doctor.getDoctorId(), result.getDoctor_id());
        assertEquals(doctor.getSpecialization(), result.getSpecialization());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void getDoctorById_doctorNotFound() {
        when(doctorRepository.findBydoctorId("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorById("nonExistentId"));
        verify(userService, never()).getUserById(anyString());
    }

    @Test
    void getDoctorBySpecialization_success() {
        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorRepository.findDoctorBySpecialization(Specialization.Cardiology)).thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorBySpecialization(Specialization.Cardiology);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(doctor.getName(), result.get(0).getName());
        assertEquals(doctor.getSpecialization(), result.get(0).getSpecialization());
    }

    @Test
    void getDoctorBySpecialization_notFound() {
        when(doctorRepository.findDoctorBySpecialization(Specialization.Dermatology)).thenReturn(List.of());

        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorBySpecialization(Specialization.Dermatology));
    }

    @Test
    void updateDoctorDetails_success_allFields() {
        when(doctorRepository.findBydoctorId(doctorDto.getDoctor_id())).thenReturn(Optional.of(doctor));
        when(userService.getUser(doctorDto.getDoctor_id())).thenReturn(user);
        when(userService.emailExist(doctorDto.getEmail())).thenReturn(false);
        when(userService.phoneNumberExist(doctorDto.getPhoneNumber())).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(new Doctor());
        doNothing().when(userService).updateUser(any(User.class));

        assertDoesNotThrow(() -> doctorService.updateDoctorDetails(doctorDto));

        assertEquals(doctorDto.getName(), doctor.getName());
        assertEquals(doctorDto.getSpecialization(), doctor.getSpecialization());
        assertEquals(doctorDto.getEmail(), user.getEmail());
        assertEquals(doctorDto.getPhoneNumber(), user.getPhoneNumber());
        assertEquals("encodedNewPassword", user.getPassword());

        verify(doctorRepository, times(1)).save(doctor);
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    void updateDoctorDetails_success_partialFields() {
        DoctorDto partialUpdate = new DoctorDto();
        partialUpdate.setDoctor_id(doctor.getDoctorId());
        partialUpdate.setName("Updated Name");

        when(doctorRepository.findBydoctorId(partialUpdate.getDoctor_id())).thenReturn(Optional.of(doctor));
        when(userService.getUser(partialUpdate.getDoctor_id())).thenReturn(user);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(new Doctor());
        doNothing().when(userService).updateUser(any(User.class));

        assertDoesNotThrow(() -> doctorService.updateDoctorDetails(partialUpdate));

        assertEquals("Updated Name", doctor.getName());
        assertEquals(doctor.getSpecialization(), doctor.getSpecialization()); // Should not be updated
        assertEquals(user.getEmail(), user.getEmail()); // Should not be updated
        assertEquals(user.getPhoneNumber(), user.getPhoneNumber()); // Should not be updated
        assertEquals(user.getPassword(), user.getPassword()); // Should not be updated

        verify(doctorRepository, times(1)).save(doctor);
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    void updateDoctorDetails_doctorNotFound() {
        when(doctorRepository.findBydoctorId(doctorDto.getDoctor_id())).thenReturn(Optional.empty());

        assertThrows(DoctorNotFoundException.class, () -> doctorService.updateDoctorDetails(doctorDto));
        verify(userService, never()).getUser(anyString());
        verify(doctorRepository, never()).save(any(Doctor.class));
        verify(userService, never()).updateUser(any(User.class));
    }


    @Test
    void getAllDoctorsdetails_success() {
        List<Doctor> doctors = Arrays.asList(doctor, new Doctor());
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = doctorService.getAllDoctorsdetails();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(doctorRepository, times(1)).findAll();
    }


    @Test
    void getAllDoctors_emptyList() {
        when(doctorRepository.findAll()).thenReturn(List.of());

        List<DoctorAvailabilityDto> result = doctorService.getAllDoctors();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorRepository, times(1)).findAll();
    }
}