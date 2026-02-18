import type { ReactNode, HTMLAttributes } from 'react';
import styles from './Card.module.css';

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode;
  variant?: 'light' | 'dark';
  hoverable?: boolean;
}

export default function Card({
  children,
  variant = 'light',
  hoverable = false,
  className = '',
  ...props
}: CardProps) {
  const classNames = [
    styles.card,
    styles[variant],
    hoverable ? styles.hoverable : '',
    className,
  ].filter(Boolean).join(' ');

  return (
    <div className={classNames} {...props}>
      {children}
    </div>
  );
}
