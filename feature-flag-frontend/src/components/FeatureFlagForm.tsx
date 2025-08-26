import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { featureFlagService } from '../services/featureFlagService';

const FeatureFlagForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    enabled: false,
    description: ''
  });

  const isEditing = Boolean(id);

  useEffect(() => {
    if (isEditing) {
      fetchFlag();
    }
  }, [id]);

  const fetchFlag = async () => {
    try {
      setLoading(true);
      const flag = await featureFlagService.getFlagById(Number(id));
      setFormData({
        name: flag.name,
        enabled: flag.enabled,
        description: flag.description || ''
      });
    } catch (err) {
      setError('Failed to fetch feature flag');
      console.error('Error fetching flag:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      setError('Flag name is required');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      if (isEditing) {
        await featureFlagService.updateFlag(Number(id), formData);
      } else {
        await featureFlagService.createFlag(formData);
      }

      navigate('/');
    } catch (err: any) {
      if (err.response?.status === 409) {
        setError('A feature flag with this name already exists');
      } else {
        setError('Failed to save feature flag');
      }
      console.error('Error saving flag:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : value
    }));
  };

  if (loading && isEditing) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">
          {isEditing ? 'Edit Feature Flag' : 'Create New Feature Flag'}
        </h1>
        <p className="text-gray-600 mt-2">
          {isEditing ? 'Update the feature flag settings below.' : 'Create a new feature flag to control application behavior.'}
        </p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white shadow-sm rounded-lg p-6">
        <div className="space-y-6">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              Flag Name *
            </label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="e.g., dark_mode, maintenance_mode"
              required
            />
            <p className="text-sm text-gray-500 mt-1">
              Use snake_case naming convention (e.g., dark_mode, maintenance_mode)
            </p>
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Describe what this feature flag controls..."
            />
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              id="enabled"
              name="enabled"
              checked={formData.enabled}
              onChange={handleChange}
              className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label htmlFor="enabled" className="ml-2 block text-sm text-gray-900">
              Enable this feature flag
            </label>
          </div>
        </div>

        <div className="flex justify-end space-x-4 mt-8">
          <button
            type="button"
            onClick={() => navigate('/')}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Saving...' : (isEditing ? 'Update Flag' : 'Create Flag')}
          </button>
        </div>
      </form>
    </div>
  );
};

export default FeatureFlagForm;
