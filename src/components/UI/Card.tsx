import React from 'react'

interface CardProps {
  children: React.ReactNode
  className?: string
  style?: React.CSSProperties
  hover?: boolean
  padding?: 'sm' | 'md' | 'lg'
}

const Card: React.FC<CardProps> = ({
  children,
  className = '',
  style,
  hover = true,
  padding = 'md'
}) => {
  const baseStyles: React.CSSProperties = {
    background: 'white',
    borderRadius: '16px',
    padding: padding === 'sm' ? '16px' : padding === 'lg' ? '32px' : '24px',
    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
    transition: hover ? 'box-shadow 0.2s ease' : 'none',
    ...style
  }

  return (
    <div
      className={`card ${className}`}
      style={baseStyles}
      onMouseEnter={hover ? (e) => {
        e.currentTarget.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)'
      } : undefined}
      onMouseLeave={hover ? (e) => {
        e.currentTarget.style.boxShadow = '0 1px 3px rgba(0, 0, 0, 0.1)'
      } : undefined}
    >
      {children}
    </div>
  )
}

export default Card
