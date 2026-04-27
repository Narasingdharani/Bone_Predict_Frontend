import React from 'react'
import { useNavigate } from 'react-router-dom'

const Demographics: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ padding: '32px', textAlign: 'center' }}>
      <h1>Patient Demographics</h1>
      <p>Patient information and demographic data</p>
      <button onClick={() => navigate('/dashboard')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Back to Dashboard
      </button>
    </div>
  )
}

export default Demographics
