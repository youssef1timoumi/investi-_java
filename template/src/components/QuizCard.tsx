import type { Quiz } from '../types';
import Card from './Card';
import Button from './Button';
import styles from './QuizCard.module.css';

interface QuizCardProps {
  quiz: Quiz;
}

/**
 * QuizCard component displays a quiz with its title, description,
 * point value, question count, and completion status.
 * Includes a visual-only "Start Quiz" button (disabled but styled).
 * 
 * @param quiz - The quiz data to display
 */
export default function QuizCard({ quiz }: QuizCardProps) {
  const { title, description, pointsReward, questionCount, isCompleted } = quiz;

  return (
    <Card 
      variant="light" 
      className={styles.quizCard}
      role="article"
      aria-label={`Quiz: ${title}${isCompleted ? ' - Completed' : ''}`}
    >
      {/* Header with title and points */}
      <div className={styles.header}>
        <h3 className={styles.title}>{title}</h3>
        <div 
          className={styles.pointsBadge}
          aria-label={`${pointsReward} points reward`}
        >
          <span className={styles.pointsIcon} aria-hidden="true">⭐</span>
          <span className={styles.pointsValue}>{pointsReward} pts</span>
        </div>
      </div>

      {/* Description */}
      <p className={styles.description}>{description}</p>

      {/* Quiz info */}
      <div className={styles.info}>
        <span className={styles.questionCount}>
          <span className={styles.infoIcon} aria-hidden="true">❓</span>
          {questionCount} question{questionCount !== 1 ? 's' : ''}
        </span>
      </div>

      {/* Completion status */}
      {isCompleted && (
        <div 
          className={styles.completedBadge}
          role="status"
          aria-label="Quiz completed"
        >
          <span className={styles.completedIcon} aria-hidden="true">✓</span>
          <span>Completed</span>
        </div>
      )}

      {/* Action button */}
      <div className={styles.actions}>
        <Button
          variant="primary"
          disabled
          aria-label={
            isCompleted 
              ? `${title} quiz already completed` 
              : `Start ${title} quiz - visual only`
          }
          className={styles.startButton}
        >
          {isCompleted ? 'Completed' : 'Start Quiz'}
        </Button>
      </div>
    </Card>
  );
}
