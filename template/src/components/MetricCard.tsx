import type { ReactNode } from 'react';
import Card from './Card';
import styles from './MetricCard.module.css';

interface MetricCardProps {
  title: string;
  value: number;
  icon: ReactNode;
  trend?: 'up' | 'down' | 'neutral';
}

export default function MetricCard({ title, value, icon, trend }: MetricCardProps) {
  const formatValue = (num: number): string => {
    if (num >= 1000000) {
      return `${(num / 1000000).toFixed(1)}M`;
    }
    if (num >= 1000) {
      return `${(num / 1000).toFixed(1)}K`;
    }
    return num.toLocaleString();
  };

  const getTrendIcon = () => {
    switch (trend) {
      case 'up':
        return '↑';
      case 'down':
        return '↓';
      case 'neutral':
        return '→';
      default:
        return null;
    }
  };

  const trendIcon = getTrendIcon();

  return (
    <Card variant="dark" className={styles.metricCard}>
      <div className={styles.header}>
        <span className={styles.icon} aria-hidden="true">{icon}</span>
        <h3 className={styles.title}>{title}</h3>
      </div>
      <div className={styles.valueContainer}>
        <span className={styles.value}>{formatValue(value)}</span>
        {trendIcon && (
          <span 
            className={`${styles.trend} ${styles[`trend${trend?.charAt(0).toUpperCase()}${trend?.slice(1)}`]}`}
            aria-label={`Trend: ${trend}`}
          >
            {trendIcon}
          </span>
        )}
      </div>
    </Card>
  );
}
