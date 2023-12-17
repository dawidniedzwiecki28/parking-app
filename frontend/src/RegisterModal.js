import React, { useState } from 'react';
import Modal from 'react-modal';
import axios from 'axios';
import './RegisterModal.css'; // Import the CSS file for styling

const RegisterModal = ({ isOpen, closeModal }) => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailError, setEmailError] = useState('');
  const [requiredFieldsError, setRequiredFieldsError] = useState('');
  const [registrationError, setRegistrationError] = useState('');

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  const handleRegister = async () => {
    if (!name || !email || !password) {
      setRequiredFieldsError('All fields are required');
      return;
    }

    if (!emailRegex.test(email)) {
      setEmailError('Invalid email format');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8090/auth/register', {
        name,
        email,
        password,
      });

      const token = response.data.token; // Assuming the token is returned from the backend
      if (token) {
        localStorage.setItem('token', token); // Store the token in localStorage
      }

      console.log(response.data); // Log the response data

      closeModal(); // Close the modal after successful registration
      window.location.href = '/cars';
    } catch (error) {
      if (error.response && error.response.status === 409) {
        setRegistrationError('User with this email already exists');
      } else {
        console.error('Registration failed:', error);
        // Handle other registration errors
      }
    }
  };

  const handleEmailChange = (e) => {
    const { value } = e.target;
    setEmail(value);
    if (!emailRegex.test(value)) {
      setEmailError('Invalid email format');
    } else {
      setEmailError('');
    }
  };

  const handleRegisterClick = () => {
    setRequiredFieldsError('');
    setEmailError('');
    setRegistrationError('');

    if (!name || !email || !password) {
      setRequiredFieldsError('All fields are required');
      return;
    }

    if (!emailRegex.test(email)) {
      setEmailError('Invalid email format');
      return;
    }

    handleRegister();
  };

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={closeModal}
      contentLabel="Register Modal"
      className="ReactModal__Content"
      overlayClassName="ReactModal__Overlay"
    >
      <h2>Register</h2>
      <form>
        <input
          type="text"
          placeholder="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={handleEmailChange}
          required
        />
        {emailError && <p className="error-message">{emailError}</p>}
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {requiredFieldsError && <p className="error-message">{requiredFieldsError}</p>}
        {registrationError && <p className="error-message">{registrationError}</p>}
      </form>
      <button type="button" onClick={handleRegisterClick}>Register</button>
      <button onClick={closeModal}>Close</button>
    </Modal>
  );
};

export default RegisterModal;
