import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import MovieSearch from '../MovieSearch';
import { movieService } from '../../services/movieService';

// Mock the movie service
jest.mock('../../services/movieService');

const mockMovies = [
  {
    Title: 'Test Movie',
    Year: '2023',
    imdbID: 'tt1234567',
    Type: 'movie',
    Poster: 'https://example.com/poster.jpg'
  },
  {
    Title: 'Another Test Movie',
    Year: '2022',
    imdbID: 'tt7654321',
    Type: 'movie',
    Poster: 'https://example.com/poster2.jpg'
  }
];

describe('MovieSearch', () => {
  beforeEach(() => {
    jest.resetAllMocks();
    // Silence expected console errors
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('renders the search form and initial state', () => {
    render(<MovieSearch darkMode={false} />);
    
    expect(screen.getByText('Movie Search')).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/enter movie title/i)).toBeInTheDocument();
    expect(screen.queryByText('No movies found')).not.toBeInTheDocument();
  });

  it('handles successful movie search', async () => {
    // Mock successful search response
    (movieService.searchMovies as jest.Mock).mockResolvedValue({
      Response: 'True',
      Search: mockMovies,
      totalResults: '2'
    });

    render(<MovieSearch darkMode={false} />);

    // Perform search
    const searchInput = screen.getByPlaceholderText(/enter movie title/i);
    const searchButton = screen.getByRole('button', { name: /search/i });

    fireEvent.change(searchInput, { target: { value: 'test' } });
    fireEvent.click(searchButton);

    // Wait for loading state to appear and then disappear
    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument();
    });

    // Wait for results
    const firstMovie = await screen.findByText('Test Movie');
    expect(firstMovie).toBeInTheDocument();
    expect(screen.getByText('Another Test Movie')).toBeInTheDocument();
    expect(screen.getByText('Found 2 results for your search')).toBeInTheDocument();
  });

  it('handles empty search input', async () => {
    render(<MovieSearch darkMode={false} />);

    const searchButton = screen.getByRole('button', { name: /search/i });
    fireEvent.click(searchButton);

    expect(screen.getByText('Please enter a movie title')).toBeInTheDocument();
    expect(movieService.searchMovies).not.toHaveBeenCalled();
  });

  it('handles no movies found', async () => {
    // Mock empty search response
    (movieService.searchMovies as jest.Mock).mockResolvedValue({
      Response: 'False',
      Error: 'Movie not found!'
    });

    render(<MovieSearch darkMode={false} />);

    // Perform search
    const searchInput = screen.getByPlaceholderText(/enter movie title/i);
    const searchButton = screen.getByRole('button', { name: /search/i });

    fireEvent.change(searchInput, { target: { value: 'nonexistent movie' } });
    fireEvent.click(searchButton);

    // Wait for error message
    const errorMessage = await screen.findByText('Movie not found!');
    expect(errorMessage).toBeInTheDocument();
  });

  it('handles service maintenance mode', async () => {
    // Mock maintenance mode error
    (movieService.searchMovies as jest.Mock).mockRejectedValue({
      response: { status: 503 }
    });

    render(<MovieSearch darkMode={false} />);

    // Perform search
    const searchInput = screen.getByPlaceholderText(/enter movie title/i);
    const searchButton = screen.getByRole('button', { name: /search/i });

    fireEvent.change(searchInput, { target: { value: 'test' } });
    fireEvent.click(searchButton);

    // Wait for maintenance message
    const maintenanceMessage = await screen.findByText('Service is currently under maintenance. Please try again later.');
    expect(maintenanceMessage).toBeInTheDocument();
  });

  it('handles general search error', async () => {
    // Mock general error
    (movieService.searchMovies as jest.Mock).mockRejectedValue(new Error('Network error'));

    render(<MovieSearch darkMode={false} />);

    // Perform search
    const searchInput = screen.getByPlaceholderText(/enter movie title/i);
    const searchButton = screen.getByRole('button', { name: /search/i });

    fireEvent.change(searchInput, { target: { value: 'test' } });
    fireEvent.click(searchButton);

    // Wait for error message
    const errorMessage = await screen.findByText('Failed to search for movies. Please try again.');
    expect(errorMessage).toBeInTheDocument();
  });

  it('renders correctly in dark mode', () => {
    render(<MovieSearch darkMode={true} />);
    
    const heading = screen.getByText('Movie Search');
    expect(heading).toHaveClass('text-white');
  });
});
