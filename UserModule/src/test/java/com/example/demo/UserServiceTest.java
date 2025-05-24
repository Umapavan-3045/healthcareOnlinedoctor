package com.example.demo;

import com.users.app.dto.UserDto;
import com.users.app.enums.Gender;
import com.users.app.enums.Role;
import com.users.app.enums.Specialization;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.exceptions.UserNotFoundException;
import com.users.app.feignClient.AvailabilityFeignClient;
import com.users.app.model.Doctor;
import com.users.app.model.Patient;
import com.users.app.model.User;
import com.users.app.repository.UserRepository;
import com.users.app.service.JWTService;
import com.users.app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private AvailabilityFeignClient availabilityFeignClient;

    private UserDto userDtoDoctor;
    private UserDto userDtoPatient;
    private User existingUserEmail;
    private User existingUserPhone;
    private User doctorUser;
    private User patientUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDtoDoctor = new UserDto();
        userDtoDoctor.setEmail("doctor@example.com");
        userDtoDoctor.setPhoneNumber("1234567890");
        userDtoDoctor.setPassword("password");
        userDtoDoctor.setRole(Role.DOCTOR);
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setSpecialization(Specialization.Cardiology);
        userDtoDoctor.setDoctor(doctor);

        userDtoPatient = new UserDto();
        userDtoPatient.setEmail("patient@example.com");
        userDtoPatient.setPhoneNumber("9876543210");
        userDtoPatient.setPassword("password");
        userDtoPatient.setRole(Role.PATIENT);
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setAge(30);
        patient.setGender(Gender.FEMALE);
        userDtoPatient.setPatient(patient);

        existingUserEmail = new User();
        existingUserEmail.setEmail("existing@example.com");

        existingUserPhone = new User();
        existingUserPhone.setPhoneNumber("1112223333");

        doctorUser = new User();
        doctorUser.setId(UUID.randomUUID().toString());
        doctorUser.setEmail("doctor@test.com");
        doctorUser.setPassword("encodedPassword");
        doctorUser.setRole(Role.DOCTOR);
        Doctor doctorDetails = new Doctor();
        doctorDetails.setName("Test Doctor");
        doctorDetails.setSpecialization(Specialization.Dermatology);
        doctorUser.setDoctor(doctorDetails);

        patientUser = new User();
        patientUser.setId(UUID.randomUUID().toString());
        patientUser.setEmail("patient@test.com");
        patientUser.setPassword("encodedPassword");
        patientUser.setRole(Role.PATIENT);
        Patient patientDetails = new Patient();
        patientDetails.setName("Test Patient");
        patientDetails.setAge(25);
        patientDetails.setGender(Gender.MALE);
        patientUser.setPatient(patientDetails);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
    }

    @Test
    void signUp_doctor_success() {
        when(userRepository.findByEmail(userDtoDoctor.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(userDtoDoctor.getPhoneNumber())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        assertDoesNotThrow(() -> userService.signUp(userDtoDoctor));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signUp_patient_success() {
        when(userRepository.findByEmail(userDtoPatient.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(userDtoPatient.getPhoneNumber())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        assertDoesNotThrow(() -> userService.signUp(userDtoPatient));
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void signUp_doctorRoleWithoutDoctorDetails() {
        userDtoDoctor.setRole(Role.DOCTOR);
        userDtoDoctor.setDoctor(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoDoctor));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_doctorRoleWithNullDoctorName() {
        userDtoDoctor.getDoctor().setName(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoDoctor));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_doctorRoleWithNullSpecialization() {
        userDtoDoctor.getDoctor().setSpecialization(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoDoctor));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }


    @Test
    void signUp_patientRoleWithoutPatientDetails() {
        userDtoPatient.setRole(Role.PATIENT);
        userDtoPatient.setPatient(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoPatient));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_patientRoleWithNullPatientName() {
        userDtoPatient.getPatient().setName(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoPatient));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_patientRoleWithNullPatientAge() {
        userDtoPatient.getPatient().setAge(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoPatient));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_patientRoleWithNullPatientGender() {
        userDtoPatient.getPatient().setGender(null);
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoPatient));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_patientRoleWithInvalidPatientGender() {
        userDtoPatient.getPatient().setGender(Gender.valueOf("OTHER"));
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(userDtoPatient));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_emailAlreadyExists() {
        when(userRepository.findByEmail(userDtoDoctor.getEmail())).thenReturn(Optional.of(existingUserEmail));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.signUp(userDtoDoctor));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void signUp_phoneNumberAlreadyExists() {
        when(userRepository.findByEmail(userDtoDoctor.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(userDtoDoctor.getPhoneNumber())).thenReturn(Optional.of(existingUserPhone));

        assertThrows(PhoneNumberAlreadyExistsException.class, () -> userService.signUp(userDtoDoctor));
        verify(userRepository, never()).save(any(User.class));
        verify(availabilityFeignClient, never()).createAvailabilityForDoctorId(anyString(), anyString(), any());
    }

    @Test
    void emailExist_true() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        assertTrue(userService.emailExist("test@example.com"));
    }

    @Test
    void emailExist_false() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertFalse(userService.emailExist("test@example.com"));
    }

    @Test
    void phoneNumberExist_true() {
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(new User()));
        assertTrue(userService.phoneNumberExist("1234567890"));
    }

    @Test
    void phoneNumberExist_false() {
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.empty());
        assertFalse(userService.phoneNumberExist("1234567890"));
    }

    @Test
    void verify_success() {
        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByEmail("doctor@test.com")).thenReturn(Optional.of(doctorUser));
        when(jwtService.getToken(doctorUser.getId(), Role.DOCTOR, "doctor@test.com")).thenReturn("mockToken");

        String token = userService.verify("doctor@test.com", "password", Role.DOCTOR);
        assertEquals("mockToken", token);
    }

    @Test
    void getUserById_success() {
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    void getUserById_notFound() {
        String userId = UUID.randomUUID().toString();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getUser_success() {
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUser(userId);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUser_notFound() {
        String userId = UUID.randomUUID().toString();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void updateUser_success() {
        User userToUpdate = new User();
        userToUpdate.setId(UUID.randomUUID().toString());
        userToUpdate.setEmail("updated@example.com");
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        assertDoesNotThrow(() -> userService.updateUser(userToUpdate));
        verify(userRepository, times(1)).save(userToUpdate);
    }

    @Test
    void deleteUser_success() {
        String userId = UUID.randomUUID().toString();
        User userToDelete = new User();
        userToDelete.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_notFound() {
        String userId = UUID.randomUUID().toString();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void loadUserByUsername_userFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        assertNotNull(userService.loadUserByUsername("test@example.com"));
    }

}