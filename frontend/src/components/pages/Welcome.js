
import React, { useState } from 'react';
import LoginModal from '../modals/LoginModal';
import RegisterModal from '../modals/RegisterModal';
import './Welcome.css'; 

const Welcome = () => {
  const [loginModalOpen, setLoginModalOpen] = useState(false);
  const [registerModalOpen, setRegisterModalOpen] = useState(false);

  const openLoginModal = () => {
    setLoginModalOpen(true);
  };

  const closeLoginModal = () => {
    setLoginModalOpen(false);
  };

  const openRegisterModal = () => {
    setRegisterModalOpen(true);
  };

  const closeRegisterModal = () => {
    setRegisterModalOpen(false);
  };

  return (
    <div className="container">
      <h1>Welcome to Carpark Management</h1>
      <div>
        <button onClick={openLoginModal}>Login</button>
        <button onClick={openRegisterModal}>Register</button>
      </div>
      <LoginModal isOpen={loginModalOpen} closeModal={closeLoginModal} />
      <RegisterModal isOpen={registerModalOpen} closeModal={closeRegisterModal} />
    </div>
  );
};

export default Welcome;