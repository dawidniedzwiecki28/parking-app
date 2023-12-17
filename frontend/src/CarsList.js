import React, { useState, useEffect } from 'react';
import axios from 'axios';
import AddCarModal from './AddCarModal';
import AccountDetailsModal from './AccountDetailsModal';
import EditCarModal from './EditCarModal'; // Import the EditCarModal component
import './CarsList.css';

const CarsList = () => {
  const [showEditCarModal, setShowEditCarModal] = useState(false);
  const [selectedCar, setSelectedCar] = useState(null);
  const [carsData, setCarsData] = useState([]);
  const [showAddCarModal, setShowAddCarModal] = useState(false);
  const [showAccountDetailsModal, setShowAccountDetailsModal] = useState(false);
  const [newCarData, setNewCarData] = useState({
    registrationNumber: '',
    country: '',
    arrivalDate: '',
    departureDate: '',
    paid: false,
    onParking: false,
  });
  const [searchTerm, setSearchTerm] = useState('');

  const fetchCarsData = async (term = '') => {
    try {
      const token = localStorage.getItem('token');
      const endpoint = term ? `http://localhost:8090/parking/cars/${term}` : 'http://localhost:8090/parking/cars';

      if (token) {
        const response = await axios.get(endpoint, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setCarsData(response.data);
      } else {
        window.location.href = '/';
      }
    } catch (error) {
      console.error('Error fetching cars data:', error);
      if (error.response && error.response.status === 403) {
        window.location.href = '/';
      }
    }
  };

  useEffect(() => {
    fetchCarsData();

    const intervalId = setInterval(() => fetchCarsData(searchTerm), 3000);

    return () => clearInterval(intervalId);
  }, [searchTerm]);

  const handleCreateCar = async () => {
    try {
      const token = localStorage.getItem('token');

      if (token) {
        await axios.post(
          'http://localhost:8090/parking/car',
          {
            ...newCarData,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        fetchCarsData();
        setShowAddCarModal(false);
      }
    } catch (error) {
      console.error('Error creating car:', error);
    }
  };

  const getFormattedDate = (date) => {
    const currentDate = new Date();
    const formattedDate = new Date(date);
  
    const options = {
      hour: 'numeric',
      minute: 'numeric',
    };
  
    if (
      currentDate.getDate() === formattedDate.getDate() &&
      currentDate.getMonth() === formattedDate.getMonth() &&
      currentDate.getFullYear() === formattedDate.getFullYear()
    ) {
      return `Today ${formattedDate.toLocaleTimeString(undefined, options)}`;
    }
  
    const tomorrow = new Date(currentDate);
    tomorrow.setDate(currentDate.getDate() + 1);
  
    if (
      tomorrow.getDate() === formattedDate.getDate() &&
      tomorrow.getMonth() === formattedDate.getMonth() &&
      tomorrow.getFullYear() === formattedDate.getFullYear()
    ) {
      return `Tomorrow ${formattedDate.toLocaleTimeString(undefined, options)}`;
    }
  
    return formattedDate.toLocaleString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    });
  };

  const openEditModal = (car) => {
    setSelectedCar(car);
    setShowEditCarModal(true);
  };

  const closeEditCarModal = () => {
    setSelectedCar(null);
    setShowEditCarModal(false);
  };

  const handleSearchChange = (e) => {
    const { value } = e.target;
    setSearchTerm(value);
  };

  return (
    <div className="container">
      <button className="account-details-button" onClick={() => setShowAccountDetailsModal(true)}>
        Account Details
      </button>
      <h1>Cars on Parking</h1>
      <div className="header">
        <button onClick={() => setShowAddCarModal(true)}>Add Car</button>
        <input
          type="text"
          placeholder="Search..."
          value={searchTerm}
          onChange={handleSearchChange}
        />
      </div>
      <table className="cars-table">
        <thead>
          <tr>
            <th>Registration Number</th>
            <th>Country</th>
            <th>Arrival Date</th>
            <th>Departure Date</th>
            <th>Paid</th>
            <th>Actions</th> {/* New column for actions */}
          </tr>
        </thead>
        <tbody>
          {carsData.map((car) => (
            <tr key={car.carId.value} className={car.departureDate != null && new Date(car.departureDate) < new Date() ? 'red-row' : ''}>
              <td data-label="Registration Number">{car.registrationNumber}</td>
              <td data-label="Country">{car.country}</td>
              <td data-label="Arrival Date">
                {car.arrivalDate ? getFormattedDate(car.arrivalDate) : 'N/A'}
              </td>
              <td data-label="Departure Date">
                {car.departureDate ? getFormattedDate(car.departureDate) : 'N/A'}
              </td>
              <td data-label="Paid">{car.paid ? 'Yes' : 'No'}</td>
              <td>
                <button onClick={() => openEditModal(car)}>Edit</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showAddCarModal && (
        <AddCarModal
          newCarData={newCarData}
          setNewCarData={setNewCarData}
          handleCreateCar={handleCreateCar}
          setShowAddCarModal={setShowAddCarModal}
        />
      )}

      {showAccountDetailsModal && (
        <AccountDetailsModal
          setShowAccountDetailsModal={(value) => {
            fetchCarsData();
            setShowAccountDetailsModal(value);
          }}
        />
      )}

      {showEditCarModal && (
        <EditCarModal
          isOpen={showEditCarModal}
          closeModal={closeEditCarModal}
          carData={selectedCar}
        />
      )}
    </div>
  );
};

export default CarsList;
