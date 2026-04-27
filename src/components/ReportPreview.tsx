import React from 'react'
import { useNavigate } from 'react-router-dom'

const ReportPreview: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ padding: '32px', textAlign: 'center' }}>
      <h1>Report Preview</h1>
      <p>Generate and preview patient reports</p>
      <button onClick={() => navigate('/dashboard')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Back to Dashboard
      </button>
    </div>
  )
}

export default ReportPreview
