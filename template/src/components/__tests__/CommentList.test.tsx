import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CommentList from '../CommentList';
import type { Comment } from '../../types';

const mockComments: Comment[] = [
  {
    id: '1',
    postId: '1',
    content: 'This is a top-level comment',
    authorId: '1',
    authorName: 'Alice',
    upvotes: 10,
    downvotes: 2,
    createdAt: '2024-01-15',
  },
  {
    id: '2',
    postId: '1',
    content: 'Another top-level comment',
    authorId: '2',
    authorName: 'Bob',
    upvotes: 5,
    downvotes: 1,
    createdAt: '2024-01-16',
  },
];

const mockNestedComments: Comment[] = [
  {
    id: '1',
    postId: '1',
    content: 'Parent comment',
    authorId: '1',
    authorName: 'Alice',
    upvotes: 10,
    downvotes: 2,
    createdAt: '2024-01-15',
  },
  {
    id: '2',
    postId: '1',
    parentId: '1',
    content: 'Reply to parent',
    authorId: '2',
    authorName: 'Bob',
    upvotes: 5,
    downvotes: 1,
    createdAt: '2024-01-16',
  },
  {
    id: '3',
    postId: '1',
    parentId: '2',
    content: 'Nested reply',
    authorId: '3',
    authorName: 'Carol',
    upvotes: 3,
    downvotes: 0,
    createdAt: '2024-01-17',
  },
];

describe('CommentList', () => {
  it('renders empty state when no comments provided', () => {
    const onVote = vi.fn();
    render(<CommentList comments={[]} onVote={onVote} />);

    expect(screen.getByText(/no comments yet/i)).toBeInTheDocument();
  });

  it('renders all comments with author names', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('Bob')).toBeInTheDocument();
  });

  it('renders comment content', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    expect(screen.getByText('This is a top-level comment')).toBeInTheDocument();
    expect(screen.getByText('Another top-level comment')).toBeInTheDocument();
  });

  it('displays correct comment count in title', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    expect(screen.getByText('2 Comments')).toBeInTheDocument();
  });

  it('displays singular "Comment" for single comment', () => {
    const onVote = vi.fn();
    const singleComment = [mockComments[0]];
    render(<CommentList comments={singleComment} onVote={onVote} />);

    expect(screen.getByText('1 Comment')).toBeInTheDocument();
  });

  it('renders nested comments correctly', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockNestedComments} onVote={onVote} />);

    expect(screen.getByText('Parent comment')).toBeInTheDocument();
    expect(screen.getByText('Reply to parent')).toBeInTheDocument();
    expect(screen.getByText('Nested reply')).toBeInTheDocument();
  });

  it('renders vote buttons for each comment', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    // Each comment should have upvote and downvote buttons
    const upvoteButtons = screen.getAllByRole('button', { name: /upvote/i });
    const downvoteButtons = screen.getAllByRole('button', { name: /downvote/i });

    expect(upvoteButtons).toHaveLength(2);
    expect(downvoteButtons).toHaveLength(2);
  });

  it('calls onVote with correct commentId and direction when upvoting', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    const upvoteButtons = screen.getAllByRole('button', { name: /upvote/i });
    fireEvent.click(upvoteButtons[0]);

    expect(onVote).toHaveBeenCalledWith('1', 'up');
  });

  it('calls onVote with correct commentId and direction when downvoting', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    const downvoteButtons = screen.getAllByRole('button', { name: /downvote/i });
    fireEvent.click(downvoteButtons[1]);

    expect(onVote).toHaveBeenCalledWith('2', 'down');
  });

  it('displays formatted dates for comments', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    // Check that dates are rendered (format: "Jan 15, 2024")
    expect(screen.getByText('Jan 15, 2024')).toBeInTheDocument();
    expect(screen.getByText('Jan 16, 2024')).toBeInTheDocument();
  });

  it('has proper accessibility attributes', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    // Check for section with aria-label
    const section = screen.getByRole('region', { name: /comments/i });
    expect(section).toBeInTheDocument();

    // Check for article elements with aria-labels
    const articles = screen.getAllByRole('article');
    expect(articles).toHaveLength(2);
    expect(articles[0]).toHaveAttribute('aria-label', 'Comment by Alice');
    expect(articles[1]).toHaveAttribute('aria-label', 'Comment by Bob');
  });

  it('renders replies group with proper aria-label', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockNestedComments} onVote={onVote} />);

    const repliesGroups = screen.getAllByRole('group', { name: /replies/i });
    expect(repliesGroups.length).toBeGreaterThan(0);
  });

  it('displays vote scores correctly', () => {
    const onVote = vi.fn();
    render(<CommentList comments={mockComments} onVote={onVote} />);

    // First comment: 10 upvotes - 2 downvotes = 8
    // Second comment: 5 upvotes - 1 downvote = 4
    expect(screen.getByText('8')).toBeInTheDocument();
    expect(screen.getByText('4')).toBeInTheDocument();
  });

  it('handles comments with missing parentId gracefully', () => {
    const onVote = vi.fn();
    const commentsWithOrphan: Comment[] = [
      ...mockComments,
      {
        id: '3',
        postId: '1',
        parentId: 'non-existent',
        content: 'Orphan comment',
        authorId: '3',
        authorName: 'Carol',
        upvotes: 1,
        downvotes: 0,
        createdAt: '2024-01-17',
      },
    ];

    render(<CommentList comments={commentsWithOrphan} onVote={onVote} />);

    // Orphan comment should still be rendered as a root comment
    expect(screen.getByText('Orphan comment')).toBeInTheDocument();
  });
});
