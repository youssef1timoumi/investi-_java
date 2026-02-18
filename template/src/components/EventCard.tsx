import { useState } from 'react';
import type { Event } from '../types';
import Card from './Card';
import Button from './Button';
import styles from './EventCard.module.css';

interface EventCardProps {
  event: Event;
  onInscribe: () => void;
  isInscribed: boolean;
}

export default function EventCard({ event, onInscribe, isInscribed: initialIsInscribed }: EventCardProps) {
  const [isInscribed, setIsInscribed] = useState(initialIsInscribed);
  const [isAnimating, setIsAnimating] = useState(false);

  const handleInscribe = () => {
    if (!isInscribed) {
      setIsAnimating(true);
      setIsInscribed(true);
      onInscribe();
      // Reset animation state after animation completes
      setTimeout(() => setIsAnimating(false), 300);
    }
  };

  const isFull = event.inscribedCount >= event.capacity;
  const spotsRemaining = event.capacity - event.inscribedCount;

  // Format date for display
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <Card variant="light" className={styles.eventCard}>
      <div className={styles.imageContainer}>
        <img
          src={event.imageUrl}
          alt={`${event.title} event`}
          className={styles.image}
          onError={(e) => {
            // Fallback for missing images
            (e.target as HTMLImageElement).src = '/placeholder-event.jpg';
          }}
        />
        {isFull && (
          <span className={styles.fullBadge} aria-label="Event is full">
            Full
          </span>
        )}
      </div>

      <div className={styles.content}>
        <h3 className={styles.title}>{event.title}</h3>
        
        <time className={styles.date} dateTime={event.date}>
          {formatDate(event.date)}
        </time>

        <p className={styles.description}>{event.description}</p>

        <div className={styles.location}>
          <span className={styles.locationIcon} aria-hidden="true">📍</span>
          <span>{event.location}</span>
        </div>

        <div className={styles.capacity}>
          <span className={styles.capacityText}>
            {isInscribed ? (
              <span className={styles.inscribedCount}>
                {event.inscribedCount + 1} / {event.capacity} inscribed
              </span>
            ) : (
              <span>
                {spotsRemaining} spot{spotsRemaining !== 1 ? 's' : ''} remaining
              </span>
            )}
          </span>
        </div>

        <div className={styles.actions}>
          <Button
            variant={isInscribed ? 'secondary' : 'primary'}
            onClick={handleInscribe}
            disabled={isInscribed || isFull}
            aria-label={
              isInscribed
                ? `Already inscribed to ${event.title}`
                : isFull
                ? `${event.title} is full`
                : `Inscribe to ${event.title}`
            }
            className={isAnimating ? styles.animating : ''}
          >
            {isInscribed ? (
              <>
                <span className={styles.checkIcon} aria-hidden="true">✓</span>
                Inscribed
              </>
            ) : isFull ? (
              'Event Full'
            ) : (
              'Inscribe'
            )}
          </Button>
        </div>

        {/* Visual feedback indicator */}
        {isInscribed && (
          <div className={styles.inscriptionFeedback} aria-live="polite">
            <span className={styles.feedbackIcon} aria-hidden="true">✓</span>
            <span>You're inscribed to this event!</span>
          </div>
        )}
      </div>
    </Card>
  );
}
