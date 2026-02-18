import { Link } from 'react-router-dom';
import Card from './Card';
import styles from './ServiceCard.module.css';

interface ServiceCardProps {
  icon: string;
  title: string;
  description: string;
  href: string;
}

export default function ServiceCard({ icon, title, description, href }: ServiceCardProps) {
  return (
    <Link 
      to={href} 
      className={styles.link}
      aria-label={`${title}: ${description}`}
    >
      <Card variant="light" hoverable className={styles.card}>
        <span className={styles.icon} aria-hidden="true">{icon}</span>
        <h3 className={styles.title}>{title}</h3>
        <p className={styles.description}>{description}</p>
        <span className={styles.arrow} aria-hidden="true">→</span>
      </Card>
    </Link>
  );
}
