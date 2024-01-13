import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './AccountDetailsModal.css';

const AccountDetailsModal = ({ setShowAccountDetailsModal }) => {
  const [accountData, setAccountData] = useState({});
  const [newName, setNewName] = useState('');

  useEffect(() => {
    const fetchAccountData = async () => {
      try {
        const token = localStorage.getItem('token');

        if (token) {
          const response = await axios.get('http://localhost:8090/account', {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          setAccountData(response.data);
        }
      } catch (error) {
        console.error('Error fetching account data:', error);
      }
    };

    fetchAccountData();
  }, []);

  const handleDeleteAccount = async () => {
    try {
      const token = localStorage.getItem('token');

      if (token) {
        await axios.delete(`http://localhost:8090/account/${accountData.id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        
        setShowAccountDetailsModal(false);

      }
    } catch (error) {
      console.error('Error deleting account:', error);
    }
  };

  const handleRenameAccount = async () => {
    try {
      const token = localStorage.getItem('token');

      if (token) {
        await axios.put(
          `http://localhost:8090/account/${accountData.id}`,
          { name: newName },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setShowAccountDetailsModal(false);
      }
    } catch (error) {
      console.error('Error renaming account:', error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setShowAccountDetailsModal(false);
  };
  return (
    <div className="modal-overlay">
      <div className="account-details-modal">
        <h2>Account Details</h2>
        <p>ID: {accountData.id}</p>
        <p>Name: {accountData.name}</p>
        <p>Email: {accountData.email}</p>
        <input
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
        />
        <button onClick={handleRenameAccount}>Rename Account</button>
        <button onClick={handleDeleteAccount}>Delete Account</button>
        <button onClick={handleLogout}>Logout</button>
        <button onClick={() => setShowAccountDetailsModal(false)}>Cancel</button>
      </div>
    </div>
  );


};

export default AccountDetailsModal;

