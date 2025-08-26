import axios from 'axios';
import { featureFlagService } from '../featureFlagService';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('FeatureFlagService', () => {
  const mockFlag = {
    id: 1,
    name: 'test_flag',
    description: 'Test flag description',
    enabled: true
  };

  beforeEach(() => {
    jest.resetAllMocks();
  });

  describe('getAllFlags', () => {
    it('fetches all feature flags successfully', async () => {
      const mockFlags = [mockFlag];
      mockedAxios.get.mockResolvedValueOnce({ data: mockFlags });

      const result = await featureFlagService.getAllFlags();

      expect(result).toEqual(mockFlags);
      expect(mockedAxios.get).toHaveBeenCalledWith(expect.stringContaining('/api/flags'));
    });

    it('handles error when fetching flags fails', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Network error'));

      await expect(featureFlagService.getAllFlags()).rejects.toThrow('Network error');
    });
  });

  describe('toggleFlag', () => {
    it('toggles flag status successfully', async () => {
      const toggledFlag = { ...mockFlag, enabled: false };
      mockedAxios.patch.mockResolvedValueOnce({ data: toggledFlag });

      const result = await featureFlagService.toggleFlag(1);

      expect(result).toEqual(toggledFlag);
      expect(mockedAxios.patch).toHaveBeenCalledWith(expect.stringContaining('/api/flags/1/toggle'));
    });

    it('handles error when toggling flag fails', async () => {
      mockedAxios.patch.mockRejectedValueOnce(new Error('Failed to toggle'));

      await expect(featureFlagService.toggleFlag(1)).rejects.toThrow('Failed to toggle');
    });
  });

  describe('deleteFlag', () => {
    it('deletes flag successfully', async () => {
      mockedAxios.delete.mockResolvedValueOnce({ data: undefined });

      await featureFlagService.deleteFlag(1);

      expect(mockedAxios.delete).toHaveBeenCalledWith(expect.stringContaining('/api/flags/1'));
    });

    it('handles error when deleting flag fails', async () => {
      mockedAxios.delete.mockRejectedValueOnce(new Error('Failed to delete'));

      await expect(featureFlagService.deleteFlag(1)).rejects.toThrow('Failed to delete');
    });
  });

  describe('createFlag', () => {
    it('creates new flag successfully', async () => {
      const newFlag = {
        name: 'new_flag',
        description: 'New flag description',
        enabled: false
      };
      mockedAxios.post.mockResolvedValueOnce({ data: { ...newFlag, id: 2 } });

      const result = await featureFlagService.createFlag(newFlag);

      expect(result).toEqual({ ...newFlag, id: 2 });
      expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/api/flags'),
        newFlag
      );
    });

    it('handles error when creating flag fails', async () => {
      mockedAxios.post.mockRejectedValueOnce(new Error('Failed to create'));

      await expect(featureFlagService.createFlag({
        name: 'new_flag',
        description: 'New flag description',
        enabled: false
      })).rejects.toThrow('Failed to create');
    });
  });

  describe('updateFlag', () => {
    it('updates flag successfully', async () => {
      const updatedFlag = { ...mockFlag, description: 'Updated description' };
      mockedAxios.put.mockResolvedValueOnce({ data: updatedFlag });

      const result = await featureFlagService.updateFlag(1, { description: 'Updated description' });

      expect(result).toEqual(updatedFlag);
      expect(mockedAxios.put).toHaveBeenCalledWith(
        expect.stringContaining('/api/flags/1'),
        { description: 'Updated description' }
      );
    });

    it('handles error when updating flag fails', async () => {
      mockedAxios.put.mockRejectedValueOnce(new Error('Failed to update'));

      await expect(featureFlagService.updateFlag(1, { description: 'Updated description' }))
        .rejects.toThrow('Failed to update');
    });
  });

  describe('getFlagById', () => {
    it('fetches flag by id successfully', async () => {
      mockedAxios.get.mockResolvedValueOnce({ data: mockFlag });

      const result = await featureFlagService.getFlagById(1);

      expect(result).toEqual(mockFlag);
      expect(mockedAxios.get).toHaveBeenCalledWith(expect.stringContaining('/api/flags/1'));
    });

    it('handles error when fetching flag fails', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Flag not found'));

      await expect(featureFlagService.getFlagById(1)).rejects.toThrow('Flag not found');
    });
  });
});
