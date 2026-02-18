import styles from './PointsDisplay.module.css';

interface PointsDisplayProps {
  points: number;
}

/**
 * Calculate level based on points
 * Level 1: 0-100 points
 * Level 2: 101-250 points
 * Level 3: 251-500 points
 * Level 4: 501-1000 points
 * Level 5: 1001+ points
 */
function calculateLevel(points: number): { level: number; nextLevelPoints: number; currentLevelMin: number } {
  if (points <= 100) {
    return { level: 1, nextLevelPoints: 101, currentLevelMin: 0 };
  }
  if (points <= 250) {
    return { level: 2, nextLevelPoints: 251, currentLevelMin: 101 };
  }
  if (points <= 500) {
    return { level: 3, nextLevelPoints: 501, currentLevelMin: 251 };
  }
  if (points <= 1000) {
    return { level: 4, nextLevelPoints: 1001, currentLevelMin: 501 };
  }
  return { level: 5, nextLevelPoints: Infinity, currentLevelMin: 1001 };
}

function formatPoints(points: number): string {
  if (points >= 1000000) {
    return `${(points / 1000000).toFixed(1)}M`;
  }
  if (points >= 10000) {
    return `${(points / 1000).toFixed(1)}K`;
  }
  return points.toLocaleString();
}

export default function PointsDisplay({ points }: PointsDisplayProps) {
  const { level, nextLevelPoints, currentLevelMin } = calculateLevel(points);
  const isMaxLevel = nextLevelPoints === Infinity;
  
  // Calculate progress to next level
  const progressToNextLevel = isMaxLevel
    ? 100
    : Math.round(((points - currentLevelMin) / (nextLevelPoints - currentLevelMin)) * 100);

  return (
    <div className={styles.container} role="region" aria-label="Points and level display">
      {/* Points Counter */}
      <div className={styles.pointsSection}>
        <span className={styles.pointsLabel}>Total Points</span>
        <span 
          className={styles.pointsValue}
          aria-label={`${points} points`}
        >
          {formatPoints(points)}
        </span>
      </div>

      {/* Level Indicator */}
      <div className={styles.levelSection}>
        <div 
          className={styles.levelBadge}
          role="status"
          aria-label={`Level ${level}`}
        >
          <span className={styles.levelIcon} aria-hidden="true">★</span>
          <span className={styles.levelText}>Level {level}</span>
        </div>
        
        {/* Progress to next level */}
        <div className={styles.progressSection}>
          <div 
            className={styles.progressTrack}
            role="progressbar"
            aria-valuenow={progressToNextLevel}
            aria-valuemin={0}
            aria-valuemax={100}
            aria-label={isMaxLevel ? 'Maximum level reached' : `Progress to level ${level + 1}`}
          >
            <div 
              className={styles.progressFill}
              style={{ width: `${progressToNextLevel}%` }}
            />
          </div>
          <span className={styles.progressText}>
            {isMaxLevel 
              ? 'Max Level!' 
              : `${nextLevelPoints - points} pts to Level ${level + 1}`
            }
          </span>
        </div>
      </div>
    </div>
  );
}
