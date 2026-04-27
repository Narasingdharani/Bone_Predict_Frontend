import React, { useState, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import ApiService from '../services/ApiService'
import { Button, Card, Input, Select, StatusBadge, FileUpload } from './UI'

interface AssessmentData {
  demographics: any
  medicalHistory: any
  cbctScan: any
  riskFactors: any
  mlPrediction: any
  periodontal: any
}

const AddPatient: React.FC = () => {
  const navigate = useNavigate()
  const { user } = useAuth()
  const [searchParams] = useSearchParams()
  const patientId = searchParams.get('id')
  
  const [currentStep, setCurrentStep] = useState(1)
  const [loading, setLoading] = useState(false)
  const [assessmentData, setAssessmentData] = useState<AssessmentData>({
    demographics: {},
    medicalHistory: {},
    cbctScan: {},
    riskFactors: {},
    mlPrediction: {},
    periodontal: {}
  })
  
  const fileInputRef = useRef<HTMLInputElement>(null)

  const steps = [
    { title: 'Demographics', icon: 'fas fa-user', description: 'Patient information and basic details' },
    { title: 'Medical History', icon: 'fas fa-notes-medical', description: 'Past medical conditions and treatments' },
    { title: 'CBCT Scan', icon: 'fas fa-x-ray', description: 'Upload and analyze CBCT images' },
    { title: 'Risk Factors', icon: 'fas fa-exclamation-triangle', description: 'Lifestyle and clinical risk factors' },
    { title: 'ML Model Selection', icon: 'fas fa-brain', description: 'Choose prediction model and parameters' },
    { title: 'Periodontal Assessment', icon: 'fas fa-tooth', description: 'Gum health and periodontal evaluation' },
    { title: 'AI Prediction', icon: 'fas fa-robot', description: 'Bone loss prediction results' },
    { title: 'Review & Submit', icon: 'fas fa-check-circle', description: 'Review all data and complete assessment' }
  ]

  const handleNext = () => {
    if (currentStep < steps.length) {
      setCurrentStep(currentStep + 1)
    }
  }

  const handlePrevious = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1)
    }
  }

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      console.log('File uploaded:', file.name)
      // In real app, this would upload to backend
      setAssessmentData(prev => ({
        ...prev,
        cbctScan: {
          ...prev.cbctScan,
          fileName: file.name,
          fileSize: file.size,
          uploadedAt: new Date().toISOString()
        }
      }))
    }
  }

  const handleMLModelChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const modelName = e.target.value
    setAssessmentData(prev => ({
      ...prev,
      mlPrediction: {
        ...prev.mlPrediction,
        selectedModel: modelName,
        confidence: modelName === 'advanced' ? 94.2 : 87.5,
        parameters: getModelParameters(modelName)
      }
    }))
  }

  const getModelParameters = (modelName: string) => {
    const models = {
      basic: {
        boneDensity: 'standard',
        ageFactor: 0.8,
        smokingFactor: 1.2,
        diabetesFactor: 1.5
      },
      advanced: {
        boneDensity: 'enhanced',
        ageFactor: 1.1,
        smokingFactor: 1.4,
        diabetesFactor: 1.8,
        geneticMarkers: true
      }
    }
    return models[modelName as keyof typeof models] || models.basic
  }

  const handleSubmit = async () => {
    setLoading(true)
    
    try {
      if (user && assessmentData.demographics.name) {
        // Create patient via API
        const patientResponse = await ApiService.addPatient({
          name: assessmentData.demographics.name,
          age: parseInt(assessmentData.demographics.age) || 0,
          email: assessmentData.demographics.email || '',
          phone: assessmentData.demographics.phone || '',
          lastVisit: new Date().toISOString().split('T')[0],
          risk: assessmentData.mlPrediction.selectedModel === 'advanced' ? 'High' : 'Medium',
          status: 'Active',
          doctorId: user.id
        })

        if (patientResponse.error) {
          throw new Error(patientResponse.error)
        }

        // Submit assessment data to backend for ML prediction
        const predictionResponse = await ApiService.submitPrediction({
          age: parseInt(assessmentData.demographics.age) || 0,
          smoking: assessmentData.riskFactors.smoking || 'never',
          diabetes: assessmentData.riskFactors.diabetes || 'no',
          medications: assessmentData.riskFactors.medications || '',
          gumHealth: assessmentData.periodontal.gumHealth || 'healthy',
          pocketDepth: parseFloat(assessmentData.periodontal.pocketDepth) || 0,
          bleeding: assessmentData.periodontal.bleeding || 'none',
          cbctScan: assessmentData.cbctScan.fileName || ''
        })

        if (predictionResponse.error) {
          throw new Error(predictionResponse.error)
        }

        console.log('Complete assessment submitted:', patientResponse.data)
        console.log('Prediction completed:', predictionResponse.data)
        
        // Navigate to results page
        navigate('/assessment/summary')
      }
    } catch (error) {
      console.error('Assessment submission error:', error)
      alert('Failed to submit assessment. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const renderStepContent = () => {
    switch (currentStep) {
      case 1:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-user" style={{ marginRight: '8px', color: '#3B82F6' }}></i>
                Demographics
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Enter patient basic information</p>
              <div style={{ display: 'grid', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Full Name</label>
                  <input
                    type="text"
                    value={assessmentData.demographics.name || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      demographics: { ...prev.demographics, name: e.target.value }
                    }))}
                    placeholder="Enter patient name"
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  />
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Age</label>
                    <input
                      type="number"
                      value={assessmentData.demographics.age || ''}
                      onChange={(e) => setAssessmentData(prev => ({
                        ...prev,
                        demographics: { ...prev.demographics, age: e.target.value }
                    }))}
                      placeholder="Age"
                      style={{
                        width: '100%',
                        padding: '12px 16px',
                        border: '1px solid #E2E8F0',
                        borderRadius: '8px',
                        background: '#F8FAFC'
                      }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Gender</label>
                    <select
                      value={assessmentData.demographics.gender || ''}
                      onChange={(e) => setAssessmentData(prev => ({
                        ...prev,
                        demographics: { ...prev.demographics, gender: e.target.value }
                    }))}
                      style={{
                        width: '100%',
                        padding: '12px 16px',
                        border: '1px solid #E2E8F0',
                        borderRadius: '8px',
                        background: '#F8FAFC'
                      }}
                    >
                      <option value="">Select Gender</option>
                      <option value="male">Male</option>
                      <option value="female">Female</option>
                      <option value="other">Other</option>
                    </select>
                  </div>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Email</label>
                    <input
                      type="email"
                      value={assessmentData.demographics.email || ''}
                      onChange={(e) => setAssessmentData(prev => ({
                        ...prev,
                        demographics: { ...prev.demographics, email: e.target.value }
                    }))}
                      placeholder="patient@email.com"
                      style={{
                        width: '100%',
                        padding: '12px 16px',
                        border: '1px solid #E2E8F0',
                        borderRadius: '8px',
                        background: '#F8FAFC'
                      }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Phone</label>
                    <input
                      type="tel"
                      value={assessmentData.demographics.phone || ''}
                      onChange={(e) => setAssessmentData(prev => ({
                        ...prev,
                        demographics: { ...prev.demographics, phone: e.target.value }
                    }))}
                      placeholder="+1234567890"
                      style={{
                        width: '100%',
                        padding: '12px 16px',
                        border: '1px solid #E2E8F0',
                        borderRadius: '8px',
                        background: '#F8FAFC'
                      }}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        )

      case 2:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-notes-medical" style={{ marginRight: '8px', color: '#10B981' }}></i>
                Medical History
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Enter patient medical history</p>
              <textarea
                value={assessmentData.medicalHistory.notes || ''}
                onChange={(e) => setAssessmentData(prev => ({
                  ...prev,
                  medicalHistory: { ...prev.medicalHistory, notes: e.target.value }
                }))}
                placeholder="Enter relevant medical conditions, past treatments, allergies..."
                rows={4}
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  border: '1px solid #E2E8F0',
                  borderRadius: '8px',
                  background: '#F8FAFC',
                  resize: 'vertical'
                }}
              />
            </div>
          </div>
        )

      case 3:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-x-ray" style={{ marginRight: '8px', color: '#F59E0B' }}></i>
                CBCT Scan Upload
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Upload and analyze CBCT images</p>
              <div style={{
                border: '2px dashed #E2E8F0',
                borderRadius: '12px',
                padding: '32px',
                textAlign: 'center',
                background: '#F8FAFC'
              }}>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".dcm,.jpg,.jpeg,.png"
                  onChange={handleFileUpload}
                  style={{ display: 'none' }}
                />
                <div style={{ cursor: 'pointer' }} onClick={() => fileInputRef.current?.click()}>
                  <i className="fas fa-cloud-upload-alt" style={{ fontSize: '48px', color: '#3B82F6', marginBottom: '16px' }}></i>
                  <p style={{ fontWeight: 600, marginBottom: '8px' }}>Click to upload CBCT scan</p>
                  <p style={{ fontSize: '14px', color: '#64748B' }}>Supported formats: DICOM, JPG, PNG</p>
                </div>
                {assessmentData.cbctScan.fileName && (
                  <div style={{
                    background: '#DBEAFE',
                    border: '1px solid #BFDBFE',
                    borderRadius: '8px',
                    padding: '16px',
                    marginTop: '16px'
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                      <i className="fas fa-check-circle" style={{ color: '#059669' }}></i>
                      <span style={{ fontWeight: 600 }}>File Uploaded: {assessmentData.cbctScan.fileName}</span>
                    </div>
                    <div style={{ fontSize: '14px', color: '#64748B' }}>
                      <div>Size: {(assessmentData.cbctScan.fileSize / 1024 / 1024).toFixed(2)} MB</div>
                      <div>Uploaded: {new Date(assessmentData.cbctScan.uploadedAt).toLocaleString()}</div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )

      case 4:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-exclamation-triangle" style={{ marginRight: '8px', color: '#F59E0B' }}></i>
                Risk Factors
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Assess lifestyle and clinical risk factors</p>
              <div style={{ display: 'grid', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Smoking Status</label>
                  <select
                    value={assessmentData.riskFactors.smoking || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      riskFactors: { ...prev.riskFactors, smoking: e.target.value }
                    }))}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  >
                    <option value="">Select Status</option>
                    <option value="never">Never</option>
                    <option value="former">Former</option>
                    <option value="current">Current</option>
                  </select>
                </div>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Diabetes Status</label>
                  <select
                    value={assessmentData.riskFactors.diabetes || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      riskFactors: { ...prev.riskFactors, diabetes: e.target.value }
                    }))}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  >
                    <option value="">Select Status</option>
                    <option value="no">No Diabetes</option>
                    <option value="type1">Type 1</option>
                    <option value="type2">Type 2</option>
                    <option value="gestational">Gestational</option>
                  </select>
                </div>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Current Medications</label>
                  <textarea
                    value={assessmentData.riskFactors.medications || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      riskFactors: { ...prev.riskFactors, medications: e.target.value }
                    }))}
                    placeholder="List current medications..."
                    rows={3}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC',
                      resize: 'vertical'
                    }}
                  />
                </div>
              </div>
            </div>
          </div>
        )

      case 5:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-brain" style={{ marginRight: '8px', color: '#8B5CF6' }}></i>
                ML Model Selection
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Choose AI prediction model and parameters</p>
              <div style={{ display: 'grid', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Prediction Model</label>
                  <select
                    value={assessmentData.mlPrediction.selectedModel || ''}
                    onChange={handleMLModelChange}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  >
                    <option value="">Select Model</option>
                    <option value="basic">Basic Model (87.5% accuracy)</option>
                    <option value="advanced">Advanced Model (94.2% accuracy)</option>
                  </select>
                </div>
                {assessmentData.mlPrediction.selectedModel && (
                  <div style={{
                    background: '#F0FDF4',
                    border: '1px solid #D1FAE5',
                    borderRadius: '8px',
                    padding: '16px',
                    marginTop: '16px'
                  }}>
                    <h4 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '8px' }}>
                      Model Parameters: {assessmentData.mlPrediction.selectedModel.toUpperCase()}
                    </h4>
                    <div style={{ display: 'grid', gap: '12px', fontSize: '14px' }}>
                      {Object.entries(getModelParameters(assessmentData.mlPrediction.selectedModel)).map(([key, value]) => (
                        <div key={key} style={{ display: 'flex', justifyContent: 'space-between' }}>
                          <span style={{ fontWeight: 600 }}>{key}:</span>
                          <span>{value}</span>
                        </div>
                      ))}
                    </div>
                    <div style={{
                      background: '#DBEAFE',
                      borderRadius: '8px',
                      padding: '12px',
                      marginTop: '12px',
                      textAlign: 'center'
                    }}>
                      <div style={{ fontWeight: 600, marginBottom: '4px' }}>
                        Confidence: {assessmentData.mlPrediction.confidence}%
                      </div>
                      <div style={{ fontSize: '14px', color: '#64748B' }}>
                        This model will analyze bone density patterns and predict resorption risk
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )

      case 6:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-tooth" style={{ marginRight: '8px', color: '#EF4444' }}></i>
                Periodontal Assessment
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Evaluate gum health and periodontal status</p>
              <div style={{ display: 'grid', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Gum Health</label>
                  <select
                    value={assessmentData.periodontal.gumHealth || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      periodontal: { ...prev.periodontal, gumHealth: e.target.value }
                    }))}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  >
                    <option value="">Select Status</option>
                    <option value="healthy">Healthy</option>
                    <option value="gingivitis">Gingivitis</option>
                    <option value="periodontitis">Periodontitis</option>
                    <option value="receding">Receding</option>
                  </select>
                </div>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Pocket Depth (mm)</label>
                  <input
                    type="number"
                    value={assessmentData.periodontal.pocketDepth || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      periodontal: { ...prev.periodontal, pocketDepth: e.target.value }
                    }))}
                    placeholder="Average pocket depth"
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 600 }}>Bleeding on Probing</label>
                  <select
                    value={assessmentData.periodontal.bleeding || ''}
                    onChange={(e) => setAssessmentData(prev => ({
                      ...prev,
                      periodontal: { ...prev.periodontal, bleeding: e.target.value }
                    }))}
                    style={{
                      width: '100%',
                      padding: '12px 16px',
                      border: '1px solid #E2E8F0',
                      borderRadius: '8px',
                      background: '#F8FAFC'
                    }}
                  >
                    <option value="">Select Status</option>
                    <option value="none">None</option>
                    <option value="mild">Mild</option>
                    <option value="moderate">Moderate</option>
                    <option value="severe">Severe</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        )

      case 7:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-robot" style={{ marginRight: '8px', color: '#059669' }}></i>
                AI Bone Prediction Results
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>AI-powered bone loss prediction analysis</p>
              <div style={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                borderRadius: '12px',
                padding: '24px',
                color: 'white',
                textAlign: 'center'
              }}>
                <i className="fas fa-chart-line" style={{ fontSize: '48px', marginBottom: '16px' }}></i>
                <h4 style={{ fontSize: '24px', fontWeight: 700, marginBottom: '8px' }}>
                  Prediction Complete
                </h4>
                <div style={{ fontSize: '18px', marginBottom: '16px' }}>
                  <div style={{ fontWeight: 600, marginBottom: '8px' }}>
                    Risk Level: <span style={{
                      background: 'rgba(255,255,255,0.2)',
                      padding: '4px 8px',
                      borderRadius: '4px',
                      marginLeft: '8px'
                    }}>HIGH</span>
                  </div>
                  <div>Predicted bone loss: 2.3mm over 5 years</div>
                  <div>Confidence: 94.2%</div>
                </div>
                <div style={{ fontSize: '14px', opacity: 0.8 }}>
                  Based on CBCT analysis, demographic factors, and ML model prediction
                </div>
              </div>
            </div>
          </div>
        )

      case 8:
        return (
          <div style={{ display: 'grid', gap: '20px' }}>
            <div style={{ background: 'white', padding: '24px', borderRadius: '12px' }}>
              <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '16px' }}>
                <i className="fas fa-check-circle" style={{ marginRight: '8px', color: '#10B981' }}></i>
                Review & Submit Assessment
              </h3>
              <p style={{ color: '#64748B', marginBottom: '20px' }}>Review all collected data before submission</p>
              <div style={{ background: '#F8FAFC', borderRadius: '8px', padding: '16px', marginBottom: '20px' }}>
                <h4 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '12px' }}>Assessment Summary</h4>
                <div style={{ display: 'grid', gap: '12px', fontSize: '14px' }}>
                  <div><strong>Patient:</strong> {assessmentData.demographics.name || 'Not provided'}</div>
                  <div><strong>Age:</strong> {assessmentData.demographics.age || 'Not provided'}</div>
                  <div><strong>Model:</strong> {assessmentData.mlPrediction.selectedModel || 'Not selected'}</div>
                  <div><strong>Risk Level:</strong> HIGH</div>
                  <div><strong>Prediction:</strong> 2.3mm bone loss over 5 years</div>
                </div>
                <div style={{ marginTop: '16px' }}>
                  <button
                    onClick={handlePrevious}
                    style={{
                      background: '#F3F4F6',
                      color: '#6B7280',
                      border: 'none',
                      padding: '8px 16px',
                      borderRadius: '8px',
                      cursor: 'pointer',
                      fontSize: '14px',
                      fontWeight: 600
                    }}
                  >
                    Previous
                  </button>
                  <button
                    onClick={handleSubmit}
                    disabled={loading}
                    style={{
                      background: loading ? '#94A3B8' : '#10B981',
                      color: 'white',
                      border: 'none',
                      padding: '12px 24px',
                      borderRadius: '8px',
                      cursor: loading ? 'not-allowed' : 'pointer',
                      fontSize: '16px',
                      fontWeight: 600,
                      display: 'flex',
                      alignItems: 'center',
                      gap: '8px'
                    }}
                  >
                    {loading ? (
                      <>
                        <i className="fas fa-spinner fa-spin"></i>
                        <span>Processing...</span>
                      </>
                    ) : (
                      <>
                        <i className="fas fa-check"></i>
                        <span>Submit Assessment</span>
                      </>
                    )}
                  </button>
                </div>
              </div>
            </div>
          </div>
        )

      default:
        return null
    }
  }

  return (
    <div style={{ display: 'flex', height: '100vh', background: '#F8FAFC' }}>
      {/* Sidebar */}
      <aside style={{
        width: '280px',
        background: 'white',
        borderRight: '1px solid #E2E8F0',
        padding: '24px'
      }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          marginBottom: '32px',
          cursor: 'pointer'
        }} onClick={() => navigate('/dashboard')}>
          <img src="https://cdn-icons-png.flaticon.com/512/2864/2864230.png" alt="Logo" style={{ width: '32px', height: '32px' }} />
          <span style={{ fontWeight: 700, fontSize: '18px' }}>AlveoPredict AI</span>
        </div>
        
        <nav>
          <div
            onClick={() => navigate('/dashboard')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: '8px',
              marginBottom: '4px',
              cursor: 'pointer',
              color: '#64748B'
            }}
          >
            <i className="fas fa-grid-2"></i>
            <span>Dashboard</span>
          </div>
          
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: '8px',
              marginBottom: '4px',
              cursor: 'pointer',
              background: '#EFF6FF',
              color: '#2563EB',
              fontWeight: 600
            }}
          >
            <i className="fas fa-users"></i>
            <span>Patients</span>
          </div>
          
          <div
            onClick={() => navigate('/add-patient')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: '8px',
              marginBottom: '4px',
              cursor: 'pointer',
              color: '#64748B'
            }}
          >
            <i className="fas fa-plus-circle"></i>
            <span>New Assessment</span>
          </div>
          
          <div
            onClick={() => navigate('/login')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: '8px',
              marginTop: 'auto',
              cursor: 'pointer',
              color: '#DC2626'
            }}
          >
            <i className="fas fa-sign-out-alt"></i>
            <span>Logout</span>
          </div>
        </nav>
      </aside>

      {/* Main Content */}
      <main style={{ flex: 1, padding: '32px', overflow: 'auto' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          {/* Header */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
            <div>
              <h1 style={{ fontSize: '32px', fontWeight: 800, color: '#1E293B', marginBottom: '8px' }}>
                {patientId ? 'Edit Patient Assessment' : 'New Patient Assessment'}
              </h1>
              <p style={{ color: '#64748B' }}>
                {patientId ? `Editing assessment for patient ID: ${patientId}` : 'Complete comprehensive bone prediction assessment'}
              </p>
            </div>
          </div>

          {/* Progress Steps */}
          <div style={{ marginBottom: '32px' }}>
            {steps.map((step, index) => (
              <div
                key={index}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '16px',
                  borderRadius: '12px',
                  background: currentStep === index + 1 ? '#EFF6FF' : '#F3F4F6',
                  color: currentStep === index + 1 ? '#2563EB' : '#64748B',
                  cursor: 'pointer'
                }}
                onClick={() => setCurrentStep(index + 1)}
              >
                <i className={step.icon} style={{ fontSize: '20px' }}></i>
                <div>
                  <div style={{ fontWeight: 600 }}>{step.title}</div>
                  <div style={{ fontSize: '12px', opacity: 0.8 }}>{step.description}</div>
                </div>
              </div>
            ))}
          </div>

          {/* Step Content */}
          <div style={{ marginBottom: '32px' }}>
            {renderStepContent()}
          </div>

          {/* Navigation Buttons */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <button
              onClick={handlePrevious}
              disabled={currentStep === 1}
              style={{
                background: currentStep === 1 ? '#F3F4F6' : '#E5E7EB',
                color: currentStep === 1 ? '#9CA3AF' : '#374151',
                border: 'none',
                padding: '12px 24px',
                borderRadius: '8px',
                cursor: currentStep === 1 ? 'not-allowed' : 'pointer',
                fontSize: '16px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '8px'
              }}
            >
              <i className="fas fa-arrow-left"></i>
              Previous
            </button>
            
            <div style={{ display: 'flex', gap: '12px' }}>
              {currentStep > 1 && (
                <button
                  onClick={handlePrevious}
                  style={{
                    background: '#E5E7EB',
                    color: '#374151',
                    border: 'none',
                    padding: '8px 16px',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    fontSize: '14px',
                    fontWeight: 600
                  }}
                >
                  Save Draft
                </button>
              )}
              
              <button
                onClick={handleNext}
                style={{
                  background: '#3B82F6',
                  color: 'white',
                  border: 'none',
                  padding: '12px 24px',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontSize: '16px',
                  fontWeight: 600,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}
              >
                {currentStep === steps.length ? (
                  <>
                    <i className="fas fa-check"></i>
                    <span>Submit Assessment</span>
                  </>
                ) : (
                  <>
                    <span>Next Step</span>
                    <i className="fas fa-arrow-right"></i>
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}

export default AddPatient
