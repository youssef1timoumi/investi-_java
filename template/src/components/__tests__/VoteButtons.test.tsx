/**
 * Unit tests for VoteButtons component
 * Tests specific examples and edge cases for the VoteButtons component
 * Validates: Requirements 6.3, 6.4, 6.5
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import VoteButtons from '../VoteButtons';

describe('VoteButtons', () => {
  const defaultProps = {
    upvotes: 10,
    downvotes: 3,
    userVote: null as 'up' | 'down' | null,
    onUpvote: vi.fn(),
    onDownvote: vi.fn(),
  };

  it('renders upvote and downvote buttons', () => {
    render(<VoteButtons {...defaultProps} />);

    expect(screen.getByRole('button', { name: /upvote/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /downvote/i })).toBeInTheDocument();
  });

  it('displays the correct score (upvotes - downvotes)', () => {
    render(<VoteButtons {...defaultProps} />);

    // Score should be 10 - 3 = 7
    expect(screen.getByText('7')).toBeInTheDocument();
  });

  it('displays negative score when downvotes exceed upvotes', () => {
    render(<VoteButtons {...defaultProps} upvotes={2} downvotes={5} />);

    // Score should be 2 - 5 = -3
    expect(screen.getByText('-3')).toBeInTheDocument();
  });

  it('displays zero score when upvotes equal downvotes', () => {
    render(<VoteButtons {...defaultProps} upvotes={5} downvotes={5} />);

    expect(screen.getByText('0')).toBeInTheDocument();
  });

  it('calls onUpvote when upvote button is clicked', () => {
    const onUpvote = vi.fn();
    render(<VoteButtons {...defaultProps} onUpvote={onUpvote} />);

    fireEvent.click(screen.getByRole('button', { name: /upvote/i }));

    expect(onUpvote).toHaveBeenCalledTimes(1);
  });

  it('calls onDownvote when downvote button is clicked', () => {
    const onDownvote = vi.fn();
    render(<VoteButtons {...defaultProps} onDownvote={onDownvote} />);

    fireEvent.click(screen.getByRole('button', { name: /downvote/i }));

    expect(onDownvote).toHaveBeenCalledTimes(1);
  });

  it('shows active state on upvote button when userVote is up', () => {
    const { container } = render(<VoteButtons {...defaultProps} userVote="up" />);

    const upvoteButton = screen.getByRole('button', { name: /upvote/i });
    expect(upvoteButton).toHaveAttribute('aria-pressed', 'true');
    expect(upvoteButton.className).toContain('active');
  });

  it('shows active state on downvote button when userVote is down', () => {
    render(<VoteButtons {...defaultProps} userVote="down" />);

    const downvoteButton = screen.getByRole('button', { name: /downvote/i });
    expect(downvoteButton).toHaveAttribute('aria-pressed', 'true');
    expect(downvoteButton.className).toContain('active');
  });

  it('shows no active state when userVote is null', () => {
    render(<VoteButtons {...defaultProps} userVote={null} />);

    const upvoteButton = screen.getByRole('button', { name: /upvote/i });
    const downvoteButton = screen.getByRole('button', { name: /downvote/i });

    expect(upvoteButton).toHaveAttribute('aria-pressed', 'false');
    expect(downvoteButton).toHaveAttribute('aria-pressed', 'false');
    expect(upvoteButton.className).not.toContain('active');
    expect(downvoteButton.className).not.toContain('active');
  });

  it('has proper accessibility attributes', () => {
    render(<VoteButtons {...defaultProps} />);

    // Check for role group
    expect(screen.getByRole('group', { name: /vote buttons/i })).toBeInTheDocument();

    // Check aria-labels on buttons
    expect(screen.getByRole('button', { name: /upvote, current upvotes: 10/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /downvote, current downvotes: 3/i })).toBeInTheDocument();
  });

  it('renders SVG icons with aria-hidden', () => {
    const { container } = render(<VoteButtons {...defaultProps} />);

    const svgElements = container.querySelectorAll('svg[aria-hidden="true"]');
    expect(svgElements).toHaveLength(2);
  });

  it('displays score with aria-label for screen readers', () => {
    render(<VoteButtons {...defaultProps} />);

    expect(screen.getByLabelText('Score: 7')).toBeInTheDocument();
  });

  it('handles large vote counts correctly', () => {
    render(<VoteButtons {...defaultProps} upvotes={1000} downvotes={500} />);

    expect(screen.getByText('500')).toBeInTheDocument();
  });
});
