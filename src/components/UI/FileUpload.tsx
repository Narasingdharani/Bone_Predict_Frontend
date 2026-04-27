import React, { useRef, useState } from 'react'

interface FileUploadProps {
  accept?: string
  maxSize?: number // in MB
  onFileSelect?: (file: File) => void
  label?: string
  description?: string
  icon?: string
  className?: string
  style?: React.CSSProperties
  disabled?: boolean
}

const FileUpload: React.FC<FileUploadProps> = ({
  accept = '.jpg,.jpeg,.png,.dcm',
  maxSize = 10,
  onFileSelect,
  label,
  description = 'Supported formats: JPG, PNG, DICOM',
  icon = 'fas fa-cloud-upload-alt',
  className = '',
  style,
  disabled = false
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [dragOver, setDragOver] = useState(false)
  const [error, setError] = useState<string>('')

  const handleFileSelect = (file: File) => {
    setError('')
    
    // Check file size
    if (file.size > maxSize * 1024 * 1024) {
      setError(`File size must be less than ${maxSize}MB`)
      return
    }
    
    // Check file type
    const allowedTypes = accept.split(',').map(type => type.trim())
    const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase()
    
    if (!allowedTypes.includes(fileExtension) && !allowedTypes.includes(file.type)) {
      setError(`Invalid file type. Allowed: ${description}`)
      return
    }
    
    onFileSelect?.(file)
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setDragOver(false)
    
    const files = e.dataTransfer.files
    if (files.length > 0) {
      handleFileSelect(files[0])
    }
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    setDragOver(true)
  }

  const handleDragLeave = () => {
    setDragOver(false)
  }

  const handleClick = () => {
    if (!disabled) {
      fileInputRef.current?.click()
    }
  }

  const baseStyles: React.CSSProperties = {
    border: dragOver ? '2px dashed #3B82F6' : '2px dashed #E2E8F0',
    borderRadius: '12px',
    padding: '32px',
    textAlign: 'center',
    background: dragOver ? '#EFF6FF' : '#F8FAFC',
    cursor: disabled ? 'not-allowed' : 'pointer',
    transition: 'all 0.2s ease',
    ...style
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
        </label>
      )}
      <div
        style={baseStyles}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onClick={handleClick}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept={accept}
          onChange={(e) => {
            const file = e.target.files?.[0]
            if (file) {
              handleFileSelect(file)
            }
          }}
          style={{ display: 'none' }}
          disabled={disabled}
        />
        <i className={icon} style={{
          fontSize: '48px',
          color: dragOver ? '#3B82F6' : '#64748B',
          marginBottom: '16px'
        }} />
        <p style={{
          fontWeight: 600,
          marginBottom: '8px',
          color: dragOver ? '#3B82F6' : '#1E293B'
        }}>
          {dragOver ? 'Drop file here' : 'Click to upload or drag and drop'}
        </p>
        <p style={{
          fontSize: '14px',
          color: '#64748B'
        }}>
          {description}
        </p>
        {error && (
          <div style={{
            color: '#DC2626',
            fontSize: '14px',
            marginTop: '12px'
          }}>
            {error}
          </div>
        )}
      </div>
    </div>
  )
}

export default FileUpload
