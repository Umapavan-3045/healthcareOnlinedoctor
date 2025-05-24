import React,{useState} from 'react';
import '../CssFiles/LoginRegister.css';
import { jwtDecode } from 'jwt-decode';
const SignIn = ({ setLoggedIn ,setLoginDetails,forgot}) =>{
    const[role,setRole] =useState('PATIENT');
    const[identifier,setIdentifier] =useState('');
    const[password,setPassword] = useState('');
    const[errors, setErrors] = useState('');
   const HandleRole = (event) =>{
        setRole(event.target.value)
    }
    const HandleLogin = async(event) =>{
        //from here Iam sending data to http://localhost/api/login in the json format
        event.preventDefault();
        const login ={
            identifier:identifier,
            password:password,
            role:role
        }
        try{
            const response = await fetch("http://localhost:8086/api/users/login",{
            method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(login)
            });
            if (response.ok) {
                const data = await response.json();
                //need to extract data from token and store in local storage
                // data contains jwt token 
                const decodedToken = jwtDecode(data.data);
                localStorage.setItem('jwtToken',data.data);
                console.log('Login successful:', decodedToken);
                GettingUserName(data.data,decodedToken);
            } else {
                const data = await response.json();
                console.log(data);
                setErrors("login failed :"+ data.message);
            }
        } catch (error) {
            console.error('Error:', error);
        }
        
    }

    const GettingUserName =async(token,decodedToken)=>{
        try{
           const apiUrl = `http://localhost:8086/api/users/${decodedToken.userId}`
            const response = await fetch(apiUrl,{
            method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            });
            if (response.ok) {
                const data = await response.json();
                if(decodedToken.role === 'DOCTOR'){
                   decodedToken.name = data.data.doctor.name;
                   localStorage.setItem('userLoggedIn',JSON.stringify(decodedToken));
                   const userdata = localStorage.getItem('userLoggedIn');
                    if (userdata) {
                        setLoginDetails(JSON.parse(userdata));
                        setLoggedIn(true);
                    }
                }
                else if(decodedToken.role === 'PATIENT'){
                    decodedToken.name = data.data.patient.name;
                   localStorage.setItem('userLoggedIn',JSON.stringify(decodedToken));
                   const userdata = localStorage.getItem('userLoggedIn');
                   if (userdata) {
                    setLoginDetails(JSON.parse(userdata));
                    setLoggedIn(true);
                   }
                }
                else{
                    decodedToken.name = 'Admin';
                    localStorage.setItem('userLoggedIn',JSON.stringify(decodedToken));
                    const userdata = localStorage.getItem('userLoggedIn');
                   if (userdata) {
                    setLoginDetails(JSON.parse(userdata));
                    setLoggedIn(true);
                   }
                }
            } else {
                const data = await response.json();
                console.log(data);
                setErrors("login failed :"+ data.message);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }
    return (
        <div className='loginpage'>
            <form onSubmit={HandleLogin}>
            <select id='role' value={role}  onChange={HandleRole}>
                        <option value="PATIENT" >Patient</option>
                        <option value="DOCTOR" >Doctor</option>
                        <option value="ADMIN" >Admin</option>
                    </select>
                <input type='text' id={identifier} onChange={(e) => setIdentifier(e.target.value)} placeholder='enter your emailId' style={{width:"80%",marginBottom:"10px",marginTop:"10px"}}/>
                <input type='password' id={password}  onChange={(e) => setPassword(e.target.value)} placeholder='enter your password' style={{width:"80%"}}/>
                <button type='submit' style={{marginTop:"10px"}}>Login</button>
                {errors && <div className='error'>{errors}</div>}
                <p style={{cursor:'pointer'}} onClick={()=>{forgot('forgotpassword')}}>forgot password?</p>
            </form>
        </div>
    );
};

export default SignIn;