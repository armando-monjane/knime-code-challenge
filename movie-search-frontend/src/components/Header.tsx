import React from 'react';

interface HeaderProps {
  darkMode: boolean;
}

const Header: React.FC<HeaderProps> = ({ darkMode }) => {
  return (
    <header className={`border-b transition-colors duration-200 ${
      darkMode 
        ? 'bg-gray-900 border-gray-700' 
        : 'bg-white border-gray-200'
    }`}>
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <h1 className={`text-2xl font-bold ${
              darkMode ? 'text-white' : 'text-gray-900'
            }`}>
              Movie Search
            </h1>
            <div className={`flex items-center space-x-2 px-3 py-1 rounded-full text-sm ${
              darkMode 
                ? 'bg-gray-800 text-gray-300' 
                : 'bg-gray-100 text-gray-600'
            }`}>
              <div className={`w-2 h-2 rounded-full ${
                darkMode ? 'bg-blue-400' : 'bg-blue-600'
              }`}></div>
              <span>{darkMode ? 'Dark Mode' : 'Light Mode'}</span>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
