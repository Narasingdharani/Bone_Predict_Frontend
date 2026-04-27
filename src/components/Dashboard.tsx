import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import ApiService from '../services/ApiService'

interface UserStats {
  totalPatients: number
  assessmentsToday: number
  highRiskCases: number
  reportsGenerated: number
}

interface Patient {
  id: string
  name: string
  age: number
  email: string
  phone: string
  lastVisit: string
  risk: 'High' | 'Medium' | 'Low'
  status: 'Active' | 'Completed'
  doctorId: string
  createdAt: string
}

interface Assessment {
  id: string
  patientId: string
  doctorId: string
  date: string
  risk: 'High' | 'Medium' | 'Low'
  status: 'Completed' | 'In Progress'
  type: string
  boneLoss?: number
  confidence?: number
  reportId?: string
  notes?: string
  riskScore?: number
}

interface Report {
  id: string
  patientId: string
  doctorId: string
  patientName: string
  assessmentDate: string
  generatedDate: string
  riskLevel: 'High' | 'Medium' | 'Low'
  predictedBoneLoss: number
  confidence: number
  mlModel: string
  cbctScan: string
  recommendations: string[]
  summary: string
  status: 'Generated' | 'Draft' | 'Archived'
  pdfUrl?: string
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate()
  const { user, logout } = useAuth()
  const [userStats, setUserStats] = useState<UserStats>({
    totalPatients: 0,
    assessmentsToday: 0,
    highRiskCases: 0,
    reportsGenerated: 0
  })
  const [patients, setPatients] = useState<Patient[]>([])
  const [assessments, setAssessments] = useState<Assessment[]>([])
  const [recentPatients, setRecentPatients] = useState<Patient[]>([])
  const [recentReports, setRecentReports] = useState<Report[]>([])
  const [loading, setLoading] = useState(true)

  const menuItems = [
    { icon: 'fas fa-grid-2', label: 'Dashboard', path: '/dashboard', active: true },
    { icon: 'fas fa-users', label: 'Patients', path: '/patients' },
    { icon: 'fas fa-plus-circle', label: 'New Assessment', path: '/add-patient' },
    { icon: 'fas fa-file-waveform', label: 'Recent Reports', path: '/assessment/summary' },
  ]

  useEffect(() => {
    const loadDashboardData = async () => {
      if (user) {
        try {
          setLoading(true)

          // Load data from backend API
          const [patientsResponse, assessmentsResponse, reportsResponse, statsResponse] = await Promise.allSettled([
            ApiService.getPatients(user.id),
            ApiService.getAssessments(user.id),
            ApiService.getReports(user.id),
            ApiService.getUserStats(user.id)
          ])

          // Handle patients data
          if (patientsResponse.status === 'fulfilled' && patientsResponse.value.data) {
            const patientsData = Array.isArray(patientsResponse.value.data) ? patientsResponse.value.data : []
            const recentPatientsData = patientsData
              .sort((a: any, b: any) => new Date(b.lastVisit).getTime() - new Date(a.lastVisit).getTime())
              .slice(0, 5)
            setPatients(recentPatientsData)
          } else if (patientsResponse.status === 'rejected') {
            console.error('Patients API error:', patientsResponse.reason)
          }

          // Handle assessments data
          if (assessmentsResponse.status === 'fulfilled' && assessmentsResponse.value.data) {
            const assessmentsData = Array.isArray(assessmentsResponse.value.data) ? assessmentsResponse.value.data : []
            setAssessments(assessmentsData)
          } else if (assessmentsResponse.status === 'rejected') {
            console.error('Assessments API error:', assessmentsResponse.reason)
          }

          // Handle reports data
          if (reportsResponse.status === 'fulfilled' && reportsResponse.value.data) {
            const reportsData = Array.isArray(reportsResponse.value.data) ? reportsResponse.value.data : []
            const recentReportsData = reportsData.slice(0, 5)
            setRecentReports(recentReportsData)
          } else if (reportsResponse.status === 'rejected') {
            console.error('Reports API error:', reportsResponse.reason)
          }

          // Handle stats data
          if (statsResponse.status === 'fulfilled' && statsResponse.value.data) {
            setUserStats(statsResponse.value.data as UserStats)
          } else if (statsResponse.status === 'rejected') {
            console.error('Stats API error:', statsResponse.reason)
            // Set default stats if API fails
            setUserStats({
              totalPatients: 0,
              assessmentsToday: 0,
              highRiskCases: 0,
              reportsGenerated: 0
            })
          }

          console.log('Dashboard loaded for user:', user.name)

        } catch (error) {
          console.error('Error loading dashboard data:', error)
          // Set default values on error
          setUserStats({
            totalPatients: 0,
            assessmentsToday: 0,
            highRiskCases: 0,
            reportsGenerated: 0
          })
          setPatients([])
          setAssessments([])
          setRecentReports([])
        } finally {
          setLoading(false)
        }
      }
    }

    loadDashboardData()
  }, [user])

  const stats = [
    { title: 'Total Patients', value: userStats.totalPatients.toString(), change: '+12%', icon: 'fas fa-users', color: '#10B981' },
    { title: 'Assessments Today', value: userStats.assessmentsToday.toString(), change: '+3', icon: 'fas fa-clipboard-check', color: '#3B82F6' },
    { title: 'High Risk Cases', value: userStats.highRiskCases.toString(), change: '-1', icon: 'fas fa-exclamation-triangle', color: '#F59E0B' },
    { title: 'Reports Generated', value: userStats.reportsGenerated.toString(), change: '+8', icon: 'fas fa-file-medical', color: '#8B5CF6' },
  ]

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
        }}>
          <img src="https://cdn-icons-png.flaticon.com/512/2864/2864230.png" alt="Logo" style={{ width: '32px', height: '32px' }} />
          <span style={{ fontWeight: 700, fontSize: '18px' }}>AlveoPredict AI</span>
        </div>

        <nav>
          {menuItems.map((item, index) => (
            <div
              key={index}
              onClick={() => navigate(item.path)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '12px 16px',
                borderRadius: '8px',
                marginBottom: '4px',
                cursor: 'pointer',
                background: item.active ? '#EFF6FF' : 'transparent',
                color: item.active ? '#2563EB' : '#64748B',
                fontWeight: item.active ? 600 : 400
              }}
            >
              <i className={item.icon}></i>
              <span>{item.label}</span>
            </div>
          ))}

          <div style={{ marginTop: '32px', padding: '0 16px' }}>
            <p style={{ fontSize: '11px', textTransform: 'uppercase', color: '#94A3B8', fontWeight: 800, letterSpacing: '1px' }}>
              Settings
            </p>
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
              color: '#64748B'
            }}
          >
            <i className="fas fa-cog"></i>
            <span>Preferences</span>
          </div>

          <div
            onClick={() => {
              logout()
              navigate('/login')
            }}
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
          <div style={{ marginBottom: '32px' }}>
            <h1 style={{ fontSize: '32px', fontWeight: 800, color: '#1E293B', marginBottom: '8px' }}>
              Dashboard
            </h1>
            <p style={{ color: '#64748B' }}>Welcome back! Here's your clinical overview.</p>
          </div>

          {/* Stats Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '24px', marginBottom: '32px' }}>
            {stats.map((stat, index) => (
              <div key={index} style={{
                background: 'white',
                padding: '24px',
                borderRadius: '16px',
                boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
                  <div style={{
                    width: '48px',
                    height: '48px',
                    borderRadius: '12px',
                    background: `${stat.color}20`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: stat.color
                  }}>
                    <i className={stat.icon} style={{ fontSize: '20px' }}></i>
                  </div>
                  <span style={{ color: stat.color, fontSize: '14px', fontWeight: 600 }}>
                    {stat.change}
                  </span>
                </div>
                <h3 style={{ fontSize: '28px', fontWeight: 800, color: '#1E293B', marginBottom: '4px' }}>
                  {stat.value}
                </h3>
                <p style={{ color: '#64748B', fontSize: '14px' }}>{stat.title}</p>
              </div>
            ))}
          </div>

          {/* Recent Patients */}
          <div style={{ background: 'white', borderRadius: '16px', padding: '24px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 700, color: '#1E293B' }}>Recent Patients</h2>
              <button
                onClick={() => navigate('/patients')}
                style={{
                  background: '#3B82F6',
                  color: 'white',
                  border: 'none',
                  padding: '8px 16px',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontWeight: 600
                }}
              >
                View All
              </button>
            </div>

            <div style={{ overflow: 'auto' }}>
              {recentPatients.length > 0 ? (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #E2E8F0' }}>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Patient</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Age</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Risk Level</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Last Visit</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentPatients.map((patient) => (
                      <tr key={patient.id} style={{ borderBottom: '1px solid #F1F5F9' }}>
                        <td style={{ padding: '12px' }}>
                          <div>
                            <div style={{ fontWeight: 600, color: '#1E293B' }}>{patient.name}</div>
                            <div style={{ fontSize: '14px', color: '#64748B' }}>{patient.email}</div>
                          </div>
                        </td>
                        <td style={{ padding: '12px' }}>{patient.age}</td>
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
                        <td style={{ padding: '12px', color: '#64748B' }}>{patient.lastVisit}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div style={{ textAlign: 'center', padding: '60px', color: '#64748B' }}>
                  <i className="fas fa-users" style={{ fontSize: '48px', marginBottom: '16px', opacity: 0.3 }}></i>
                  <h3 style={{ fontSize: '20px', marginBottom: '8px' }}>No Recent Patients</h3>
                  <p style={{ fontSize: '16px' }}>You haven't added any patients yet.</p>
                  <button
                    onClick={() => navigate('/add-patient')}
                    style={{
                      background: '#3B82F6',
                      color: 'white',
                      border: 'none',
                      padding: '12px 24px',
                      borderRadius: '8px',
                      cursor: 'pointer',
                      fontSize: '14px',
                      fontWeight: 600
                    }}
                  >
                    Add First Patient
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Recent Reports */}
          <div style={{ background: 'white', borderRadius: '16px', padding: '24px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <div>
                <h3 style={{ fontSize: '20px', fontWeight: 700, color: '#1E293B', marginBottom: '4px' }}>
                  Recent Reports
                </h3>
                <p style={{ color: '#64748B', fontSize: '14px' }}>Latest bone prediction reports</p>
              </div>
              <button
                onClick={() => navigate('/assessment/summary')}
                style={{
                  background: '#3B82F6',
                  color: 'white',
                  border: 'none',
                  padding: '8px 16px',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontWeight: 600
                }}
              >
                View All
              </button>
            </div>

            <div style={{ overflow: 'auto' }}>
              {recentReports.length > 0 ? (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #E2E8F0' }}>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Patient</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Risk Level</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Predicted Loss</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Confidence</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Generated</th>
                      <th style={{ textAlign: 'left', padding: '12px', color: '#64748B', fontWeight: 600 }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentReports.map((report) => (
                      <tr key={report.id} style={{ borderBottom: '1px solid #F1F5F9' }}>
                        <td style={{ padding: '12px' }}>
                          <div>
                            <div style={{ fontWeight: 600, color: '#1E293B' }}>{report.patientName}</div>
                            <div style={{ fontSize: '14px', color: '#64748B' }}>{report.assessmentDate}</div>
                          </div>
                        </td>
                        <td style={{ padding: '12px' }}>
                          <span style={{
                            padding: '4px 8px',
                            borderRadius: '4px',
                            fontSize: '12px',
                            fontWeight: 600,
                            background: report.riskLevel === 'High' ? '#FEE2E2' : report.riskLevel === 'Medium' ? '#FEF3C7' : '#D1FAE5',
                            color: report.riskLevel === 'High' ? '#DC2626' : report.riskLevel === 'Medium' ? '#D97706' : '#059669'
                          }}>
                            {report.riskLevel}
                          </span>
                        </td>
                        <td style={{ padding: '12px' }}>{report.predictedBoneLoss}mm</td>
                        <td style={{ padding: '12px' }}>{report.confidence}%</td>
                        <td style={{ padding: '12px', color: '#64748B' }}>
                          {new Date(report.generatedDate).toLocaleDateString()}
                        </td>
                        <td style={{ padding: '12px' }}>
                          <div style={{ display: 'flex', gap: '8px' }}>
                            <button
                              onClick={() => navigate(`/report/preview?id=${report.id}`)}
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
                              <i className="fas fa-eye"></i>
                            </button>
                            <button
                              onClick={() => window.open(report.pdfUrl, '_blank')}
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
                              <i className="fas fa-download"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div style={{ textAlign: 'center', padding: '60px', color: '#64748B' }}>
                  <i className="fas fa-file-medical" style={{ fontSize: '48px', marginBottom: '16px', opacity: 0.3 }}></i>
                  <h3 style={{ fontSize: '20px', marginBottom: '8px' }}>No Reports Generated</h3>
                  <p style={{ fontSize: '16px' }}>Complete assessments to generate reports.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}

export default Dashboard
