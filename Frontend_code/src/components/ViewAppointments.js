import React, { useEffect, useState, useCallback } from "react";
import '@fortawesome/fontawesome-free/css/all.min.css';
import '../CssFiles/viewAppointment.css';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faTimesCircle, faCheckCircle, faCalendarCheck } from '@fortawesome/free-solid-svg-icons';


const ViewAppointments = ({ patientId, token }) => {
    const [appointments, setAppointments] = useState([]);
    const [doctorforRescheduling,setDoctorforRescheduling] = useState([]);
    const [IsReschedule,setIsReschedule] = useState(false);
    const [IdforReschedule,setIdforReschedule]=useState();
    const [confirmationPopup,setConfirmationPopup] = useState(false);
    const [cancelId,setCancelId]=useState();
    const [refreshAppointments, setRefreshAppointments] = useState(false); // State to trigger refresh

    const retrieveData = useCallback(async () => {
        const apiUrl = `http://localhost:8065/appointments/viewByPatient/${patientId}`;
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            setAppointments(data.data || []);
        } else {
            console.log("Failed to fetch appointments");
        }
        setRefreshAppointments(false); // Reset the refresh trigger
    }, [patientId, token]); // Add patientId and token as dependencies

    useEffect(() => {
        retrieveData();
    }, [retrieveData, refreshAppointments]); // Re-run when retrieveData or refreshAppointments changes

    const handleCancel = async (id) => {
        const apiUrl = `http://localhost:8065/appointments/cancel/${id}`;
        const response = await fetch(apiUrl, {
            method: 'DELETE',
            headers: {
                "Content-Type": "application/json",
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Appointment Cancelled");
            setConfirmationPopup(false);
            setRefreshAppointments(true); // Trigger data refresh
        } else {
            console.log("Unable to Cancel");
        }
    };

    const handleRedirect = () => {
        window.location.href = "/myConsultations";
    };

    const handleUpdate = async(appointmentId,doctorId) =>{
        const apiUrl = `http://localhost:8000/availability/doctor/${doctorId}`;
        const response = await fetch(apiUrl,{
            method:'GET',
            headers:{
                'Content-type':'Application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if(response.ok){
         const data = await response.json();
                filterFutureAppointments(data);
                setIsReschedule(true);
                setIdforReschedule(appointmentId);
        }
    };


    const ReScheduleDoctor = async(Id) =>{
        const apiUrl = `http://localhost:8065/appointments/update/${IdforReschedule}/${Id}`;
        const response = await fetch(apiUrl,{
            method:'PUT',
            headers:{
                'Content-type':'Application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if(response.ok){
            alert("appointment Rescheduled Successfully");
            setIsReschedule(false);
            setRefreshAppointments(true); // Trigger data refresh
        }
        else{
            alert("unable to Reschedule appointment , Try Again later");
            setIsReschedule(false);
        }
    }


    const filterFutureAppointments = (data) => {
        const currentDate = new Date();
        const filteredData = [];

        for (let i = 0; i < data.length; i++) {
            const appointment = data[i];
            const [startHour, endHour] = appointment.timeSlots.split('_TO_').map(time => {
                switch (time) {
                    case 'FOUR': return 16; // 4 PM
                    case 'SIX': return 18; // 6 PM
                    case 'TWO': return 14; // 2 PM
                    case 'NINE': return 9; // 9 AM
                    case 'ELEVEN': return 11; // 11 AM
                    case 'ONE': return 13; // 1 PM
                    default: return parseInt(time);
                }
            });

            const appointmentDate = new Date(appointment.date);

            if (appointmentDate > currentDate ||
                (appointmentDate.toDateString() === currentDate.toDateString() && currentDate.getHours() < endHour)) {
                filteredData.push(appointment);
            }
        }

        setDoctorforRescheduling(filteredData);
    };

    const handleCancelPopup = (Id)=>{
        setConfirmationPopup(true);
        setCancelId(Id);
    }
    return (
        <div className="appointmentdetails">
            {appointments.length === 0 ? (
                <p>No appointments found.</p>
            ) : (
                appointments.map((appointment) => (
                    <div key={appointment.appointmentId} className="appointment-card">
                        {appointment.status==="Booked" && <FontAwesomeIcon icon={faCalendarCheck} className="appointment-icon" style={{color:"blue"}}/>}
                        {appointment.status==="Completed" && <FontAwesomeIcon icon={faCheckCircle} className="appointment-icon" style={{color:"green"}}/>}
                        {appointment.status==="Cancelled" && <FontAwesomeIcon icon={faTimesCircle} className="appointment-icon" style={{color:"red"}}/>}
                        <p><strong>Doctor:</strong> {appointment.doctorName}</p>
                        <p><strong>Date:</strong> {appointment.date}</p>
                        <p><strong>Time Slot:</strong> {appointment.timeSlot}</p>
                        <p><strong>Status:</strong> {appointment.status}</p>
                        {appointment.status === "Booked" && <button onClick={() => handleCancelPopup(appointment.appointmentId)}>Cancel Appointment</button>}
                        {appointment.status === "Completed" && <button onClick={() => handleRedirect()}>View Consultation</button>}
                        {appointment.status === "Booked" && <button style={{backgroundColor:"orange",marginLeft:"10px"}} onClick={() => handleUpdate(appointment.appointmentId,appointment.doctorId)}>Reschedule Appointment</button>}

                    </div>
                ))
            )}

            {IsReschedule && <div className="modal-overlay">
            <div className="tablecontainer">
            <div className="msg">
            <h3>Select the Date and TimeSlot to Reschedule</h3>
            <FontAwesomeIcon icon={faTimesCircle} style={{color:"red",fontSize:"24px"}} onClick={()=>{setIsReschedule(false)}}/>
            </div>
            <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Specialization</th>
                    <th>Date</th>
                    <th>Time Slot</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                {doctorforRescheduling.map((doctor, index) => (
                    <tr key={index}>
                        <td>{doctor.doctorName}</td>
                        <td>{doctor.specialization}</td>
                        <td>{doctor.date}</td>
                        <td>{doctor.timeSlots}</td>
                        <td>
                            <button onClick={() => ReScheduleDoctor(doctor.availabilityId)}>Select</button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
        </div>
        </div>
        }
          {confirmationPopup &&
                <div className='modal-overlay'>
                    <div className='confirmingAppointment'>
                    <FontAwesomeIcon icon={faTimesCircle} style={{color:"red",fontSize:"24px",marginLeft:"200px"}} onClick={()=>{setConfirmationPopup(false)}}/>
                        <p>Are You Sure About Cancelling Appointment?</p>
                    <button onClick={() => handleCancel(cancelId)}>Cancel Appointment</button>
                    </div>
                </div>}
        </div>
    );
};

export default ViewAppointments;