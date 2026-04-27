import React from 'react'
import { useNavigate } from 'react-router-dom'

const RiskAssessment: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ padding: '32px', textAlign: 'center' }}>
      <h1>Risk Assessment</h1>
      <p>Bone loss risk evaluation tools</p>
      <button onClick={() => navigate('/dashboard')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Back to Dashboard
      </button>
    </div>
  )
}

export default RiskAssessment
