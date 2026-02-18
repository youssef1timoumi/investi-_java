import styles from './ProgressBar.module.css';

interface ProgressBarProps {
  value: number;
  max: number;
  color?: 'primary' | 'accent';
  label?: string;
}

export default function ProgressBar({
  value,
  max,
  color = 'primary',
  label,
}: ProgressBarProps) {
  const percentage = Math.min(Math.max((value / max) * 100, 0), 100);

  return (
    <div className={styles.container}>
      {label && <span className={styles.label}>{label}</span>}
      <div
        className={styles.track}
        role="progressbar"
        aria-valuenow={value}
        aria-valuemin={0}
        aria-valuemax={max}
        aria-label={label || 'Progress'}
      >
        <div
          className={`${styles.fill} ${styles[color]}`}
          style={{ width: `${percentage}%` }}
        />
      </div>
      <span className={styles.percentage}>{Math.round(percentage)}%</span>
    </div>
  );
}
