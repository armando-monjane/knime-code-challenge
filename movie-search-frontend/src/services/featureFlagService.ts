import axios from 'axios';

export interface FeatureFlags {
    darkMode: boolean;
    maintenanceMode: boolean;
}

const API_BASE_URL = process.env.REACT_APP_API_URL;

class FeatureFlagService {
    async getFeatureFlags(): Promise<FeatureFlags> {
        try {
            const [darkMode, maintenanceMode] = await Promise.all([
                axios.get(`${API_BASE_URL}/api/flags/name/dark_mode`),
                axios.get(`${API_BASE_URL}/api/flags/maintenance_mode`)
            ]);

            return {
                darkMode: darkMode.data,
                maintenanceMode: maintenanceMode.data
            };
        } catch (error) {
            console.error('Failed to fetch feature flags:', error);
            throw error;
        }
    }
}

export const featureFlagService = new FeatureFlagService();
