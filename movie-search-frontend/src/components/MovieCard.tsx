import React from 'react';

interface Movie {
  Title: string;
  Year: string;
  imdbID: string;
  Type: string;
  Poster: string;
}

interface MovieCardProps {
  movie: Movie;
  darkMode: boolean;
}

const MovieCard: React.FC<MovieCardProps> = ({ movie, darkMode }) => {
  const handleImageError = (e: React.SyntheticEvent<HTMLImageElement>) => {
    e.currentTarget.src = 'https://via.placeholder.com/300x450/cccccc/666666?text=No+Poster';
  };

  return (
    <div className={`group relative overflow-hidden rounded-lg shadow-lg transition-transform duration-200 hover:scale-105 ${
      darkMode 
        ? 'bg-gray-800 hover:shadow-xl hover:shadow-gray-900/50' 
        : 'bg-white hover:shadow-xl'
    }`}>
      <div className="aspect-[2/3] overflow-hidden">
        <img
          src={movie.Poster !== 'N/A' ? movie.Poster : 'https://via.placeholder.com/300x450/cccccc/666666?text=No+Poster'}
          alt={`${movie.Title} (${movie.Year})`}
          className="w-full h-full object-cover transition-transform duration-200 group-hover:scale-110"
          onError={handleImageError}
          loading="lazy"
        />
      </div>
      
      <div className={`p-4 ${
        darkMode ? 'bg-gray-800' : 'bg-white'
      }`}>
        <h3 className={`font-semibold text-lg mb-2 line-clamp-2 ${
          darkMode ? 'text-white' : 'text-gray-900'
        }`}>
          {movie.Title}
        </h3>
        
        <div className={`flex items-center justify-between text-sm ${
          darkMode ? 'text-gray-300' : 'text-gray-600'
        }`}>
          <span className="font-medium">{movie.Year}</span>
          <span className={`px-2 py-1 rounded-full text-xs font-medium ${
            darkMode 
              ? 'bg-blue-900/30 text-blue-300' 
              : 'bg-blue-100 text-blue-800'
          }`}>
            {movie.Type}
          </span>
        </div>
        
        <div className="mt-3">
          <a
            href={`https://www.imdb.com/title/${movie.imdbID}`}
            target="_blank"
            rel="noopener noreferrer"
            className={`inline-flex items-center text-sm font-medium transition-colors ${
              darkMode 
                ? 'text-blue-400 hover:text-blue-300' 
                : 'text-blue-600 hover:text-blue-700'
            }`}
          >
            View on IMDB
            <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
          </a>
        </div>
      </div>
    </div>
  );
};

export default MovieCard;
