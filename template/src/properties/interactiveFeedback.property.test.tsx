/**
 * Property 4: Interactive Feedback
 * For any user interaction with an action button (collaboration, voting, inscription),
 * the UI should provide immediate visual feedback indicating the action was registered
 * (state change, color change, or visual indicator update).
 *
 * **Feature: startup-platform-template, Property 4: Interactive Feedback**
 * **Validates: Requirements 4.3**
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen, cleanup, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import * as fc from 'fast-check';
import IdeaCard from '../components/IdeaCard';
import type { Idea } from '../types';

// Arbitrary for generating random Idea data
// Using fc.constantFrom for createdAt to avoid Invalid time value errors
const ideaArbitrary = fc.record({
  id: fc.uuid(),
  title: fc.stringMatching(/^[A-Z][a-zA-Z0-9 ]{4,30}$/),
  description: fc.stringMatching(/^[A-Z][a-zA-Z0-9 .,!?]{10,100}$/),
  authorId: fc.uuid(),
  authorName: fc.stringMatching(/^[A-Z][a-z]{2,10} [A-Z][a-z]{2,10}$/),
  status: fc.constantFrom<Idea['status']>('open', 'in-collaboration'),
  investorCount: fc.integer({ min: 0, max: 100 }),
  createdAt: fc.constantFrom('2024-01-15T10:30:00', '2024-02-20T14:45:00', '2024-03-10T09:00:00'),
  tags: fc.array(fc.stringMatching(/^[a-z]{3,10}$/), { minLength: 0, maxLength: 5 }),
});

describe('Property 4: Interactive Feedback', () => {
  /**
   * **Validates: Requirements 4.3**
   * Tests that clicking "Add to Collaboration" button provides visual feedback
   * through button text change
   */
  it('clicking "Add to Collaboration" should change button text to indicate action was registered', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        const mockOnCollaborate = vi.fn();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="investor"
              onCollaborate={mockOnCollaborate}
            />
          </MemoryRouter>
        );

        // Find the "Add to Collaboration" button before clicking
        const collaborateButton = screen.getByRole('button', {
          name: /add.*collaboration/i,
        });

        // Verify initial state - button should say "Add to Collaboration"
        expect(collaborateButton).toHaveTextContent('Add to Collaboration');

        // Click the button
        fireEvent.click(collaborateButton);

        // Verify visual feedback - button text should change to "Added to Collaboration"
        expect(collaborateButton).toHaveTextContent('Added to Collaboration');

        // Verify the callback was called
        expect(mockOnCollaborate).toHaveBeenCalledTimes(1);

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 4.3**
   * Tests that clicking "Add to Collaboration" button disables the button
   * to prevent duplicate actions
   */
  it('clicking "Add to Collaboration" should disable the button after action', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        const mockOnCollaborate = vi.fn();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="investor"
              onCollaborate={mockOnCollaborate}
            />
          </MemoryRouter>
        );

        // Find the button
        const collaborateButton = screen.getByRole('button', {
          name: /add.*collaboration/i,
        });

        // Verify button is not disabled initially
        expect(collaborateButton).not.toBeDisabled();

        // Click the button
        fireEvent.click(collaborateButton);

        // Verify visual feedback - button should be disabled after click
        expect(collaborateButton).toBeDisabled();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 4.3**
   * Tests that clicking "Add to Collaboration" shows a collaboration indicator
   */
  it('clicking "Add to Collaboration" should show collaboration status indicator', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        const mockOnCollaborate = vi.fn();

        const { container } = render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="investor"
              onCollaborate={mockOnCollaborate}
            />
          </MemoryRouter>
        );

        // Find the button
        const collaborateButton = screen.getByRole('button', {
          name: /add.*collaboration/i,
        });

        // Check if collaboration indicator exists before click (may exist if status is 'in-collaboration')
        const indicatorBefore = container.querySelector('[aria-live="polite"]');
        const hadIndicatorBefore = indicatorBefore !== null;

        // Click the button
        fireEvent.click(collaborateButton);

        // After clicking, there should be a collaboration indicator visible
        const indicatorAfter = container.querySelector('[aria-live="polite"]');
        expect(indicatorAfter).not.toBeNull();
        expect(indicatorAfter?.textContent).toContain('Active Collaboration');

        // If there was no indicator before, this confirms visual feedback was provided
        if (!hadIndicatorBefore) {
          expect(indicatorAfter).toBeInTheDocument();
        }

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 4.3**
   * Tests that the aria-label updates to reflect the new state after clicking
   */
  it('clicking "Add to Collaboration" should update aria-label for accessibility', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        const mockOnCollaborate = vi.fn();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="investor"
              onCollaborate={mockOnCollaborate}
            />
          </MemoryRouter>
        );

        // Find the button
        const collaborateButton = screen.getByRole('button', {
          name: /add.*collaboration/i,
        });

        // Verify initial aria-label contains "Add"
        const initialAriaLabel = collaborateButton.getAttribute('aria-label');
        expect(initialAriaLabel).toContain('Add');
        expect(initialAriaLabel).toContain(idea.title);

        // Click the button
        fireEvent.click(collaborateButton);

        // Verify aria-label updates to reflect the completed action
        const updatedAriaLabel = collaborateButton.getAttribute('aria-label');
        expect(updatedAriaLabel).toContain('Added');

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 4.3**
   * Tests that multiple clicks do not trigger multiple callbacks (button is disabled)
   */
  it('disabled button should not trigger callback on subsequent clicks', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        const mockOnCollaborate = vi.fn();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="investor"
              onCollaborate={mockOnCollaborate}
            />
          </MemoryRouter>
        );

        // Find the button
        const collaborateButton = screen.getByRole('button', {
          name: /add.*collaboration/i,
        });

        // Click the button multiple times
        fireEvent.click(collaborateButton);
        fireEvent.click(collaborateButton);
        fireEvent.click(collaborateButton);

        // Callback should only be called once (button is disabled after first click)
        expect(mockOnCollaborate).toHaveBeenCalledTimes(1);

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });
});
