import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

const Login: React.FC = () => {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuth()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    // Validation
    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/
    if (!emailRegex.test(email.trim())) {
      setError('Only @gmail.com email addresses are acceptable.')
      setLoading(false)
      return
    }

    if (password.length < 8) {
      setError('Password must be at least 8 characters long.')
      setLoading(false)
      return
    }

    if (password[0] !== password[0].toUpperCase()) {
      setError('First letter of password must be capital.')
      setLoading(false)
      return
    }

    const specialCharPattern = /[!@#$%^&*(),.?":{}|<>]/
    if (!specialCharPattern.test(password)) {
      setError('Password must contain at least one special character.')
      setLoading(false)
      return
    }

    const numberPattern = /[0-9]/
    if (!numberPattern.test(password)) {
      setError('Password must contain at least one number.')
      setLoading(false)
      return
    }

    // Simulate login with user-specific accounts
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // Create user-specific accounts based on email
      let userData;
      if (email.includes('sam')) {
        userData = {
          id: '123',
          name: 'Dr. Sam Wilson',
          email: email,
          specialization: 'Periodontist'
        };
      } else if (email.includes('sarah')) {
        userData = {
          id: '456',
          name: 'Dr. Sarah Johnson',
          email: email,
          specialization: 'Implantologist'
        };
      } else if (email.includes('michael')) {
        userData = {
          id: '789',
          name: 'Dr. Michael Chen',
          email: email,
          specialization: 'General Dentist'
        };
      } else {
        // Default user account
        userData = {
          id: `user_${Date.now()}`,
          name: `Dr. ${email.split('@')[0].charAt(0).toUpperCase() + email.split('@')[0].slice(1)}`,
          email: email,
          specialization: 'General Dentist'
        };
      }
      
      // Use AuthContext to login
      login(userData);
      
      navigate('/dashboard')
    } catch (err) {
      setError('Clinical server unreachable. Check connection.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      background: 'linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      margin: 0,
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
        <div style={{
          width: '64px',
          height: '64px',
          margin: '0 auto 32px',
          background: '#E0F2FE',
          borderRadius: '16px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#0EA5E9',
          fontSize: '32px'
        }}>
          <i className="fas fa-microscope"></i>
        </div>
        
        <h1 style={{ fontSize: '32px', marginBottom: '12px', fontWeight: 800 }}>Welcome Back</h1>
        <p style={{ color: '#64748B', marginBottom: '48px', fontSize: '15px' }}>
          Sign in to continue your clinical analysis
        </p>

        {error && (
          <div style={{
            color: '#DC2626',
            background: '#FEF2F2',
            padding: '12px',
            borderRadius: '12px',
            marginBottom: '24px',
            display: 'block',
            fontSize: '13px',
            fontWeight: 600
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ textAlign: 'left' }}>
          <div style={{ marginBottom: '24px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Email Address</label>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              background: '#F8FAFC',
              border: '1px solid #E2E8F0',
              borderRadius: '12px',
              padding: '12px 16px'
            }}>
              <i className="fas fa-envelope" style={{ color: '#94A3B8', marginRight: '12px' }}></i>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="doctor@hospital.com"
                required
                style={{ flex: 1, border: 'none', outline: 'none', background: 'transparent' }}
              />
            </div>
          </div>

          <div style={{ marginBottom: '24px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Password</label>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              background: '#F8FAFC',
              border: '1px solid #E2E8F0',
              borderRadius: '12px',
              padding: '12px 16px'
            }}>
              <i className="fas fa-lock" style={{ color: '#94A3B8', marginRight: '12px' }}></i>
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
                style={{ flex: 1, border: 'none', outline: 'none', background: 'transparent' }}
              />
              <i
                className={`fas ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`}
                onClick={() => setShowPassword(!showPassword)}
                style={{ cursor: 'pointer', color: '#94A3B8', marginLeft: '12px', width: '20px', textAlign: 'center' }}
              ></i>
            </div>
          </div>

          <div style={{ textAlign: 'right', marginBottom: '40px' }}>
            <a href="#" style={{ color: '#0EA5E9', textDecoration: 'none', fontWeight: 700, fontSize: '13px' }}>
              Forgot Password?
            </a>
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
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              fontWeight: 600
            }}
          >
            {loading ? (
              <>
                <i className="fas fa-spinner fa-spin"></i>
                <span>Verifying...</span>
              </>
            ) : (
              <>
                <span>Sign In</span>
                <i className="fas fa-arrow-right"></i>
              </>
            )}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '40px' }}>
          <p style={{ color: '#64748B', fontSize: '14px' }}>
            Don't have an account?{' '}
            <a href="/register" style={{ color: '#0EA5E9', textDecoration: 'none', fontWeight: 800 }}>
              Sign Up
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login
