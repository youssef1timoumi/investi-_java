import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Homepage from './pages/Homepage';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import InvestorView from './pages/InvestorView';
import InnovatorView from './pages/InnovatorView';
import Forum from './pages/Forum';
import Events from './pages/Events';
import Collaboration from './pages/Collaboration';
import Projects from './pages/Projects';
import Achievements from './pages/Achievements';
import NotFound from './pages/NotFound';
import ErrorBoundary from './components/ErrorBoundary';

function App() {
  return (
    <BrowserRouter>
      <ErrorBoundary>
        <Routes>
          <Route path="/" element={<Homepage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/investor" element={<InvestorView />} />
          <Route path="/innovator" element={<InnovatorView />} />
          <Route path="/forum" element={<Forum />} />
          <Route path="/events" element={<Events />} />
          <Route path="/collaboration" element={<Collaboration />} />
          <Route path="/projects" element={<Projects />} />
          <Route path="/achievements" element={<Achievements />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </ErrorBoundary>
    </BrowserRouter>
  );
}

export default App;
