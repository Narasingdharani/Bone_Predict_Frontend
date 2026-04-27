import React from 'react'
import { useNavigate } from 'react-router-dom'

const CBCT: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ padding: '32px', textAlign: 'center' }}>
      <h1>CBCT Analysis</h1>
      <p>Cone Beam CT imaging and bone density analysis</p>
      <button onClick={() => navigate('/dashboard')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Back to Dashboard
      </button>
    </div>
  )
}

export default CBCT
