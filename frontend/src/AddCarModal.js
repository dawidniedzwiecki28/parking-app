import React, { useState } from 'react';
import './AddCarModal.css';

const AddCarModal = ({
  newCarData,
  setNewCarData,
  handleCreateCar,
  setShowAddCarModal,
}) => {
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewCarData({ ...newCarData, [name]: value });
  };

  const handleSubmit = async () => {
    if (!newCarData.registrationNumber) {
      setError('Registration Number is required');
      return;
    }

    await handleCreateCar();
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>Add New Car</h2>
        <form>
          <label>
            Registration Number:
            <input
              type="text"
              name="registrationNumber"
              value={newCarData.registrationNumber}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Country:
            <input
              type="text"
              name="country"
              value={newCarData.country}
              onChange={handleChange}
            />
          </label>
          <label>
            Arrival Date:
            <input
              type="datetime-local"
              name="arrivalDate"
              value={newCarData.arrivalDate}
              onChange={handleChange}
            />
          </label>
          <label>
            Departure Date:
            <input
              type="datetime-local"
              name="departureDate"
              value={newCarData.departureDate}
              onChange={handleChange}
            />
          </label>
          <label>
            Paid:
            <input
              type="checkbox"
              name="paid"
              checked={newCarData.paid}
              onChange={(e) =>
                setNewCarData({ ...newCarData, paid: e.target.checked })
              }
            />
          </label>
          <button type="button" onClick={handleSubmit}>
            Create
          </button>
          <button type="button" onClick={() => setShowAddCarModal(false)}>
            Cancel
          </button>
          {error && <p>{error}</p>}
        </form>
      </div>
    </div>
  );
};

export default AddCarModal;