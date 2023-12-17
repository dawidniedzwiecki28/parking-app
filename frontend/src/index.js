import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import Modal from 'react-modal';

// Set the app element for react-modal
Modal.setAppElement('#root'); // Assuming your main content is in an element with ID 'root'

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);
