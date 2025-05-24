import React, { useState } from 'react';
import '../CssFiles/LoginRegister.css';
import SignIn from './SignIn';
import SignUp from './SignUp';
import ForgotPassword from './ForgotPassword';

const LoginandRegister = ({ setLoggedIn,setLoginDetails}) =>{
    const [clickbutton,setClickButton] = useState('SignIn');
    const handlebuttonClick = (event) => {
        setClickButton(event.target.name)
    }
    return(<div className='switching'>
           <div className='switchingbuttons'><button name='SignIn'  className={clickbutton === 'SignIn' ? 'active' : 'inactive'} onClick={handlebuttonClick}>SignIn</button>
            <button name='SignUp' onClick={handlebuttonClick}  className={clickbutton === 'SignUp' ? 'active' : 'inactive'}>SignUp</button></div> 
            {clickbutton === 'SignIn' && <SignIn setLoggedIn={ setLoggedIn } setLoginDetails={setLoginDetails} forgot={setClickButton}/>}
            {clickbutton === 'SignUp' && <SignUp back={setClickButton}/>}
            {clickbutton === 'forgotpassword' && <ForgotPassword back={setClickButton}/>}
    </div>);
};
export default LoginandRegister;