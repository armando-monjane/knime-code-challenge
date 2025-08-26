import React from 'react';

const MaintenancePage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center px-4">
      <h1 className="text-3xl font-bold mb-4">Under Maintenance</h1>
      <p className="text-lg text-gray-300 mb-6 text-center">
        Our movie search service is temporarily unavailable due to maintenance.
      </p>
      <p className="text-gray-400 text-center">
        We'll be back soon. Thank you for your patience!
      </p>
    </div>
  );
};

export default MaintenancePage;
