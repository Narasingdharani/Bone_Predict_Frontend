// Mock user data service - in real app this would connect to backend API

export interface Patient {
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

export interface Assessment {
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

export interface Report {
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

export interface UserStats {
  totalPatients: number
  assessmentsToday: number
  highRiskCases: number
  reportsGenerated: number
}

class UserDataService {
  // Mock data storage - in real app this would be in a database
  private static users: Record<string, {
    patients: Patient[]
    assessments: Assessment[]
    reports: Report[]
    stats: UserStats
  }> = {}

  static getUserData(doctorId: string) {
    if (!this.users[doctorId]) {
      // Initialize user data if not exists
      this.users[doctorId] = {
        patients: this.generateMockPatients(doctorId),
        assessments: this.generateMockAssessments(doctorId),
        reports: this.generateMockReports(doctorId),
        stats: this.generateMockStats(doctorId)
      }
    }
    return this.users[doctorId]
  }

  static addPatient(doctorId: string, patient: Omit<Patient, 'id' | 'doctorId' | 'createdAt'>) {
    const userData = this.getUserData(doctorId)
    const newPatient: Patient = {
      ...patient,
      id: `patient_${Date.now()}`,
      doctorId,
      createdAt: new Date().toISOString()
    }
    userData.patients.push(newPatient)
    userData.stats.totalPatients++
    return newPatient
  }

  static addAssessment(doctorId: string, assessment: Omit<Assessment, 'id' | 'doctorId'>) {
    const userData = this.getUserData(doctorId)
    const newAssessment: Assessment = {
      ...assessment,
      id: `assessment_${Date.now()}`,
      doctorId
    }
    userData.assessments.push(newAssessment)
    userData.stats.assessmentsToday++
    return newAssessment
  }

  static generateReport(doctorId: string, assessmentId: string, assessmentData: any): Report {
    const userData = this.getUserData(doctorId)
    const assessment = userData.assessments.find(a => a.id === assessmentId)
    const patient = userData.patients.find(p => p.id === assessment?.patientId)
    
    if (!assessment || !patient) {
      throw new Error('Assessment or patient not found')
    }

    const report: Report = {
      id: `report_${Date.now()}`,
      patientId: patient.id,
      doctorId,
      patientName: patient.name,
      assessmentDate: assessment.date,
      generatedDate: new Date().toISOString(),
      riskLevel: assessment.risk,
      predictedBoneLoss: assessment.boneLoss || 0,
      confidence: assessment.confidence || 0,
      mlModel: assessmentData.mlModel || 'advanced',
      cbctScan: assessmentData.cbctScan?.fileName || 'No scan uploaded',
      recommendations: this.generateRecommendations(assessment.risk, assessmentData),
      summary: this.generateSummary(patient, assessment, assessmentData),
      status: 'Generated',
      pdfUrl: `#reports/report_${Date.now()}.pdf` // Mock PDF URL
    }

    userData.reports.push(report)
    userData.stats.reportsGenerated++
    
    // Link report to assessment
    assessment.reportId = report.id
    
    return report
  }

  static getReports(doctorId: string): Report[] {
    const userData = this.getUserData(doctorId)
    return userData.reports.sort((a, b) => new Date(b.generatedDate).getTime() - new Date(a.generatedDate).getTime())
  }

  static getRecentReports(doctorId: string, limit: number = 5): Report[] {
    return this.getReports(doctorId).slice(0, limit)
  }

  static getPatientHistory(doctorId: string, patientId: string): { assessments: Assessment[], reports: Report[] } {
    const userData = this.getUserData(doctorId)
    const assessments = userData.assessments.filter(a => a.patientId === patientId)
    const reports = userData.reports.filter(r => r.patientId === patientId)
    return { assessments, reports }
  }

  private static generateMockReports(doctorId: string): Report[] {
    return [
      {
        id: `report_${doctorId}_1`,
        patientId: `patient_${doctorId}_1`,
        doctorId,
        patientName: 'John Doe',
        assessmentDate: '2024-01-15',
        generatedDate: '2024-01-15T14:30:00Z',
        riskLevel: 'High',
        predictedBoneLoss: 2.3,
        confidence: 94.2,
        mlModel: 'advanced',
        cbctScan: 'john_cbct_scan.dcm',
        recommendations: [
          'Consider immediate intervention to prevent bone loss',
          'Schedule follow-up assessment in 3 months',
          'Review smoking cessation program',
          'Monitor bone density closely'
        ],
        summary: 'Patient shows high risk of alveolar ridge resorption with predicted bone loss of 2.3mm over 5 years. Advanced ML model indicates 94.2% confidence in prediction. Immediate intervention recommended.',
        status: 'Generated',
        pdfUrl: '#reports/john_doe_report.pdf'
      },
      {
        id: `report_${doctorId}_2`,
        patientId: `patient_${doctorId}_2`,
        doctorId,
        patientName: 'Jane Smith',
        assessmentDate: '2024-01-14',
        generatedDate: '2024-01-14T11:45:00Z',
        riskLevel: 'Medium',
        predictedBoneLoss: 1.5,
        confidence: 87.5,
        mlModel: 'basic',
        cbctScan: 'jane_cbct_scan.dcm',
        recommendations: [
          'Schedule regular monitoring appointments',
          'Consider preventive treatments',
          'Maintain good oral hygiene',
          'Review dietary factors'
        ],
        summary: 'Patient shows moderate risk of bone loss with predicted 1.5mm loss over 5 years. Basic ML model indicates 87.5% confidence. Regular monitoring recommended.',
        status: 'Generated',
        pdfUrl: '#reports/jane_smith_report.pdf'
      }
    ]
  }

  private static generateRecommendations(riskLevel: string, assessmentData: any): string[] {
    const baseRecommendations = [
      'Maintain good oral hygiene',
      'Schedule regular dental check-ups'
    ]

    const riskSpecific = {
      High: [
        'Consider immediate intervention',
        'Schedule follow-up in 3 months',
        'Review lifestyle factors',
        'Monitor bone density closely'
      ],
      Medium: [
        'Schedule regular monitoring',
        'Consider preventive treatments',
        'Review dietary factors'
      ],
      Low: [
        'Continue routine care',
        'Monitor annually'
      ]
    }

    return [...baseRecommendations, ...(riskSpecific[riskLevel as keyof typeof riskSpecific] || [])]
  }

  private static generateSummary(patient: Patient, assessment: Assessment, assessmentData: any): string {
    return `Assessment completed for ${patient.name}, age ${patient.age}. Risk level: ${assessment.risk}. Predicted bone loss: ${assessment.boneLoss}mm over 5 years with ${assessment.confidence}% confidence. ${assessment.notes || ''}`
  }

  private static generateMockPatients(doctorId: string): Patient[] {
    const doctorName = this.getDoctorName(doctorId)
    return [
      {
        id: `patient_${doctorId}_1`,
        name: 'John Doe',
        age: 45,
        email: 'john@email.com',
        phone: '+1234567890',
        lastVisit: '2024-01-15',
        risk: 'High',
        status: 'Active',
        doctorId,
        createdAt: '2024-01-10T10:00:00Z'
      },
      {
        id: `patient_${doctorId}_2`,
        name: 'Jane Smith',
        age: 38,
        email: 'jane@email.com',
        phone: '+1234567891',
        lastVisit: '2024-01-14',
        risk: 'Medium',
        status: 'Active',
        doctorId,
        createdAt: '2024-01-09T14:30:00Z'
      },
      {
        id: `patient_${doctorId}_3`,
        name: 'Robert Johnson',
        age: 52,
        email: 'robert@email.com',
        phone: '+1234567892',
        lastVisit: '2024-01-13',
        risk: 'Low',
        status: 'Completed',
        doctorId,
        createdAt: '2024-01-08T09:15:00Z'
      }
    ]
  }

  private static generateMockAssessments(doctorId: string): Assessment[] {
    return [
      {
        id: `assessment_${doctorId}_1`,
        patientId: `patient_${doctorId}_1`,
        doctorId,
        type: 'Bone Loss Assessment',
        date: '2024-01-15',
        status: 'Completed',
        risk: 'High',
        boneLoss: 2.3,
        confidence: 94.2,
        riskScore: 85,
        notes: 'High risk of alveolar ridge resorption'
      },
      {
        id: `assessment_${doctorId}_2`,
        patientId: `patient_${doctorId}_2`,
        doctorId,
        type: 'Periodontal Evaluation',
        date: '2024-01-14',
        status: 'In Progress',
        risk: 'Medium',
        boneLoss: 1.5,
        confidence: 87.5,
        riskScore: 65,
        notes: 'Moderate risk factors identified'
      }
    ]
  }

  private static generateMockStats(doctorId: string): UserStats {
    // Different stats for different users to demonstrate dynamic data
    const userStats: Record<string, UserStats> = {
      '123': { // Sam's account
        totalPatients: 127,
        assessmentsToday: 8,
        highRiskCases: 3,
        reportsGenerated: 45
      },
      '456': { // Another user
        totalPatients: 89,
        assessmentsToday: 5,
        highRiskCases: 2,
        reportsGenerated: 32
      },
      '789': { // Third user
        totalPatients: 156,
        assessmentsToday: 12,
        highRiskCases: 5,
        reportsGenerated: 67
      }
    }
    return userStats[doctorId] || {
      totalPatients: 50,
      assessmentsToday: 3,
      highRiskCases: 1,
      reportsGenerated: 20
    }
  }

  private static getDoctorName(doctorId: string): string {
    const doctorNames: Record<string, string> = {
      '123': 'Dr. Sam Wilson',
      '456': 'Dr. Sarah Johnson',
      '789': 'Dr. Michael Chen'
    }
    return doctorNames[doctorId] || 'Doctor'
  }
}

export default UserDataService
