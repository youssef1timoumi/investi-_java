import { useState } from 'react';
import type { Idea } from '../types';
import Card from './Card';
import Button from './Button';
import styles from './IdeaCard.module.css';

interface IdeaCardProps {
  idea: Idea;
  variant: 'investor' | 'innovator';
  onCollaborate?: () => void;
  onInvest?: () => void;
}

export default function IdeaCard({ idea, variant, onCollaborate, onInvest }: IdeaCardProps) {
  const [isCollaborating, setIsCollaborating] = useState(false);

  const handleCollaborate = () => {
    setIsCollaborating(true);
    onCollaborate?.();
  };

  const getStatusLabel = (status: Idea['status']): string => {
    switch (status) {
      case 'open':
        return 'Open';
      case 'in-collaboration':
        return 'In Collaboration';
      case 'funded':
        return 'Funded';
      case 'project':
        return 'Project';
      default:
        return status;
    }
  };

  const getStatusClass = (status: Idea['status']): string => {
    switch (status) {
      case 'open':
        return styles.statusOpen;
      case 'in-collaboration':
        return styles.statusInCollaboration;
      case 'funded':
        return styles.statusFunded;
      case 'project':
        return styles.statusProject;
      default:
        return '';
    }
  };

  return (
    <Card variant="light" className={styles.ideaCard}>
      <div className={styles.header}>
        <h3 className={styles.title}>{idea.title}</h3>
        <span className={`${styles.status} ${getStatusClass(idea.status)}`}>
          {getStatusLabel(idea.status)}
        </span>
      </div>

      <p className={styles.description}>{idea.description}</p>

      <div className={styles.meta}>
        <span className={styles.author}>By {idea.authorName}</span>
        <span className={styles.investors}>
          {idea.investorCount} investor{idea.investorCount !== 1 ? 's' : ''}
        </span>
      </div>

      {idea.tags.length > 0 && (
        <div className={styles.tags} role="list" aria-label="Tags">
          {idea.tags.map((tag) => (
            <span key={tag} className={styles.tag} role="listitem">
              {tag}
            </span>
          ))}
        </div>
      )}

      {/* Collaboration status indicator */}
      {(idea.status === 'in-collaboration' || isCollaborating) && (
        <div className={styles.collaborationIndicator} aria-live="polite">
          <span className={styles.indicatorDot} aria-hidden="true" />
          <span>Active Collaboration</span>
        </div>
      )}

      <div className={styles.actions}>
        {variant === 'investor' && (
          <Button
            variant={isCollaborating ? 'secondary' : 'primary'}
            onClick={handleCollaborate}
            disabled={isCollaborating || idea.status === 'funded' || idea.status === 'project'}
            aria-label={isCollaborating ? 'Added to collaboration' : `Add ${idea.title} to collaboration`}
          >
            {isCollaborating ? 'Added to Collaboration' : 'Add to Collaboration'}
          </Button>
        )}

        {variant === 'innovator' && (
          <Button
            variant="primary"
            onClick={onInvest}
            disabled={idea.status === 'funded' || idea.status === 'project'}
            aria-label={`Invest in ${idea.title}`}
          >
            Invest
          </Button>
        )}
      </div>
    </Card>
  );
}
