import { useNavigate } from 'react-router-dom';
import styles from './HeroSection.module.css';

interface CTAButton {
  label: string;
  role: 'admin' | 'investor' | 'innovator';
  onClick: () => void;
}

interface HeroSectionProps {
  title: string;
  subtitle: string;
  ctaButtons: CTAButton[];
}

export default function HeroSection({ title, subtitle }: HeroSectionProps) {
  const navigate = useNavigate();

  return (
    <section className={styles.hero} aria-labelledby="hero-title">
      {/* Animated background elements */}
      <div className={styles.backgroundEffects} aria-hidden="true">
        <div className={styles.glowOrb} />
        <div className={styles.glowOrbSecondary} />
        <div className={styles.stars} />
        <div className={styles.gradientOverlay} />
      </div>

      <div className={styles.content}>
        <h1 id="hero-title" className={styles.title}>
          {title.split(' ').map((word, i) => (
            <span key={i} className={styles.word} style={{ animationDelay: `${i * 0.1}s` }}>
              {word}{' '}
            </span>
          ))}
        </h1>
        
        <p className={styles.subtitle}>{subtitle}</p>

        <div className={styles.buttons} role="group" aria-label="Get started options">
          <button 
            className={styles.primaryButton}
            onClick={() => navigate('/login')}
            aria-label="Start free - go to login"
          >
            Start Free <span className={styles.arrow}>→</span>
          </button>
          <button 
            className={styles.secondaryButton}
            onClick={() => navigate('/forum')}
            aria-label="Watch demo - explore forum"
          >
            <span className={styles.playIcon}>▶</span> Watch Demo
          </button>
        </div>

        <p className={styles.disclaimer}>No credit card required.</p>
      </div>

      {/* Bottom glow effect */}
      <div className={styles.bottomGlow} aria-hidden="true" />
    </section>
  );
}
