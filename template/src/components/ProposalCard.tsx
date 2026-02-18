import type { Proposal } from '../types';
import Card from './Card';
import Button from './Button';
import styles from './ProposalCard.module.css';

interface ProposalCardProps {
  proposal: Proposal;
  investorCount: number;
  onChatClick: () => void;
}

export default function ProposalCard({ proposal, investorCount, onChatClick }: ProposalCardProps) {
  const getStatusLabel = (status: Proposal['status']): string => {
    switch (status) {
      case 'pending':
        return 'Pending';
      case 'active':
        return 'Active';
      case 'converted':
        return 'Converted';
      default:
        return status;
    }
  };

  const getStatusClass = (status: Proposal['status']): string => {
    switch (status) {
      case 'pending':
        return styles.statusPending;
      case 'active':
        return styles.statusActive;
      case 'converted':
        return styles.statusConverted;
      default:
        return '';
    }
  };

  // Calculate interest level for visual indicator
  const getInterestLevel = (interest: number): 'low' | 'medium' | 'high' => {
    if (interest >= 70) return 'high';
    if (interest >= 40) return 'medium';
    return 'low';
  };

  const interestLevel = getInterestLevel(proposal.investorInterest);

  return (
    <Card variant="light" className={styles.proposalCard}>
      <div className={styles.header}>
        <h3 className={styles.title}>{proposal.title}</h3>
        <span className={`${styles.status} ${getStatusClass(proposal.status)}`}>
          {getStatusLabel(proposal.status)}
        </span>
      </div>

      <p className={styles.description}>{proposal.description}</p>

      <div className={styles.meta}>
        <span className={styles.author}>By {proposal.authorName}</span>
        <span className={styles.investors}>
          {investorCount} investor{investorCount !== 1 ? 's' : ''} interested
        </span>
      </div>

      {/* Investor Interest Indicator/Meter */}
      <div className={styles.interestSection}>
        <div className={styles.interestHeader}>
          <span className={styles.interestLabel}>Investor Interest</span>
          <span className={styles.interestValue}>{proposal.investorInterest}%</span>
        </div>
        <div
          className={styles.interestMeter}
          role="meter"
          aria-valuenow={proposal.investorInterest}
          aria-valuemin={0}
          aria-valuemax={100}
          aria-label={`Investor interest: ${proposal.investorInterest}%`}
        >
          <div
            className={`${styles.interestFill} ${styles[interestLevel]}`}
            style={{ width: `${proposal.investorInterest}%` }}
          />
        </div>
        <span className={`${styles.interestLevelBadge} ${styles[interestLevel]}`}>
          {interestLevel === 'high' && '🔥 High Interest'}
          {interestLevel === 'medium' && '📈 Growing Interest'}
          {interestLevel === 'low' && '🌱 Early Stage'}
        </span>
      </div>

      {/* Chat Entry Point Button */}
      <div className={styles.actions}>
        <Button
          variant="primary"
          onClick={onChatClick}
          aria-label={`Start chat about ${proposal.title}`}
        >
          <span className={styles.chatIcon} aria-hidden="true">💬</span>
          Start Chat
        </Button>
      </div>
    </Card>
  );
}
