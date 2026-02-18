import styles from './BadgeGrid.module.css';
import type { Badge } from '../types';

interface BadgeGridProps {
  allBadges: Badge[];
  earnedBadgeIds: string[];
}

/**
 * BadgeGrid component displays a grid of all available badges.
 * Earned badges are shown with full styling (Faded Copper border).
 * Locked/unearned badges are shown with a grayed-out appearance.
 * 
 * @param allBadges - Array of all available badges
 * @param earnedBadgeIds - Array of badge IDs that the user has earned
 */
export default function BadgeGrid({ allBadges, earnedBadgeIds }: BadgeGridProps) {
  const isEarned = (badgeId: string): boolean => earnedBadgeIds.includes(badgeId);

  return (
    <div 
      className={styles.container}
      role="region"
      aria-label="Badge collection"
    >
      <div className={styles.grid} role="list">
        {allBadges.map((badge) => {
          const earned = isEarned(badge.id);
          
          return (
            <div
              key={badge.id}
              className={`${styles.badgeCard} ${earned ? styles.earned : styles.locked}`}
              role="listitem"
              aria-label={`${badge.name} badge${earned ? ' - earned' : ' - locked'}`}
            >
              {/* Badge Icon */}
              <div 
                className={styles.iconContainer}
                aria-hidden="true"
              >
                <span className={styles.icon}>
                  {earned ? badge.icon : '🔒'}
                </span>
              </div>

              {/* Badge Info */}
              <div className={styles.badgeInfo}>
                <h3 className={styles.badgeName}>
                  {badge.name}
                </h3>
                <p className={styles.badgeDescription}>
                  {badge.description}
                </p>
              </div>

              {/* Status Indicator */}
              <div className={styles.statusIndicator}>
                {earned ? (
                  <span className={styles.earnedBadge} aria-label="Earned">
                    ✓ Earned
                  </span>
                ) : (
                  <span className={styles.lockedBadge} aria-label="Locked">
                    Locked
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Empty State */}
      {allBadges.length === 0 && (
        <div className={styles.emptyState} role="status">
          <p>No badges available yet.</p>
        </div>
      )}
    </div>
  );
}
