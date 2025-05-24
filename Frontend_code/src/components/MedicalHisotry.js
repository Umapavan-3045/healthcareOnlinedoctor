import React, { useState, useEffect } from "react";
import '../CssFiles/generalCss.css';
const MedicalHistory = ({userId,token}) => {
  const [history, setHistory] = useState([]);
  const [medicalHistoryText, setMedicalHistoryText] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [popup, setShowPopup] = useState(false);
 
  const closePopup = () => {
    setShowPopup(false);
  };

  useEffect(() => {
    const fetchMedicalHistory = async () => {
      try {
        const apiUrl = `http://localhost:8050/history/patient/${userId}`;
        const response = await fetch(apiUrl,{
            method:'GET',
            headers:{
                'Content-type':'Application/json',
                'Authorization': `Bearer ${token}`
            }
        });
 
        const data = await response.json();
        if (response.ok) {
         console.log(data);
          setHistory(data.data);
        } else {
            console.log(data.errorMessage);
          setHistory([]); // No history exists
        }
 
        setLoading(false);
      } catch (error) {
        console.error("Error fetching medical history:", error);
        setError("Error fetching medical history");
        setLoading(false);
      }
    };
 
    fetchMedicalHistory();
  }, [userId]);
 
  const handleAddHistory = async () => {
    if (!medicalHistoryText.trim()) {
      alert("Please enter medical history!");
      return;
    }
 
    try {
        const patientdetails ={
            patientId:userId,
            healthHistory: medicalHistoryText
        }
      const response = await fetch("http://localhost:8050/history/create", {
            method:'POST',
            headers:{
                'Content-type':"Application/json",
                'Authorization': `Bearer ${token}`
            },
            body:JSON.stringify(patientdetails)
      });
 
      const responseData = await response.json();
 
      if (response.ok) {
        alert("Medical history added successfully!");
        setHistory([...history, responseData.data]); // Append new history in UI
        setMedicalHistoryText(""); // Clear input field after adding
        setShowPopup(false); // Close modal after successful entry
      } else {
        alert("Error: " + responseData.message);
      }
    }catch (error) {
      console.error("Error adding medical history:", error);
      alert("Failed to add medical history.");
    }
  };
 
  if (loading) return <p>Loading medical history...</p>;
  if (error) return <p>{error}</p>;
 
  return (
    <div className="medicalHistory">
      <h2>My Medical History</h2>
      {history.length != 0 ? (
        <table>
          <thead>
            <tr>
              <th>History ID</th>
              <th>Health History</th>
            </tr>
          </thead>
          <tbody>
            {history.map((record) => (
              <tr key={record.historyId} style={{ backgroundColor: "#f0fff0" }}>
                <td>{record.historyId}</td>
                <td>{record.healthHistory}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ):(
        <p className="text-center text-danger fw-bold">No medical history exists.</p>
      )}
      
      <button onClick={() => setShowPopup(true)}>Add Medical History</button>
      {popup && (
        <div className="popup">
          <div className="popup-content">
          <span className="close" onClick={closePopup}>
            &times;
          </span>
            <p>
              <strong>Patient ID:</strong> {userId}
            </p>
            <textarea
              placeholder="Enter your medical history..."
              value={medicalHistoryText}
              onChange={(e) => setMedicalHistoryText(e.target.value)}
              className="form-control"
              rows="4"
            />
            <button onClick={handleAddHistory}>
              Save History
            </button>
          </div>
          </div>
      )}

    </div>
  );
};
 
export default MedicalHistory;