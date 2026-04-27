import React from 'react'

interface InputProps {
  type?: 'text' | 'email' | 'tel' | 'number' | 'password'
  placeholder?: string
  value?: string | number
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void
  label?: string
  error?: string
  disabled?: boolean
  required?: boolean
  className?: string
  style?: React.CSSProperties
  icon?: string
}

const Input: React.FC<InputProps> = ({
  type = 'text',
  placeholder,
  value,
  onChange,
  label,
  error,
  disabled = false,
  required = false,
  className = '',
  style,
  icon
}) => {
  const baseStyles: React.CSSProperties = {
    display: 'flex',
    alignItems: 'center',
    background: '#F8FAFC',
    border: error ? '1px solid #DC2626' : '1px solid #E2E8F0',
    borderRadius: '12px',
    padding: '12px 16px',
    transition: 'border-color 0.2s ease',
    ...style
  }

  const inputStyles: React.CSSProperties = {
    flex: 1,
    border: 'none',
    outline: 'none',
    background: 'transparent',
    fontSize: '16px',
    color: '#1E293B'
  }

  return (
    <div className={`form-group ${className}`}>
      {label && (
        <label style={{
          display: 'block',
          marginBottom: '8px',
          fontWeight: 600,
          color: '#1E293B'
        }}>
          {label}
          {required && <span style={{ color: '#DC2626', marginLeft: '4px' }}>*</span>}
        </label>
      )}
      <div
        className="input-surface"
        style={baseStyles}
        onFocus={(e) => {
          if (!error) {
            e.currentTarget.style.borderColor = '#3B82F6'
            e.currentTarget.style.boxShadow = '0 0 0 3px rgba(59, 130, 246, 0.1)'
          }
        }}
        onBlur={(e) => {
          if (!error) {
            e.currentTarget.style.borderColor = '#E2E8F0'
            e.currentTarget.style.boxShadow = 'none'
          }
        }}
      >
        {icon && <i className={icon} style={{ marginRight: '8px', color: '#64748B' }} />}
        <input
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          disabled={disabled}
          style={inputStyles}
        />
      </div>
      {error && (
        <div style={{
          color: '#DC2626',
          fontSize: '14px',
          marginTop: '4px'
        }}>
          {error}
        </div>
      )}
    </div>
  )
}

export default Input
