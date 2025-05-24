import React, { useState } from 'react';
import "../CssFiles/DoctorNavbar.css";
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faUserEdit, faSignOutAlt, faCalendarCheck, faHistory } from '@fortawesome/free-solid-svg-icons';
import doctorimage from "../assets/doctor.png";
function DoctorNavbar({userdata,token}) {
  const [popup, setPopup] = useState(false);
  const [userData, setUserData] = useState(null);

  const handleLogout = () => {
    localStorage.clear();
  };

  const closePopup = () => {
    setPopup(false);
  };

  const editProfile = () =>{
    window.location.href = '/edit-profile';
  }

  const handleProfile = async (event) => {
    event.preventDefault();
    const apiUrl = `http://localhost:8086/api/doctor/${userdata.userId}`;

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

  return (
    <div className="navbarcss">
      <div className="profile">
        <img src={doctorimage} alt="DoctorProfile" className="profileIcon" onClick={handleProfile} />
        <p>{userdata.name}</p>
      </div>
      <nav>
        <ul>
          <li><Link to="/edit-profile"><FontAwesomeIcon icon={faUserEdit} /> &nbsp; Edit Profile</Link></li>
          <li><Link to="/"><FontAwesomeIcon icon={faCalendarCheck} /> &nbsp; Manage Availability</Link></li>
          <li><Link to="/consultations"><FontAwesomeIcon icon={faHistory} /> &nbsp; Manage Consultations</Link></li>
          <li><a href="/" onClick={handleLogout}><FontAwesomeIcon icon={faSignOutAlt} /> &nbsp; Logout</a></li>
        </ul>
      </nav>

      {popup && userData && (
        <div className="popup">
        <span className="close" onClick={closePopup}>&times;</span>
          <div className="popup-content">
            <h2>User Details</h2>
            <p><strong>ID:</strong> &nbsp; &nbsp;{userData.data.doctor_id}</p>
            <p><strong>Email:</strong> &nbsp; &nbsp;{userData.data.email}</p>
            <p><strong>PhoneNumber:</strong> &nbsp; &nbsp;{userData.data.phoneNumber}</p>
            <p><strong>Name:</strong> &nbsp; &nbsp;{userData.data.name}</p>
            <p><strong>Specialization:</strong> &nbsp; &nbsp;{userData.data.specialization}</p>
            <button onClick={editProfile}>Edit Profile</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default DoctorNavbar;