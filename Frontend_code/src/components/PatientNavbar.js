import React, { useState }  from "react";
import "../CssFiles/PatientNavbar.css";
import {Link} from'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faUserEdit, faSignOutAlt,faCalendarCheck, faCalendarAlt, faNotesMedical ,faClipboardList} from '@fortawesome/free-solid-svg-icons';
import patientimage from '../assets/patient.png';
function PatientNavBar({userdata,token}){
    const [popup, setPopup] = useState(false);
    const [userData, setUserData] = useState(null);
    const handleLogout = () =>{
        localStorage.clear();
    }
    const closePopup = () => {
        setPopup(false);
      };
    
      const handleProfile = async (event) => {
        event.preventDefault();
        const apiUrl = `http://localhost:8086/api/patient/${userdata.userId}`;
    
        try {
          const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
          });
    
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
    
          const data = await response.json();
          console.log(data);
          setUserData(data);
          setPopup(true);
        } catch (error) {
          console.error('Error fetching user data:', error);
        }
      };

      const editProfile = () =>{
        window.location.href = '/edit-profile';
      }

    return (
        <div className="navbarcss">
            <div className="profile">  
            <img src={patientimage} alt="DoctorProfile" className="profileIcon" onClick={handleProfile} />
            <p>{userdata.name}</p> 
            </div>
            <nav>
                <ul>
                <li><Link to="/edit-profile"><FontAwesomeIcon icon={faUserEdit} /> &nbsp; Edit Profile</Link></li>
                <li><Link to="/"><FontAwesomeIcon icon={faCalendarAlt} />  &nbsp; Manage Bookings</Link></li>
                <li><Link to="/myAppointments"><FontAwesomeIcon icon={faCalendarCheck} />  &nbsp; My Appointments</Link></li>
                <li><Link to="/myConsultations"><FontAwesomeIcon icon={faClipboardList} /> &nbsp; My Consultations</Link></li>
                <li><Link to="/medicalHistory"><FontAwesomeIcon icon={faNotesMedical} />  &nbsp; Medical History</Link></li>
                <li><a href="/" onClick={handleLogout}><FontAwesomeIcon icon={faSignOutAlt} /> &nbsp; Logout</a></li>
                </ul>
            </nav>

            {popup && userData && (
        <div className="popup">
        <span className="close" onClick={closePopup}>&times;</span>
          <div className="popup-content">
            <h2>User Details</h2>
            <p><strong>ID:</strong>&nbsp; &nbsp;{userData.data.patient_id}</p>
            <p><strong>Email:</strong>&nbsp;&nbsp; {userData.data.email}</p>
            <p><strong>PhoneNumber:</strong>&nbsp;&nbsp;{userData.data.phoneNumber}</p>
            <p><strong>Name:</strong>&nbsp;&nbsp; {userData.data.name}</p>
            <p><strong>Gender:</strong>&nbsp;&nbsp;{userData.data.gender}</p>
            <p><strong>Age:</strong>&nbsp;&nbsp;{userData.data.age}</p>
            <p><strong>Address:</strong>&nbsp;&nbsp;{userData.data.address}</p>
            {/* Add more user details as needed */}
            <button onClick={editProfile}>Edit Profile</button>
          </div>
        </div>
      )}
        </div>
    );
}

export default PatientNavBar;