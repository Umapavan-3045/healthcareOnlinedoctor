package com.availabilitySchedule;

import com.availabilitySchedule.dto.DoctorAvailabilityDto;
import com.availabilitySchedule.exception.AvailabilityNotFoundException;
import com.availabilitySchedule.exception.DoctorNotFoundException;
import com.availabilitySchedule.exception.UnavailableException;
import com.availabilitySchedule.feignClient.DoctorFeignClient;
import com.availabilitySchedule.model.Availability;
import com.availabilitySchedule.model.Specialization;
import com.availabilitySchedule.model.Status;
import com.availabilitySchedule.model.Timeslots;
import com.availabilitySchedule.repository.AvailabilityRepository;
import com.availabilitySchedule.service.AvailabilityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityScheduleApplicationTests {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private DoctorFeignClient doctorFeignClient;

    @InjectMocks
    private AvailabilityService availabilityService;

    private List<DoctorAvailabilityDto> mockDoctors;
    private LocalDate today;
    private LocalDate nextMonday;
    private LocalDate thisMonday;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        nextMonday = thisMonday.plusWeeks(1);

        mockDoctors = Arrays.asList(
                new DoctorAvailabilityDto("doc1", "Doctor One", Specialization.Cardiology),
                new DoctorAvailabilityDto("doc2", "Doctor Two", Specialization.Dermatology)
        );
    }

//    @Test
//    void initializeAvailability_emptyRepository_shouldCreateInitialAvailability() {
//        when(availabilityRepository.count()).thenReturn(0L);
//        when(doctorFeignClient.getAllDoctors()).thenReturn(mockDoctors);
//        when(availabilityRepository.existsByDateBetween(nextMonday, nextMonday.plusDays(4))).thenReturn(false);
//
//        availabilityService.initializeAvailability();
//
//        ArgumentCaptor<Availability> availabilityCaptor = ArgumentCaptor.forClass(Availability.class);
//        verify(availabilityRepository, times(mockDoctors.size() * 5 * Timeslots.values().length)).save(availabilityCaptor.capture());
//        List<Availability> savedAvailabilities = availabilityCaptor.getAllValues();
//
//        assertEquals(mockDoctors.size() * 5 * Timeslots.values().length, savedAvailabilities.size());
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getDate().isAfter(today.minusDays(1)) && av.getDate().isBefore(nextMonday.plusDays(5))));
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
//        verify(availabilityRepository, times(1)).findByDateBefore(today);
//        verify(availabilityRepository, atLeastOnce()).save(any(Availability.class));
//    }

//    @Test
//    void initializeAvailability_repositoryNotEmpty_shouldNotCreateInitialAvailability() {
//        when(availabilityRepository.count()).thenReturn(10L);
//
//        availabilityService.initializeAvailability();
//
//        verify(doctorFeignClient, never()).getAllDoctors();
//        verify(availabilityRepository, never()).save(any(Availability.class));
//        verify(availabilityRepository, times(1)).findByDateBefore(today);
//        verify(availabilityRepository, atLeastOnce()).save(any(Availability.class));
//    }

//    @Test
//    void initializeAvailability_nextWeekAvailabilityExists_shouldNotCreateNewEntries() {
//        when(availabilityRepository.count()).thenReturn(0L);
//        when(doctorFeignClient.getAllDoctors()).thenReturn(mockDoctors);
//        when(availabilityRepository.existsByDateBetween(nextMonday, nextMonday.plusDays(4))).thenReturn(true);
//
//        availabilityService.initializeAvailability();
//
//        verify(availabilityRepository, never()).save(any(Availability.class));
//        verify(availabilityRepository, times(1)).findByDateBefore(today);
//        verify(availabilityRepository, atLeastOnce()).save(any(Availability.class));
//    }

//    @Test
//    void updateAvailabilityForWeek_noExistingAvailability_shouldCreateAvailabilityForCurrentAndNextWeek() {
//        when(doctorFeignClient.getAllDoctors()).thenReturn(mockDoctors);
//        when(availabilityRepository.existsByDateBetween(thisMonday, thisMonday.plusDays(4))).thenReturn(false);
//        when(availabilityRepository.existsByDateBetween(nextMonday, nextMonday.plusDays(4))).thenReturn(false);
//
//        availabilityService.updateAvailabilityForWeek();
//
//        ArgumentCaptor<Availability> availabilityCaptor = ArgumentCaptor.forClass(Availability.class);
//        verify(availabilityRepository, times(mockDoctors.size() * 10 * Timeslots.values().length)).save(availabilityCaptor.capture());
//        List<Availability> savedAvailabilities = availabilityCaptor.getAllValues();
//
//        assertEquals(mockDoctors.size() * 10 * Timeslots.values().length, savedAvailabilities.size());
//        assertTrue(savedAvailabilities.stream().anyMatch(av -> av.getDate().isAfter(thisMonday.minusDays(1)) && av.getDate().isBefore(thisMonday.plusDays(5))));
//        assertTrue(savedAvailabilities.stream().anyMatch(av -> av.getDate().isAfter(nextMonday.minusDays(1)) && av.getDate().isBefore(nextMonday.plusDays(5))));
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
//    }

//    @Test
//    void updateAvailabilityForWeek_currentWeekAvailabilityExists_shouldCreateOnlyForNextWeek() {
//        when(doctorFeignClient.getAllDoctors()).thenReturn(mockDoctors);
//        when(availabilityRepository.existsByDateBetween(thisMonday, thisMonday.plusDays(4))).thenReturn(true);
//        when(availabilityRepository.existsByDateBetween(nextMonday, nextMonday.plusDays(4))).thenReturn(false);
//
//        availabilityService.updateAvailabilityForWeek();
//
//        ArgumentCaptor<Availability> availabilityCaptor = ArgumentCaptor.forClass(Availability.class);
//        verify(availabilityRepository, times(mockDoctors.size() * 5 * Timeslots.values().length)).save(availabilityCaptor.capture());
//        List<Availability> savedAvailabilities = availabilityCaptor.getAllValues();
//
//        assertEquals(mockDoctors.size() * 5 * Timeslots.values().length, savedAvailabilities.size());
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getDate().isAfter(nextMonday.minusDays(1)) && av.getDate().isBefore(nextMonday.plusDays(5))));
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
//    }

//    @Test
//    void updateAvailabilityForWeek_nextWeekAvailabilityExists_shouldCreateOnlyForCurrentWeek() {
//        when(doctorFeignClient.getAllDoctors()).thenReturn(mockDoctors);
//        when(availabilityRepository.existsByDateBetween(thisMonday, thisMonday.plusDays(4))).thenReturn(false);
//        when(availabilityRepository.existsByDateBetween(nextMonday, nextMonday.plusDays(4))).thenReturn(true);
//
//        availabilityService.updateAvailabilityForWeek();
//
//        ArgumentCaptor<Availability> availabilityCaptor = ArgumentCaptor.forClass(Availability.class);
//        verify(availabilityRepository, times(mockDoctors.size() * 5 * Timeslots.values().length)).save(availabilityCaptor.capture());
//        List<Availability> savedAvailabilities = availabilityCaptor.getAllValues();
//
//        assertEquals(mockDoctors.size() * 5 * Timeslots.values().length, savedAvailabilities.size());
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getDate().isAfter(thisMonday.minusDays(1)) && av.getDate().isBefore(thisMonday.plusDays(5))));
//        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
//    }

    @Test
    void updatePastDates_shouldUpdateStatusToUnavailableForPastDates() {
        LocalDate yesterday = today.minusDays(1);
        List<Availability> pastAvailabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), "doc1", "Doctor One", Specialization.Cardiology, yesterday, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), "doc2", "Doctor Two", Specialization.Dermatology, yesterday, Timeslots.ELEVEN_TO_ONE, Status.Available)
        );
        when(availabilityRepository.findByDateBefore(today)).thenReturn(pastAvailabilities);

        availabilityService.updatePastDates();

        ArgumentCaptor<Availability> availabilityCaptor = ArgumentCaptor.forClass(Availability.class);
        verify(availabilityRepository, times(pastAvailabilities.size())).save(availabilityCaptor.capture());
        List<Availability> updatedAvailabilities = availabilityCaptor.getAllValues();

        assertTrue(updatedAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Unavailable));
    }

    @Test
    void updateAvailabilityStatus_validIds_shouldUpdateStatuses() {
        String bookedId = UUID.randomUUID().toString();
        String availableId = UUID.randomUUID().toString();
        Availability bookedAvailability = new Availability(bookedId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
        Availability availableAvailability = new Availability(availableId, "doc2", "Doctor Two", Specialization.Dermatology, today, Timeslots.ELEVEN_TO_ONE, Status.Available);

        when(availabilityRepository.findById(bookedId)).thenReturn(Optional.of(bookedAvailability));
        when(availabilityRepository.findById(availableId)).thenReturn(Optional.of(availableAvailability));

        availabilityService.updateAvailabilityStatus(bookedId, availableId);

        assertEquals(Status.Available, bookedAvailability.getStatus());
        assertEquals(Status.Booked, availableAvailability.getStatus());
        verify(availabilityRepository, times(1)).save(bookedAvailability);
        verify(availabilityRepository, times(1)).save(availableAvailability);
    }

    @Test
    void updateAvailabilityStatus_invalidBookedId_shouldThrowAvailabilityNotFoundException() {
        String availableId = UUID.randomUUID().toString();
        when(availabilityRepository.findById("bookedId")).thenReturn(Optional.empty());
//        when(availabilityRepository.findById(availableId)).thenReturn(Optional.of(new Availability(availableId, "doc2", "Doctor Two", Specialization.Dermatology, today, Timeslots.ELEVEN_TO_ONE, Status.Available)));

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.updateAvailabilityStatus("bookedId", availableId));

        verify(availabilityRepository, never()).save(any(Availability.class));
    }

//    @Test
//    void updateAvailabilityStatus_invalidAvailableId_shouldThrowAvailabilityNotFoundException() {
//        String bookedId = UUID.randomUUID().toString();
//        Availability bookedAvailability = new Availability(bookedId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
//        when(availabilityRepository.findById(bookedId)).thenReturn(Optional.of(bookedAvailability));
//        when(availabilityRepository.findById("availableId")).thenReturn(Optional.empty());
//
//        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.updateAvailabilityStatus(bookedId, "availableId"));
//
//        verify(availabilityRepository, times(1)).save(bookedAvailability); // Verify save was called once for the booked availability
//        verify(availabilityRepository, never()).save(argThat(av -> av.getAvailabilityId().equals("availableId"))); // Verify save was never called for the invalid availableId
//    }

    @Test
    void getAvailabilityByDoctorIdAndDate_validDoctorIdAndDate_shouldReturnAvailableAvailabilities() {
        String doctorId = "doc1";
        LocalDate date = today;
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, date, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, date, Timeslots.ELEVEN_TO_ONE, Status.Booked),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, date, Timeslots.TWO_TO_FOUR, Status.Available)
        );
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(availabilities);
        when(availabilityRepository.findByDoctorIdAndDate(doctorId, date)).thenReturn(availabilities);

        List<Availability> availableAvailabilities = availabilityService.getAvailabilityByDoctorIdAndDate(doctorId, date);

        assertEquals(2, availableAvailabilities.size());
        assertTrue(availableAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
    }

    @Test
    void getAvailabilityByDoctorIdAndDate_invalidDoctorId_shouldThrowDoctorNotFoundException() {
        String doctorId = "nonExistentDoc";
        LocalDate date = today;
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(null);

        assertThrows(DoctorNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorIdAndDate(doctorId, date));
        verify(availabilityRepository, never()).findByDoctorIdAndDate(anyString(), any());
    }

    @Test
    void getAvailabilityByDoctorIdAndDate_noAvailabilityFound_shouldThrowAvailabilityNotFoundException() {
        String doctorId = "doc1";
        LocalDate date = today;
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(List.of());
        when(availabilityRepository.findByDoctorIdAndDate(doctorId, date)).thenReturn(List.of());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorIdAndDate(doctorId, date));
    }

    @Test
    void getAvailabilityByDoctorIdAndDate_noAvailableSlotsFound_shouldThrowAvailabilityNotFoundException() {
        String doctorId = "doc1";
        LocalDate date = today;
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, date, Timeslots.NINE_TO_ELEVEN, Status.Booked),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, date, Timeslots.ELEVEN_TO_ONE, Status.Unavailable)
        );
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(availabilities);
        when(availabilityRepository.findByDoctorIdAndDate(doctorId, date)).thenReturn(availabilities);

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorIdAndDate(doctorId, date));
    }

    @Test
    void getAvailabilityBySpecializationAndDate_validSpecializationAndDate_shouldReturnAvailableAvailabilities() {
        Specialization specialization = Specialization.Cardiology;
        LocalDate date = today;
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), "doc1", "Doctor One", specialization, date, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), "doc2", "Doctor Two", Specialization.Dermatology, date, Timeslots.ELEVEN_TO_ONE, Status.Booked),
                new Availability(UUID.randomUUID().toString(), "doc3", "Doctor Three", specialization, date, Timeslots.TWO_TO_FOUR, Status.Available)
        );
        when(availabilityRepository.findBySpecializationAndDate(specialization, date)).thenReturn(availabilities);

        List<Availability> availableAvailabilities = availabilityService.getAvailabilityBySpecializationAndDate(specialization, date);

        assertEquals(2, availableAvailabilities.size());
        assertTrue(availableAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
    }

    @Test
    void getAvailabilityBySpecializationAndDate_noAvailabilityFound_shouldThrowUnavailableException() {
        Specialization specialization = Specialization.Cardiology;
        LocalDate date = today;
        when(availabilityRepository.findBySpecializationAndDate(specialization, date)).thenReturn(List.of());

        assertThrows(UnavailableException.class, () -> availabilityService.getAvailabilityBySpecializationAndDate(specialization, date));
    }

    @Test
    void getAvailabilityBySpecializationAndDate_noAvailableSlotsFound_shouldThrowUnavailableException() {
        Specialization specialization = Specialization.Cardiology;
        LocalDate date = today;
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), "doc1", "Doctor One", specialization, date, Timeslots.NINE_TO_ELEVEN, Status.Booked),
                new Availability(UUID.randomUUID().toString(), "doc3", "Doctor Three", specialization, date, Timeslots.TWO_TO_FOUR, Status.Unavailable)
        );
        when(availabilityRepository.findBySpecializationAndDate(specialization, date)).thenReturn(availabilities);

        assertThrows(UnavailableException.class, () -> availabilityService.getAvailabilityBySpecializationAndDate(specialization, date));
    }

    @Test
    void getAvailabilityByDoctorIdAndDateRange_validDoctorIdAndDateRange_shouldReturnAvailabilities() {
        String doctorId = "doc1";
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(2);
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, startDate, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, endDate, Timeslots.ELEVEN_TO_ONE, Status.Booked)
        );
        when(availabilityRepository.findByDoctorIdAndDateBetween(doctorId, startDate, endDate)).thenReturn(availabilities);

        List<Availability> result = availabilityService.getAvailabilityByDoctorIdAndDateRange(doctorId, startDate, endDate);

        assertEquals(2, result.size());
    }

    @Test
    void getAvailabilityByDoctorIdAndDateRange_noAvailabilityFound_shouldThrowAvailabilityNotFoundException() {
        String doctorId = "doc1";
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(2);
        when(availabilityRepository.findByDoctorIdAndDateBetween(doctorId, startDate, endDate)).thenReturn(List.of());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorIdAndDateRange(doctorId, startDate, endDate));
    }

    @Test
    void getAvailabilityBySpecializationAndDateRange_validSpecializationAndDateRange_shouldReturnAvailabilities() {
        Specialization specialization = Specialization.Cardiology;
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(2);
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), "doc1", "Doctor One", specialization, startDate, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), "doc3", "Doctor Three", specialization, endDate, Timeslots.ELEVEN_TO_ONE, Status.Booked)
        );
        when(availabilityRepository.findBySpecializationAndDateBetween(specialization, startDate, endDate)).thenReturn(availabilities);

        List<Availability> result = availabilityService.getAvailabilityBySpecializationAndDateRange(specialization, startDate, endDate);

        assertEquals(2, result.size());
    }

    @Test
    void getAvailabilityBySpecializationAndDateRange_noAvailabilityFound_shouldThrowAvailabilityNotFoundException() {
        Specialization specialization = Specialization.Cardiology;
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(2);
        when(availabilityRepository.findBySpecializationAndDateBetween(specialization, startDate, endDate)).thenReturn(List.of());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.getAvailabilityBySpecializationAndDateRange(specialization, startDate, endDate));
    }

    @Test
    void blockTimeSlot_availableSlot_shouldUpdateStatusToUnavailable() {
        String availabilityId = UUID.randomUUID().toString();
        Availability availableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Available);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availableSlot));

        availabilityService.blockTimeSlot(availabilityId);

        assertEquals(Status.Unavailable, availableSlot.getStatus());
        verify(availabilityRepository, times(1)).save(availableSlot);
    }

    @Test
    void blockTimeSlot_unavailableSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability unavailableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(unavailableSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.blockTimeSlot(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void blockTimeSlot_bookedSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability bookedSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Booked);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(bookedSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.blockTimeSlot(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void blockTimeSlot_invalidId_shouldThrowAvailabilityNotFoundException() {
        String availabilityId = UUID.randomUUID().toString();
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.blockTimeSlot(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void bookTimeSlot_availableSlot_shouldUpdateStatusToBooked() {
        String availabilityId = UUID.randomUUID().toString();
        Availability availableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Available);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availableSlot));

        availabilityService.bookTimeSlot(availabilityId);

        assertEquals(Status.Booked, availableSlot.getStatus());
        verify(availabilityRepository, times(1)).save(availableSlot);
    }

    @Test
    void bookTimeSlot_unavailableSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability unavailableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(unavailableSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.bookTimeSlot(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void bookTimeSlot_bookedSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability bookedSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Booked);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(bookedSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.bookTimeSlot(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void bookTimeSlot_invalidId_shouldThrowAvailabilityNotFoundException() {
        String availabilityId = UUID.randomUUID().toString();
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.bookTimeSlot(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void viewAllAvailabilities_shouldReturnListOfAvailableAvailabilities() {
        List<Availability> allAvailabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), "doc2", "Doctor Two", Specialization.Dermatology, today, Timeslots.ELEVEN_TO_ONE, Status.Booked),
                new Availability(UUID.randomUUID().toString(), "doc1", "Doctor One", Specialization.Cardiology, today.plusDays(1), Timeslots.TWO_TO_FOUR, Status.Available)
        );
        when(availabilityRepository.findAll()).thenReturn(allAvailabilities);

        List<Availability> availableAvailabilities = availabilityService.viewAllAvailabilities();

        assertEquals(2, availableAvailabilities.size());
        assertTrue(availableAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
    }

    @Test
    void deleteAvailability_validId_shouldDeleteAvailability() {
        String availabilityId = UUID.randomUUID().toString();

        availabilityService.deleteAvailability(availabilityId);

        verify(availabilityRepository, times(1)).deleteById(availabilityId);
    }

    @Test
    void viewById_availableId_shouldReturnAvailableAvailability() {
        String availabilityId = UUID.randomUUID().toString();
        Availability available = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Available);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(available));

        Availability result = availabilityService.viewById(availabilityId);

        assertEquals(available, result);
    }

    @Test
    void viewById_unavailableId_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability unavailable = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(unavailable));

        assertThrows(UnavailableException.class, () -> availabilityService.viewById(availabilityId));
    }

    @Test
    void viewById_bookedId_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability booked = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Booked);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(booked));

        assertThrows(UnavailableException.class, () -> availabilityService.viewById(availabilityId));
    }

    @Test
    void viewById_invalidId_shouldThrowAvailabilityNotFoundException() {
        String availabilityId = UUID.randomUUID().toString();
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.viewById(availabilityId));
    }

    @Test
    void releaseAvailabilityById_unavailableSlot_shouldUpdateStatusToAvailable() {
        String availabilityId = UUID.randomUUID().toString();
        Availability unavailableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(unavailableSlot));

        availabilityService.releaseAvailabilityById(availabilityId);

        assertEquals(Status.Available, unavailableSlot.getStatus());
        verify(availabilityRepository, times(1)).save(unavailableSlot);
    }

//    @Test
//    void releaseAvailabilityById_availableSlot_shouldNotUpdateStatusAndNotThrowException() {
//        String availabilityId = UUID.randomUUID().toString();
//        Availability availableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
//        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availableSlot));
//
//        availabilityService.releaseAvailabilityById(availabilityId);
//
//        assertEquals(Status.Available, availableSlot.getStatus());
//        verify(availabilityRepository, never()).save(any(Availability.class));
//    }

    @Test
    void releaseAvailabilityById_bookedSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability bookedSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Booked);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(bookedSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.releaseAvailabilityById(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void releaseAvailabilityById_invalidId_shouldThrowAvailabilityNotFoundException() {
        String availabilityId = UUID.randomUUID().toString();
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.releaseAvailabilityById(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void cancelAvailabilityStatus_bookedSlot_shouldUpdateStatusToAvailable() {
        String availabilityId = UUID.randomUUID().toString();
        Availability bookedSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Booked);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(bookedSlot));

        availabilityService.cancelAvailabilityStatus(availabilityId);

        assertEquals(Status.Available, bookedSlot.getStatus());
        verify(availabilityRepository, times(1)).save(bookedSlot);
    }

    @Test
    void cancelAvailabilityStatus_availableSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability availableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Available);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availableSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.cancelAvailabilityStatus(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void cancelAvailabilityStatus_unavailableSlot_shouldThrowUnavailableException() {
        String availabilityId = UUID.randomUUID().toString();
        Availability unavailableSlot = new Availability(availabilityId, "doc1", "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Unavailable);
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(unavailableSlot));

        assertThrows(UnavailableException.class, () -> availabilityService.cancelAvailabilityStatus(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void cancelAvailabilityStatus_invalidId_shouldThrowAvailabilityNotFoundException() {
        String availabilityId = UUID.randomUUID().toString();
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.cancelAvailabilityStatus(availabilityId));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void getAvailabilityByDoctorId_validDoctorId_shouldReturnAvailableAvailabilities() {
        String doctorId = "doc1";
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Available),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, today, Timeslots.ELEVEN_TO_ONE, Status.Booked),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, today.plusDays(1), Timeslots.TWO_TO_FOUR, Status.Available)
        );
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(availabilities);

        List<Availability> availableAvailabilities = availabilityService.getAvailabilityByDoctorId(doctorId);

        assertEquals(2, availableAvailabilities.size());
        assertTrue(availableAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
    }

    @Test
    void getAvailabilityByDoctorId_invalidDoctorId_shouldThrowDoctorNotFoundException() {
        String doctorId = "nonExistentDoc";
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(null);

        assertThrows(DoctorNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorId(doctorId));
    }

    @Test
    void getAvailabilityByDoctorId_noAvailabilityFound_shouldThrowAvailabilityNotFoundException() {
        String doctorId = "doc1";
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(List.of());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorId(doctorId));
    }

    @Test
    void getAvailabilityByDoctorId_noAvailableSlotsFound_shouldThrowAvailabilityNotFoundException() {
        String doctorId = "doc1";
        List<Availability> availabilities = Arrays.asList(
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, today, Timeslots.NINE_TO_ELEVEN, Status.Booked),
                new Availability(UUID.randomUUID().toString(), doctorId, "Doctor One", Specialization.Cardiology, today, Timeslots.ELEVEN_TO_ONE, Status.Unavailable)
        );
        when(availabilityRepository.findByDoctorId(doctorId)).thenReturn(availabilities);

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.getAvailabilityByDoctorId(doctorId));
    }

    @Test
    void createAvailabilityForDoctorId_shouldCreateAvailabilityForCurrentAndNextWeek() {
        String doctorId = "newDoc";
        String doctorName = "New Doctor";
        Specialization specialization = Specialization.Neurology;

        availabilityService.createAvailabilityForDoctorId(doctorId, doctorName, specialization);

        ArgumentCaptor<Availability> availabilityCaptor = ArgumentCaptor.forClass(Availability.class);
        verify(availabilityRepository, times(10 * Timeslots.values().length)).save(availabilityCaptor.capture());
        List<Availability> savedAvailabilities = availabilityCaptor.getAllValues();

        assertEquals(10 * Timeslots.values().length, savedAvailabilities.size());
        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getDoctorId().equals(doctorId)));
        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getDoctorName().equals(doctorName)));
        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getSpecialization().equals(specialization)));
        assertTrue(savedAvailabilities.stream().anyMatch(av -> av.getDate().isAfter(thisMonday.minusDays(1)) && av.getDate().isBefore(thisMonday.plusDays(5))));
        assertTrue(savedAvailabilities.stream().anyMatch(av -> av.getDate().isAfter(nextMonday.minusDays(1)) && av.getDate().isBefore(nextMonday.plusDays(5))));
        assertTrue(savedAvailabilities.stream().allMatch(av -> av.getStatus() == Status.Available));
    }
}