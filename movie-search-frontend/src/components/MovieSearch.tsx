import React, { useState } from 'react';
import MovieCard from './MovieCard';
import SearchForm from './SearchForm';
import { movieService, Movie } from '../services/movieService';

interface MovieSearchProps {
  darkMode: boolean;
}

const MovieSearch: React.FC<MovieSearchProps> = ({ darkMode }) => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [totalResults, setTotalResults] = useState<number>(0);
  const [searched, setSearched] = useState(false);

  const searchMovies = async (title: string) => {
    if (!title.trim()) {
      setError('Please enter a movie title');
      return;
    }

    setLoading(true);
    setError(null);
    setSearched(true);

    try {
      const response = await movieService.searchMovies(title);
      
      if (response.Response === 'True') {
        setMovies(response.Search || []);
        setTotalResults(parseInt(response.totalResults) || 0);
      } else {
        setMovies([]);
        setTotalResults(0);
        setError(response.Error || 'No movies found');
      }
    } catch (err: any) {
      setMovies([]);
      setTotalResults(0);
      
      if (err.response?.status === 503) {
        setError('Service is currently under maintenance. Please try again later.');
      } else {
        setError('Failed to search for movies. Please try again.');
      }
      console.error('Error searching movies:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto">
      <div className="text-center mb-8">
        <h1 className={`text-4xl font-bold mb-4 ${
          darkMode ? 'text-white' : 'text-gray-900'
        }`}>
          Movie Search
        </h1>
        <p className={`text-lg ${
          darkMode ? 'text-gray-300' : 'text-gray-600'
        }`}>
          Search for your favorite movies and discover new ones
        </p>
      </div>

      <SearchForm onSearch={searchMovies} darkMode={darkMode} />

      {loading && (
        <div className="flex justify-center items-center py-12" role="status">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <span className="sr-only">Loading...</span>
        </div>
      )}

      {error && (
        <div className={`mt-6 p-4 rounded-lg ${
          darkMode 
            ? 'bg-red-900/20 border border-red-700 text-red-300' 
            : 'bg-red-50 border border-red-200 text-red-700'
        }`}>
          <div className="flex items-center">
            <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
            </svg>
            {error}
          </div>
        </div>
      )}

      {searched && !loading && !error && movies.length > 0 && (
        <div className="mt-8">
          <div className={`mb-6 ${
            darkMode ? 'text-gray-300' : 'text-gray-600'
          }`}>
            <p className="text-lg">
              Found {totalResults} result{totalResults !== 1 ? 's' : ''} for your search
            </p>
            <p className="text-sm">
              Showing {movies.length} movie{movies.length !== 1 ? 's' : ''}
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            {movies.map((movie) => (
              <MovieCard 
                key={movie.imdbID} 
                movie={movie} 
                darkMode={darkMode} 
              />
            ))}
          </div>
        </div>
      )}

      {searched && !loading && !error && movies.length === 0 && (
        <div className={`mt-8 text-center py-12 ${
          darkMode ? 'text-gray-400' : 'text-gray-500'
        }`}>
          <svg className="w-16 h-16 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <p className="text-lg">No movies found</p>
          <p className="text-sm">Try searching with different keywords</p>
        </div>
      )}
    </div>
  );
};

export default MovieSearch;
