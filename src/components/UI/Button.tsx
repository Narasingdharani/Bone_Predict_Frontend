import React from 'react'

interface ButtonProps {
  children: React.ReactNode
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
  onClick?: () => void
  type?: 'button' | 'submit' | 'reset'
  className?: string
  style?: React.CSSProperties
}

const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  disabled = false,
  loading = false,
  onClick,
  type = 'button',
  className = '',
  style
}) => {
  const baseStyles: React.CSSProperties = {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    padding: size === 'sm' ? '8px 16px' : size === 'lg' ? '16px 32px' : '12px 24px',
    borderRadius: '12px',
    fontWeight: 600,
    fontSize: size === 'sm' ? '14px' : size === 'lg' ? '18px' : '16px',
    border: 'none',
    cursor: disabled || loading ? 'not-allowed' : 'pointer',
    transition: 'all 0.2s ease',
    textDecoration: 'none',
    ...style
  }

  const variantStyles: Record<string, React.CSSProperties> = {
    primary: {
      background: (disabled || loading ? '#94A3B8' : '#3B82F6') as string,
      color: 'white'
    },
    secondary: {
      background: (disabled || loading ? '#F3F4F6' : '#F3F4F6') as string,
      color: (disabled || loading ? '#9CA3AF' : '#374151') as string
    },
    success: {
      background: (disabled || loading ? '#94A3B8' : '#10B981') as string,
      color: 'white'
    },
    warning: {
      background: (disabled || loading ? '#94A3B8' : '#F59E0B') as string,
      color: 'white'
    },
    danger: {
      background: (disabled || loading ? '#94A3B8' : '#DC2626') as string,
      color: 'white'
    }
  }

  const hoverStyles: Record<string, React.CSSProperties> = {
    primary: { background: '#2563EB' },
    secondary: { background: '#E5E7EB' },
    success: { background: '#059669' },
    warning: { background: '#D97706' },
    danger: { background: '#B91C1C' }
  }

  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={`btn-app btn-${variant} ${className}`}
      style={{
        ...baseStyles,
        ...variantStyles[variant]
      }}
      onMouseEnter={(e) => {
        if (!disabled && !loading) {
          e.currentTarget.style.background = (hoverStyles[variant].background as string)
        }
      }}
      onMouseLeave={(e) => {
        if (!disabled && !loading) {
          e.currentTarget.style.background = variantStyles[variant].background as string
        }
      }}
    >
      {loading && <i className="fas fa-spinner fa-spin" />}
      {children}
    </button>
  )
}

export default Button
