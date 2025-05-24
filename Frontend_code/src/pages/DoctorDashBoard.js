import React, { useState } from 'react';
import DoctorNavbar from '../components/DoctorNavbar';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'; 
import "../CssFiles/DoctorDashBoard.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBell } from '@fortawesome/free-solid-svg-icons';
import EditProfile from '../components/EditProfile';
import Notifications from '../components/Notifications';
import AppointmentList from '../components/AppointmentList';
import DoctorAvailability from '../components/DoctorAvailability';

function DoctorDashBoard() {
    const [notificationTab, setNotificationTab] = useState(false);
    const userdata = JSON.parse(localStorage.getItem('userLoggedIn'));
    const token = localStorage.getItem('jwtToken');
    const handleBellClick = () => {
        setNotificationTab(!notificationTab);
        console.log('Notification tab state:', !notificationTab);
    };

    return (
        <Router>
            <div className='doctorDashBoard'>
                <div className='navbarforPatient'><DoctorNavbar userdata={userdata} token={token}/></div>
                <div className='dashBoardContent'>
                    <div className='topTabDoctor'>
                        <h2>DOCTOR DASHBOARD</h2>
                        <FontAwesomeIcon className="bell" icon={faBell} onClick={handleBellClick} />
                        {notificationTab && <Notifications tab={setNotificationTab} userType={userdata.role} token={token} userId={userdata.userId}/>}
                    </div>
                    <div className='otherComponents'>
                        <Routes>
                            <Route path="/edit-profile" element={<EditProfile userdata={userdata} token={token}/>} />
                            <Route path='/consultations' element={<AppointmentList doctorId={userdata.userId} token={token}/>}/>
                            <Route path='/' element={<DoctorAvailability doctorId={userdata.userId} token={token}/>}></Route>
                        </Routes>   
                    </div>
                </div>
            </div>
        </Router>
    );
}

export default DoctorDashBoard;