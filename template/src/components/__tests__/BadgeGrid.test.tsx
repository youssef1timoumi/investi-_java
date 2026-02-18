import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import BadgeGrid from '../BadgeGrid';
import type { Badge } from '../../types';

const mockBadges: Badge[] = [
  { id: 'pioneer', name: 'Pioneer', description: 'One of the first 100 users', icon: '🚀' },
  { id: 'leader', name: 'Community Leader', description: 'Helped 10+ users', icon: '👑' },
  { id: 'first-investment', name: 'First Investment', description: 'Made your first investment', icon: '💰' },
  { id: 'idea-creator', name: 'Idea Creator', description: 'Submitted your first idea', icon: '💡' },
];

describe('BadgeGrid', () => {
  it('renders all badges in the grid', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={[]} />);
    
    expect(screen.getByText('Pioneer')).toBeInTheDocument();
    expect(screen.getByText('Community Leader')).toBeInTheDocument();
    expect(screen.getByText('First Investment')).toBeInTheDocument();
    expect(screen.getByText('Idea Creator')).toBeInTheDocument();
  });

  it('displays badge descriptions', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={[]} />);
    
    expect(screen.getByText('One of the first 100 users')).toBeInTheDocument();
    expect(screen.getByText('Helped 10+ users')).toBeInTheDocument();
  });

  it('shows earned badges with earned status', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={['pioneer', 'leader']} />);
    
    const earnedStatuses = screen.getAllByText('✓ Earned');
    expect(earnedStatuses).toHaveLength(2);
  });

  it('shows locked badges with locked status', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={['pioneer']} />);
    
    const lockedStatuses = screen.getAllByText('Locked');
    expect(lockedStatuses).toHaveLength(3);
  });

  it('displays badge icons for earned badges', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={['pioneer']} />);
    
    // Earned badge shows its icon
    expect(screen.getByText('🚀')).toBeInTheDocument();
  });

  it('displays lock icon for unearned badges', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={[]} />);
    
    // All badges are locked, so we should see lock icons
    const lockIcons = screen.getAllByText('🔒');
    expect(lockIcons).toHaveLength(4);
  });

  it('has proper accessibility attributes', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={['pioneer']} />);
    
    // Check for region role
    expect(screen.getByRole('region', { name: 'Badge collection' })).toBeInTheDocument();
    
    // Check for list role
    expect(screen.getByRole('list')).toBeInTheDocument();
    
    // Check for listitem roles
    const listItems = screen.getAllByRole('listitem');
    expect(listItems).toHaveLength(4);
  });

  it('includes earned/locked status in aria-label', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={['pioneer']} />);
    
    expect(screen.getByLabelText('Pioneer badge - earned')).toBeInTheDocument();
    expect(screen.getByLabelText('Community Leader badge - locked')).toBeInTheDocument();
  });

  it('renders empty state when no badges provided', () => {
    render(<BadgeGrid allBadges={[]} earnedBadgeIds={[]} />);
    
    expect(screen.getByText('No badges available yet.')).toBeInTheDocument();
  });

  it('handles all badges being earned', () => {
    const allEarnedIds = mockBadges.map(b => b.id);
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={allEarnedIds} />);
    
    const earnedStatuses = screen.getAllByText('✓ Earned');
    expect(earnedStatuses).toHaveLength(4);
    
    // No locked badges
    expect(screen.queryByText('Locked')).not.toBeInTheDocument();
  });

  it('handles no badges being earned', () => {
    render(<BadgeGrid allBadges={mockBadges} earnedBadgeIds={[]} />);
    
    const lockedStatuses = screen.getAllByText('Locked');
    expect(lockedStatuses).toHaveLength(4);
    
    // No earned badges
    expect(screen.queryByText('✓ Earned')).not.toBeInTheDocument();
  });
});
