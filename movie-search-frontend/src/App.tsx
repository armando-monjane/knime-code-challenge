import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MovieSearch from './components/MovieSearch';
import Header from './components/Header';
import MaintenancePage from './components/MaintenancePage';
import { featureFlagService, FeatureFlags } from './services/featureFlagService';
import './App.css';

function App() {
  const [featureFlags, setFeatureFlags] = useState<FeatureFlags>({
    darkMode: false,
    maintenanceMode: false
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFeatureFlags = async () => {
      try {
        const flags = await featureFlagService.getFeatureFlags();
        setFeatureFlags(flags);
      } catch (error) {
        console.error('Failed to fetch feature flags:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchFeatureFlags();
    
    // Poll for feature flag updates every 30 seconds
    const interval = setInterval(fetchFeatureFlags, 30000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (featureFlags.maintenanceMode) {
    return <MaintenancePage />;
  }

  return (
    <Router>
      <div className={`min-h-screen transition-colors duration-200 ${
        featureFlags.darkMode 
          ? 'bg-gray-900 text-white' 
          : 'bg-gray-50 text-gray-900'
      }`}>
        <Header darkMode={featureFlags.darkMode} />
        <main className="container mx-auto px-4 py-8">
          <Routes>
            <Route path="/" element={<MovieSearch darkMode={featureFlags.darkMode} />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
