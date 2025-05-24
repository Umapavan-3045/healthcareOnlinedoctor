import React, { useState, useEffect } from "react";
 
const UpdateConsultationForm = ({ isOpen, onClose, appointmentId, onSubmit ,token}) => {
    const [notes, setNotes] = useState("");
    const [prescription, setPrescription] = useState("");
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);
 
    useEffect(() => {
        if (isOpen && appointmentId) {
            fetch(`http://localhost:8050/consultation/appointment/${appointmentId}`,{
                method:'GET',
                headers:{
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                }})
                .then(response => response.json())
                .then(data => {
                    if (data.success && data.data.length > 0) {
                        const consultation = data.data[0];
                        setNotes(consultation.notes);
                        setPrescription(consultation.prescription);
                    }
                })
                .catch(error => {
                    console.error("Error fetching consultation details:", error);
                });
        } else if (!isOpen) {
            setNotes("");
            setPrescription("");
            setMessage("");
        }
    }, [isOpen, appointmentId]);
 
    const handleSubmit = async (event) => {
        event.preventDefault();
        setLoading(true);
 
        const consulData = { appointmentId, notes, prescription };
 
        try {
            const response = await fetch(`http://localhost:8050/consultation/update/appointment/${appointmentId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(consulData),
            });
 
            const data = await response.json();
            if (response.ok) {
                setMessage("Consultation updated successfully!");
                console.log("Response:", data);
                setTimeout(() => {
                    setLoading(false);
                    onClose();
                    if (onSubmit) {
                        onSubmit(appointmentId);
                    }
                }, 2000);
            } else {
                setMessage("Error updating consultation. Please try again.");
                console.error("Error:", data);
                setLoading(false);
            }
        } catch (err) {
            setMessage("Error updating consultation. Please try again.");
            console.error("Error:", err);
            setLoading(false);
        }
    };
 
    if (!isOpen) {
        return null;
    }
 
    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2 className="modal-title">✏️ Update Consultation</h2>
                {message && <p className={message.includes("success") ? "success-message" : "error-message"}>{message}</p>}
 
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
                        className={`form-button ${loading ? "disabled-button" : ""}`}
                        disabled={loading}
                    >
                        {loading ? "Updating..." : "Update Consultation"}
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
 
export default UpdateConsultationForm;