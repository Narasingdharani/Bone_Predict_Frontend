import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

const Splash: React.FC = () => {
  const navigate = useNavigate()

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate('/login')
    }, 3000)
    return () => clearTimeout(timer)
  }, [navigate])

  return (
    <div className="splash-bg" style={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <div style={{ textAlign: 'center' }}>
        {/* Logo Container */}
        <div style={{
          width: '120px', 
          height: '120px', 
          background: 'rgba(255,255,255,0.2)', 
          borderRadius: '24px', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center', 
          margin: '0 auto 32px', 
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
        }}>
          <i className="fas fa-microscope" style={{ fontSize: '60px', color: 'white' }}></i>
        </div>

        {/* App Name */}
        <h1 style={{ color: 'white', fontSize: '32px', marginBottom: '16px', fontWeight: 'bold' }}>
          AlveoPredict AI
        </h1>

        {/* Subtitle */}
        <p style={{ 
          color: 'rgba(255,255,255,0.8)', 
          fontSize: '16px', 
          maxWidth: '300px',
          margin: '0 auto'
        }}>
          Predictive Modeling of Alveolar Ridge Resorption
        </p>

        {/* Progress Indicators (Dots) */}
        <div style={{ display: 'flex', gap: '8px', marginTop: '60px', justifyContent: 'center' }}>
          <div style={{ width: '10px', height: '10px', borderRadius: '50%', background: 'white' }}></div>
          <div style={{ width: '10px', height: '10px', borderRadius: '50%', background: 'rgba(255,255,255,0.5)' }}></div>
          <div style={{ width: '10px', height: '10px', borderRadius: '50%', background: 'rgba(255,255,255,0.5)' }}></div>
        </div>

        {/* Skip Button */}
        <button
          onClick={() => navigate('/login')}
          style={{
            marginTop: '40px',
            padding: '12px 24px',
            background: 'rgba(255,255,255,0.2)',
            color: 'white',
            border: '1px solid rgba(255,255,255,0.3)',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '14px',
            fontWeight: 600,
            transition: 'all 0.2s ease'
          }}
          onMouseOver={(e) => {
            e.currentTarget.style.background = 'rgba(255,255,255,0.3)'
          }}
          onMouseOut={(e) => {
            e.currentTarget.style.background = 'rgba(255,255,255,0.2)'
          }}
        >
          Continue to Login →
        </button>
      </div>
    </div>
  )
}

export default Splash
