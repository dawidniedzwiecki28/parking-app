import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Welcome from './Welcome';
import CarsList from './CarsList'; // Import CarsList or the component associated with /cars route

function App() {
  return (
    <Router> {/* Ensure your Routes are inside the Router */}
      <div className="App">
        <Routes>
          <Route path="/" element={<Welcome />} />
          <Route path="/cars" element={<CarsList />} />
          {/* Add more Route components for other routes if needed */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
