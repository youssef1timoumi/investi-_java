import { useNavigate } from 'react-router-dom';
import PageLayout from '../components/PageLayout';
import MetricCard from '../components/MetricCard';
import { getDashboardMetrics } from '../data/mockData';
import styles from './AdminDashboard.module.css';

export default function AdminDashboard() {
  const navigate = useNavigate();
  const metrics = getDashboardMetrics();

  const handleLogout = () => {
    navigate('/');
  };

  return (
    <PageLayout
      backgroundColor="dark"
      isLoggedIn={true}
      currentRole="admin"
      onLogoutClick={handleLogout}
    >
      <div className={styles.dashboard}>
        <header className={styles.header}>
          <h1 className={styles.title}>Admin Dashboard</h1>
          <p className={styles.subtitle}>Platform Overview</p>
        </header>

        <section className={styles.metricsSection} aria-label="Platform Metrics">
          <div className={styles.metricsGrid}>
            <MetricCard
              title="Total Users"
              value={metrics.usersCount}
              icon="👥"
              trend="up"
            />
            <MetricCard
              title="Ideas"
              value={metrics.ideasCount}
              icon="💡"
              trend="up"
            />
            <MetricCard
              title="Events"
              value={metrics.eventsCount}
              icon="📅"
              trend="neutral"
            />
          </div>
        </section>

        <section className={styles.additionalMetrics} aria-label="Additional Metrics">
          <h2 className={styles.sectionTitle}>Additional Metrics</h2>
          <div className={styles.metricsGrid}>
            <MetricCard
              title="Active Projects"
              value={metrics.projectsCount}
              icon="📊"
              trend="up"
            />
            <MetricCard
              title="Collaborations"
              value={metrics.activeCollaborations}
              icon="🤝"
              trend="up"
            />
          </div>
        </section>
      </div>
    </PageLayout>
  );
}
