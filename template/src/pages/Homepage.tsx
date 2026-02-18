import { useNavigate } from 'react-router-dom';
import PageLayout from '../components/PageLayout';
import HeroSection from '../components/HeroSection';
import ServiceCard from '../components/ServiceCard';
import AnimatedCounter from '../components/AnimatedCounter';
import TestimonialCarousel from '../components/TestimonialCarousel';
import FeaturedIdeas from '../components/FeaturedIdeas';
import HowItWorks from '../components/HowItWorks';
import Button from '../components/Button';
import { getServices, getDashboardMetrics } from '../data/mockData';
import styles from './Homepage.module.css';

export default function Homepage() {
  const navigate = useNavigate();
  const services = getServices();
  const metrics = getDashboardMetrics();

  const ctaButtons = [
    {
      label: 'Login as Admin',
      role: 'admin' as const,
      onClick: () => navigate('/login?role=admin'),
    },
    {
      label: 'Login as Investor',
      role: 'investor' as const,
      onClick: () => navigate('/login?role=investor'),
    },
    {
      label: 'Login as Innovator',
      role: 'innovator' as const,
      onClick: () => navigate('/login?role=innovator'),
    },
  ];

  return (
    <PageLayout backgroundColor="dark">
      <HeroSection
        title="Where Innovation Meets Investment"
        subtitle="Connect with visionary innovators and strategic investors. Transform groundbreaking ideas into successful ventures."
        ctaButtons={ctaButtons}
      />

      {/* Animated Stats Section */}
      <section className={styles.stats} aria-label="Platform statistics">
        <div className={styles.statsContainer}>
          <AnimatedCounter end={metrics.usersCount} suffix="+" label="Active Users" />
          <AnimatedCounter end={metrics.ideasCount} suffix="+" label="Ideas Shared" />
          <AnimatedCounter end={metrics.projectsCount} suffix="+" label="Projects Funded" />
          <AnimatedCounter end={metrics.activeCollaborations} suffix="+" label="Collaborations" />
        </div>
      </section>

      {/* How It Works Section */}
      <section className={styles.howItWorks} aria-labelledby="how-it-works-title">
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 id="how-it-works-title" className={styles.sectionTitle}>How It Works</h2>
            <p className={styles.sectionSubtitle}>
              Your journey from idea to funded project in four simple steps
            </p>
          </div>
          <HowItWorks />
        </div>
      </section>

      {/* Featured Ideas Section */}
      <section className={styles.featuredIdeas} aria-labelledby="featured-ideas-title">
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 id="featured-ideas-title" className={styles.sectionTitle}>Featured Ideas</h2>
            <p className={styles.sectionSubtitle}>
              Discover innovative concepts looking for investors and collaborators
            </p>
          </div>
          <FeaturedIdeas />
        </div>
      </section>

      {/* Services Section */}
      <section className={styles.services} aria-labelledby="services-title">
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 id="services-title" className={styles.sectionTitle}>Our Services</h2>
            <p className={styles.sectionSubtitle}>
              Everything you need to bring your ideas to life
            </p>
          </div>
          <div className={styles.grid}>
            {services.map((service) => (
              <ServiceCard
                key={service.id}
                icon={service.icon}
                title={service.title}
                description={service.description}
                href={service.href}
              />
            ))}
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section className={styles.testimonials} aria-labelledby="testimonials-title">
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 id="testimonials-title" className={styles.sectionTitle}>What People Say</h2>
            <p className={styles.sectionSubtitle}>
              Hear from innovators and investors who found success on our platform
            </p>
          </div>
          <TestimonialCarousel />
        </div>
      </section>

      {/* CTA Section */}
      <section className={styles.cta} aria-labelledby="cta-title">
        <div className={styles.ctaContent}>
          <h2 id="cta-title" className={styles.ctaTitle}>
            Ready to Start Your Journey?
          </h2>
          <p className={styles.ctaText}>
            Join thousands of innovators and investors transforming ideas into reality.
          </p>
          <div className={styles.ctaButton}>
            <Button 
              variant="secondary" 
              size="lg" 
              onClick={() => navigate('/login')}
            >
              Get Started Today
            </Button>
          </div>
        </div>
      </section>
    </PageLayout>
  );
}
