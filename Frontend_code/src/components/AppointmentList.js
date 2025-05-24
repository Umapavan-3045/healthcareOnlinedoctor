import { useEffect, useState } from "react";
import { FaFilter } from "react-icons/fa"; // Import filter icon
import ConsultationForm from "./ConsultationForm";
import UpdateConsultationForm from "./UpdateConsultation";
import "../CssFiles/generalCss.css";
 import "..//CssFiles/MedicalHistoryModel.css";
const AppointmentList = ({ doctorId, token }) => {
  const [appointments, setAppointments] = useState([]);
  const [filteredAppointments, setFilteredAppointments] = useState([]);
  const [consultationStatus, setConsultationStatus] = useState({});
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAppointmentId, setSelectedAppointmentId] = useState("");
  const [isUpdate, setIsUpdate] = useState(false);
  const [selectedDate, setSelectedDate] = useState("");
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  const [historyData, setHistoryData] = useState([]);
  const [selectedPatientId, setSelectedPatientId] = useState("");
  const [showDatePicker, setShowDatePicker] = useState(false); // Date Picker
  const [sortDirection, setSortDirection] = useState("desc"); // 'asc' or 'desc'
  const [sortBy, setSortBy] = useState(null);
 
  const TimeslotsMapping = {
    NINE_TO_ELEVEN: "9:00 am - 11:00 am",
    ELEVEN_TO_ONE: "11:00 am - 1:00 pm",
    TWO_TO_FOUR: "2:00 pm - 4:00 pm",
    FOUR_TO_SIX: "4:00 pm - 6:00 pm",
  };
 
  const fetchAppointments = async () => {
    
    try {
      const response = await fetch(
        `http://localhost:8065/appointments/viewByDoctor/${doctorId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      console.log("Fetched Appointments:", data);
      const formattedAppointments = (data.data || []).map((appointment) => ({
        ...appointment,
        parsedDate: new Date(appointment.date),
        // Format the date toYYYY-MM-DD for consistent comparison with date picker
        date: new Date(appointment.date).toISOString().split("T")[0],
      }));
      setAppointments(formattedAppointments);
      setFilteredAppointments(formattedAppointments);
      checkConsultationStatus(formattedAppointments);
    } catch (error) {
      console.error("Error fetching appointments:", error);
      setAppointments([]);
      setFilteredAppointments([]);
      setConsultationStatus({});
    }
  };
 
  useEffect(() => {
    fetchAppointments();
  }, []);
 
  useEffect(() => {
    // No need for this separate useEffect anymore as formatting is done in fetch
  }, [appointments]);
 
  useEffect(() => {
    // Sort the filtered appointments whenever the sort criteria change
    if (sortBy === "date") {
      const sorted = [...filteredAppointments].sort((a, b) => {
        const dateA = a.parsedDate;
        const dateB = b.parsedDate;
 
        if (sortDirection === "asc") {
          return dateA.getTime() - dateB.getTime(); // Ascending (oldest first)
        } else {
          return dateB.getTime() - dateA.getTime(); // Descending (latest first)
        }
      });
      setFilteredAppointments(sorted);
    }
    // You can add more sorting logic for other columns if needed
  }, [filteredAppointments, sortBy, sortDirection]);
 
  const checkConsultationStatus = async (appointments) => {
    let statusMap = {};
    for (const appointment of appointments) {
      statusMap[appointment.appointmentId] = await fetchConsultationStatus(
        appointment.appointmentId
      );
    }
    setConsultationStatus(statusMap);
  };
 
  const fetchConsultationStatus = async (appointmentId) => {
    try {
      const response = await fetch(
        `http://localhost:8050/consultation/appointment/${appointmentId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      return data.success && data.data ? data.data : null;
    } catch (error) {
      console.error("Error checking consultation status:", error);
      return null;
    }
  };
 
//   const handleOpenModal = (appointmentId) => {
//     setSelectedAppointmentId(appointmentId);
//     setIsUpdate(!!consultationStatus[appointmentId]); // Check if consultation data exists
//     setIsModalOpen(true);
//   };
const handleOpenModal = async (appointmentId) => {
    setSelectedAppointmentId(appointmentId);
    const consultationData = await fetchConsultationStatus(appointmentId);
    setIsUpdate(!!consultationData); // Set isUpdate based on whether consultation data exists
    setIsModalOpen(true);
  };
 
  const handleDateChange = (event) => {
    const selectedDate = event.target.value;
    setSelectedDate(selectedDate);
 
    if (!selectedDate) {
      // If no date is selected, reset filteredAppointments to all appointments
      setFilteredAppointments(appointments);
      // Optionally reset sorting as well
      setSortBy(null);
      return;
    }
 
    const filtered = appointments.filter((app) => {
      return app.date === selectedDate;
    });
 
    setFilteredAppointments(filtered);
 
    // Reset sorting when filtering by date
    setSortBy(null);
  };
 
  const fetchMedicalHistory = async (patientId) => {
    try {
      const response = await fetch(
        `http://localhost:8050/history/patient/${patientId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer  ${token}`,
          },
        }
      );
      const data = await response.json();
      const historyRecords = data.data || [];
 
      setSelectedPatientId(patientId);
 
      if (historyRecords.length === 0) {
        alert(
          `Patient ID ${patientId} has not created their medical history yet.`
        );
        return;
      }
 
      setHistoryData(historyRecords);
      setShowHistoryModal(true);
    } catch (error) {
      alert(
        `Failed to retrieve medical history for Patient ID ${patientId}.`
      );
      setHistoryData([{ healthHistory: "Failed to retrieve medical history." }]);
      setShowHistoryModal(true);
    }
  };
 
  const handleConsultationFormSubmit = async (appointmentId) => {
    try {
      await fetchConsultationStatus(appointmentId); // Optionally re-check status
      await fetchAppointments(); // Re-fetch all appointments
      setIsModalOpen(false);
    } catch (error) {
      console.error("Error after creating consultation:", error);
      // Handle error
    }
  };
 
  const handleUpdateConsultationFormSubmit = async (appointmentId) => {
    try {
      await fetchConsultationStatus(appointmentId); // Optionally re-check status
      await fetchAppointments(); // Re-fetch all appointments to update UI
      setIsModalOpen(false);
    } catch (error) {
      console.error("Error after updating consultation:", error);
      // Handle error
    }
  };
 
  const handleSort = (column) => {
    if (column === sortBy) {
      // Toggle sort direction if the same column is clicked again
      setSortDirection(sortDirection === "asc" ? "desc" : "asc");
    } else {
      // Sort by the new column, default to descending (latest first) for date
      setSortBy(column);
      setSortDirection("desc");
    }
  };
 
  const filterTodayAppointments = () => {
    const today = new Date().toISOString().split("T")[0];
    const todaysAppointments = appointments.filter(
      (app) => app.date === today
    );
    setFilteredAppointments(todaysAppointments);
    setSelectedDate(today); // Update selected date for UI
    setSortBy(null); // Reset sorting
  };
 
  const filterCompletedAppointments = () => {
    const completed = appointments.filter(
      (app) => app.status && app.status.toLowerCase() === "completed"
    );
    setFilteredAppointments(completed);
    setSelectedDate(""); // Clear selected date
    setSortBy(null); // Reset sorting
  };
 
  const showAllAppointments = () => {
    setFilteredAppointments(appointments);
    setSelectedDate(""); // Clear selected date
    setSortBy(null); // Reset sorting
  };
 
  const isAppointmentTimeSlotPassed = (appointment) => {
    const now = new Date();
    const appointmentDate = new Date(appointment.date);
    let [year, month, day] = appointment.date.split('-');
    appointmentDate.setDate(parseInt(day, 10));
    appointmentDate.setMonth(parseInt(month, 10) - 1); // Month is 0-indexed
    appointmentDate.setFullYear(parseInt(year, 10));
 
    const [startTimeStr] = TimeslotsMapping[appointment.timeSlot].split(" - ")[0].split(":");
    const appointmentStartHour = parseInt(startTimeStr.split(" ")[0], 10) + (startTimeStr.includes("pm") && parseInt(startTimeStr.split(" ")[0], 10) !== 12 ? 12 : 0);
    const appointmentStartMinute = parseInt(startTimeStr.split(" ")[1] || 0, 10);
 
    appointmentDate.setHours(appointmentStartHour, appointmentStartMinute, 0, 0);
 
    return now >= appointmentDate;
  };
 
  const getCreateButtonStyles = (appointment) => {
    if (!consultationStatus[appointment.appointmentId] && !isAppointmentTimeSlotPassed(appointment)) {
      return {
        width: "65px",
        marginRight: "10px",
        backgroundColor: "#90EE90", // Light Green
        color: "#000000", // Gray for disabled look
        cursor: "not-allowed",
        border: "1px solid #808080",
      };
    } else {
      return {
        width: "65px",
        marginRight: "10px",
        backgroundColor: consultationStatus[appointment.appointmentId] ? "orange" : "green",
      };
    }
  };
 
  const getCreateButtonClassName = (appointment) => {
    return `btn btn-sm ${
      consultationStatus[appointment.appointmentId] ? "btn-warning" : "btn-success"
    }`;
  };
 
  return (
    <div className="appointments">
      <h2 className="text-center mb-4">My Appointments</h2>
 
      <div className="navbuttons">
        <button className="btn1" onClick={filterTodayAppointments}>
          Today's Appointments
        </button>
        <button className="btn2" onClick={filterCompletedAppointments}>
          Completed Appointments
        </button>
        <button className="btn3" onClick={showAllAppointments}>
          All Appointments
        </button>
      </div>
 
      {filteredAppointments.length === 0 && selectedDate && (
        <p className="text-center text-danger fw-bold">
          No available appointments for {selectedDate}.
        </p>
      )}
 
      {filteredAppointments.length === 0 && !selectedDate && (
        <p className="text-center text-info fw-bold">No appointments available.</p>
      )}
 
      {filteredAppointments.length > 0 && (
        <table className="consultationdata">
          <thead className="table-dark">
            <tr>
              <th>Appointment ID</th>
              <th style={{ width: "120px" }}>Patient Name</th>
              <th
                onClick={() => handleSort("date")}
                style={{ cursor: "pointer" }}
              >
                Date
                {/* Clickable Filter Icon */}
                <FaFilter
                  className="ms-2 text-light cursor-pointer"
                  onClick={() => setShowDatePicker(!showDatePicker)}
                />
                {sortBy === "date" && (
                  <span>{sortDirection === "asc" ? " ▲" : " ▼"}</span>
                )}
              </th>
              <th>Time Slot</th>
              <th style={{ width: "140px" }}>Status</th>
              <th>Consultation</th>
              <th>Medical History</th>
            </tr>
          </thead>
          <tbody>
            {/* Show Date Picker when Icon is clicked */}
            {showDatePicker && (
              <tr>
                <td colSpan="7">
                  <div className="d-flex justify-content-center">
                    <input
                      type="date"
                      value={selectedDate}
                      onChange={handleDateChange}
                      className="form-control w-25"
                    />
                  </div>
                </td>
              </tr>
            )}
 
            {filteredAppointments.map((appointment) => (
              <tr key={appointment.availabilityId}>
                <td>{appointment.appointmentId}</td>
                <td>{appointment.patientName}</td>
                <td>{appointment.date}</td>
                <td>
                  {TimeslotsMapping[appointment.timeSlot] ||
                    appointment.timeSlot}{" "}
                </td>
                <td>{appointment.status}</td>
                <td style={{ width: "150px" }}>
                  <div className="d-flex justify-content-center gap-3">
                    {appointment.status && appointment.status.toLowerCase() === "cancelled" ? (
                      "--"
                    ) : (
                      <button
                        style={getCreateButtonStyles(appointment)}
                        onClick={() => {
                            if (!(!consultationStatus[appointment.appointmentId] && !isAppointmentTimeSlotPassed(appointment))) {
                              handleOpenModal(appointment.appointmentId);
                            }
                          }}
                          className={getCreateButtonClassName(appointment)}
                          disabled={!consultationStatus[appointment.appointmentId] && !isAppointmentTimeSlotPassed(appointment)}
                        >
                          {consultationStatus[appointment.appointmentId]
                            ? "Update"
                            : "Create"}
                      </button>
                    )}
                  </div>
                </td>
                <td>
                  <button
                    style={{ width: "65px" }}
                    className="btn btn-sm btn-info"
                    onClick={() => fetchMedicalHistory(appointment.patientId)}
                  >
                    View History
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
 
 {showHistoryModal && (
      <div className="medical-history-modal-overlay">
        <div className="medical-history-modal">
          <div className="modal-header">
            <h5 className="modal-title">
              Medical History (Patient ID: {selectedPatientId})
            </h5>
            <button
              type="button"
              className="close-button"
              onClick={() => setShowHistoryModal(false)}
            >
              &times;
            </button>
          </div>
          <div className="modal-body">
            {historyData.length > 0 ? (
              <ul className="medical-history-list">
                {historyData.map((record, index) => (
                  <li key={index}>{record.healthHistory}</li>
                ))}
              </ul>
            ) : (
              <p className="text-danger fw-bold">
                Patient ID {selectedPatientId} has not created their medical
                history yet.
              </p>
            )}
          </div>
        </div>
      </div>
    )}
      
 
      {isModalOpen &&
        (isUpdate ? (
          <UpdateConsultationForm
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            appointmentId={selectedAppointmentId}
            onSubmit={handleUpdateConsultationFormSubmit}
            token={token} //  submit handler
          />
        ) : (
          <ConsultationForm
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            appointmentId={selectedAppointmentId}
            isUpdate={isUpdate}
            onSubmit={handleConsultationFormSubmit} // submit handler
            token={token}
          />
        ))}
    </div>
  );
};
 
export default AppointmentList;
 