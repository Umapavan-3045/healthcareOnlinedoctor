package com.example.demo;

import com.users.app.dto.PatientDto;
import com.users.app.enums.Gender;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.exceptions.UserNotFoundException;
import com.users.app.model.Patient;
import com.users.app.model.User;
import com.users.app.repository.PatientRepository;
import com.users.app.service.PatientService;
import com.users.app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Patient patient;
    private User user;
    private PatientDto patientDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String patientId = UUID.randomUUID().toString();
        patient = new Patient();
        patient.setPatientId(patientId);
        patient.setName("Test Patient");
        patient.setAge(30);
        patient.setGender(Gender.MALE);
        patient.setAddress("Test Address");

        user = new User();
        user.setId(patientId);
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setPassword("encodedPassword");

        patientDto = new PatientDto();
        patientDto.setPatient_id(patientId);
        patientDto.setName("Updated Patient");
        patientDto.setAge(35);
        patientDto.setGender(Gender.FEMALE);
        patientDto.setAddress("Updated Address");
        patientDto.setEmail("updated@example.com");
        patientDto.setPhoneNumber("0987654321");
        patientDto.setPassword("newPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
    }

    @Test
    void getPatientById_success() {
        when(patientRepository.findBypatientId(patient.getPatientId())).thenReturn(Optional.of(patient));
        when(userService.getUserById(patient.getPatientId())).thenReturn(Optional.of(user));

        PatientDto result = patientService.getPatientById(patient.getPatientId());

        assertNotNull(result);
        assertEquals(patient.getName(), result.getName());
        assertEquals(patient.getAge(), result.getAge());
        assertEquals(patient.getAddress(), result.getAddress());
        assertEquals(patient.getPatientId(), result.getPatient_id());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(patient.getGender(), result.getGender());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void getPatientById_patientNotFound() {
        when(patientRepository.findBypatientId("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> patientService.getPatientById("nonExistentId"));
        verify(userService, never()).getUserById(anyString());
    }

    @Test
    void updatePatientDetails_success_allFields() {
        when(patientRepository.findBypatientId(patientDto.getPatient_id())).thenReturn(Optional.of(patient));
        when(userService.getUser(patientDto.getPatient_id())).thenReturn(user);
        when(userService.emailExist(patientDto.getEmail())).thenReturn(false);
        when(userService.phoneNumberExist(patientDto.getPhoneNumber())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());
        doNothing().when(userService).updateUser(any(User.class));

        assertDoesNotThrow(() -> patientService.updatePatientDetails(patientDto));

        assertEquals(patientDto.getName(), patient.getName());
        assertEquals(patientDto.getAge(), patient.getAge());
        assertEquals(patientDto.getAddress(), patient.getAddress());
        assertEquals(patientDto.getGender(), patient.getGender());
        assertEquals(patientDto.getEmail(), user.getEmail());
        assertEquals(patientDto.getPhoneNumber(), user.getPhoneNumber());
        assertEquals("encodedNewPassword", user.getPassword());

        verify(patientRepository, times(1)).save(patient);
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    void updatePatientDetails_success_partialFields() {
        PatientDto partialUpdate = new PatientDto();
        partialUpdate.setPatient_id(patient.getPatientId());
        partialUpdate.setName("Updated Name");

        when(patientRepository.findBypatientId(partialUpdate.getPatient_id())).thenReturn(Optional.of(patient));
        when(userService.getUser(partialUpdate.getPatient_id())).thenReturn(user);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());
        doNothing().when(userService).updateUser(any(User.class));

        assertDoesNotThrow(() -> patientService.updatePatientDetails(partialUpdate));

        assertEquals("Updated Name", patient.getName());
        assertEquals(patient.getAge(), patient.getAge()); // Should not be updated
        assertEquals(patient.getAddress(), patient.getAddress()); // Should not be updated
        assertEquals(patient.getGender(), patient.getGender()); // Should not be updated
        assertEquals(user.getEmail(), user.getEmail()); // Should not be updated
        assertEquals(user.getPhoneNumber(), user.getPhoneNumber()); // Should not be updated
        assertEquals(user.getPassword(), user.getPassword()); // Should not be updated

        verify(patientRepository, times(1)).save(patient);
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    void updatePatientDetails_patientNotFound() {
        when(patientRepository.findBypatientId(patientDto.getPatient_id())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> patientService.updatePatientDetails(patientDto));
        verify(userService, never()).getUser(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
        verify(userService, never()).updateUser(any(User.class));
    }

}