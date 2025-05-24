import React,{useState} from 'react';
import '../CssFiles/LoginRegister.css';
const SignUp = ({back}) => {
    const role = 'PATIENT';
    const [gender, setGender] = useState('');
    const [email, setEmail] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [password, setPassword] = useState('');
    const [address, setAddress] = useState('');
    const [age, setAge] = useState();
    const [name, setName] = useState('');
    const [retypePassword, setRetypePassword] = useState('');
    const [errors, setErrors] = useState({});
    const [errorfrombackend, setErrorfromBackend] = useState('');
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phonePattern = /^[0-9]{10}$/;
    const handleGender = (event) => {
        setGender(event.target.value);
    };

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
        if (password !== retypePassword) formErrors.retypePassword = 'Passwords do not match';
        if (!name) {
            formErrors.name = 'Name is required';
          } else if (!/^[a-zA-Z]+$/.test(name)) {
            formErrors.name = 'Name is Invalid. Only alphabetic characters are allowed.';
          }
        if (!age) formErrors.age = 'Age is required';
        else if (isNaN(age)) {
            formErrors.age = 'Age must be a number';
          } else if (age >= 150 || age <= 0) {
            formErrors.age = 'Age must be in the range 0 to 150';
          }
        if (!gender) formErrors.gender = 'Gender is required';
        if (!address) formErrors.address = 'Address is required';
        if(password.length<8) formErrors.password = 'password should be atleast 8 characters';
        return formErrors;
    };

    const signupdata = {
        email: email,
        phoneNumber: phoneNumber,
        password: password,
        role: role,
        doctor: null,
        patient: {
            name: name,
            address: address,
            age: age,
            gender: gender
        }
    };

    const HandleSignUp = async (event) => {
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
                    console.log('SignUp successful:', data);
                    back('SignIn');
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

    return (
        <div className='signuppage'>
            <form onSubmit={HandleSignUp} id='loginForm'>
                <div className='logindetails'>
                    <p> </p>
                    <input type='text' value={email} onChange={(e) => setEmail(e.target.value)} placeholder='Enter your email ID' />
                    {errors.email && <div className='error'>{errors.email}</div>}
                    <input type='text' value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} placeholder='Enter your phone number' />
                    {errors.phoneNumber && <div className='error'>{errors.phoneNumber}</div>}
                    <input type='password' value={password} onChange={(e) => setPassword(e.target.value)} placeholder='Enter your password' />
                    {errors.password && <div className='error'>{errors.password}</div>}
                    <input type='password' value={retypePassword} onChange={(e) => setRetypePassword(e.target.value)} placeholder='Re-enter your password' />
                    {errors.retypePassword && <div className='error'>{errors.retypePassword}</div>}
                    <button type='submit'>SignUp</button>
                </div>
                <div className='linedivide'></div>
                <div className='personaldetails'>
                    <p> </p>
                    <select id='role' value={role} onChange={null}>
                        <option value="PATIENT">Patient</option>
                    </select>
                    <div className='patientdetails'>
                        <input type='text' value={name} onChange={(e) => setName(e.target.value)} placeholder='Enter your name' />
                        {errors.name && <div className='error'>{errors.name}</div>}
                        <input type='text' value={age} onChange={(e) => setAge(e.target.value)} placeholder='Enter your age' />
                        {errors.age && <div className='error'>{errors.age}</div>}
                        <div>
                            <label>Gender</label>
                            <div>
                                <label><input type='radio' className="radio" value='MALE' checked={gender === 'MALE'} onChange={handleGender} />Male</label>
                                <label><input type='radio' className="radio" value='FEMALE' checked={gender === 'FEMALE'} onChange={handleGender} />Female</label>
                                <label><input type='radio' className="radio" value='OTHER' checked={gender === 'OTHER'} onChange={handleGender} />Other</label>
                            </div>
                            {errors.gender && <div className='error'>{errors.gender}</div>}
                        </div>
                        <textarea value={address} onChange={(e) => setAddress(e.target.value)} className="address" placeholder='Add your Address' />
                        {errors.address && <div className='error'>{errors.address}</div>}
                        {errorfrombackend && <div className='error'>{errorfrombackend}</div>}
                    </div>
                </div>
            </form>
        </div>
    );
};

export default SignUp;