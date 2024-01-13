import React, { useState } from 'react';
import Modal from 'react-modal';
import axios from 'axios';
import './LoginModal.css'; // Import the CSS file for styling

const LoginModal = ({ isOpen, closeModal }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [requiredFieldsError, setRequiredFieldsError] = useState('');

  const handleLogin = async () => {
    if (!email || !password) {
      setRequiredFieldsError('Email and password are required');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8090/auth/login', {
        email,
        password,
      });

      const token = response.data.token;
      if (token) {
        localStorage.setItem('token', token); 
      }

      console.log(response.data); 

      closeModal(); // Close the modal after successful login
      window.location.href = '/cars';
    } catch (error) {
      if (error.response && error.response.status === 403) {
        setError('Wrong email or password'); // Set error message for wrong credentials
        setEmail('');
        setPassword('');
      } else {
        console.error('Login failed:', error);
        setError('Something went wrong'); // Set error message for other errors
      }
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={closeModal}
      contentLabel="Login Modal"
      className="ReactModal__Content" // Apply custom class for styling
      overlayClassName="ReactModal__Overlay" // Apply custom class for styling
    >
      <h2>Login</h2>
      {error && <p>{error}</p>} {/* Display error message */}
      <form>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {requiredFieldsError && <p className="error-message">{requiredFieldsError}</p>}
      </form>
      <button type="button" onClick={handleLogin}>
          Login
      </button>
      <button onClick={closeModal}>Close</button>
    </Modal>
  );
};

export default LoginModal;
