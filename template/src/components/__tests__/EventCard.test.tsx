import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import EventCard from '../EventCard';
import type { Event } from '../../types';

const mockEvent: Event = {
  id: '1',
  title: 'Startup Pitch Night',
  description: 'Present your ideas to a panel of investors',
  date: '2024-02-15',
  location: 'Innovation Hub, NYC',
  imageUrl: '/event1.jpg',
  capacity: 100,
  inscribedCount: 78,
};

describe('EventCard', () => {
  it('renders event title, description, date, and location', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    expect(screen.getByText('Startup Pitch Night')).toBeInTheDocument();
    expect(screen.getByText('Present your ideas to a panel of investors')).toBeInTheDocument();
    expect(screen.getByText('Innovation Hub, NYC')).toBeInTheDocument();
    // Date is formatted
    expect(screen.getByText(/Feb/)).toBeInTheDocument();
  });

  it('renders event image with correct alt text', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    const image = screen.getByRole('img');
    expect(image).toHaveAttribute('alt', 'Startup Pitch Night event');
    expect(image).toHaveAttribute('src', '/event1.jpg');
  });

  it('displays inscription button when not inscribed', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    const button = screen.getByRole('button', { name: /inscribe to startup pitch night/i });
    expect(button).toBeInTheDocument();
    expect(button).not.toBeDisabled();
  });

  it('calls onInscribe when inscription button is clicked', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    const button = screen.getByRole('button', { name: /inscribe to startup pitch night/i });
    fireEvent.click(button);

    expect(onInscribe).toHaveBeenCalledTimes(1);
  });

  it('provides visual feedback after inscription', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    const button = screen.getByRole('button', { name: /inscribe to startup pitch night/i });
    fireEvent.click(button);

    // Button text changes to indicate inscription
    expect(screen.getByText('Inscribed')).toBeInTheDocument();
    // Feedback message appears
    expect(screen.getByText("You're inscribed to this event!")).toBeInTheDocument();
  });

  it('disables button after inscription', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    const button = screen.getByRole('button', { name: /inscribe to startup pitch night/i });
    fireEvent.click(button);

    // Button should now be disabled
    const inscribedButton = screen.getByRole('button', { name: /already inscribed/i });
    expect(inscribedButton).toBeDisabled();
  });

  it('renders as inscribed when isInscribed prop is true', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={true} />);

    expect(screen.getByText('Inscribed')).toBeInTheDocument();
    expect(screen.getByText("You're inscribed to this event!")).toBeInTheDocument();
    
    const button = screen.getByRole('button', { name: /already inscribed/i });
    expect(button).toBeDisabled();
  });

  it('shows spots remaining when not inscribed', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    // 100 capacity - 78 inscribed = 22 spots remaining
    expect(screen.getByText('22 spots remaining')).toBeInTheDocument();
  });

  it('shows inscribed count when inscribed', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={true} />);

    // Shows inscribed count + 1 (for the user)
    expect(screen.getByText('79 / 100 inscribed')).toBeInTheDocument();
  });

  it('shows "Full" badge and disables button when event is at capacity', () => {
    const fullEvent: Event = {
      ...mockEvent,
      inscribedCount: 100,
    };
    const onInscribe = vi.fn();
    render(<EventCard event={fullEvent} onInscribe={onInscribe} isInscribed={false} />);

    expect(screen.getByText('Full')).toBeInTheDocument();
    expect(screen.getByText('Event Full')).toBeInTheDocument();
    
    const button = screen.getByRole('button', { name: /is full/i });
    expect(button).toBeDisabled();
  });

  it('does not call onInscribe when event is full', () => {
    const fullEvent: Event = {
      ...mockEvent,
      inscribedCount: 100,
    };
    const onInscribe = vi.fn();
    render(<EventCard event={fullEvent} onInscribe={onInscribe} isInscribed={false} />);

    const button = screen.getByRole('button', { name: /is full/i });
    fireEvent.click(button);

    expect(onInscribe).not.toHaveBeenCalled();
  });

  it('has proper accessibility attributes', () => {
    const onInscribe = vi.fn();
    render(<EventCard event={mockEvent} onInscribe={onInscribe} isInscribed={false} />);

    // Date has datetime attribute
    const dateElement = screen.getByText(/Feb/).closest('time');
    expect(dateElement).toHaveAttribute('dateTime', '2024-02-15');

    // Button has aria-label
    const button = screen.getByRole('button');
    expect(button).toHaveAttribute('aria-label');
  });

  it('uses singular "spot" when only 1 spot remaining', () => {
    const almostFullEvent: Event = {
      ...mockEvent,
      inscribedCount: 99,
    };
    const onInscribe = vi.fn();
    render(<EventCard event={almostFullEvent} onInscribe={onInscribe} isInscribed={false} />);

    expect(screen.getByText('1 spot remaining')).toBeInTheDocument();
  });
});
