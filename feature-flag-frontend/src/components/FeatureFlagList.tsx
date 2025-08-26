import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { featureFlagService, FeatureFlag } from '../services/featureFlagService';

const FeatureFlagList: React.FC = () => {
  const [flags, setFlags] = useState<FeatureFlag[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchFlags = async () => {
    try {
      setLoading(true);
      const flags = await featureFlagService.getAllFlags();
      setFlags(flags || []);
      setError(null);
    } catch (err) {
      setError('Failed to fetch feature flags');
      console.error('Error fetching flags:', err);
    } finally {
      setLoading(false);
    }
  };

  const toggleFlag = async (id: number) => {
    try {
      const updatedFlag = await featureFlagService.toggleFlag(id);
      setFlags(flags.map((flag: FeatureFlag) => 
        flag.id === id ? updatedFlag : flag
      ));
    } catch (err) {
      setError('Failed to toggle feature flag');
      console.error('Error toggling flag:', err);
    }
  };

  const deleteFlag = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this feature flag?')) {
      return;
    }

    try {
      await featureFlagService.deleteFlag(id);
      setFlags(flags.filter((flag: FeatureFlag) => flag.id !== id));
    } catch (err) {
      setError('Failed to delete feature flag');
      console.error('Error deleting flag:', err);
    }
  };

  useEffect(() => {
    fetchFlags();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64" role="status">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <span className="sr-only">Loading...</span>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Feature Flags</h1>
        <Link
          to="/add"
          className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
        >
          Add New Flag
        </Link>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      <div className="bg-white shadow-sm rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Name
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Description
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {flags && flags.map((flag: FeatureFlag) => (
                <tr key={flag.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{flag.name}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span
                      className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                        flag.enabled
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      }`}
                    >
                      {flag.enabled ? 'Enabled' : 'Disabled'}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm text-gray-900 max-w-xs truncate">
                      {flag.description || 'No description'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                    <button
                      onClick={() => toggleFlag(flag.id)}
                      className={`px-3 py-1 rounded-md text-xs font-medium transition-colors ${
                        flag.enabled
                          ? 'bg-red-100 text-red-700 hover:bg-red-200'
                          : 'bg-green-100 text-green-700 hover:bg-green-200'
                      }`}
                    >
                      {flag.enabled ? 'Disable' : 'Enable'}
                    </button>
                    <Link
                      to={`/edit/${flag.id}`}
                      className="text-blue-600 hover:text-blue-900 px-3 py-1 rounded-md text-xs font-medium hover:bg-blue-50"
                    >
                      Edit
                    </Link>
                    <button
                      onClick={() => deleteFlag(flag.id)}
                      className="text-red-600 hover:text-red-900 px-3 py-1 rounded-md text-xs font-medium hover:bg-red-50"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {flags && flags.length === 0 && (
          <div className="text-center py-12">
            <div className="text-gray-500 text-lg">No feature flags found</div>
            <div className="text-gray-400 text-sm mt-2">
              Create your first feature flag to get started
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default FeatureFlagList;
