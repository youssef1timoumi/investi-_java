import styles from './VoteButtons.module.css';

interface VoteButtonsProps {
  upvotes: number;
  downvotes: number;
  userVote: 'up' | 'down' | null;
  onUpvote: () => void;
  onDownvote: () => void;
}

export default function VoteButtons({
  upvotes,
  downvotes,
  userVote,
  onUpvote,
  onDownvote,
}: VoteButtonsProps) {
  const score = upvotes - downvotes;

  return (
    <div className={styles.voteButtons} role="group" aria-label="Vote buttons">
      <button
        type="button"
        className={`${styles.voteButton} ${styles.upvote} ${userVote === 'up' ? styles.active : ''}`}
        onClick={onUpvote}
        aria-label={`Upvote, current upvotes: ${upvotes}`}
        aria-pressed={userVote === 'up'}
      >
        <svg
          className={styles.voteIcon}
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          aria-hidden="true"
        >
          <path d="M12 19V5M5 12l7-7 7 7" />
        </svg>
      </button>

      <span className={styles.score} aria-label={`Score: ${score}`}>
        {score}
      </span>

      <button
        type="button"
        className={`${styles.voteButton} ${styles.downvote} ${userVote === 'down' ? styles.active : ''}`}
        onClick={onDownvote}
        aria-label={`Downvote, current downvotes: ${downvotes}`}
        aria-pressed={userVote === 'down'}
      >
        <svg
          className={styles.voteIcon}
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          aria-hidden="true"
        >
          <path d="M12 5v14M5 12l7 7 7-7" />
        </svg>
      </button>
    </div>
  );
}
