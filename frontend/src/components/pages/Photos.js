import React from 'react';
import image1 from '../../images/image1.jpeg';
import image2 from '../../images/image2.jpeg';
import image3 from '../../images/image3.webp';
import image4 from '../../images/image4.webp';
import './Photos.css';

const Photos = () => {
  const images = [image1, image2, image3, image4];

  return (
    <div className='container'>
        <h1>Cars photos</h1>
        <div className="gallery-container">
        {images.map((imageUrl, index) => (
            <img
            key={index}
            src={imageUrl}
            alt={`Gallery Image ${index + 1}`}
            className="gallery-image"
            />
        ))}
        </div>
    </div>
  );
};

export default Photos;
