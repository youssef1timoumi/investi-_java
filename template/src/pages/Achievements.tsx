import { useMemo } from 'react';
import PageLayout from '../components/PageLayout';
import PointsDisplay from '../components/PointsDisplay';
import BadgeGrid from '../components/BadgeGrid';
import QuizCard from '../components/QuizCard';
import { getBadges, getQuizzes, users } from '../data/mockData';
import styles from './Achievements.module.css';

/**
 * Achievements Page
 * 
 * Displays user achievements including points, badges, and available quizzes.
 * Requirements: 10.1, 10.2, 10.3
 */
export default function Achievements() {
  // Get current user data (using first user for demo)
  const currentUser = users[0];
  
  // Get all badges and quizzes from mock data
  const allBadges = useMemo(() => getBadges(), []);
  const quizzes = useMemo(() => getQuizzes(), []);

  // Calculate achievement statistics
  const stats = useMemo(() => {
    const totalBadges = allBadges.length;
    const earnedBadges = currentUser.badges.length;
    const totalQuizzes = quizzes.length;
    const completedQuizzes = quizzes.filter(q => q.isCompleted).length;
    const totalPoints = currentUser.points;
    
    return { totalBadges, earnedBadges, totalQuizzes, completedQuizzes, totalPoints };
  }, [allBadges, quizzes, currentUser]);

  return (
    <PageLayout backgroundColor="light">
      <div className={styles.achievementsPage}>
        <div className={styles.container}>
          {/* Header Section */}
          <header className={styles.headerSection}>
            <h1 className={styles.pageTitle}>Achievements</h1>
            <p className={styles.pageDescription}>
              Track your progress, earn badges, and test your knowledge with quizzes.
              Your journey on the platform is rewarded!
            </p>
          </header>

          {/* Points Display Section */}
          <section 
            className={styles.pointsSection}
            aria-label="Points and level"
          >
            <PointsDisplay points={currentUser.points} />
          </section>

          {/* Stats Summary */}
          <section 
            className={styles.statsSummary}
            aria-label="Achievement statistics"
          >
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">{stats.totalPoints}</span>
              <span className={styles.statLabel}>Total Points</span>
              <span className="sr-only">{stats.totalPoints} total points</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">
                {stats.earnedBadges}/{stats.totalBadges}
              </span>
              <span className={styles.statLabel}>Badges Earned</span>
              <span className="sr-only">{stats.earnedBadges} of {stats.totalBadges} badges earned</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue} aria-hidden="true">
                {stats.completedQuizzes}/{stats.totalQuizzes}
              </span>
              <span className={styles.statLabel}>Quizzes Completed</span>
              <span className="sr-only">{stats.completedQuizzes} of {stats.totalQuizzes} quizzes completed</span>
            </div>
          </section>

          {/* Badges Section */}
          <section 
            className={styles.badgesSection}
            aria-labelledby="badges-heading"
          >
            <h2 id="badges-heading" className={styles.sectionTitle}>
              <span className={styles.sectionIcon} aria-hidden="true">🏅</span>
              Your Badges
            </h2>
            <p className={styles.sectionDescription}>
              Collect badges by participating in the platform. Locked badges show what you can earn next!
            </p>
            <BadgeGrid 
              allBadges={allBadges} 
              earnedBadgeIds={currentUser.badges} 
            />
          </section>

          {/* Quizzes Section */}
          <section 
            className={styles.quizzesSection}
            aria-labelledby="quizzes-heading"
          >
            <h2 id="quizzes-heading" className={styles.sectionTitle}>
              <span className={styles.sectionIcon} aria-hidden="true">📝</span>
              Available Quizzes
            </h2>
            <p className={styles.sectionDescription}>
              Test your knowledge and earn points! Complete quizzes to boost your level.
            </p>
            {quizzes.length > 0 ? (
              <div className={styles.quizzesGrid}>
                {quizzes.map((quiz) => (
                  <QuizCard key={quiz.id} quiz={quiz} />
                ))}
              </div>
            ) : (
              <div className={styles.emptyState} role="status">
                <span className={styles.emptyIcon} aria-hidden="true">📝</span>
                <h3 className={styles.emptyTitle}>No Quizzes Available</h3>
                <p className={styles.emptyDescription}>
                  Check back later for new quizzes to complete and earn points!
                </p>
              </div>
            )}
          </section>
        </div>
      </div>
    </PageLayout>
  );
}
