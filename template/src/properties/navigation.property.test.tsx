/**
 * Property 8: Navigation Routing
 * For any navigation link in the header or footer, clicking the link should navigate
 * to the correct corresponding route without errors.
 * 
 * **Feature: startup-platform-template, Property 8: Navigation Routing**
 * **Validates: Requirements 13.3**
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import * as fc from 'fast-check';
import Header from '../components/Header';
import Footer from '../components/Footer';

const navigationRoutes = [
  { label: 'Forum', href: '/forum' },
  { label: 'Events', href: '/events' },
  { label: 'Collaboration', href: '/collaboration' },
  { label: 'Projects', href: '/projects' },
  { label: 'Achievements', href: '/achievements' },
];

describe('Property 8: Navigation Routing', () => {
  it('Header navigation links should have correct href attributes', () => {
    fc.assert(
      fc.property(
        fc.constantFrom(...navigationRoutes),
        (route) => {
          const { unmount } = render(
            <MemoryRouter>
              <Header />
            </MemoryRouter>
          );

          const link = screen.getByRole('link', { name: route.label });
          expect(link).toBeInTheDocument();
          expect(link).toHaveAttribute('href', route.href);

          unmount();
        }
      ),
      { numRuns: 100 }
    );
  });

  it('Footer navigation links should have correct href attributes', () => {
    fc.assert(
      fc.property(
        fc.constantFrom(...navigationRoutes),
        (route) => {
          const { unmount } = render(
            <MemoryRouter>
              <Footer />
            </MemoryRouter>
          );

          const link = screen.getByRole('link', { name: route.label });
          expect(link).toBeInTheDocument();
          expect(link).toHaveAttribute('href', route.href);

          unmount();
        }
      ),
      { numRuns: 100 }
    );
  });

  it('Header logo should link to homepage', () => {
    fc.assert(
      fc.property(
        fc.boolean(),
        (isLoggedIn) => {
          const { unmount } = render(
            <MemoryRouter>
              <Header isLoggedIn={isLoggedIn} />
            </MemoryRouter>
          );

          const logoLink = screen.getByRole('link', { name: /go to homepage/i });
          expect(logoLink).toBeInTheDocument();
          expect(logoLink).toHaveAttribute('href', '/');

          unmount();
        }
      ),
      { numRuns: 100 }
    );
  });
});
