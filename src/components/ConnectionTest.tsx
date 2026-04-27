import React, { useState, useEffect } from 'react'
import ApiService from '../services/ApiService'

const ConnectionTest: React.FC = () => {
  const [testResults, setTestResults] = useState<string[]>([])
  const [isLoading, setIsLoading] = useState(false)

  const addResult = (message: string) => {
    setTestResults(prev => [...prev, `${new Date().toLocaleTimeString()}: ${message}`])
  }

  const runConnectionTest = async () => {
    setIsLoading(true)
    setTestResults([])

    addResult('Starting connection test...')

    // Test 1: Basic API connectivity
    try {
      addResult('Testing basic API connection...')
      const response = await fetch('/api/test')
      if (response.ok) {
        addResult('✅ Basic API connection successful')
      } else {
        addResult(`❌ Basic API failed: ${response.status}`)
      }
    } catch (error) {
      addResult(`❌ Basic API error: ${error}`)
    }

    // Test 2: Direct backend connection
    try {
      addResult('Testing direct backend connection...')
      const response = await fetch('http://180.235.121.245:8030/api/test')
      if (response.ok) {
        addResult('✅ Direct backend connection successful')
      } else {
        addResult(`❌ Direct backend failed: ${response.status}`)
      }
    } catch (error) {
      addResult(`❌ Direct backend error: ${error}`)
    }

    // Test 3: API Service login endpoint
    try {
      addResult('Testing API Service login endpoint...')
      const result = await ApiService.login('test@example.com', 'test')
      if (result.data) {
        addResult('✅ API Service login endpoint responded')
      } else if (result.error) {
        addResult(`⚠️ API Service login error: ${result.error}`)
      }
    } catch (error) {
      addResult(`❌ API Service login error: ${error}`)
    }

    // Test 4: Check if backend is running on port 180.235.121.245:8030
    try {
      addResult('Checking if backend is running on port 180.235.121.245:8030...')
      const response = await fetch('http://180.235.121.245:8030/')
      if (response.ok) {
        addResult('✅ Backend server is running on port 180.235.121.245:8030')
      } else {
        addResult(`❌ Backend responded with: ${response.status}`)
      }
    } catch (error) {
      addResult(`❌ Backend not accessible on port 180.235.121.245:8030: ${error}`)
    }

    setIsLoading(false)
    addResult('Connection test completed!')
  }

  return (
    <div style={{
      padding: '20px',
      maxWidth: '800px',
      margin: '20px auto',
      background: 'white',
      borderRadius: '8px',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    }}>
      <h2 style={{ marginBottom: '20px' }}>Backend Connection Test</h2>

      <button
        onClick={runConnectionTest}
        disabled={isLoading}
        style={{
          padding: '10px 20px',
          background: isLoading ? '#ccc' : '#3B82F6',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: isLoading ? 'not-allowed' : 'pointer',
          marginBottom: '20px'
        }}
      >
        {isLoading ? 'Testing...' : 'Run Connection Test'}
      </button>

      <div style={{
        background: '#f5f5f5',
        padding: '15px',
        borderRadius: '4px',
        fontFamily: 'monospace',
        fontSize: '14px',
        maxHeight: '400px',
        overflowY: 'auto'
      }}>
        {testResults.length === 0 ? (
          <div style={{ color: '#666' }}>Click "Run Connection Test" to diagnose backend connectivity issues.</div>
        ) : (
          testResults.map((result, index) => (
            <div key={index} style={{ marginBottom: '5px' }}>
              {result}
            </div>
          ))
        )}
      </div>

      <div style={{ marginTop: '20px', fontSize: '14px', color: '#666' }}>
        <strong>Troubleshooting Tips:</strong>
        <ul style={{ marginLeft: '20px', marginTop: '5px' }}>
          <li>Make sure your Flask backend is running: <code>cd bone_backend && python app.py</code></li>
          <li>Check that the backend is running on port 180.235.121.245:8030</li>
          <li>Verify there are no firewall blocking the connection</li>
          <li>Check the browser console for detailed error messages</li>
        </ul>
      </div>
    </div>
  )
}

export default ConnectionTest
