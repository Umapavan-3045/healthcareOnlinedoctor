import React, { useState, useEffect } from 'react';
import "../CssFiles/generalCss.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimesCircle } from '@fortawesome/free-solid-svg-icons';

const EditProfile = ({ userdata, token }) => {
    const [userName, setUserName] = useState('');
    const [userEmailId, setUserEmailId] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [age, setAge] = useState('');
    const [address, setAddress] = useState('');
    const [userId, setUserId] = useState('');
    const [gender, setGender] = useState('');
    const [specialization, setSpecialization] = useState('');
    const [errors, setErrors] = useState({});
    const [newpassword, setNewPassword] = useState('');
    const [currentpassword, setCurrentPassword] = useState('');
    const [retypednewpassword, setRetypedPassword] = useState('');
    const [popup, setPopup] = useState(false);
    const [passwordErrors, setPasswordErrors] = useState({});

    useEffect(() => {
        console.log('Fetching user data...');
        const HandleGetData = async () => {
            const apiUrl = (userdata.role === 'DOCTOR') ?
                `http://localhost:8086/api/doctor/${userdata.userId}` :
                `http://localhost:8086/api/patient/${userdata.userId}`;
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
                console.log('User data fetched:', data);
                setUserId(userdata.userId);
                setUserName(data.data.name);
                setUserEmailId(data.data.email);
                setPhoneNumber(data.data.phoneNumber);
                if (userdata.role === 'PATIENT') {
                    setGender(data.data.gender || 'Male'); // Default to Male if not provided
                    setAddress(data.data.address || '');
                    setAge(data.data.age !== undefined ? String(data.data.age) : '');
                } else {
                    setSpecialization(data.data.specialization || '');
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
            }
        };
        HandleGetData();
    }, [userdata.role, userdata.userId, token]);

    const validateForm = () => {
        let formErrors = {};
        if (!userName) formErrors.userName = "Name is required";
        else if (!/^[a-zA-Z\s]*$/.test(userName)) {
            formErrors.userName = 'Name is Invalid. Only alphabetic characters and spaces are allowed.';
        }
        if (!userEmailId) {
            formErrors.userEmailId = "Email is required";
        } else if (!/\S+@\S+\.\S+/.test(userEmailId)) {
            formErrors.userEmailId = "Email is invalid";
        }
        if (!phoneNumber) {
            formErrors.phoneNumber = "Phone number is required";
        } else if (!/^\d{10}$/.test(phoneNumber)) {
            formErrors.phoneNumber = "Phone number is invalid (must be 10 digits)";
        }
        if (userdata.role === 'PATIENT') {
            if (!age) formErrors.age = "Age is required";
            else if (isNaN(age)) formErrors.age = "Age must be a number";
            else if (parseInt(age) >= 150 || parseInt(age) <= 0) {
                formErrors.age = 'Age must be in the range 1 to 149';
            }
        }
        setErrors(formErrors);
        return Object.keys(formErrors).length === 0;
    };

    const passwordValidate = () => {
        let formErrors = {};
        if (!currentpassword) formErrors.currentpassword = 'Current password is required';
        if (!newpassword) formErrors.newpassword = 'New password is required';
        if (!retypednewpassword) formErrors.retypednewpassword = 'Retype new password is required';
        if (newpassword !== retypednewpassword) {
            formErrors.retypednewpassword = 'New passwords do not match';
        }
        setPasswordErrors(formErrors);
        return Object.keys(formErrors).length === 0;
    };

    const HandleUpdate = async (event) => {
        event.preventDefault();
        if (!validateForm()) return;
        const apiUrl = (userdata.role === 'DOCTOR') ?
            `http://localhost:8086/api/doctor/update` :
            `http://localhost:8086/api/patient/update`;
        const updateData = (userdata.role === 'DOCTOR') ? {
            doctor_id: userId,
            email: userEmailId,
            name: userName,
            phoneNumber: phoneNumber,
            specialization: specialization
        } : {
            patient_id: userId,
            email: userEmailId,
            name: userName,
            phoneNumber: phoneNumber,
            gender: gender,
            age: parseInt(age),
            address: address
        };

        try {
            const response = await fetch(apiUrl, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updateData)
            });
            if (response.ok) {
                const data = await response.json();
                alert("Updated Successfully");
            } else {
                const data = await response.json();
                console.log("Update failed", data);
                alert(`Update failed: ${data?.message || 'Something went wrong'}`);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An unexpected error occurred during update.');
        }
    };

    const HandleUpdatePassword = async (event) => {
        event.preventDefault();
        if (!passwordValidate()) return;
        const apiUrl = `http://localhost:8086/api/users/change-password/${userdata.userId}`;
        const updateData = {
            oldPassword: currentpassword,
            newPassword: newpassword
        };

        try {
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updateData)
            });
            if (response.ok) {
                alert("Password Changed Successfully");
                setPopup(false);
                setCurrentPassword('');
                setNewPassword('');
                setRetypedPassword('');
                setPasswordErrors({});
            } else {
                const data = await response.json();
                console.log("Password update failed", data);
                setPasswordErrors({ general: data?.message || 'Incorrect current password' });
            }
        } catch (error) {
            console.error('Error:', error);
            setPasswordErrors({ general: 'An unexpected error occurred' });
        }
    };

    return (
        <div className='editProfile'>
            <form onSubmit={HandleUpdate}>
                <div className='sub'> <label>UserId:</label>
                    <input type='text' value={userId} readOnly /></div>
                <div className='sub'> <label>Username:</label>
                    <input
                        type='text'
                        value={userName}
                        onChange={(e) => setUserName(e.target.value)}
                    />
                    {errors.userName && <span className='error'>{errors.userName}</span>}
                </div>
                <div className='sub'><label>EmailId:</label>
                    <input
                        type='text'
                        value={userEmailId}
                        onChange={(e) => setUserEmailId(e.target.value)}
                    />
                    {errors.userEmailId && <span className='error'>{errors.userEmailId}</span>}
                </div>
                <div className='sub'><label>Phone Number:</label>
                    <input
                        type='text'
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                    />
                    {errors.phoneNumber && <span className='error'>{errors.phoneNumber}</span>}
                </div>

                {userdata.role === 'PATIENT' && (
                    <>
                        <div className='sub'><label>Age:</label>
                            <input
                                type='text'
                                value={age}
                                onChange={(e) => setAge(e.target.value)}
                            />
                            {errors.age && <span className='error'>{errors.age}</span>}
                        </div>
                        <div className='sub'> <label>Gender:</label>
                            <select
                                id='gender'
                                value={gender}
                                onChange={(e) => setGender(e.target.value)}
                            >
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Other">Others</option>
                            </select></div>
                        <div className='sub'><label>Address:</label>
                            <input
                                type='text'
                                value={address}
                                onChange={(e) => setAddress(e.target.value)}
                            /></div>
                    </>
                )}

                {userdata.role === 'DOCTOR' && (
                    <div className='sub'><label>Specialization:</label>
                        <input type='text' value={specialization} readOnly />
                    </div>
                )}

                <div className='buttons'>
                    <button type="button" onClick={() => setPopup(true)}>Reset password</button>
                    <button type="submit">Update</button>
                </div>
            </form>

            {popup && (
                <div className='modal-overlay'>
                    <div className='confirmingAppointment'>
                        <FontAwesomeIcon
                            icon={faTimesCircle}
                            style={{ color: "red", fontSize: "36px", marginLeft: "200px", cursor: "pointer" }}
                            onClick={() => setPopup(false)}
                        />
                        {passwordErrors.general && <span className='error'>{passwordErrors.general}</span>}
                        <input
                            type='password'
                            value={currentpassword}
                            onChange={(e) => setCurrentPassword(e.target.value)}
                            placeholder='Current password'
                            style={{width:"90%",marginTop:"10px"}}
                        />
                        {passwordErrors.currentpassword && <span className='error'>{passwordErrors.currentpassword}</span>}
                        <input
                            type='password'
                            value={newpassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            placeholder='New password'
                            style={{width:"90%",marginTop:"10px"}}
                        />
                        {passwordErrors.newpassword && <span className='error'>{passwordErrors.newpassword}</span>}
                        <input
                            type='password'
                            value={retypednewpassword}
                            onChange={(e) => setRetypedPassword(e.target.value)}
                            placeholder='Retype new password'
                            style={{width:"90%",marginTop:"10px"}}
                        />
                        {passwordErrors.retypednewpassword && <span className='error'>{passwordErrors.retypednewpassword}</span>}
                        <button onClick={HandleUpdatePassword}>Change Password</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default EditProfile;