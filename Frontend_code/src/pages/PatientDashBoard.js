import React ,{useState}from 'react';
import PatientNavBar from '../components/PatientNavbar';
import { BrowserRouter as Router, Route, Routes} from 'react-router-dom'; 
import "../CssFiles/PatientDashBoard.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faBell} from '@fortawesome/free-solid-svg-icons';
import EditProfile from '../components/EditProfile';
import Notifications from '../components/Notifications';
import MedicalHistory from '../components/MedicalHisotry';
import PatientConsultations from '../components/PatientConsultations';
import DoctorTable from '../components/DoctorTable';
import ViewAppointments from '../components/ViewAppointments';
function PatientDashBoard(){
    const [notificationTab, setNotificationTab] = useState(false);
    const userdata = JSON.parse(localStorage.getItem('userLoggedIn'));
    const token = localStorage.getItem('jwtToken');
        const handleBellClick = () => {
            setNotificationTab(!notificationTab);
            console.log('Notification tab state:', !notificationTab);
        };
    return (
        
        <Router>
        <div className='patientDashBoard'>
            <div className='navbarforPatient'><PatientNavBar userdata={userdata} token={token}/></div>
            <div className='dashBoardContent'>
                <div className='topTabPatient'>
                <h2>PATIENT DASHBOARD</h2>
                <FontAwesomeIcon className="bell" icon={faBell} onClick={handleBellClick} />
                {notificationTab && <Notifications tab={setNotificationTab} userType={userdata.role} token={token} userId={userdata.userId}/>}
                </div>
                <div className='otherComponents'>
                <Routes>
                        <Route path="/edit-profile" element={<EditProfile userdata={userdata} token={token}/>} />
                            {/* Add more routes here as needed */}
                        <Route path="/" element={<DoctorTable token={token} patientId={userdata.userId} patientName={userdata.name}/>}></Route> 
                        <Route path='/myConsultations' element={<PatientConsultations token={token} patientId={userdata.userId}/>}></Route>
                        <Route path='/myAppointments' element={<ViewAppointments token={token} patientId={userdata.userId}/>}></Route>
                        <Route path="/medicalHistory" element={<MedicalHistory userId={userdata.userId} token={token}/>}/>
                        </Routes> 
                </div>
            </div>
        </div>
        </Router>
    );
}

export default PatientDashBoard;