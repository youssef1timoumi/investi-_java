/**
 * Property 6: Post-Comment Association
 * For any forum post, when displayed, all comments associated with that post (matching postId)
 * should be rendered in the comment section.
 *
 * **Feature: startup-platform-template, Property 6: Post-Comment Association**
 * **Validates: Requirements 6.2**
 */

import { describe, it, expect } from 'vitest';
import { render, screen, cleanup } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import * as fc from 'fast-check';
import CommentList from '../components/CommentList';
import type { Post, Comment } from '../types';

// Arbitrary for generating random Post data
// Using fc.constantFrom for createdAt to avoid Invalid time value errors
const postArbitrary = fc.record({
  id: fc.uuid(),
  title: fc.stringMatching(/^[A-Z][a-zA-Z0-9 ]{4,30}$/),
  content: fc.stringMatching(/^[A-Z][a-zA-Z0-9 ]{10,50}$/),
  authorId: fc.uuid(),
  authorName: fc.stringMatching(/^[A-Z][a-z]{2,8} [A-Z][a-z]{2,8}$/),
  upvotes: fc.integer({ min: 0, max: 1000 }),
  downvotes: fc.integer({ min: 0, max: 100 }),
  commentCount: fc.integer({ min: 0, max: 50 }),
  createdAt: fc.constantFrom('2024-01-15T10:30:00', '2024-02-20T14:45:00', '2024-03-10T09:00:00'),
});

// Generate a single comment with unique content
const generateComment = (postId: string, index: number): fc.Arbitrary<Comment> =>
  fc.record({
    id: fc.constant(`comment-${index}`),
    postId: fc.constant(postId),
    parentId: fc.constant(undefined),
    content: fc.constantFrom(
      `This is comment number ${index} with unique content`,
      `Another unique comment ${index} for testing`,
      `Comment ${index} discussing the post topic`,
      `Interesting point ${index} about the discussion`,
      `Reply ${index} with thoughtful feedback`
    ),
    authorId: fc.uuid(),
    authorName: fc.constantFrom(
      'Alice Smith',
      'Bob Johnson',
      'Carol Williams',
      'David Brown',
      'Eva Martinez'
    ),
    upvotes: fc.integer({ min: 0, max: 500 }),
    downvotes: fc.integer({ min: 0, max: 50 }),
    createdAt: fc.constantFrom('2024-01-15T10:30:00', '2024-02-20T14:45:00', '2024-03-10T09:00:00'),
  });

// Generate a post with 1-5 associated comments
const postWithCommentsArbitrary = postArbitrary.chain((post) =>
  fc.integer({ min: 1, max: 5 }).chain((numComments) =>
    fc
      .tuple(...Array.from({ length: numComments }, (_, i) => generateComment(post.id, i)))
      .map((comments) => ({ post, comments: comments as Comment[] }))
  )
);

describe('Property 6: Post-Comment Association', () => {
  /**
   * **Validates: Requirements 6.2**
   * Tests that all comments associated with a post are rendered in the comment section
   */
  it('all comments with matching postId should be rendered', () => {
    fc.assert(
      fc.property(postWithCommentsArbitrary, ({ post, comments }) => {
        cleanup();

        const mockOnVote = () => {};

        render(
          <MemoryRouter>
            <CommentList comments={comments} onVote={mockOnVote} />
          </MemoryRouter>
        );

        // Verify each comment's content is rendered using a flexible matcher
        comments.forEach((comment) => {
          const commentElement = screen.getByText((content) => content.includes(comment.content));
          expect(commentElement).toBeInTheDocument();
        });

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 6.2**
   * Tests that comment author names are displayed correctly
   */
  it('comment author names should be displayed for all comments', () => {
    fc.assert(
      fc.property(postWithCommentsArbitrary, ({ post, comments }) => {
        cleanup();

        const mockOnVote = () => {};

        render(
          <MemoryRouter>
            <CommentList comments={comments} onVote={mockOnVote} />
          </MemoryRouter>
        );

        // Verify each comment's author name is rendered
        comments.forEach((comment) => {
          const authorElements = screen.getAllByText(comment.authorName);
          expect(authorElements.length).toBeGreaterThan(0);
        });

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 6.2**
   * Tests that the comment count displayed matches the number of comments
   */
  it('comment count should match the number of comments rendered', () => {
    fc.assert(
      fc.property(postWithCommentsArbitrary, ({ post, comments }) => {
        cleanup();

        const mockOnVote = () => {};

        render(
          <MemoryRouter>
            <CommentList comments={comments} onVote={mockOnVote} />
          </MemoryRouter>
        );

        // The CommentList displays the count in the title
        const expectedText = comments.length === 1 ? '1 Comment' : `${comments.length} Comments`;
        const countElement = screen.getByText(expectedText);
        expect(countElement).toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 6.2**
   * Tests that the comments section has proper accessibility label
   */
  it('comments section should have proper accessibility label', () => {
    fc.assert(
      fc.property(postWithCommentsArbitrary, ({ post, comments }) => {
        cleanup();

        const mockOnVote = () => {};

        render(
          <MemoryRouter>
            <CommentList comments={comments} onVote={mockOnVote} />
          </MemoryRouter>
        );

        // Verify the comments section has proper aria-label
        const commentsSection = screen.getByRole('region', { name: /comments/i });
        expect(commentsSection).toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 6.2**
   * Tests that each comment has proper aria-label with author name
   */
  it('each comment should have aria-label containing author name', () => {
    fc.assert(
      fc.property(postWithCommentsArbitrary, ({ post, comments }) => {
        cleanup();

        const mockOnVote = () => {};

        render(
          <MemoryRouter>
            <CommentList comments={comments} onVote={mockOnVote} />
          </MemoryRouter>
        );

        // Get all comment articles
        const commentArticles = screen.getAllByRole('article');

        // Verify the number of articles matches the number of comments
        expect(commentArticles.length).toBe(comments.length);

        // Verify each article has an aria-label containing "Comment by"
        commentArticles.forEach((article) => {
          const ariaLabel = article.getAttribute('aria-label');
          expect(ariaLabel).toMatch(/Comment by .+/i);
        });

        // Verify each unique author name appears in at least one aria-label
        const uniqueAuthors = [...new Set(comments.map((c) => c.authorName))];
        uniqueAuthors.forEach((authorName) => {
          const articlesWithAuthor = commentArticles.filter((article) =>
            article.getAttribute('aria-label')?.includes(authorName)
          );
          expect(articlesWithAuthor.length).toBeGreaterThan(0);
        });

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 6.2**
   * Tests that empty comments array shows empty state message
   */
  it('empty comments array should show empty state message', () => {
    fc.assert(
      fc.property(postArbitrary, (post) => {
        cleanup();

        const mockOnVote = () => {};

        render(
          <MemoryRouter>
            <CommentList comments={[]} onVote={mockOnVote} />
          </MemoryRouter>
        );

        // Verify empty state message is shown
        const emptyMessage = screen.getByText(/no comments yet/i);
        expect(emptyMessage).toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 50 }
    );
  });

  /**
   * **Validates: Requirements 6.2**
   * Tests that comments from different posts are not mixed
   * (only comments with matching postId should be displayed)
   */
  it('only comments with matching postId should be displayed', () => {
    fc.assert(
      fc.property(
        fc.tuple(postArbitrary, postArbitrary).filter(([p1, p2]) => p1.id !== p2.id),
        ([post1, post2]) => {
          cleanup();

          const mockOnVote = () => {};

          // Generate comments for post1
          const commentsForPost1: Comment[] = [
            {
              id: 'comment-1',
              postId: post1.id,
              content: 'Comment for post one specific content',
              authorId: 'author-1',
              authorName: 'Author One',
              upvotes: 5,
              downvotes: 0,
              createdAt: '2024-01-15T10:30:00',
            },
          ];

          // Generate comments for post2
          const commentsForPost2: Comment[] = [
            {
              id: 'comment-2',
              postId: post2.id,
              content: 'Comment for post two different content',
              authorId: 'author-2',
              authorName: 'Author Two',
              upvotes: 3,
              downvotes: 1,
              createdAt: '2024-02-20T14:45:00',
            },
          ];

          // Render only comments for post1
          render(
            <MemoryRouter>
              <CommentList comments={commentsForPost1} onVote={mockOnVote} />
            </MemoryRouter>
          );

          // Verify post1's comment is displayed
          expect(screen.getByText('Comment for post one specific content')).toBeInTheDocument();

          // Verify post2's comment is NOT displayed
          expect(screen.queryByText('Comment for post two different content')).not.toBeInTheDocument();

          cleanup();
          return true;
        }
      ),
      { numRuns: 50 }
    );
  });
});
