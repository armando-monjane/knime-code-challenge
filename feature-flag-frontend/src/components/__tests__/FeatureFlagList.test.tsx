import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import '@testing-library/jest-dom';
import FeatureFlagList from '../FeatureFlagList';
import { featureFlagService } from '../../services/featureFlagService';

// Mock the feature flag service
jest.mock('../../services/featureFlagService');

const mockFeatureFlags = [
    {
        id: 1,
        name: 'test_flag',
        description: 'Test flag description',
        enabled: true
    },
    {
        id: 2,
        name: 'maintenance_mode',
        description: 'Maintenance mode flag',
        enabled: false
    }
];

describe('FeatureFlagList', () => {
    beforeEach(() => {
        // Reset all mocks before each test
        jest.resetAllMocks();

        // Silence expected console errors
        jest.spyOn(console, 'error').mockImplementation(() => {});
        jest.spyOn(console, 'warn').mockImplementation(() => {});

        // Mock window.confirm
        window.confirm = jest.fn(() => true);
    });

    afterEach(() => {
        jest.restoreAllMocks();
    });

    it('renders loading state initially', () => {
        render(
            <BrowserRouter>
                <FeatureFlagList />
            </BrowserRouter>
        );

        expect(screen.getByRole('status')).toBeInTheDocument();
    });

    it('renders feature flags after loading', async () => {
        // Mock the service call
        (featureFlagService.getAllFlags as jest.Mock).mockResolvedValue(mockFeatureFlags);

        render(
            <BrowserRouter>
                <FeatureFlagList />
            </BrowserRouter>
        );

        // Wait for the flags to load
        const firstFlag = await screen.findByText('test_flag');
        expect(firstFlag).toBeInTheDocument();
        expect(screen.getByText('maintenance_mode')).toBeInTheDocument();
        expect(screen.getByText('Test flag description')).toBeInTheDocument();
    });

    it('handles toggle flag action', async () => {
        // Mock the service calls
        (featureFlagService.getAllFlags as jest.Mock).mockResolvedValue(mockFeatureFlags);
        (featureFlagService.toggleFlag as jest.Mock).mockResolvedValue({
            ...mockFeatureFlags[0],
            enabled: false
        });

        render(
            <BrowserRouter>
                <FeatureFlagList />
            </BrowserRouter>
        );

        // Wait for the initial loading to complete
        await screen.findByText('test_flag');
        
        // Wait for the toggle button to be available
        const toggleButton = await screen.findByRole('button', { name: 'Disable' });
        fireEvent.click(toggleButton);

        // Verify the service was called
        await screen.findByText('test_flag');
        expect(featureFlagService.toggleFlag).toHaveBeenCalledWith(1);
    });

    it('handles error state when loading flags fails', async () => {
        // Mock the service to throw an error
        (featureFlagService.getAllFlags as jest.Mock).mockRejectedValue(new Error('Failed to fetch'));

        render(
            <BrowserRouter>
                <FeatureFlagList />
            </BrowserRouter>
        );

        // Wait for the error message
        const errorMessage = await screen.findByText('Failed to fetch feature flags');
        expect(errorMessage).toBeInTheDocument();
    });

    it('renders empty state when no flags exist', async () => {
        // Mock the service to return empty array
        (featureFlagService.getAllFlags as jest.Mock).mockResolvedValue([]);

        render(
            <BrowserRouter>
                <FeatureFlagList />
            </BrowserRouter>
        );

        // Wait for the empty state message
        const emptyMessage = await screen.findByText('No feature flags found');
        expect(emptyMessage).toBeInTheDocument();
        expect(screen.getByText('Create your first feature flag to get started')).toBeInTheDocument();
    });
});
