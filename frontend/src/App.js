import React from 'react';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Welcome from './components/pages/Welcome';
import CarsList from './components/pages/CarsList';
import Photos from './components/pages/Photos';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route
            path="/"
            element={
              <div className="welcome-container">
                <Welcome />
              </div>
            }
          />
          <Route
            path="/cars"
            element={
              <div className="cars-list-container">
                <CarsList />
              </div>
            }
          />
          <Route
            path="/photos"
            element={
              <div className="photos-container">
                <Photos />
              </div>
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
