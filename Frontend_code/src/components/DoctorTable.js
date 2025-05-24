import React, { useState, useEffect } from 'react';
import "../CssFiles/DoctorTable.css";
import '@fortawesome/fontawesome-free/css/all.min.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faTimesCircle} from '@fortawesome/free-solid-svg-icons';
const DoctorTable = ({token,patientId,patientName}) => {
  const [displayDoctors, setDisplayDoctors] = useState([]);
  const [fetchError, setFetchError] = useState(null);
  const [specialization, setSpecialization] = useState('');
  const [date, setDate] = useState('');
  const [search, setSearch] = useState('');
  const [timeSlot, setTimeSlot] = useState('');
  const [confirmationPopup,setConfirmationPopup] = useState(false);
  const [doctordata,setDoctorData]=useState();
  const [checkingAppointment,setCheckingAppointment] = useState(false);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:8000/availability/doctors', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('Network response was not ok');
        }

        const data = await response.json();

        if (data.success && data.data) {
          filterFutureAppointments(data.data);
          setFetchError(null);
        } else {
          setFetchError('Failed to fetch doctor data. Please try again later.');
        }
      } catch (error) {
        setFetchError('Failed to connect to the server. Please check your network connection.');
      }
    };

    fetchData();
  }, []);

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

    setDisplayDoctors(filteredData);
};




  const handlePopup = (doctor) =>{
    setDoctorData(doctor);
    setConfirmationPopup(true);
  }

  const filteredDoctors = displayDoctors.filter(doctor =>
    (specialization ? doctor.specialization === specialization : true) &&
    (date ? doctor.date === date : true) &&
    (search ? doctor.doctorName.toLowerCase().includes(search.toLowerCase()) : true) &&
    (timeSlot ? doctor.timeSlots === timeSlot : true)
  );

  const handleBook = async (doctor) => {
    try {
        const patientdetails ={
            patientId:patientId,
            patientName:patientName,
            doctorName:doctor.doctorName
        }
      const response = await fetch(`http://localhost:8065/appointments/create/${doctor.availabilityId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body:JSON.stringify(patientdetails)
      });

      if (!response.ok) {
        throw new Error('Failed to create appointment');
      }

      const data = await response.json();

      setDisplayDoctors(prevDoctors => prevDoctors.filter(d => d.doctorId !== doctor.doctorId));

      alert('Appointment booked successfully!');
      setConfirmationPopup(false);
    } catch (error) {
      alert('Failed to book appointment. Please try again.');
      setConfirmationPopup(false);
    }
  };
  const HandleChecking = async(doctordata)=>{
    try{
    const apiUrl = `http://localhost:8065/appointments/checkappointment/${doctordata.date}/${doctordata.timeSlots}`;
    const response = await fetch(apiUrl,{
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      }
    })
    const result = await response.json();
    if(!response.ok)
    {
      alert("failed to checking appointment details");
    }
    if(result.data === true){
      setConfirmationPopup(false);
      setCheckingAppointment(true);
    }
    else{
      handleBook(doctordata);
    }
  }
  catch (error) {
    alert('Failed to Check Appointment. Please try again.');
    setConfirmationPopup(false);
  }
  }
  const HandleRedirect = () =>{
    window.location.href = "/myAppointments";
  }
  return (
    <div className='doctortable'>
      <div className="filter-container">
        <label>Specialization:</label>
        <select value={specialization} onChange={(e) => setSpecialization(e.target.value)}>
          <option value="">All</option>
          <option value="General">General</option>
          <option value="Neurology">Neurology</option>
          <option value="Cardiology">Cardiology</option>
          <option value="Gynaecology">Gynaecology</option>
          <option value="Orthodontics">Orthodontics</option>
          <option value="Pulmonology">Pulmonology</option>
          <option value="Nephrology">Nephrology</option>
          <option value="Oncology">Oncology</option>
          <option value="Dermatology">Dermatology</option>
          <option value="Psychology">Psychology</option>
        </select>
        <label>Date:</label>
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
      </div>
      <div className='filter-container'>
      <label>Search:</label>
        <input type="text" value={search} onChange={(e) => setSearch(e.target.value)} placeholder='search doctor name' />
        <label>Time Slot:</label>
        <select value={timeSlot} onChange={(e) => setTimeSlot(e.target.value)}>
          <option value="">All</option>
          <option value="NINE_TO_ELEVEN">9:00 - 11:00</option>
          <option value="ELEVEN_TO_ONE">11:00 - 1:00</option>
          <option value="TWO_TO_FOUR">2:00 - 4:00</option>
          <option value="FOUR_TO_SIX">4:00 - 6:00</option>
        </select>
      </div>
      {fetchError && <p style={{ color: 'red' }}>{fetchError}</p>}
      {filteredDoctors.length === 0 ? (
        <p className="no-doctors">No doctors available for the selected criteria.</p>
      ) : (
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
            {filteredDoctors.map((doctor, index) => (
              <tr key={index}>
                <td>{doctor.doctorName}</td>
                <td>{doctor.specialization}</td>
                <td>{doctor.date}</td>
                <td>{doctor.timeSlots}</td>
                <td>
                  <button onClick={() => handlePopup(doctor)}>Book</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {confirmationPopup && 
      <div className='modal-overlay'>
        <div className='confirmingAppointment'>
        <FontAwesomeIcon icon={faTimesCircle} style={{color:"red",fontSize:"24px",marginLeft:"200px"}} onClick={()=>{setConfirmationPopup(false)}}/>
       <div className='doctorDetails'> <label><b>Doctor_Name :</b> &nbsp; &nbsp;{doctordata.doctorName}</label>
        <label><b>Doctor_Specialization :</b>&nbsp; &nbsp; {doctordata.specialization}</label>
        <label><b>Date of Appointment :</b>&nbsp; &nbsp; {doctordata.date}</label>
        <label><b>Time Slot :</b> &nbsp; &nbsp;{doctordata.timeSlots}</label>
        </div>
        <button onClick={() => HandleChecking(doctordata)}>confirm Booking</button>
        </div>
      </div>}

      {checkingAppointment && 
      <div className='modal-overlay'>
        <div className='confirmingAppointment'>
        <FontAwesomeIcon icon={faTimesCircle} style={{color:"red",fontSize:"24px",marginLeft:"400px"}} onClick={()=>{setCheckingAppointment(false)}}/>
       <div className='doctorDetails'> 
        <p>Your are already having appointment on the selected Date and timeSlot</p>
        </div>
        <button onClick={() => HandleRedirect()}>view</button>
        </div>
      </div>}

    </div>
  );
};

export default DoctorTable;
