import React from 'react'

interface StatusBadgeProps {
  children: React.ReactNode
  variant?: 'high' | 'medium' | 'low' | 'active' | 'completed' | 'success' | 'warning' | 'danger'
  className?: string
  style?: React.CSSProperties
}

const StatusBadge: React.FC<StatusBadgeProps> = ({
  children,
  variant = 'medium',
  className = '',
  style
}) => {
  const variantStyles: Record<string, React.CSSProperties> = {
    high: {
      background: '#FEE2E2',
      color: '#DC2626'
    },
    medium: {
      background: '#FEF3C7',
      color: '#D97706'
    },
    low: {
      background: '#D1FAE5',
      color: '#059669'
    },
    active: {
      background: '#DBEAFE',
      color: '#1D4ED8'
    },
    completed: {
      background: '#F3F4F6',
      color: '#6B7280'
    },
    success: {
      background: '#D1FAE5',
      color: '#059669'
    },
    warning: {
      background: '#FEF3C7',
      color: '#D97706'
    },
    danger: {
      background: '#FEE2E2',
      color: '#DC2626'
    }
  }

  const baseStyles: React.CSSProperties = {
    display: 'inline-block',
    padding: '4px 8px',
    borderRadius: '4px',
    fontSize: '12px',
    fontWeight: 600,
    textTransform: 'uppercase',
    ...variantStyles[variant],
    ...style
  }

  return (
    <span className={`status-badge status-${variant} ${className}`} style={baseStyles}>
      {children}
    </span>
  )
}

export default StatusBadge
