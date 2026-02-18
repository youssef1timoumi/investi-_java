/**
 * Unit tests for PointsDisplay component
 * Tests specific examples and edge cases for the PointsDisplay component
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import PointsDisplay from '../PointsDisplay';

describe('PointsDisplay', () => {
  describe('Points Display', () => {
    it('renders points value correctly', () => {
      render(<PointsDisplay points={500} />);

      expect(screen.getByText('500')).toBeInTheDocument();
      expect(screen.getByText('Total Points')).toBeInTheDocument();
    });

    it('formats large point values with K suffix', () => {
      render(<PointsDisplay points={15000} />);

      expect(screen.getByText('15.0K')).toBeInTheDocument();
    });

    it('formats very large point values with M suffix', () => {
      render(<PointsDisplay points={2500000} />);

      expect(screen.getByText('2.5M')).toBeInTheDocument();
    });

    it('displays small values without formatting', () => {
      render(<PointsDisplay points={42} />);

      expect(screen.getByText('42')).toBeInTheDocument();
    });

    it('handles zero points', () => {
      render(<PointsDisplay points={0} />);

      expect(screen.getByText('0')).toBeInTheDocument();
    });
  });

  describe('Level Calculation', () => {
    it('shows Level 1 for 0-100 points', () => {
      render(<PointsDisplay points={50} />);

      expect(screen.getByText('Level 1')).toBeInTheDocument();
    });

    it('shows Level 1 at boundary (100 points)', () => {
      render(<PointsDisplay points={100} />);

      expect(screen.getByText('Level 1')).toBeInTheDocument();
    });

    it('shows Level 2 for 101-250 points', () => {
      render(<PointsDisplay points={150} />);

      expect(screen.getByText('Level 2')).toBeInTheDocument();
    });

    it('shows Level 3 for 251-500 points', () => {
      render(<PointsDisplay points={400} />);

      expect(screen.getByText('Level 3')).toBeInTheDocument();
    });

    it('shows Level 4 for 501-1000 points', () => {
      render(<PointsDisplay points={750} />);

      expect(screen.getByText('Level 4')).toBeInTheDocument();
    });

    it('shows Level 5 for 1001+ points', () => {
      render(<PointsDisplay points={1500} />);

      expect(screen.getByText('Level 5')).toBeInTheDocument();
    });
  });

  describe('Progress to Next Level', () => {
    it('shows points needed to reach next level', () => {
      render(<PointsDisplay points={50} />);

      // At 50 points, need 51 more to reach Level 2 (101 points)
      expect(screen.getByText('51 pts to Level 2')).toBeInTheDocument();
    });

    it('shows "Max Level!" when at Level 5', () => {
      render(<PointsDisplay points={1500} />);

      expect(screen.getByText('Max Level!')).toBeInTheDocument();
    });

    it('displays progress bar with correct aria attributes', () => {
      render(<PointsDisplay points={200} />);

      const progressBar = screen.getByRole('progressbar');
      expect(progressBar).toBeInTheDocument();
      expect(progressBar).toHaveAttribute('aria-valuemin', '0');
      expect(progressBar).toHaveAttribute('aria-valuemax', '100');
    });
  });

  describe('Accessibility', () => {
    it('has accessible region with label', () => {
      render(<PointsDisplay points={100} />);

      expect(screen.getByRole('region', { name: 'Points and level display' })).toBeInTheDocument();
    });

    it('has accessible level status', () => {
      render(<PointsDisplay points={300} />);

      expect(screen.getByRole('status', { name: 'Level 3' })).toBeInTheDocument();
    });

    it('has aria-label on points value', () => {
      render(<PointsDisplay points={250} />);

      expect(screen.getByLabelText('250 points')).toBeInTheDocument();
    });

    it('has accessible progress bar label for non-max level', () => {
      render(<PointsDisplay points={200} />);

      const progressBar = screen.getByRole('progressbar');
      expect(progressBar).toHaveAttribute('aria-label', 'Progress to level 3');
    });

    it('has accessible progress bar label for max level', () => {
      render(<PointsDisplay points={1500} />);

      const progressBar = screen.getByRole('progressbar');
      expect(progressBar).toHaveAttribute('aria-label', 'Maximum level reached');
    });

    it('hides decorative star icon from screen readers', () => {
      const { container } = render(<PointsDisplay points={100} />);

      const starIcon = container.querySelector('[aria-hidden="true"]');
      expect(starIcon).toBeInTheDocument();
      expect(starIcon?.textContent).toBe('★');
    });
  });

  describe('Edge Cases', () => {
    it('handles exact level boundaries correctly', () => {
      // Test boundary at 101 (start of Level 2)
      const { rerender } = render(<PointsDisplay points={101} />);
      expect(screen.getByText('Level 2')).toBeInTheDocument();

      // Test boundary at 251 (start of Level 3)
      rerender(<PointsDisplay points={251} />);
      expect(screen.getByText('Level 3')).toBeInTheDocument();

      // Test boundary at 501 (start of Level 4)
      rerender(<PointsDisplay points={501} />);
      expect(screen.getByText('Level 4')).toBeInTheDocument();

      // Test boundary at 1001 (start of Level 5)
      rerender(<PointsDisplay points={1001} />);
      expect(screen.getByText('Level 5')).toBeInTheDocument();
    });

    it('handles very large point values', () => {
      render(<PointsDisplay points={999999999} />);

      expect(screen.getByText('Level 5')).toBeInTheDocument();
      expect(screen.getByText('Max Level!')).toBeInTheDocument();
    });
  });
});
