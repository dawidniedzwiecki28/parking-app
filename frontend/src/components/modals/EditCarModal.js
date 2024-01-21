import React, { useState, useEffect } from 'react';
import Modal from 'react-modal';
import axios from 'axios';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import './EditCarModal.css';

const EditCarModal = ({ isOpen, closeModal, carData }) => {
  const [editedCarData, setEditedCarData] = useState({
    registrationNumber: carData?.registrationNumber || '',
    country: carData?.country || '',
    arrivalDate: carData?.arrivalDate ? new Date(carData.arrivalDate) : null,
    departureDate: carData?.departureDate ? new Date(carData.departureDate) : null,
    paid: carData?.paid || false,
    onParking: carData?.onParking || true,
  });

  useEffect(() => {
    setEditedCarData({
      registrationNumber: carData?.registrationNumber || '',
      country: carData?.country || '',
      arrivalDate: carData?.arrivalDate ? new Date(carData.arrivalDate) : null,
      departureDate: carData?.departureDate ? new Date(carData.departureDate) : null,
      paid: carData?.paid || false,
      onParking: carData?.onParking || true,
    });
  }, [carData]);

  const handleEditChange = (name, value) => {
    setEditedCarData((prevData) => ({ ...prevData, [name]: value }));
  };

  const handleEditSubmit = async () => {
    try {
      const token = localStorage.getItem('token');

      if (token) {
        const updatedData = {};

        Object.keys(editedCarData).forEach((key) => {
          if (editedCarData[key] !== carData?.[key]) {
            updatedData[key] = editedCarData[key];
          } else {
            updatedData[key] = null; 
          }
        });

        const response = await axios.patch(
          `http://localhost:8090/parking/car/${carData?.carId}`,
          updatedData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        console.log('Car updated:', response.data);
        closeModal();
      } else {
        window.location.href = '/';
      }
    } catch (error) {
      closeModal();
    }
  };
  

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={closeModal}
      contentLabel="Edit Car Modal"
      className="EditCarModal__Content" 
      overlayClassName="EditCarModal__Overlay" 
    >
      <h2>Edit Car</h2>
      <form>
        <label>
          Registration Number:
          <input
            type="text"
            name="registrationNumber"
            value={editedCarData.registrationNumber}
            onChange={(e) => handleEditChange(e.target.name, e.target.value)}
          />
        </label>
        <label>
          Country:
          <input
            type="text"
            name="country"
            value={editedCarData.country}
            onChange={(e) => handleEditChange(e.target.name, e.target.value)}
          />
        </label>
        <label>
          Arrival Date:
          <DatePicker
            selected={editedCarData.arrivalDate}
            onChange={(date) => handleEditChange('arrivalDate', date)}
            dateFormat="yyyy-MM-dd HH:mm"
            showTimeInput
            showTimeSelect
            timeFormat="HH:mm"
            timeInputLabel="Time:"
          />
        </label>
        <label>
          Departure Date:
          <DatePicker
            selected={editedCarData.departureDate}
            onChange={(date) => handleEditChange('departureDate', date)}
            dateFormat="yyyy-MM-dd HH:mm"
            showTimeInput
            showTimeSelect
            timeFormat="HH:mm"
            timeInputLabel="Time:"
          />
        </label>
        <label>
          Paid:
          <input
            type="checkbox"
            name="paid"
            checked={editedCarData.paid}
            onChange={(e) => handleEditChange(e.target.name, e.target.checked)}
          />
        </label>
        <label>
          On Parking:
          <input
            type="checkbox"
            name="onParking"
            checked={editedCarData.onParking}
            onChange={(e) => handleEditChange(e.target.name, e.target.checked)}
          />
        </label>
        <button type="button" onClick={handleEditSubmit}>
          Update
        </button>
        <button onClick={closeModal}>Close</button>
      </form>
    </Modal>
  );
};

export default EditCarModal;

