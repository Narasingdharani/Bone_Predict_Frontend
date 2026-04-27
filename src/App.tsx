import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import './App.css'
import './styles.css'

// Import components
import Splash from './components/Splash'
import Login from './components/Login'
import Register from './components/Register'
import Dashboard from './components/Dashboard'
import Patients from './components/Patients'
import AddPatient from './components/AddPatient'
import ConnectionTest from './components/ConnectionTest'
import RiskAssessment from './components/RiskAssessment'
import Periodontal from './components/Periodontal'
import CBCT from './components/CBCT'
import Demographics from './components/Demographics'
import ResultSummary from './components/ResultSummary'
import ReportPreview from './components/ReportPreview'

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/" element={<Splash />} />
            <Route path="/splash" element={<Splash />} />
            <Route path="/welcome" element={<Splash />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/patients" element={<Patients />} />
            <Route path="/add-patient" element={<AddPatient />} />
            <Route path="/assessment/risk" element={<RiskAssessment />} />
            <Route path="/assessment/periodontal" element={<Periodontal />} />
            <Route path="/assessment/cbct" element={<CBCT />} />
            <Route path="/assessment/demographics" element={<Demographics />} />
            <Route path="/assessment/summary" element={<ResultSummary />} />
            <Route path="/report/preview" element={<ReportPreview />} />
            <Route path="/test-connection" element={<ConnectionTest />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  )
}

export default App
