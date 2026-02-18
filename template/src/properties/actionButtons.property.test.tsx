/**
 * Property 3: Action Button Presence
 * For any interactive card component, the appropriate action button should be present based on context:
 * - Investor idea cards should have "Add to Collaboration" button
 * - Innovator idea cards should have "Invest" button
 *
 * **Feature: startup-platform-template, Property 3: Action Button Presence**
 * **Validates: Requirements 4.2, 5.2**
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen, cleanup } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import * as fc from 'fast-check';
import IdeaCard from '../components/IdeaCard';
import type { Idea } from '../types';

// Arbitrary for generating random Idea data
const ideaArbitrary = fc.record({
  id: fc.uuid(),
  title: fc.stringMatching(/^[A-Z][a-zA-Z0-9 ]{4,30}$/),
  description: fc.stringMatching(/^[A-Z][a-zA-Z0-9 .,!?]{10,100}$/),
  authorId: fc.uuid(),
  authorName: fc.stringMatching(/^[A-Z][a-z]{2,10} [A-Z][a-z]{2,10}$/),
  status: fc.constantFrom<Idea['status']>('open', 'in-collaboration', 'funded', 'project'),
  investorCount: fc.integer({ min: 0, max: 100 }),
  createdAt: fc.constantFrom('2024-01-15T10:30:00', '2024-02-20T14:45:00', '2024-03-10T09:00:00'),
  tags: fc.array(fc.stringMatching(/^[a-z]{3,10}$/), { minLength: 0, maxLength: 5 }),
});

// Arbitrary for card variant
const variantArbitrary = fc.constantFrom<'investor' | 'innovator'>('investor', 'innovator');

describe('Property 3: Action Button Presence', () => {
  /**
   * **Validates: Requirements 4.2**
   * Tests that investor variant IdeaCards have "Add to Collaboration" button
   */
  it('investor variant IdeaCard should have "Add to Collaboration" button', () => {
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

        // Find the "Add to Collaboration" button
        const collaborateButton = screen.getByRole('button', {
          name: /add.*collaboration/i,
        });

        expect(collaborateButton).toBeInTheDocument();
        expect(collaborateButton).toHaveTextContent(/Add to Collaboration/i);

        // Verify the "Invest" button is NOT present for investor variant
        const investButton = screen.queryByRole('button', {
          name: /^invest$/i,
        });
        expect(investButton).not.toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 5.2**
   * Tests that innovator variant IdeaCards have "Invest" button
   */
  it('innovator variant IdeaCard should have "Invest" button', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        const mockOnInvest = vi.fn();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="innovator"
              onInvest={mockOnInvest}
            />
          </MemoryRouter>
        );

        // Find the "Invest" button
        const investButton = screen.getByRole('button', {
          name: /invest/i,
        });

        expect(investButton).toBeInTheDocument();
        expect(investButton).toHaveTextContent(/Invest/i);

        // Verify the "Add to Collaboration" button is NOT present for innovator variant
        const collaborateButton = screen.queryByRole('button', {
          name: /add.*collaboration/i,
        });
        expect(collaborateButton).not.toBeInTheDocument();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 4.2, 5.2**
   * Tests that any variant has exactly one appropriate action button
   */
  it('IdeaCard should have exactly one action button based on variant', () => {
    fc.assert(
      fc.property(ideaArbitrary, variantArbitrary, (idea, variant) => {
        cleanup();

        const mockOnCollaborate = vi.fn();
        const mockOnInvest = vi.fn();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant={variant}
              onCollaborate={mockOnCollaborate}
              onInvest={mockOnInvest}
            />
          </MemoryRouter>
        );

        // Get all buttons in the card
        const allButtons = screen.getAllByRole('button');

        // There should be exactly one action button
        expect(allButtons.length).toBe(1);

        // Verify the correct button is present based on variant
        if (variant === 'investor') {
          expect(allButtons[0]).toHaveTextContent(/Add to Collaboration|Added to Collaboration/i);
        } else {
          expect(allButtons[0]).toHaveTextContent(/Invest/i);
        }

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 4.2, 5.2**
   * Tests that buttons have correct accessible labels
   */
  it('action buttons should have accessible aria-labels', () => {
    fc.assert(
      fc.property(ideaArbitrary, variantArbitrary, (idea, variant) => {
        cleanup();

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant={variant}
              onCollaborate={vi.fn()}
              onInvest={vi.fn()}
            />
          </MemoryRouter>
        );

        const button = screen.getByRole('button');

        // Verify button has an aria-label attribute
        expect(button).toHaveAttribute('aria-label');

        // Verify aria-label contains the idea title for context
        const ariaLabel = button.getAttribute('aria-label');
        expect(ariaLabel).toContain(idea.title);

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });
});
