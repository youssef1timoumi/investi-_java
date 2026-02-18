import type { Project } from '../types';
import Card from './Card';
import ProgressBar from './ProgressBar';
import styles from './ProjectCard.module.css';

interface ProjectCardProps {
  project: Project;
}

export default function ProjectCard({ project }: ProjectCardProps) {
  const getStatusLabel = (status: Project['status']): string => {
    switch (status) {
      case 'planning':
        return 'Planning';
      case 'in-progress':
        return 'In Progress';
      case 'completed':
        return 'Completed';
      default:
        return status;
    }
  };

  const getStatusClass = (status: Project['status']): string => {
    switch (status) {
      case 'planning':
        return styles.statusPlanning;
      case 'in-progress':
        return styles.statusInProgress;
      case 'completed':
        return styles.statusCompleted;
      default:
        return '';
    }
  };

  // Format date for display
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <Card 
      variant="light" 
      className={styles.projectCard}
      role="article"
      aria-label={`Project: ${project.title}`}
    >
      <div className={styles.header}>
        <h3 className={styles.title}>{project.title}</h3>
        <span 
          className={`${styles.status} ${getStatusClass(project.status)}`}
          role="status"
          aria-label={`Status: ${getStatusLabel(project.status)}`}
        >
          {getStatusLabel(project.status)}
        </span>
      </div>

      <p className={styles.description}>{project.description}</p>

      {/* Progress Section with Faded Copper color */}
      <div className={styles.progressSection}>
        <ProgressBar
          value={project.progress}
          max={100}
          color="accent"
          label="Progress"
        />
      </div>

      {/* Team Members */}
      <div className={styles.teamSection}>
        <span className={styles.teamLabel}>Team Members</span>
        <ul className={styles.teamList} aria-label="Team members">
          {project.teamMembers.map((member, index) => (
            <li key={index} className={styles.teamMember}>
              <span className={styles.memberAvatar} aria-hidden="true">
                {member.charAt(0).toUpperCase()}
              </span>
              <span className={styles.memberName}>{member}</span>
            </li>
          ))}
        </ul>
      </div>

      {/* Start Date */}
      <div className={styles.meta}>
        <time 
          className={styles.startDate} 
          dateTime={project.startDate}
          aria-label={`Started on ${formatDate(project.startDate)}`}
        >
          <span className={styles.dateIcon} aria-hidden="true">📅</span>
          Started {formatDate(project.startDate)}
        </time>
      </div>
    </Card>
  );
}
