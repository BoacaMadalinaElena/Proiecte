import React from 'react';
import { BrowserRouter as Router, Route ,Routes} from 'react-router-dom';


import Login from './components/LoginComponent/Login';
import HomePatient from './components/HomePatient/HomePatient';
import HomeDoctor from './components/HomeDoctor/HomeDoctor';
import DoctorList from './components/DoctorList/DoctorList';
import CreateAppointment from './components/CreateAppointment/CreateAppointment';
import AppointmentsList from './components/AppointmentsList/AppointmentsList';
import PatientsDoctor from './components/PatientsDoctor/PatientsDoctor';
import AppointmentsListDoctor from './components/AppointmentsListDoctor/AppointmentsListDoctor';
import ConsultationDoctorComponent from './components/ConsultationDoctorComponent/ConsultationDoctorComponent';
import CreateConsutation from './components/CreateConsutation/CreateConsutation';
import IstoricMedicalPacient from './components/IstoricMedicalPacient/IstoricMedicalPacient';

const App = () => {
  return (
    <Router>
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Login />} />
      <Route path="/homePatient" element={<HomePatient />} />
      <Route path="/homeDoctor" element={<HomeDoctor />} />
      <Route path="/doctorList" element={<DoctorList />} />
      <Route path="/createAppointment" element={<CreateAppointment />} />
      <Route path="/appointmentsList" element={<AppointmentsList />} />
      <Route path="/patientsDoctor" element={<PatientsDoctor />} />
      <Route path="/appointmentsListDoctor" element={<AppointmentsListDoctor />} />
      <Route path="/consultationDoctorComponent" element={<ConsultationDoctorComponent />} />
      <Route path="/createConsulation" element={<CreateConsutation />} />
      <Route path="/istoricMedicalPacient" element={<IstoricMedicalPacient />} />
    </Routes>
  </Router>
  );
};

export default App;
