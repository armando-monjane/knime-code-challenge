import axios from 'axios';

export interface FeatureFlag {
  id: number;
  name: string;
  enabled: boolean;
  description: string;
}

const API_BASE_URL = process.env.REACT_APP_API_URL;

class FeatureFlagService {
  async getAllFlags(): Promise<FeatureFlag[]> {
    const response = await axios.get(`${API_BASE_URL}/api/flags`);
    return response.data;
  }

  async toggleFlag(id: number): Promise<FeatureFlag> {
    const response = await axios.patch(`${API_BASE_URL}/api/flags/${id}/toggle`);
    return response.data;
  }

  async deleteFlag(id: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/api/flags/${id}`);
  }

  async createFlag(flagData: Omit<FeatureFlag, 'id'>): Promise<FeatureFlag> {
    const response = await axios.post(`${API_BASE_URL}/api/flags`, flagData);
    return response.data;
  }

  async updateFlag(id: number, flagData: Partial<Omit<FeatureFlag, 'id'>>): Promise<FeatureFlag> {
    const response = await axios.put(`${API_BASE_URL}/api/flags/${id}`, flagData);
    return response.data;
  }

  async getFlagById(id: number): Promise<FeatureFlag> {
    const response = await axios.get(`${API_BASE_URL}/api/flags/${id}`);
    return response.data;
  }
}

export const featureFlagService = new FeatureFlagService();
