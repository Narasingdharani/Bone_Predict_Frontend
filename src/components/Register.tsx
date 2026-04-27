import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'

const Register: React.FC = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    specialization: ''
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match')
      setLoading(false)
      return
    }

    // Simulate registration
    await new Promise(resolve => setTimeout(resolve, 1000))
    navigate('/login')
  }

  return (
    <div style={{
      background: 'linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      padding: '20px'
    }}>
      <div style={{
        width: '100%',
        maxWidth: '480px',
        padding: '60px',
        borderRadius: '32px',
        boxShadow: '0 20px 50px rgba(0, 0, 0, 0.05)',
        background: 'white',
        textAlign: 'center'
      }}>
        <h1 style={{ fontSize: '32px', marginBottom: '12px', fontWeight: 800 }}>Create Account</h1>
        <p style={{ color: '#64748B', marginBottom: '48px', fontSize: '15px' }}>
          Join AlveoPredict AI for clinical analysis
        </p>

        {error && (
          <div style={{
            color: '#DC2626',
            background: '#FEF2F2',
            padding: '12px',
            borderRadius: '12px',
            marginBottom: '24px',
            fontSize: '13px',
            fontWeight: 600
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ textAlign: 'left' }}>
          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Full Name</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              placeholder="Dr. John Smith"
              required
              style={{
                width: '100%',
                padding: '12px 16px',
                border: '1px solid #E2E8F0',
                borderRadius: '12px',
                background: '#F8FAFC'
              }}
            />
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Email</label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              placeholder="doctor@gmail.com"
              required
              style={{
                width: '100%',
                padding: '12px 16px',
                border: '1px solid #E2E8F0',
                borderRadius: '12px',
                background: '#F8FAFC'
              }}
            />
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Specialization</label>
            <select
              value={formData.specialization}
              onChange={(e) => setFormData({...formData, specialization: e.target.value})}
              required
              style={{
                width: '100%',
                padding: '12px 16px',
                border: '1px solid #E2E8F0',
                borderRadius: '12px',
                background: '#F8FAFC'
              }}
            >
              <option value="">Select Specialization</option>
              <option value="periodontist">Periodontist</option>
              <option value="implantologist">Implantologist</option>
              <option value="general">General Dentist</option>
              <option value="oral-surgeon">Oral Surgeon</option>
            </select>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Password</label>
            <input
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              placeholder="Create password"
              required
              style={{
                width: '100%',
                padding: '12px 16px',
                border: '1px solid #E2E8F0',
                borderRadius: '12px',
                background: '#F8FAFC'
              }}
            />
          </div>

          <div style={{ marginBottom: '32px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Confirm Password</label>
            <input
              type="password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
              placeholder="Confirm password"
              required
              style={{
                width: '100%',
                padding: '12px 16px',
                border: '1px solid #E2E8F0',
                borderRadius: '12px',
                background: '#F8FAFC'
              }}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{
              width: '100%',
              height: '60px',
              fontSize: '16px',
              background: loading ? '#94A3B8' : '#0EA5E9',
              color: 'white',
              border: 'none',
              borderRadius: '12px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontWeight: 600
            }}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '40px' }}>
          <p style={{ color: '#64748B', fontSize: '14px' }}>
            Already have an account?{' '}
            <a href="/login" style={{ color: '#0EA5E9', textDecoration: 'none', fontWeight: 800 }}>
              Sign In
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Register
