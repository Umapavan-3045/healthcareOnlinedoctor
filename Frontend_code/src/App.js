import React, { useState, useEffect } from 'react';
import './CssFiles/App.css';
import LoginandRegister from './components/SignInandSignUp';
import DoctorDashBoard from './pages/DoctorDashBoard';
import PatientDashBoard from './pages/PatientDashBoard';
import {jwtDecode} from 'jwt-decode';
import AdminDashBoard from './pages/AdminDashBoard';
//import Home from './pages/Home';

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [loginDetails, setLoginDetails] = useState(null);
  const [sessionExpired, setSessionExpired] = useState(false);

  const checkTokenExpiration = () => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      const decodedToken = jwtDecode(token);
      const currentTime = Date.now();
      const expirationTime = decodedToken.exp * 1000;
      if (currentTime >= expirationTime) {
        setSessionExpired(true);
        setLoggedIn(false);
        localStorage.clear();
      }
    }
  };

  useEffect(() => {
    const userdata = localStorage.getItem('userLoggedIn');
    if (userdata) {
      setLoginDetails(JSON.parse(userdata));
      console.log(userdata);
      setLoggedIn(true);
      const token = localStorage.getItem('jwtToken');
      if (token) {
        const decodedToken = jwtDecode(token);
        const currentTime = Date.now();
        const expirationTime = decodedToken.exp * 1000;
        const timeUntilExpiration = expirationTime - currentTime;

        if (timeUntilExpiration > 0) {
          setTimeout(() => {
            setSessionExpired(true);
            setLoggedIn(false);
            localStorage.clear();
          }, timeUntilExpiration);
        } else {
          setSessionExpired(true);
          setLoggedIn(false);
          localStorage.clear();
        }
      }
      const intervalId = setInterval(checkTokenExpiration, 60000);
      return () => clearInterval(intervalId);
    }
  }, []);

  return (
    <div className="App">
      <p className='copyright'>&copy; 2025 HealthCare Management </p>
      {sessionExpired && (
        <div className='modal-overlay'>
          <div className='modal-container'>
            <p className='session-expired'>Session expired. Please log in again.</p>
            <button onClick={() => { setSessionExpired(false); }}>OK</button>
          </div>
        </div>
      )}
      {loggedIn ? (
        loginDetails && loginDetails.role === 'PATIENT' ? (
          <PatientDashBoard />
        ) : loginDetails.role === 'DOCTOR' ? (
          <DoctorDashBoard />
        ):(
          <AdminDashBoard/>
        )
      ) : (
        <LoginandRegister setLoggedIn={setLoggedIn} setLoginDetails={setLoginDetails} />
      )}
    </div>
  );
}

export default App;
