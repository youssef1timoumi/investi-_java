import { useState } from 'react';
import type { Course } from '../types';
import Card from './Card';
import Button from './Button';
import styles from './CourseCard.module.css';

interface CourseCardProps {
  course: Course;
  onEnroll: () => void;
  isEnrolled: boolean;
}

export default function CourseCard({ course, onEnroll, isEnrolled: initialIsEnrolled }: CourseCardProps) {
  const [isEnrolled, setIsEnrolled] = useState(initialIsEnrolled);
  const [isAnimating, setIsAnimating] = useState(false);

  const handleEnroll = () => {
    if (!isEnrolled) {
      setIsAnimating(true);
      setIsEnrolled(true);
      onEnroll();
      // Reset animation state after animation completes
      setTimeout(() => setIsAnimating(false), 300);
    }
  };

  return (
    <Card variant="light" className={styles.courseCard}>
      <div className={styles.imageContainer}>
        <img
          src={course.imageUrl}
          alt={`${course.title} course`}
          className={styles.image}
          onError={(e) => {
            // Fallback for missing images
            (e.target as HTMLImageElement).src = '/placeholder-course.jpg';
          }}
        />
        {isEnrolled && (
          <span className={styles.enrolledBadge} aria-label="Enrolled in course">
            Enrolled
          </span>
        )}
      </div>

      <div className={styles.content}>
        <h3 className={styles.title}>{course.title}</h3>
        
        <div className={styles.duration}>
          <span className={styles.durationIcon} aria-hidden="true">⏱️</span>
          <span>{course.duration}</span>
        </div>

        <p className={styles.description}>{course.description}</p>

        <div className={styles.instructor}>
          <span className={styles.instructorIcon} aria-hidden="true">👤</span>
          <span>Instructor: {course.instructor}</span>
        </div>

        <div className={styles.enrolledCount}>
          <span className={styles.enrolledCountText}>
            {isEnrolled ? (
              <span className={styles.enrolledNumber}>
                {course.enrolledCount + 1} students enrolled
              </span>
            ) : (
              <span>
                {course.enrolledCount} students enrolled
              </span>
            )}
          </span>
        </div>

        <div className={styles.actions}>
          <Button
            variant={isEnrolled ? 'secondary' : 'primary'}
            onClick={handleEnroll}
            disabled={isEnrolled}
            aria-label={
              isEnrolled
                ? `Already enrolled in ${course.title}`
                : `Enroll in ${course.title}`
            }
            className={isAnimating ? styles.animating : ''}
          >
            {isEnrolled ? (
              <>
                <span className={styles.checkIcon} aria-hidden="true">✓</span>
                Enrolled
              </>
            ) : (
              'Enroll Now'
            )}
          </Button>
        </div>

        {/* Visual feedback indicator */}
        {isEnrolled && (
          <div className={styles.enrollmentFeedback} aria-live="polite">
            <span className={styles.feedbackIcon} aria-hidden="true">✓</span>
            <span>You're enrolled in this course!</span>
          </div>
        )}
      </div>
    </Card>
  );
}
