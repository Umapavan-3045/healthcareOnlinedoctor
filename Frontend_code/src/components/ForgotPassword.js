import React, { useState } from "react";

const ForgotPassword = ({back})=>{
    const [errors,setErrors] = useState(true);
    const [identifier,setIdentifier] = useState('');

    const HandleForgotPassword = async(event) =>{
        event.preventDefault();
        try{
            const apiUrl = `http://localhost:8086/api/users/forgot-password/${identifier}`
        const response = await fetch(apiUrl,{
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
            }
        });

        if(response.ok){
            const data = await response.json();
            alert(data.message);
            back('SignIn');
        }
        else{
            const data = await response.json();
            alert(data.message);
        }
    }
    catch(error){
        console.log("unable to reset Password :",error);
    }
    }
    return (
        <div className='loginpage'>
            <form>
                <input type='text' id={identifier} onChange={(e) => setIdentifier(e.target.value)} placeholder='Enter your Register emailId' style={{width:"80%",marginTop:"20px"}}/>
                <button onClick={HandleForgotPassword}>Send New Password</button>
                {errors && <div className='error'>{errors}</div>}
            </form>
        </div>
    );
};
export default ForgotPassword;