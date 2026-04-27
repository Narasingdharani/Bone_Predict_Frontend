import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import UserDataService, { Patient } from '../services/UserDataService'

const Patients: React.FC = () => {
  const navigate = useNavigate()
  const { user } = useAuth()
  const [patients, setPatients] = useState<Patient[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) {
      // Load user-specific patients
      const userData = UserDataService.getUserData(user.id)
      setPatients(userData.patients)
      console.log('Patients loaded for user:', user.name, userData.patients.length, 'patients')
    }
    setLoading(false)
  }, [user])

  const handleAddPatient = (patientData: Omit<Patient, 'id' | 'doctorId' | 'createdAt'>) => {
    if (user) {
      const newPatient = UserDataService.addPatient(user.id, patientData)
      setPatients(prev => [newPatient, ...prev])
    }
  }

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#F8FAFC' }}>
        <div style={{ textAlign: 'center' }}>
          <i className="fas fa-spinner fa-spin" style={{ fontSize: '48px', color: '#3B82F6', marginBottom: '16px' }}></i>
          <h3 style={{ color: '#64748B', marginBottom: '8px' }}>Loading Patients...</h3>
          <p style={{ color: '#94A3B8' }}>Please wait while we load your patient data</p>
        </div>
      </div>
    )
  }

  if (!user) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#F8FAFC' }}>
        <div style={{ textAlign: 'center' }}>
          <i className="fas fa-exclamation-triangle" style={{ fontSize: '48px', color: '#F59E0B', marginBottom: '16px' }}></i>
          <h3 style={{ color: '#1E293B', marginBottom: '8px' }}>Authentication Required</h3>
          <p style={{ color: '#64748B', marginBottom: '16px' }}>Please log in to view patient records</p>
          <button
            onClick={() => navigate('/login')}
            style={{
              background: '#3B82F6',
              color: 'white',
              border: 'none',
              padding: '12px 24px',
              borderRadius: '8px',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: 600
            }}
          >
            Go to Login
          </button>
        </div>
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', height: '100vh', background: '#F8FAFC' }}>
      {/* Sidebar - Same as Dashboard */}
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
            <span>Patients ({patients.length})</span>
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
                Patients ({patients.length})
              </h1>
              <p style={{ color: '#64748B' }}>Manage and view all patient records for {user?.name}</p>
            </div>
            <button
              onClick={() => navigate('/add-patient')}
              style={{
                background: '#3B82F6',
                color: 'white',
                border: 'none',
                padding: '12px 24px',
                borderRadius: '12px',
                cursor: 'pointer',
                fontSize: '16px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '8px'
              }}
            >
              <i className="fas fa-plus"></i>
              New Patient
            </button>
          </div>

          {/* Patients Table */}
          <div style={{ background: 'white', borderRadius: '16px', padding: '24px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
            {patients.length > 0 ? (
              <div style={{ overflow: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #E2E8F0' }}>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Patient</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Contact</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Risk Level</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Status</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Last Visit</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {patients.map((patient) => (
                      <tr key={patient.id} style={{ borderBottom: '1px solid #F1F5F9' }}>
                        <td style={{ padding: '12px' }}>
                          <div>
                            <div style={{ fontWeight: 600, color: '#1E293B' }}>{patient.name}</div>
                            <div style={{ fontSize: '14px', color: '#64748B' }}>Age: {patient.age}</div>
                          </div>
                        </td>
                        <td style={{ padding: '12px' }}>
                          <div>
                            <div style={{ fontSize: '14px', color: '#1E293B' }}>{patient.email}</div>
                            <div style={{ fontSize: '14px', color: '#64748B' }}>{patient.phone}</div>
                          </div>
                        </td>
                        <td style={{ padding: '12px' }}>
                          <span style={{
                            padding: '4px 8px',
                            borderRadius: '4px',
                            fontSize: '12px',
                            fontWeight: 600,
                            background: patient.risk === 'High' ? '#FEE2E2' : patient.risk === 'Medium' ? '#FEF3C7' : '#D1FAE5',
                            color: patient.risk === 'High' ? '#DC2626' : patient.risk === 'Medium' ? '#D97706' : '#059669'
                          }}>
                            {patient.risk}
                          </span>
                        </td>
                        <td style={{ padding: '12px' }}>
                          <span style={{
                            padding: '4px 8px',
                            borderRadius: '4px',
                            fontSize: '12px',
                            fontWeight: 600,
                            background: patient.status === 'Active' ? '#DBEAFE' : '#F3F4F6',
                            color: patient.status === 'Active' ? '#1D4ED8' : '#6B7280'
                          }}>
                            {patient.status}
                          </span>
                        </td>
                        <td style={{ padding: '12px', color: '#64748B' }}>{patient.lastVisit}</td>
                        <td style={{ padding: '12px' }}>
                          <div style={{ display: 'flex', gap: '8px' }}>
                            <button
                              onClick={() => navigate(`/add-patient?id=${patient.id}`)}
                              style={{
                                background: '#EFF6FF',
                                color: '#2563EB',
                                border: '1px solid #DBEAFE',
                                padding: '6px 12px',
                                borderRadius: '6px',
                                cursor: 'pointer',
                                fontSize: '12px'
                              }}
                            >
                              <i className="fas fa-edit"></i>
                            </button>
                            <button
                              onClick={() => navigate(`/assessment/summary?patient=${patient.id}`)}
                              style={{
                                background: '#F0FDF4',
                                color: '#059669',
                                border: '1px solid #D1FAE5',
                                padding: '6px 12px',
                                borderRadius: '6px',
                                cursor: 'pointer',
                                fontSize: '12px'
                              }}
                            >
                              <i className="fas fa-file-medical"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '60px', color: '#64748B' }}>
                <i className="fas fa-users" style={{ fontSize: '48px', marginBottom: '16px', opacity: 0.3 }}></i>
                <h3 style={{ fontSize: '20px', marginBottom: '8px' }}>No Patients Found</h3>
                <p style={{ fontSize: '16px', marginBottom: '24px' }}>You haven't added any patients yet.</p>
                <button
                  onClick={() => navigate('/add-patient')}
                  style={{
                    background: '#3B82F6',
                    color: 'white',
                    border: 'none',
                    padding: '12px 24px',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    fontSize: '16px',
                    fontWeight: 600
                  }}
                >
                  <i className="fas fa-plus"></i>
                  Add Your First Patient
                </button>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}

export default Patients
