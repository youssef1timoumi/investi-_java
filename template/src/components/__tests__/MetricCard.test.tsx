/**
 * Unit tests for MetricCard component
 * Tests specific examples and edge cases for the MetricCard component
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import MetricCard from '../MetricCard';

describe('MetricCard', () => {
  it('renders title, value, and icon correctly', () => {
    render(
      <MetricCard
        title="Users"
        value={1234}
        icon="👥"
      />
    );

    expect(screen.getByText('Users')).toBeInTheDocument();
    expect(screen.getByText('1.2K')).toBeInTheDocument();
    expect(screen.getByText('👥')).toBeInTheDocument();
  });

  it('formats large values correctly (thousands)', () => {
    render(
      <MetricCard
        title="Ideas"
        value={5678}
        icon="💡"
      />
    );

    expect(screen.getByText('5.7K')).toBeInTheDocument();
  });

  it('formats large values correctly (millions)', () => {
    render(
      <MetricCard
        title="Revenue"
        value={2500000}
        icon="💰"
      />
    );

    expect(screen.getByText('2.5M')).toBeInTheDocument();
  });

  it('displays small values without formatting', () => {
    render(
      <MetricCard
        title="Events"
        value={42}
        icon="📅"
      />
    );

    expect(screen.getByText('42')).toBeInTheDocument();
  });

  it('renders up trend indicator when trend is up', () => {
    render(
      <MetricCard
        title="Growth"
        value={100}
        icon="📈"
        trend="up"
      />
    );

    expect(screen.getByText('↑')).toBeInTheDocument();
    expect(screen.getByLabelText('Trend: up')).toBeInTheDocument();
  });

  it('renders down trend indicator when trend is down', () => {
    render(
      <MetricCard
        title="Churn"
        value={50}
        icon="📉"
        trend="down"
      />
    );

    expect(screen.getByText('↓')).toBeInTheDocument();
    expect(screen.getByLabelText('Trend: down')).toBeInTheDocument();
  });

  it('renders neutral trend indicator when trend is neutral', () => {
    render(
      <MetricCard
        title="Stable"
        value={200}
        icon="➡️"
        trend="neutral"
      />
    );

    expect(screen.getByText('→')).toBeInTheDocument();
    expect(screen.getByLabelText('Trend: neutral')).toBeInTheDocument();
  });

  it('does not render trend indicator when trend is not provided', () => {
    render(
      <MetricCard
        title="Count"
        value={100}
        icon="🔢"
      />
    );

    expect(screen.queryByText('↑')).not.toBeInTheDocument();
    expect(screen.queryByText('↓')).not.toBeInTheDocument();
    expect(screen.queryByText('→')).not.toBeInTheDocument();
  });

  it('renders icon with aria-hidden for accessibility', () => {
    const { container } = render(
      <MetricCard
        title="Test"
        value={100}
        icon="🎯"
      />
    );

    const iconElement = container.querySelector('[aria-hidden="true"]');
    expect(iconElement).toBeInTheDocument();
    expect(iconElement?.textContent).toBe('🎯');
  });

  it('renders within a Card component', () => {
    const { container } = render(
      <MetricCard
        title="Dark Card"
        value={500}
        icon="🌙"
      />
    );

    // The MetricCard should render a card div element
    const cardElement = container.firstChild;
    expect(cardElement).toBeInTheDocument();
    expect(cardElement?.nodeName).toBe('DIV');
  });
});
