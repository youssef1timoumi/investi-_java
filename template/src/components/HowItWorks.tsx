import { useState } from 'react';
import styles from './HowItWorks.module.css';

interface Step {
  id: number;
  icon: string;
  title: string;
  description: string;
}

const steps: Step[] = [
  {
    id: 1,
    icon: '💡',
    title: 'Share Your Idea',
    description: 'Submit your innovative concept and let the community discover your vision.'
  },
  {
    id: 2,
    icon: '🤝',
    title: 'Connect & Collaborate',
    description: 'Match with investors and partners who share your passion and expertise.'
  },
  {
    id: 3,
    icon: '📈',
    title: 'Grow Together',
    description: 'Transform your idea into a funded project with community support.'
  },
  {
    id: 4,
    icon: '🚀',
    title: 'Launch & Scale',
    description: 'Take your project to market with the backing of your network.'
  }
];

export default function HowItWorks() {
  const [activeStep, setActiveStep] = useState<number | null>(null);

  return (
    <div className={styles.container}>
      <div className={styles.timeline}>
        {steps.map((step, index) => (
          <div
            key={step.id}
            className={`${styles.step} ${activeStep === step.id ? styles.active : ''}`}
            onMouseEnter={() => setActiveStep(step.id)}
            onMouseLeave={() => setActiveStep(null)}
            onFocus={() => setActiveStep(step.id)}
            onBlur={() => setActiveStep(null)}
            tabIndex={0}
            role="button"
            aria-label={`Step ${step.id}: ${step.title}`}
          >
            <div className={styles.stepNumber}>{step.id}</div>
            <div className={styles.stepIcon}>{step.icon}</div>
            <h3 className={styles.stepTitle}>{step.title}</h3>
            <p className={styles.stepDescription}>{step.description}</p>
            {index < steps.length - 1 && <div className={styles.connector} />}
          </div>
        ))}
      </div>
    </div>
  );
}
