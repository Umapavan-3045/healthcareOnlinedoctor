import React, { useState } from "react";
import "../CssFiles/ConsultationForm.css";
 
const ConsultationForm = ({ isOpen, onClose, appointmentId, isUpdate, onSubmit ,token}) => {
  const [notes, setNotes] = useState("");
  const [prescription, setPrescription] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
 
  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    const consulData = {
      appointmentId,
      notes,
      prescription,
    };
 
    try {
      let response;
      if (isUpdate) {
        response = await fetch(`http://localhost:8050/consultation/update/consul/${appointmentId}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(consulData),
        });
        if (response.ok) {
          setMessage("Consultation updated successfully!");
          if (onSubmit) {
            onSubmit(appointmentId);
          }
        } else {
          throw new Error("Failed to update consultation");
        }
      } else {
        response = await fetch(`http://localhost:8050/consultation/create`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(consulData),
        });
        if (response.ok) {
          setMessage("Consultation created successfully!");
          HandleNoifyCompletion(appointmentId);
          if (onSubmit) {
            onSubmit(appointmentId);
          }
        } else {
          throw new Error("Failed to create consultation");
        }
      }
      const responseData = await response.json();
      console.log("Response:", responseData);
      setTimeout(() => {
        setNotes("");
        setPrescription("");
        setLoading(false);
        onClose();
        setMessage("");
      }, 2000);
    } catch (err) {
      setMessage("Error processing consultation. Please try again.");
      console.error("Error:", err);
      setLoading(false);
    }
  };
  
  const HandleNoifyCompletion = async(appointmentId)=>{
    const apiUrl = `http://localhost:8065/appointments/notifyCompletion/${appointmentId}`;
    try{
    const response = fetch(apiUrl,{
        method:'GET',
        headers:{
          'Content-type':'Application/json',
          'Authorization': `Bearer ${token}`
        }
    });
    if(response.ok){
      console.log("notified Successfully",response);
    }
  }
  catch(error){
    console.log("unable to notify Completed",error);
  }
}

  if (!isOpen) {
    return null; // Don't render if not open
  }
 
  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <h2 className="modal-title">🩺 {isUpdate ? "Update" : "Create"} Consultation</h2>
        {message && (
          <p className={message.includes("success") ? "success-message" : "error-message"}>
            {message}
          </p>
        )}
 
        <form onSubmit={handleSubmit} className="consultation-form">
          <label className="form-label">
            <strong>Appointment ID:</strong> {appointmentId}
          </label>
          <br />
 
          <label className="form-label">Notes:</label>
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Enter consultation notes..."
            required
            maxLength={500}
            className="form-textarea"
          />
          <br />
 
          <label className="form-label">Prescription:</label>
          <input
            type="text"
            value={prescription}
            onChange={(e) => setPrescription(e.target.value)}
            placeholder="Enter prescribed medicine..."
            required
            className="form-input"
          />
          <br />
 
          <button
            type="submit"
            disabled={loading}
          >
            {loading ? "Submitting..." : (isUpdate ? "Update Consultation" : "Submit Consultation")}
          </button>
 
          <button
            type="button"
            onClick={onClose}
            className="form-button cancel-button"
          >
            Cancel
          </button>
        </form>
      </div>
    </div>
  );
};
 
export default ConsultationForm;