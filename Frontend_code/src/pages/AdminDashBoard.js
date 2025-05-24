import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faPlus, faTimesCircle,faSignOutAlt } from '@fortawesome/free-solid-svg-icons';
import "../CssFiles/AdminDashBoard.css";
import doctorimage from "../assets/doctor.png";
const AdminDashBoard = () => {
    const role ='DOCTOR';
    const [doctors, setDoctors] = useState([]);
    const [deleteCheckResultPopup, setDeleteCheckResultPopup] = useState(false);
    const [doctorToDeleteId, setDoctorToDeleteId] = useState(null);
    const [confirmation,setConfirmation]=useState(false);
    const token = localStorage.getItem('jwtToken');
    const [email, setEmail] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [errors, setErrors] = useState({});
    const [specialization,setSpecialization]=useState('');
    const [errorfrombackend, setErrorfromBackend] = useState('');
    const [formPopup,setFormPopup]=useState(false);
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phonePattern = /^[0-9]{10}$/;

        const validateForm = () => {
            let formErrors = {};
            if (!email) formErrors.email = 'Email is required';
            else if (!emailPattern.test(email)) {
                formErrors.email = 'Invalid email format';
              }
            if (!phoneNumber) formErrors.phoneNumber = 'Phone number is required';
            else if (!phonePattern.test(phoneNumber)) {
                formErrors.phoneNumber = 'Invalid phone number format';
              }
            if (!password) formErrors.password = 'Password is required';
            if(!specialization) formErrors.specialization = 'select any Specialization';
            if (!name) {
                formErrors.name = 'Name is required';
              } else if (!/^[a-zA-Z]+$/.test(name)) {
                formErrors.name = 'Name is Invalid. Only alphabetic characters are allowed.';
              }
            if(password.length<8) formErrors.password = 'password should be atleast 8 characters';
            return formErrors;
        };
    
        const signupdata = {
            email: email,
            phoneNumber: phoneNumber,
            password: password,
            role: role,
            doctor:{
                name:name,
                specialization:specialization,
            },
            patient: null
        };

    const retrieveDoctors = async () => {
        const apiUrl = `http://localhost:8086/api/doctor/alldoctorDetails`;
        try {
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            const doctorsData = await response.json();
            if (response.ok && doctorsData.data) {
                setDoctors(doctorsData.data);
            } else {
                alert("Failed to fetch Doctors");
            }
        } catch (error) {
            console.error("Error fetching doctors:", error);
            alert("Failed to fetch Doctors due to a network error.");
        }
    };

    useEffect(() => {
        retrieveDoctors();
    }, []);

    const handleCheckAndDelete = async (doctorId) => {
        setDoctorToDeleteId(doctorId);
        const apiUrl = `http://localhost:8065/appointments/viewByDoctor/${doctorId}`;
        try {
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            const details = await response.json();
            if (response.ok) {
                if (checkingBookedAppointments(details.data)) {
                    setConfirmation(true);
                    setDoctorToDeleteId(doctorId);
                } else {
                    setDeleteCheckResultPopup(true);
                }
            } else {
                alert("Failed to check for booked appointments.");
            }
        } catch (error) {
            console.error("Error checking appointments:", error);
            alert("Failed to check for booked appointments due to a network error.");
        }
    };

    const checkingBookedAppointments = (details) => {
        if (!Array.isArray(details) || details.length === 0) {
          return true; // Consider empty or non-array as no booked appointments
        }
        console.log(details[0].status);
        for (let i = 0; i < details.length; i++) {
          if (details[i].status === 'Booked') {
            return false;
          }
        }
      
        return true;
      };

    const handleDelete = async (doctorId) => {
        setConfirmation(false);
        const apiUrl = `http://localhost:8086/api/users/delete/${doctorId}`;
        try {
            const response = await fetch(apiUrl, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                alert("Doctor deleted successfully");
                // Optionally, you can update the doctors list after successful deletion
                retrieveDoctors();
            } else {
                alert("Unable to delete, please try again.");
            }
        } catch (error) {
            console.error("Error deleting doctor:", error);
            alert("Failed to delete doctor due to a network error.");
        } finally {
            setDoctorToDeleteId(null);
        }
    };
    const handleAddDoctor = async(event) => {
        event.preventDefault();
        var formErrors = validateForm();
        if (Object.keys(formErrors).length === 0) {
            try {
                const response = await fetch("http://localhost:8086/api/users/signup", {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(signupdata)
                });
                if (response.ok) {
                    const data = await response.json();
                    console.log('Doctor Added successfully:', data);
                    alert("Doctor Added successfully");
                    retrieveDoctors();
                    setFormPopup(false);
                } else {
                    const data = await response.json();
                    console.error('SignUp failed:', data);
                    setErrorfromBackend(data.message);
                }
            } catch (error) {
                console.error('Error:', error);
            }
        } else {
            setErrors(formErrors);
        }
    };

    const closePopup = () => {
        setFormPopup(false);
        setDeleteCheckResultPopup(false);
        setDoctorToDeleteId(null);
        setConfirmation(false);
    };
    const handleLogout = () =>{
        localStorage.clear();
        window.location.href = '/';
    }
    return (
        <div className="listofDoctors">
            <div className="header">
                <h2>ADMIN DASHBOARD</h2>
                <FontAwesomeIcon  className ="logout" icon={faSignOutAlt} onClick={handleLogout}/>
            </div>
            <button className="addButton" onClick={()=>{setFormPopup(true)}}>
                <FontAwesomeIcon className="plus" style={{fontSize: "36px"}} icon={faPlus} />
            </button>
            <div className="list">
                    {doctors.map((doctor, index) => (
                        <div key={index} className="doctor">
                            <button className="deleteButton" onClick={() => handleCheckAndDelete(doctor.doctorId)}>
                                <FontAwesomeIcon className="trash" icon={faTrash} />
                            </button>
                            <img src={doctorimage} alt="DoctorProfile" className="doctorPhoto"/>
                            <label className="doctorDetails">DoctorId : {doctor.doctorId}</label>
                            
                            <label className="doctorDetails">Doctorname : {doctor.name}</label>
                            
                            <label className="doctorDetails">Doctor Specialization : {doctor.specialization}</label>
                            
                        </div>
                    ))}
            </div>
            {deleteCheckResultPopup && (
                <div className='modal-overlay'>
                    <div className='confirmingAppointment'>
                        <FontAwesomeIcon
                            icon={faTimesCircle}
                            style={{ color: "red", fontSize: "36px", marginLeft: "200px", cursor: "pointer" }}
                            onClick={closePopup}
                        />
                        <p>Doctor you are trying to delete is<br /> Currently Dealing with some Appointments</p>
                    </div>
                </div>
            )}
            {confirmation && (
                <div className='modal-overlay'>
                    <div className='confirmingAppointment'>
                        <FontAwesomeIcon
                            icon={faTimesCircle}
                            style={{ color: "red", fontSize: "36px", marginLeft: "200px", cursor: "pointer" }}
                            onClick={closePopup}
                        />
                        <p>Are you Sure want to delete this Doctor?</p>
                        <button onClick={()=>handleDelete(doctorToDeleteId)}>Sure</button>
                    </div>
                </div>
            )}
            {formPopup && (
                <div className='modal-overlay'>
                    <div className="popupform">
                        <FontAwesomeIcon
                            icon={faTimesCircle}
                            style={{ color: "red", fontSize: "36px", marginLeft: "90%", cursor: "pointer" }}
                            onClick={closePopup}
                        />
                        <form>
                           <label>DoctorName : <input type="text" value={name} placeholder="doctor name" onChange={(e) => setName(e.target.value)} autoComplete="off"/> </label>
                           {errors.name && <div className='error'>{errors.name}</div>}
                           <label>Contact Number :<input type="text"  value={phoneNumber} placeholder="mobile number" onChange={(e) => setPhoneNumber(e.target.value)} autoComplete="off"/></label> 
                           {errors.phoneNumber && <div className='error'>{errors.phoneNumber}</div>}
                           <label>Email id :<input type="text" value={email} placeholder="emailId" onChange={(e) => setEmail(e.target.value)} autoComplete="off"/></label>
                           {errors.email && <div className='error'>{errors.email}</div>}
                           <label>Doctor Specialization:<select id='specialization' value={specialization}  onChange={(e) => setSpecialization(e.target.value)}>
                                <option>Select</option>
                                <option value="General" >General</option>
                                <option value="Cardiology" >Cardiology</option>
                                <option value="Pulmonology" >Pulmonology</option>
                                <option value="Neurology" >Neurology</option>
                                <option value="Gynaecology" >Gynaecology</option>
                                <option value="Dermatology" >Dermatology</option>
                                <option value="Oncology" >Oncology</option>
                                <option value="Psychology" >Psychology</option>
                                <option value="Orthodontics" >Orthodontics</option>
                                <option value="Nephrology" >Nephrology</option>
                            </select></label>
                            {errors.specialization && <div className='error'>{errors.specialization}</div>}
                            <label>Password :<input type="password" value={password} placeholder="password" onChange={(e) => setPassword(e.target.value)} autoComplete="off" /></label>
                            {errors.password && <div className='error'>{errors.password}</div>}
                        </form>
                        {errorfrombackend && <div className='error'>{errorfrombackend}</div>}

                        <button onClick={handleAddDoctor}>Add</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminDashBoard;