import React from 'react'
import { useNavigate } from 'react-router-dom'

const ResultSummary: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ padding: '32px', textAlign: 'center' }}>
      <h1>Assessment Results</h1>
      <p>Summary of bone prediction assessments</p>
      <button onClick={() => navigate('/dashboard')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Back to Dashboard
      </button>
    </div>
  )
}

export default ResultSummary
