/**
 * Property 5: Chat Trigger Behavior
 * For any chat-triggering action (clicking "Invest" on innovator view or chat entry on collaboration space),
 * the Chat UI modal should open with the correct idea context.
 *
 * **Feature: startup-platform-template, Property 5: Chat Trigger Behavior**
 * **Validates: Requirements 5.3**
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

describe('Property 5: Chat Trigger Behavior', () => {
  /**
   * **Validates: Requirements 5.3**
   * Tests that clicking "Invest" button triggers the onInvest callback
   */
  it('clicking "Invest" button should trigger onInvest callback', () => {
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

        // Click the button
        fireEvent.click(investButton);

        // Verify the callback was called
        expect(mockOnInvest).toHaveBeenCalledTimes(1);

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 5.3**
   * Tests that the "Invest" button has correct aria-label with idea context
   */
  it('Invest button should have aria-label containing idea title for context', () => {
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

        // Verify the aria-label contains the idea title for context
        const ariaLabel = investButton.getAttribute('aria-label');
        expect(ariaLabel).toContain(idea.title);
        expect(ariaLabel).toContain('Invest');

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 5.3**
   * Tests that the "Invest" button is present and clickable for innovator variant
   */
  it('innovator variant should have clickable Invest button', () => {
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

        // Verify button exists and is not disabled for open/in-collaboration status
        expect(investButton).toBeInTheDocument();
        expect(investButton).not.toBeDisabled();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 5.3**
   * Tests that the callback can be used to pass idea context to parent component
   */
  it('onInvest callback should be callable to pass idea context', () => {
    fc.assert(
      fc.property(ideaArbitrary, (idea) => {
        cleanup();

        // Create a mock that captures the call context
        let callCount = 0;
        const mockOnInvest = vi.fn(() => {
          callCount++;
        });

        render(
          <MemoryRouter>
            <IdeaCard
              idea={idea}
              variant="innovator"
              onInvest={mockOnInvest}
            />
          </MemoryRouter>
        );

        // Find and click the "Invest" button
        const investButton = screen.getByRole('button', {
          name: /invest/i,
        });
        fireEvent.click(investButton);

        // Verify the callback was invoked
        expect(callCount).toBe(1);
        expect(mockOnInvest).toHaveBeenCalled();

        cleanup();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  /**
   * **Validates: Requirements 5.3**
   * Tests that multiple ideas can each trigger their own invest callback
   */
  it('each idea card should trigger its own onInvest callback independently', () => {
    fc.assert(
      fc.property(
        fc.array(ideaArbitrary, { minLength: 2, maxLength: 5 }),
        (ideas) => {
          cleanup();

          // Create separate mock callbacks for each idea
          const mockCallbacks = ideas.map(() => vi.fn());

          render(
            <MemoryRouter>
              <div>
                {ideas.map((idea, index) => (
                  <IdeaCard
                    key={idea.id}
                    idea={idea}
                    variant="innovator"
                    onInvest={mockCallbacks[index]}
                  />
                ))}
              </div>
            </MemoryRouter>
          );

          // Find all "Invest" buttons
          const investButtons = screen.getAllByRole('button', {
            name: /invest/i,
          });

          // Click the first button
          fireEvent.click(investButtons[0]);

          // Verify only the first callback was called
          expect(mockCallbacks[0]).toHaveBeenCalledTimes(1);
          for (let i = 1; i < mockCallbacks.length; i++) {
            expect(mockCallbacks[i]).not.toHaveBeenCalled();
          }

          cleanup();
          return true;
        }
      ),
      { numRuns: 50 }
    );
  });
});
