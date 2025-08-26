import axios from 'axios';

export interface Movie {
  Title: string;
  Year: string;
  imdbID: string;
  Type: string;
  Poster: string;
}

export interface MovieSearchResponse {
  Search: Movie[];
  totalResults: string;
  Response: string;
  Error?: string;
}

const API_BASE_URL = process.env.REACT_APP_API_URL;

class MovieService {
  async searchMovies(title: string): Promise<MovieSearchResponse> {
    const response = await axios.get<MovieSearchResponse>(`${API_BASE_URL}/api/movies/search?title=${encodeURIComponent(title)}`);
    return response.data;
  }
}

export const movieService = new MovieService();
