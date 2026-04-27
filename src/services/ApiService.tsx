// API Service for connecting to Flask backend
const API_BASE_URL = '/api'

export interface ApiResponse<T> {
  data?: T
  error?: string
  message?: string
}

class ApiService {
  private static async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    try {
      // Add timeout to prevent hanging requests
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), 10000) // 10 second timeout

      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        headers: {
          'Content-Type': 'application/json',
          ...options.headers,
        },
        signal: controller.signal,
        ...options,
      })

      clearTimeout(timeoutId)

      // Check if response is ok
      if (!response.ok) {
        const errorText = await response.text()
        console.error(`API Error ${response.status}:`, errorText)
        return { error: `Server error: ${response.status}` }
      }

      // Try to parse JSON, but handle cases where response isn't JSON
      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('application/json')) {
        const data = await response.json()
        return { data }
      } else {
        const text = await response.text()
        console.log('Non-JSON response:', text)
        return { error: 'Invalid response format from server' }
      }
    } catch (error) {
      console.error('API request error:', error)
      if (error instanceof Error) {
        if (error.name === 'AbortError') {
          return { error: 'Request timeout - server not responding' }
        }
        return { error: error.message }
      }
      return { error: 'Network error - unable to connect to server' }
    }
  }

  // Authentication endpoints
  static async login(email: string, password: string) {
    return this.request<{
      id: string
      name: string
      email: string
      token?: string
    }>('/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })
  }

  static async register(userData: {
    name: string
    email: string
    password: string
    role?: string
  }) {
    return this.request('/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    })
  }

  // Patient endpoints
  static async getPatients(doctorId: string) {
    return this.request('/patients-with-risk', {
      method: 'GET',
    })
  }

  static async addPatient(patientData: {
    name: string
    age: number
    email: string
    phone: string
    lastVisit: string
    risk: string
    status: string
    doctorId: string
  }) {
    return this.request('/patients', {
      method: 'POST',
      body: JSON.stringify(patientData),
    })
  }

  static async getPatient(patientId: string) {
    return this.request(`/patients/${patientId}`, {
      method: 'GET',
    })
  }

  // Assessment endpoints
  static async getAssessments(doctorId: string) {
    return this.request('/clinical-data', {
      method: 'GET',
    })
  }

  static async addAssessment(assessmentData: {
    patientId: string
    doctorId: string
    date: string
    type: string
    risk: string
    status: string
    boneLoss?: number
    confidence?: number
    notes?: string
  }) {
    return this.request('/clinical-data', {
      method: 'POST',
      body: JSON.stringify(assessmentData),
    })
  }

  static async getAssessment(patientId: string) {
    return this.request(`/clinical-data/${patientId}`, {
      method: 'GET',
    })
  }

  // Prediction endpoints
  static async submitPrediction(predictionData: {
    age: number
    smoking: string
    diabetes: string
    medications: string
    gumHealth: string
    pocketDepth: number
    bleeding: string
    cbctScan?: string
  }) {
    return this.request('/predictions', {
      method: 'POST',
      body: JSON.stringify(predictionData),
    })
  }

  static async getPredictions(doctorId?: string) {
    const endpoint = doctorId ? `/predictions?doctorId=${doctorId}` : '/predictions'
    return this.request(endpoint, {
      method: 'GET',
    })
  }

  // Report endpoints
  static async generateReport(patientId: string, reportData: any) {
    return this.request(`/patients/${patientId}/report`, {
      method: 'GET',
    })
  }

  static async getPatientReport(patientId: string) {
    return this.request(`/patients/${patientId}/report`, {
      method: 'GET',
    })
  }

  static async getReports(doctorId: string) {
    return this.request('/predictions', {
      method: 'GET',
    })
  }

  // File upload endpoint
  static async uploadFile(file: File, type: string = 'cbct') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)

    try {
      const response = await fetch(`${API_BASE_URL}/upload`, {
        method: 'POST',
        body: formData,
      })

      if (!response.ok) {
        throw new Error('File upload failed')
      }

      const data = await response.json()
      return { data }
    } catch (error) {
      console.error('File upload error:', error)
      return { error: error instanceof Error ? error.message : 'Upload failed' }
    }
  }

  // Stats endpoint
  static async getUserStats(doctorId: string) {
    return this.request(`/stats?doctorId=${doctorId}`, {
      method: 'GET',
    })
  }

  // OTP endpoints
  static async sendOTP(email: string) {
    return this.request('/send-otp', {
      method: 'POST',
      body: JSON.stringify({ email }),
    })
  }

  static async verifyOTP(email: string, otp: string) {
    return this.request('/verify-otp', {
      method: 'POST',
      body: JSON.stringify({ email, otp }),
    })
  }

  static async resetPassword(email: string, newPassword: string) {
    return this.request('/reset-password', {
      method: 'POST',
      body: JSON.stringify({ email, password: newPassword }),
    })
  }
}

export default ApiService
