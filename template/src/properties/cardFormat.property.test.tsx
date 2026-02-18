/**
 * Property 7: Card Format Consistency
 * For any course card, the rendered structure should match the event card format
 * (containing image, title, description, date/duration, and action button).
 *
 * **Feature: startup-platform-template, Property 7: Card Format Consistency**
 * **Validates: Requirements 7.4**
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen, cleanup } from '@testing-library/react';
import * as fc from 'fast-check';
import EventCard from '../components/EventCard';
import CourseCard from '../components/CourseCard';
import type { Event, Course } from '../types';

// Helper to normalize whitespace (browser collapses multiple spaces)
const normalizeWhitespace = (str: string): string => str.replace(/\s+/g, ' ').trim();

// Arbitrary for generating random Event data
const eventArbitrary: fc.Arbitrary<Event> = fc.record({
  id: fc.uuid(),
  title: fc.constantFrom(
    'Tech Innovation Summit',
    'Startup Networking Event',
    'Investment Workshop',
    'Founders Meetup',
    'Demo Day 2024'
  ),
  description: fc.constantFrom(
    'Join us for an exciting event focused on innovation and technology.',
    'Network with fellow entrepreneurs and investors in this exclusive event.',
    'Learn the fundamentals of startup investment strategies.',
    'Connect with founders and share your startup journey.',
    'Watch startups pitch their ideas to top investors.'
  ),
  date: fc.constantFrom(
    '2024-01-15T10:30:00',
    '2024-02-20T14:45:00',
    '2024-03-10T09:00:00',
    '2024-04-05T16:00:00',
    '2024-05-12T11:30:00'
  ),
  location: fc.constantFrom(
    'Innovation Hub, Building A',
    'Tech Center, Room 101',
    'Conference Hall B',
    'Virtual Event',
    'Downtown Convention Center'
  ),
  imageUrl: fc.constantFrom(
    '/event-image-1.jpg',
    '/event-image-2.jpg',
    '/event-image-3.jpg'
  ),
  capacity: fc.integer({ min: 10, max: 500 }),
  inscribedCount: fc.integer({ min: 0, max: 100 }),
});

// Arbitrary for generating random Course data
const courseArbitrary: fc.Arbitrary<Course> = fc.record({
  id: fc.uuid(),
  title: fc.constantFrom(
    'Introduction to Startup Funding',
    'Business Model Canvas Workshop',
    'Pitch Deck Masterclass',
    'Financial Modeling Basics',
    'Marketing for Startups'
  ),
  description: fc.constantFrom(
    'Learn the essentials of raising capital for your startup.',
    'Master the art of creating effective business models.',
    'Create compelling pitch decks that attract investors.',
    'Build financial models to forecast your startup growth.',
    'Discover marketing strategies tailored for early-stage startups.'
  ),
  duration: fc.constantFrom(
    '2 hours',
    '4 hours',
    '1 day',
    '2 days',
    '1 week',
    '3 weeks'
  ),
  instructor: fc.constantFrom(
    'John Smith',
    'Sarah Johnson',
    'Michael Chen',
    'Emily Davis',
    'Robert Wilson'
  ),
  imageUrl: fc.constantFrom(
    '/course-image-1.jpg',
    '/course-image-2.jpg',
    '/course-image-3.jpg'
  ),
  enrolledCount: fc.integer({ min: 0, max: 200 }),
});

describe('Property 7: Card Format Consistency', () => {
  /**
   * **Validates: Requirements 7.4**
   * Tests that EventCard has all required structural elements
   */
  it('EventCard should have image, title, description, date, and action button', () => {
    fc.assert(
      fc.property(eventArbitrary, (event) => {
        cleanup();

        const mockOnInscribe = vi.fn();

        render(
          <EventCard
            event={event}
            onInscribe={mockOnInscribe}
            isInscribed={false}
          />
        );

        // Image should be present
        const image = screen.getByRole('img');
        expect(image).toBeInTheDocument();
        expect(image).toHaveAttribute('alt');

        // Title should be present (h3 element)
        const title = screen.getByRole('heading', { level: 3 });
        expect(title).toBeInTheDocument();
        expect(title).toHaveTextContent(event.title);

        // Description should be present
        const description = screen.getByText(event.description);
        expect(description).toBeInTheDocument();

        // Date should be present (time element)
        const dateElement = document.querySelector('time');
        expect(dateElement).not.toBeNull();
        expect(dateElement).toHaveAttribute('dateTime');

        // Action button should be present
        const actionButton = screen.getByRole('button');
        expect(actionButton).toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 7.4**
   * Tests that CourseCard has all required structural elements
   */
  it('CourseCard should have image, title, description, duration, and action button', () => {
    fc.assert(
      fc.property(courseArbitrary, (course) => {
        cleanup();

        const mockOnEnroll = vi.fn();

        render(
          <CourseCard
            course={course}
            onEnroll={mockOnEnroll}
            isEnrolled={false}
          />
        );

        // Image should be present
        const image = screen.getByRole('img');
        expect(image).toBeInTheDocument();
        expect(image).toHaveAttribute('alt');

        // Title should be present (h3 element)
        const title = screen.getByRole('heading', { level: 3 });
        expect(title).toBeInTheDocument();
        expect(title).toHaveTextContent(course.title);

        // Description should be present
        const description = screen.getByText(course.description);
        expect(description).toBeInTheDocument();

        // Duration should be present (instead of date)
        const duration = screen.getByText(course.duration);
        expect(duration).toBeInTheDocument();

        // Action button should be present
        const actionButton = screen.getByRole('button');
        expect(actionButton).toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 7.4**
   * Tests that CourseCard structure matches EventCard structure
   */
  it('CourseCard structure should match EventCard structure', () => {
    fc.assert(
      fc.property(eventArbitrary, courseArbitrary, (event, course) => {
        cleanup();

        // Render EventCard
        const { container: eventContainer } = render(
          <EventCard
            event={event}
            onInscribe={vi.fn()}
            isInscribed={false}
          />
        );

        // Get EventCard structural elements
        const eventImage = eventContainer.querySelector('img');
        const eventTitle = eventContainer.querySelector('h3');
        const eventDescription = eventContainer.querySelector('p');
        const eventButton = eventContainer.querySelector('button');
        const eventImageContainer = eventContainer.querySelector('[class*="imageContainer"]');
        const eventContent = eventContainer.querySelector('[class*="content"]');
        const eventActions = eventContainer.querySelector('[class*="actions"]');

        cleanup();

        // Render CourseCard
        const { container: courseContainer } = render(
          <CourseCard
            course={course}
            onEnroll={vi.fn()}
            isEnrolled={false}
          />
        );

        // Get CourseCard structural elements
        const courseImage = courseContainer.querySelector('img');
        const courseTitle = courseContainer.querySelector('h3');
        const courseDescription = courseContainer.querySelector('p');
        const courseButton = courseContainer.querySelector('button');
        const courseImageContainer = courseContainer.querySelector('[class*="imageContainer"]');
        const courseContent = courseContainer.querySelector('[class*="content"]');
        const courseActions = courseContainer.querySelector('[class*="actions"]');

        // Both should have image element
        expect(eventImage).not.toBeNull();
        expect(courseImage).not.toBeNull();

        // Both should have title (h3)
        expect(eventTitle).not.toBeNull();
        expect(courseTitle).not.toBeNull();

        // Both should have description (p)
        expect(eventDescription).not.toBeNull();
        expect(courseDescription).not.toBeNull();

        // Both should have action button
        expect(eventButton).not.toBeNull();
        expect(courseButton).not.toBeNull();

        // Both should have similar container structure
        expect(eventImageContainer).not.toBeNull();
        expect(courseImageContainer).not.toBeNull();
        expect(eventContent).not.toBeNull();
        expect(courseContent).not.toBeNull();
        expect(eventActions).not.toBeNull();
        expect(courseActions).not.toBeNull();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 7.4**
   * Tests that CourseCard has duration where EventCard has date
   */
  it('CourseCard should have duration where EventCard has date', () => {
    fc.assert(
      fc.property(eventArbitrary, courseArbitrary, (event, course) => {
        cleanup();

        // Render EventCard and check for date
        const { container: eventContainer } = render(
          <EventCard
            event={event}
            onInscribe={vi.fn()}
            isInscribed={false}
          />
        );

        // EventCard should have a time element with dateTime attribute
        const eventDateElement = eventContainer.querySelector('time');
        expect(eventDateElement).not.toBeNull();
        expect(eventDateElement).toHaveAttribute('dateTime');

        cleanup();

        // Render CourseCard and check for duration
        const { container: courseContainer } = render(
          <CourseCard
            course={course}
            onEnroll={vi.fn()}
            isEnrolled={false}
          />
        );

        // CourseCard should have duration displayed (not a time element)
        const courseDurationElement = courseContainer.querySelector('[class*="duration"]');
        expect(courseDurationElement).not.toBeNull();
        expect(courseDurationElement?.textContent).toContain(course.duration);

        // CourseCard should NOT have a time element (uses duration instead)
        const courseTimeElement = courseContainer.querySelector('time');
        expect(courseTimeElement).toBeNull();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 7.4**
   * Tests that both cards have action buttons with similar behavior
   */
  it('both cards should have action buttons with similar behavior', () => {
    fc.assert(
      fc.property(
        eventArbitrary,
        courseArbitrary,
        fc.boolean(),
        (event, course, isActive) => {
          cleanup();

          // Render EventCard with inscription state
          render(
            <EventCard
              event={event}
              onInscribe={vi.fn()}
              isInscribed={isActive}
            />
          );

          const eventButton = screen.getByRole('button');
          expect(eventButton).toBeInTheDocument();

          // When inscribed, button should show inscribed state
          if (isActive) {
            expect(eventButton).toHaveTextContent(/inscribed/i);
          } else {
            expect(eventButton).toHaveTextContent(/inscribe|event full/i);
          }

          cleanup();

          // Render CourseCard with enrollment state
          render(
            <CourseCard
              course={course}
              onEnroll={vi.fn()}
              isEnrolled={isActive}
            />
          );

          const courseButton = screen.getByRole('button');
          expect(courseButton).toBeInTheDocument();

          // When enrolled, button should show enrolled state
          if (isActive) {
            expect(courseButton).toHaveTextContent(/enrolled/i);
          } else {
            expect(courseButton).toHaveTextContent(/enroll/i);
          }

          cleanup();
          return true;
        }
      ),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 7.4**
   * Tests that both cards have accessible aria-labels on action buttons
   */
  it('both cards should have accessible aria-labels on action buttons', () => {
    fc.assert(
      fc.property(eventArbitrary, courseArbitrary, (event, course) => {
        cleanup();

        // Render EventCard
        render(
          <EventCard
            event={event}
            onInscribe={vi.fn()}
            isInscribed={false}
          />
        );

        const eventButton = screen.getByRole('button');
        expect(eventButton).toHaveAttribute('aria-label');
        expect(eventButton.getAttribute('aria-label')).toContain(event.title);

        cleanup();

        // Render CourseCard
        render(
          <CourseCard
            course={course}
            onEnroll={vi.fn()}
            isEnrolled={false}
          />
        );

        const courseButton = screen.getByRole('button');
        expect(courseButton).toHaveAttribute('aria-label');
        expect(courseButton.getAttribute('aria-label')).toContain(course.title);

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });
});
