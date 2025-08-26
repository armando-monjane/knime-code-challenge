import React, { useState } from 'react';

interface SearchFormProps {
  onSearch: (title: string) => void;
  darkMode: boolean;
}

const SearchForm: React.FC<SearchFormProps> = ({ onSearch, darkMode }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(searchTerm);
  };

  return (
    <div className="max-w-2xl mx-auto">
      <form onSubmit={handleSubmit} className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Enter movie title..."
            className={`w-full px-4 py-3 rounded-lg border-2 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 ${
              darkMode
                ? 'bg-gray-800 border-gray-600 text-white placeholder-gray-400 focus:border-blue-500'
                : 'bg-white border-gray-300 text-gray-900 placeholder-gray-500 focus:border-blue-500'
            }`}
          />
        </div>
        <button
          type="submit"
          className={`px-6 py-3 rounded-lg font-medium transition-colors ${
            darkMode
              ? 'bg-blue-600 hover:bg-blue-700 text-white'
              : 'bg-blue-600 hover:bg-blue-700 text-white'
          }`}
        >
          Search
        </button>
      </form>
      
      <div className={`mt-4 text-sm text-center ${
        darkMode ? 'text-gray-400' : 'text-gray-600'
      }`}>
        Try searching for movies like "The Matrix", "Inception", or "Avatar"
      </div>
    </div>
  );
};

export default SearchForm;
