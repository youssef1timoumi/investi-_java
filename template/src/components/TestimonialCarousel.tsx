import { useState, useEffect, useCallback } from 'react';
import styles from './TestimonialCarousel.module.css';

interface Testimonial {
  id: string;
  quote: string;
  author: string;
  role: string;
  avatar: string;
}

const testimonials: Testimonial[] = [
  {
    id: '1',
    quote: "INVESTI helped me connect with the right investors who truly understood my vision. Within 6 months, my startup went from idea to funded project.",
    author: "Sarah Chen",
    role: "Founder, GreenTech Solutions",
    avatar: "🚀"
  },
  {
    id: '2',
    quote: "As an investor, I've discovered incredible opportunities here that I wouldn't have found elsewhere. The platform makes due diligence so much easier.",
    author: "Michael Ross",
    role: "Angel Investor",
    avatar: "💼"
  },
  {
    id: '3',
    quote: "The collaboration tools are fantastic. I've partnered with three other innovators and we're now building something amazing together.",
    author: "Elena Martinez",
    role: "Tech Entrepreneur",
    avatar: "💡"
  },
  {
    id: '4',
    quote: "From pitch to partnership in just 3 weeks. The community here is supportive and the process is streamlined perfectly.",
    author: "David Kim",
    role: "CEO, HealthAI",
    avatar: "⭐"
  }
];

export default function TestimonialCarousel() {
  const [activeIndex, setActiveIndex] = useState(0);
  const [isAutoPlaying, setIsAutoPlaying] = useState(true);

  const nextSlide = useCallback(() => {
    setActiveIndex((prev) => (prev + 1) % testimonials.length);
  }, []);

  const prevSlide = useCallback(() => {
    setActiveIndex((prev) => (prev - 1 + testimonials.length) % testimonials.length);
  }, []);

  const goToSlide = (index: number) => {
    setActiveIndex(index);
    setIsAutoPlaying(false);
    // Resume auto-play after 5 seconds of inactivity
    setTimeout(() => setIsAutoPlaying(true), 5000);
  };

  useEffect(() => {
    if (!isAutoPlaying) return;
    
    const interval = setInterval(nextSlide, 5000);
    return () => clearInterval(interval);
  }, [isAutoPlaying, nextSlide]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'ArrowLeft') {
      prevSlide();
      setIsAutoPlaying(false);
    } else if (e.key === 'ArrowRight') {
      nextSlide();
      setIsAutoPlaying(false);
    }
  };

  return (
    <div 
      className={styles.carousel}
      role="region"
      aria-label="Testimonials"
      aria-roledescription="carousel"
      onKeyDown={handleKeyDown}
      tabIndex={0}
    >
      <div className={styles.slidesContainer}>
        {testimonials.map((testimonial, index) => (
          <div
            key={testimonial.id}
            className={`${styles.slide} ${index === activeIndex ? styles.active : ''}`}
            role="group"
            aria-roledescription="slide"
            aria-label={`${index + 1} of ${testimonials.length}`}
            aria-hidden={index !== activeIndex}
          >
            <div className={styles.quoteIcon} aria-hidden="true">"</div>
            <blockquote className={styles.quote}>
              {testimonial.quote}
            </blockquote>
            <div className={styles.author}>
              <span className={styles.avatar} aria-hidden="true">{testimonial.avatar}</span>
              <div className={styles.authorInfo}>
                <span className={styles.authorName}>{testimonial.author}</span>
                <span className={styles.authorRole}>{testimonial.role}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className={styles.controls}>
        <button
          className={styles.navButton}
          onClick={() => { prevSlide(); setIsAutoPlaying(false); }}
          aria-label="Previous testimonial"
        >
          ←
        </button>
        
        <div className={styles.dots} role="tablist" aria-label="Testimonial navigation">
          {testimonials.map((_, index) => (
            <button
              key={index}
              className={`${styles.dot} ${index === activeIndex ? styles.activeDot : ''}`}
              onClick={() => goToSlide(index)}
              role="tab"
              aria-selected={index === activeIndex}
              aria-label={`Go to testimonial ${index + 1}`}
            />
          ))}
        </div>

        <button
          className={styles.navButton}
          onClick={() => { nextSlide(); setIsAutoPlaying(false); }}
          aria-label="Next testimonial"
        >
          →
        </button>
      </div>
    </div>
  );
}
