import { useMemo } from 'react';
import PageLayout from '../components/PageLayout';
import ProjectCard from '../components/ProjectCard';
import { getProjects } from '../data/mockData';
import styles from './Projects.module.css';

/**
 * Projects Page
 * 
 * Displays projects that originated from collaborations with progress/status visuals.
 * Requirements: 9.1, 9.2
 */
export default function Projects() {
  const projects = useMemo(() => getProjects(), []);

  // Calculate project statistics
  const stats = useMemo(() => {
    const total = projects.length;
    const completed = projects.filter(p => p.status === 'completed').length;
    const inProgress = projects.filter(p => p.status === 'in-progress').length;
    const planning = projects.filter(p => p.status === 'planning').length;
    
    return { total, completed, inProgress, planning };
  }, [projects]);

  return (
    <PageLayout backgroundColor="light">
      <div className={styles.projectsPage}>
        <div className={styles.container}>
          {/* Header Section */}
          <header className={styles.headerSection}>
            <h1 className={styles.pageTitle}>Projects</h1>
            <p className={styles.pageDescription}>
              Explore successful collaborations that have transformed from ideas into active projects.
              Track their progress and see innovation in action.
            </p>
          </header>

          {/* Stats Summary */}
          <section 
            className={styles.statsSummary}
            aria-label="Project statistics"
          >
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">{stats.total}</span>
              <span className={styles.statLabel}>Total Projects</span>
              <span className="sr-only">{stats.total} total projects</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">{stats.completed}</span>
              <span className={styles.statLabel}>Completed</span>
              <span className="sr-only">{stats.completed} completed projects</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">{stats.inProgress}</span>
              <span className={styles.statLabel}>In Progress</span>
              <span className="sr-only">{stats.inProgress} projects in progress</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">{stats.planning}</span>
              <span className={styles.statLabel}>Planning</span>
              <span className="sr-only">{stats.planning} projects in planning</span>
            </div>
          </section>

          {/* Projects Grid */}
          {projects.length > 0 ? (
            <section 
              className={styles.projectsGrid}
              aria-label="Projects list"
              role="region"
            >
              {projects.map((project) => (
                <ProjectCard key={project.id} project={project} />
              ))}
            </section>
          ) : (
            <div className={styles.emptyState} role="status">
              <span className={styles.emptyIcon} aria-hidden="true">📊</span>
              <h2 className={styles.emptyTitle}>No Projects Yet</h2>
              <p className={styles.emptyDescription}>
                Projects will appear here once collaborations are converted into active initiatives.
              </p>
            </div>
          )}
        </div>
      </div>
    </PageLayout>
  );
}
