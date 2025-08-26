import axios from 'axios';
import { movieService } from '../movieService';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('MovieService', () => {
  const mockMovies = [
    {
      Title: 'Test Movie',
      Year: '2023',
      imdbID: 'tt1234567',
      Type: 'movie',
      Poster: 'https://example.com/poster.jpg'
    }
  ];

  beforeEach(() => {
    jest.resetAllMocks();
  });

  describe('searchMovies', () => {
    it('searches movies successfully', async () => {
      const mockResponse = {
        Search: mockMovies,
        totalResults: '1',
        Response: 'True'
      };

      mockedAxios.get.mockResolvedValueOnce({ data: mockResponse });

      const result = await movieService.searchMovies('test');

      expect(result).toEqual(mockResponse);
      expect(mockedAxios.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/movies/search?title=test')
      );
    });

    it('handles no movies found', async () => {
      const mockResponse = {
        Search: [],
        totalResults: '0',
        Response: 'False',
        Error: 'Movie not found!'
      };

      mockedAxios.get.mockResolvedValueOnce({ data: mockResponse });

      const result = await movieService.searchMovies('nonexistent');

      expect(result).toEqual(mockResponse);
      expect(result.Response).toBe('False');
      expect(result.Error).toBe('Movie not found!');
    });

    it('handles error when search fails', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Network error'));

      await expect(movieService.searchMovies('test')).rejects.toThrow('Network error');
    });

    it('properly encodes search query', async () => {
      const mockResponse = {
        Search: mockMovies,
        totalResults: '1',
        Response: 'True'
      };

      mockedAxios.get.mockResolvedValueOnce({ data: mockResponse });

      await movieService.searchMovies('test movie & action');

      expect(mockedAxios.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/movies/search?title=test%20movie%20%26%20action')
      );
    });

    it('handles maintenance mode error', async () => {
      mockedAxios.get.mockRejectedValueOnce({
        response: {
          status: 503,
          data: { message: 'Service is under maintenance' }
        }
      });

      await expect(movieService.searchMovies('test')).rejects.toMatchObject({
        response: {
          status: 503
        }
      });
    });
  });
});
