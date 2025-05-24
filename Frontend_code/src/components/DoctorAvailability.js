import React, { useState, useEffect, useCallback, useMemo } from "react";
import "../CssFiles/Availability.css";
 
const DoctorAvailability = ({token,doctorId}) => {
  const now = useMemo(() => Date.now(), []);
 
  const getStartOfWeek = useCallback((date) => {
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  }, []);
 
  const todayAtMount = useMemo(() => new Date(), []);
  const todayString = useMemo(() => todayAtMount.toDateString(), [todayAtMount]);
 
  const startOfWeek = useMemo(() => getStartOfWeek(new Date(todayAtMount)), [getStartOfWeek, todayAtMount]);
  const endOfWeek = useMemo(() => new Date(new Date(startOfWeek).setDate(startOfWeek.getDate() + 4)), [startOfWeek]);
 
  const getNextWeekStart = useCallback((startDate) => {
    return new Date(new Date(startDate).setDate(startDate.getDate() + 7));
  }, []);
 
  const nextWeekStart = useMemo(() => getNextWeekStart(startOfWeek), [getNextWeekStart, startOfWeek]);
  const nextWeekEnd = useMemo(() => new Date(new Date(nextWeekStart).setDate(nextWeekStart.getDate() + 4)), [nextWeekStart]);
 
  const [currentWeek, setCurrentWeek] = useState({ start: startOfWeek, end: endOfWeek });
  const [availability, setAvailability] = useState(
    Array(4)
      .fill(null)
      .map(() => Array(5).fill({ status: "Unavailable", availabilityId: null }))
  );
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [isModalOpen, setModalOpen] = useState(false);
  const [error, setError] = useState(null);
 
 
  const timeslotMapping = useMemo(
    () => ({
      NINE_TO_ELEVEN: "9:00 AM - 11:00 AM",
      ELEVEN_TO_ONE: "11:00 AM - 01:00 PM",
      TWO_TO_FOUR: "02:00 PM - 04:00 PM",
      FOUR_TO_SIX: "04:00 PM - 06:00 PM",
    }),
    []
  );
 
  const timeslots = useMemo(() => Object.values(timeslotMapping), [timeslotMapping]);
  const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];
 
  const isPreviousDisabled = useMemo(() => getStartOfWeek(todayAtMount).toDateString() === currentWeek.start.toDateString(), [getStartOfWeek, todayAtMount, currentWeek.start]);
  const isNextDisabled = useMemo(() => getNextWeekStart(getStartOfWeek(todayAtMount)).toDateString() === currentWeek.start.toDateString(), [getNextWeekStart, getStartOfWeek, todayAtMount, currentWeek.start]);
 
  const formatDate = useCallback((date) => {
    return `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
  }, []);
 
  const generateDatesForWeek = useCallback((startDate) => {
    const dates = [];
    const start = new Date(startDate); // Create a new Date object from startDate
    start.setHours(0, 0, 0, 0); // Ensure the start of the week has no time component
 
    for (let i = 0; i < 5; i++) {
      const newDate = new Date(start); // Create a *new* Date object based on the *initial* 'start'
      newDate.setDate(start.getDate() + i); // Increment the *newDate*
      dates.push({
        day: days[i],
        date: formatDate(newDate),
        fullDate: newDate,
      });
    }
    return dates;
  }, [days, formatDate]);
 
  const updateAvailability = useCallback(
    (data) => {
      const updatedAvailability = Array(4)
        .fill(null)
        .map(() => Array(5).fill({ status: "Unavailable", availabilityId: null }));
 
      const currentToday = new Date();
 
      data.forEach((slot) => {
        const timeSlotString = timeslotMapping[slot.timeSlots];
        const slotDate = new Date(slot.date);
        const dayIndex = slotDate.getDay() - 1;
        const timeslotsArray = timeSlotString ? timeSlotString.split(" - ")[0].split(":") : []; // Safe split
        console.log("timeslotsArray:", timeslotsArray); // LOG 4: Check the array after splitting the start time
 
        let startTimeHours = 0;
        let startTimeMinutes = 0;
        let ampm = "";
 
        if (timeslotsArray.length >= 2) {
          startTimeHours = parseInt(timeslotsArray[0], 10);
          const minutesAndAmPm = timeslotsArray[1];
          startTimeMinutes = parseInt(minutesAndAmPm.substring(0, 2), 10);
          ampm = minutesAndAmPm.substring(2).trim().toUpperCase();
 
          if (ampm === "PM" && startTimeHours !== 12) {
            startTimeHours += 12;
          } else if (ampm === "AM" && startTimeHours === 12) {
            startTimeHours = 0;
          }
        } else if (timeslotsArray.length === 1) {
          startTimeHours = parseInt(timeslotsArray[0], 10); // Handle cases with only hour
        }
 
        const slotStartTime = new Date(slotDate);
        slotStartTime.setHours(startTimeHours, startTimeMinutes, 0, 0);
        let calculatedStatus = slot.status;
        if (slotDate.toDateString() === currentToday.toDateString()) {
          if (slotStartTime.getTime() < now) {
            calculatedStatus = "Unavailable";
            
          }
          else if (slotStartTime.getTime() < now){
            calculatedStatus = "Available";
          }
        } else if (slotDate < currentToday) {
          calculatedStatus = "Unavailable";
          
        }
 
        const timeIndex = timeslots.indexOf(timeSlotString);
        if (timeIndex !== -1 && dayIndex >= 0 && dayIndex < 5) {
          updatedAvailability[timeIndex][dayIndex] = {
            status: calculatedStatus,
            availabilityId: slot.availabilityId,
          };
          console.log(`  Updated Availability[${timeIndex}][${dayIndex}]:`, updatedAvailability[timeIndex][dayIndex]);
        } else {
          console.warn("Invalid timeIndex or dayIndex:", { timeIndex, dayIndex, slot });
        }
      });
      console.log("Final Updated Availability State:", updatedAvailability);
      setAvailability(updatedAvailability);
    },
    [timeslots, timeslotMapping, now, formatDate]
  );
 
  const fetchAvailability = useCallback(
    async (startDate, endDate) => {
      setError(null);
      console.log("Fetching Availability for:", startDate, "to", endDate);
      try {
        const response = await fetch(
          `http://localhost:8000/availability/doctor/${doctorId}/date-range?startDate=${startDate}&endDate=${endDate}`,
          {
            method: "GET",
            headers: {
              "Content-type": "Application/json",
               'Authorization': `Bearer ${token}`
            },
          }
        );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        updateAvailability(data);
      } catch (error) {
        console.error("Error fetching availability:", error);
        setError("Failed to load availability.");
      }
    },
    [doctorId, updateAvailability]
  );
 
  const navigateWeek = useCallback((direction) => {
    let newStartOfWeek;
    let newEndOfWeek;
 
    if (direction === "previous") {
      newStartOfWeek = new Date(currentWeek.start);
      newStartOfWeek.setDate(newStartOfWeek.getDate() - 7);
      newEndOfWeek = new Date(newStartOfWeek);
      newEndOfWeek.setDate(newEndOfWeek.getDate() + 4);
    } else if (direction === "next") {
      newStartOfWeek = new Date(currentWeek.start);
      newStartOfWeek.setDate(currentWeek.start.getDate() + 7);
      newEndOfWeek = new Date(newStartOfWeek);
      newEndOfWeek.setDate(newEndOfWeek.getDate() + 4);
    }
 
    if (newStartOfWeek && newEndOfWeek) {
      setCurrentWeek({ start: newStartOfWeek, end: newEndOfWeek });
    }
  }, [currentWeek.start]);
 
  const weekDates = useMemo(() => generateDatesForWeek(currentWeek.start), [generateDatesForWeek, currentWeek.start]);
 
  useEffect(() => {
    const startDateISO = currentWeek.start.toISOString().split("T")[0];
    const endDateISO = currentWeek.end.toISOString().split("T")[0];
    console.log("useEffect triggered, fetching for:", startDateISO, endDateISO);
    fetchAvailability(startDateISO, endDateISO);
  }, [currentWeek.start, currentWeek.end, fetchAvailability]);
 
  const openModal = useCallback((timeIndex, dayIndex, isPast, fullDate, timeslot) => {
    const slotData = availability[timeIndex][dayIndex];
    const timePart = timeslot.split(" - ")[0];
    const parts = timePart.split(":");
    let adjustedHours = parseInt(parts[0], 10);
    let startTimeMinutes = 0;
    let ampmMatch = timePart.match(/([AP]M)$/i);
    let ampm = ampmMatch ? ampmMatch[1].toUpperCase() : "";
 
    if (parts.length > 1) {
      startTimeMinutes = parseInt(parts[1].substring(0, 2), 10);
    }
    if (ampm === "PM" && adjustedHours !== 12) {
      adjustedHours += 12;
    } else if (ampm === "AM" && adjustedHours === 12) {
        adjustedHours = 0;
    }
    const slotStartTime = new Date(fullDate);
    slotStartTime.setHours(adjustedHours, startTimeMinutes, 0, 0);
    const isCurrentDay = fullDate.toDateString() === todayString;
    const isPastTimeSlot = isCurrentDay && slotStartTime.getTime() < Date.now();
    const today = new Date();
    const calendarDate = fullDate;
    const isPastDay = (
    calendarDate.getFullYear() < today.getFullYear() ||
      (calendarDate.getFullYear() === today.getFullYear() && calendarDate.getMonth() < today.getMonth()) ||
      (calendarDate.getFullYear() === today.getFullYear() && calendarDate.getMonth() === today.getMonth() && calendarDate.getDate() < today.getDate())
    );
 
    if (!isPastDay && slotData.status !== "Booked" && !isPastTimeSlot) {
      setSelectedSlot({ timeIndex, dayIndex, fullDate, timeslot });
      setModalOpen(true);
    }
  }, [availability, todayString]);
 
  const closeModal = useCallback(() => {
    setSelectedSlot(null);
    setModalOpen(false);
  }, []);
 
  const toggleSlot = useCallback(async (status, availabilityId) => {
    setError(null);
    const endpoint =
      status === "Available"
        ? `http://localhost:8000/availability/block/${availabilityId}`
        : `http://localhost:8000/availability/release/${availabilityId}`;
    try {
      const response = await fetch(endpoint, { 
        method: "PUT",
        headers:{
          'Content-type':"Application/json",
          'Authorization': `Bearer ${token}`
          }
          
       });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      fetchAvailability(currentWeek.start.toISOString().split("T")[0], currentWeek.end.toISOString().split("T")[0]);
    } catch (error) {
      console.error("Error toggling slot:", error);
      setError("Failed to update availability.");
    }
    closeModal();
  }, [fetchAvailability, closeModal, currentWeek.start, currentWeek.end]);
 
  if (error) {
    return <div className="error-container">{error}</div>;
  }
  return (
    <div className="calendar-container">
      <h2 className="calendar-title">Doctor Availability Scheduler</h2>
      <table className="calendar-table">
        <thead>
          <tr>
            <th>Timeslots</th>
            {weekDates.map((entry, index) => (
              <th key={index} className={entry.fullDate.toDateString() === todayString ? 'current-day' : ''}>
                {entry.date} <br /> {entry.day}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {timeslots.map((timeslot, timeIndex) => (
            <tr key={timeIndex}>
              <td className="timeslot-label">{timeslot}</td>
              {weekDates.map((dateEntry, dayIndex) => {
                const slotData = availability[timeIndex][dayIndex];
                const timePart = timeslot.split(" - ")[0];
                const parts = timePart.split(":");
                let adjustedHours = parseInt(parts[0], 10);
                let startTimeMinutes = 0;
                let ampmMatch = timePart.match(/([AP]M)$/i);
                let ampm = ampmMatch ? ampmMatch[1].toUpperCase() : "";
 
                if (parts.length > 1) {
                  startTimeMinutes = parseInt(parts[1].substring(0, 2), 10);
                }
 
                if (ampm === "PM" && adjustedHours !== 12) {
                  adjustedHours += 12;
                } else if (ampm === "AM" && adjustedHours === 12) {
                  adjustedHours = 0;
                }
 
                const slotStartTime = new Date(dateEntry.fullDate);
                slotStartTime.setHours(adjustedHours, startTimeMinutes, 0, 0);
                const isCurrentDay = dateEntry.fullDate.toDateString() === todayString;
                const isPastTimeSlot = isCurrentDay && slotStartTime.getTime() < Date.now();
               
                const today = new Date();
                const calendarDate = dateEntry.fullDate;
                const isPastDay = (
                  calendarDate.getFullYear() < today.getFullYear() ||
                  (calendarDate.getFullYear() === today.getFullYear() && calendarDate.getMonth() < today.getMonth()) ||
                  (calendarDate.getFullYear() === today.getFullYear() && calendarDate.getMonth() === today.getMonth() && calendarDate.getDate() < today.getDate())
                );
               
                const shouldDisable = slotData.status === "Booked" || slotData.status === "Blocked" || (isCurrentDay && isPastTimeSlot) || isPastDay;
                const slotClassName = `slot ${slotData.status.toLowerCase()} ${shouldDisable ? 'disabled' : ''}`;
                const isClickable = !shouldDisable;
                return (
                  <td
                    key={dayIndex}
                    className={slotClassName}
                    onClick={isClickable ? () => openModal(timeIndex, dayIndex, isPastDay || (isCurrentDay && isPastTimeSlot), dateEntry.fullDate, timeslot) : undefined}
                    style={{ cursor: isClickable ? 'pointer' : 'default' }}
                  >
                    {slotData.status.charAt(0).toUpperCase() + slotData.status.slice(1)}
                    {(isCurrentDay && isPastTimeSlot && slotData.status !== "Unavailable") && <span className="past-label">(Past)</span>}
                    {isPastDay && slotData.status !== "Unavailable" && <span className="past-label">(Past)</span>}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
      <div className="calendar-navigation">
        <button
          className={`nav-button ${isPreviousDisabled ? "disabled-button" : ""}`}
          onClick={() => navigateWeek("previous")}
          disabled={isPreviousDisabled}
        >
          ← Previous Week
        </button>
        <div className="current-week-indicator">
          {formatDate(currentWeek.start)} - {formatDate(currentWeek.end)}
        </div>
        <button
          className={`nav-button ${isNextDisabled ? "disabled-button" : ""}`}
          onClick={() => navigateWeek("next")}
          disabled={isNextDisabled}
        >
          Next Week →
        </button>
      </div>
 
      {isModalOpen && selectedSlot && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Manage Availability</h3>
            <p>
              Date: <strong>{formatDate(selectedSlot.fullDate)}</strong>
            </p>
            <p>
              Time: <strong>{selectedSlot.timeslot}</strong>
            </p>
            <p>
              Current Status: <strong>{availability[selectedSlot.timeIndex][selectedSlot.dayIndex].status}</strong>
            </p>
            {availability[selectedSlot.timeIndex][selectedSlot.dayIndex].status === "Booked" ? (
              <p>This slot has been booked and cannot be changed.</p>
            ) : (
              <button
                className="modal-button"
                onClick={() =>
                  toggleSlot(
                    availability[selectedSlot.timeIndex][selectedSlot.dayIndex].status,
                    availability[selectedSlot.timeIndex][selectedSlot.dayIndex].availabilityId
                  )
                }
              >
                {availability[selectedSlot.timeIndex][selectedSlot.dayIndex].status === "Available"
                  ? "Block Slot"
                  : "Release Slot"}
              </button>
            )}
            <button className="modal-button cancel-button" onClick={closeModal}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
 
export default DoctorAvailability;
 