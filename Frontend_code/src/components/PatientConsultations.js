import React, { useEffect, useState } from 'react';

const PatientConsultations = ({ token, patientId }) => {
    const [consultations, setConsultations] = useState([]);

    useEffect(() => {
        const handleExtractAppointments = async () => {
            try {
                const apiUrl = `http://localhost:8065/appointments/viewByPatient/${patientId}`;
                const response = await fetch(apiUrl, {
                    method: 'GET',
                    headers: {
                        'Content-type': 'Application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    handleFilteringAppointmentIds(data.data);
                } else {
                    throw new Error('Network response was not ok');
                }
            } catch (error) {
                console.log("error ", error);
            }
        };

        handleExtractAppointments();
    }, [patientId, token]);

    const handleFilteringAppointmentIds = (appointments) => {
        const filteredAppointmentIds = [];
        for (let i = 0; i < appointments.length; i++) {
            if (appointments[i].status === "Completed") {
                const obj = {
                    doctorName: appointments[i].doctorName,
                    date: appointments[i].date,
                    id: appointments[i].appointmentId,
                };
                filteredAppointmentIds.push(obj);
            }
        }
        HandleGetConsultations(filteredAppointmentIds);
    };

    const getConsultation = async (obj) => {
        const apiUrl = `http://localhost:8050/consultation/appointment/${obj.id}`;
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-type': 'Application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            if (data.data && data.data.length > 0) {
                const consultation = {
                    ...data.data[0],
                    doctorName: obj.doctorName,
                    date: obj.date
                };
                setConsultations((prevConsultations) => {
                    if (!prevConsultations.some(c => c.consultationId === consultation.consultationId)) {
                        return [...prevConsultations, consultation];
                    }
                    return prevConsultations;
                });
            }
        }
    };

    const HandleGetConsultations = (appointmentIds) => {
        appointmentIds.forEach(obj => {
            getConsultation(obj);
        });
    };

    return (
        <div className='patientConsultation'>
            <h3>Consultation Details</h3>
            {consultations.length > 0 ? (
                <div className='patientConsultation' >
                    {consultations.map((consultation) => (
                        <div className='Consultation' key={consultation.consultationId}>
                            <p><strong>Doctor Name:</strong> {consultation.doctorName}</p>
                            <p><strong>Date Of Consultation:</strong> {consultation.date}</p>
                            <p><strong>Notes:</strong> {consultation.notes}</p>
                            <p><strong>Prescription:</strong> {consultation.prescription}</p>
                        </div>
                    ))}
                </div>
            ) : (
                <div>No Consultations yet...!</div>
            )}
        </div>
    );
};

export default PatientConsultations;
