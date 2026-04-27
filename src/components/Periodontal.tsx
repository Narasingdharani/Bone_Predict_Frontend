import React from 'react'
import { useNavigate } from 'react-router-dom'

const Periodontal: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ padding: '32px', textAlign: 'center' }}>
      <h1>Periodontal Assessment</h1>
      <p>Gum health and periodontal disease evaluation</p>
      <button onClick={() => navigate('/dashboard')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Back to Dashboard
      </button>
    </div>
  )
}

export default Periodontal
