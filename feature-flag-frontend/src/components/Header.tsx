import React from 'react';
import { Link } from 'react-router-dom';

const Header: React.FC = () => {
  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <Link to="/" className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm">
              Management Console
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
