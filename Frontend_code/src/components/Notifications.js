import React, { useEffect, useState } from "react";
import "../CssFiles/notifications.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye} from "@fortawesome/free-solid-svg-icons";
import notificationSound from "../assets/notification.mp3"; // Import your sound file
 
const Notifications = ({ tab, userType, token, userId }) => {
  const [notifications, setNotifications] = useState([]);
  const [newNotificationCount, setNewNotificationCount] = useState(0); // State to track the count of new notifications
  const [seenNotifications, setSeenNotifications] = useState(new Set()); // Track IDs of seen notifications
  const timeMap = {
    "NINE_TO_ELEVEN": "9:00 am - 11:00 am",
    "ELEVEN_TO_ONE": "11:00 am - 1:00 pm",
    "TWO_TO_FOUR": "2:00 pm - 4:00 pm",
    "FOUR_TO_SIX": "4:00 pm - 6:00 pm",
  };
 
  const extractTimeSlotFromMessage = (message) => {
    for (let key in timeMap) {
      if (message.includes(key)) {
        return key; // Return the matching key (e.g., "TWO_TO_FOUR")
      }
    }
    return null; // Return null if no time slot is found
  };
 
  // Replace the time slot enum in the message with the readable format
  const mapTimeSlotInMessage = (message, timeSlot) => {
    if (!timeSlot) return message; // Handle undefined/null timeSlot
    if (timeMap[timeSlot]) {
      return message.replace(timeSlot, timeMap[timeSlot]);
    }
    return message; // Default fallback
  };
 
  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const apiUrl =
          userType === "DOCTOR"
            ? `http://localhost:8089/notifications/fetchByDoctorOrPatient?doctorId=${userId}`
            : `http://localhost:8089/notifications/fetchByDoctorOrPatient?patientId=${userId}`;
        const response = await fetch(apiUrl, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        if (response.ok) {
          console.log("Notifications fetched successfully");
          const data = await response.json();
 
          // Sort notifications by creation time
          const sortedNotifications = data.sort(
            (a, b) => new Date(b.date) - new Date(a.date)
          );
 
          const newUnseenNotifications = sortedNotifications.filter(
            (noti) => !seenNotifications.has(noti.id)
          );
 
          if (newUnseenNotifications.length > 0) {
            setNewNotificationCount((prevCount) => prevCount + newUnseenNotifications.length);
            const audio = new Audio(notificationSound);
            audio.play();
 
            // Update the main notifications list
            setNotifications((prevNotifications) => {
              const existingIds = new Set(prevNotifications.map((n) => n.id));
              const trulyNew = sortedNotifications.filter((n) => !existingIds.has(n.id));
              return [...trulyNew, ...prevNotifications];
            });
 
            // Add the IDs of the newly fetched notifications to the seen set
            const newSeenIds = newUnseenNotifications.map((n) => n.id);
            setSeenNotifications((prevSeen) => new Set([...prevSeen, ...newSeenIds]));
 
          } else if (sortedNotifications.length > 0 && notifications.length === 0) {
            // Initial load
            setNotifications(sortedNotifications);
            setSeenNotifications(new Set(sortedNotifications.map((n) => n.id)));
            setNewNotificationCount(0); // No new notifications on initial load
          }
        } else {
          console.error("Failed to fetch notifications");
        }
      } catch (error) {
        console.error("Unable to load notifications", error);
      }
    };
 
    fetchNotifications();
    const intervalId = setInterval(fetchNotifications, 5000);
 
    return () => clearInterval(intervalId);
  }, [userType, userId, token, seenNotifications]); // Include seenNotifications in dependency array
 
  const handleRedirect = (notificationId) => {
    if (userType === "PATIENT") {
      window.location.href = "/myAppointments";
    } else if (userType === "DOCTOR") {
      window.location.href = "/consultations";
    }
    // Mark the clicked notification as seen
    setSeenNotifications((prevSeen) => new Set([...prevSeen, notificationId]));
    // Decrement the new notification count if it's greater than 0
    if (newNotificationCount > 0) {
      setNewNotificationCount((prevCount) => prevCount - 1);
    }
  };
 
  
 
  const handleCloseTab = () => {
    tab(false);
    setNewNotificationCount(0); // Reset the count when closing as well
  };
 
  const notificationMessage = notifications.length > 0 ? (
    notifications.map((noti) => {
      const msg = noti.message;
      const slot = extractTimeSlotFromMessage(msg);
      const updatedMessage = mapTimeSlotInMessage(msg, slot);
      const isNew = !seenNotifications.has(noti.id);
 
      return (
        <div key={noti.id} style={{ backgroundColor: isNew ? '#f0f8ff' : 'white', padding: '10px', borderBottom: '1px solid #eee' }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", width: "100%" }}>
            <div>
              <p>{updatedMessage}</p>
              <small style={{ color: 'gray' }}>{new Date(noti.date).toLocaleString()}</small>
            </div>
            <FontAwesomeIcon
              icon={faEye}
              onClick={() => handleRedirect(noti.id)}
              style={{ marginLeft: '10px', cursor: 'pointer', color: isNew ? 'blue' : 'gray' }}
            />
          </div>
        </div>
      );
    })
  ) : (
    <p>No notifications</p>
  );
 
  return (
    <div className="tab">
      <div className="notificationsMessages">
        <div>{notificationMessage}</div>
        <button className="closetab" onClick={handleCloseTab}>
          Close
        </button>
      </div>
     
    </div>
  );
};
 
export default Notifications;