/**
 * Property 1: Role Selection Routes Correctly
 * For any user role selection (Admin, Investor, or Innovator), the platform should navigate
 * to the corresponding role-specific page (Admin Dashboard, Investor View, or Innovator View respectively).
 *
 * **Feature: startup-platform-template, Property 1: Role Selection Routes Correctly**
 * **Validates: Requirements 2.3, 2.4, 2.5**
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { MemoryRouter, Routes, Route, useLocation } from 'react-router-dom';
import * as fc from 'fast-check';
import RoleSelector from '../components/RoleSelector';

// Role to route mapping as defined in requirements
const roleRouteMapping: Record<'admin' | 'investor' | 'innovator', string> = {
  admin: '/admin',
  investor: '/investor',
  innovator: '/innovator',
};

// Helper component to capture current location for testing
function LocationDisplay() {
  const location = useLocation();
  return <div data-testid="location-display">{location.pathname}</div>;
}

// Test wrapper that provides routing context
function TestWrapper({
  children,
  initialEntries = ['/login'],
}: {
  children: React.ReactNode;
  initialEntries?: string[];
}) {
  return (
    <MemoryRouter initialEntries={initialEntries}>
      <Routes>
        <Route path="/login" element={children} />
        <Route path="/admin" element={<LocationDisplay />} />
        <Route path="/investor" element={<LocationDisplay />} />
        <Route path="/innovator" element={<LocationDisplay />} />
      </Routes>
    </MemoryRouter>
  );
}

describe('Property 1: Role Selection Routes Correctly', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should navigate to the correct route for any role selection', () => {
    // Arbitrary for role types
    const roleArbitrary = fc.constantFrom<'admin' | 'investor' | 'innovator'>(
      'admin',
      'investor',
      'innovator'
    );

    fc.assert(
      fc.property(roleArbitrary, (role) => {
        const mockOnRoleSelect = vi.fn();

        const { unmount } = render(
          <TestWrapper>
            <RoleSelector onRoleSelect={mockOnRoleSelect} />
          </TestWrapper>
        );

        // Find the role card by its title
        const roleTitle = role.charAt(0).toUpperCase() + role.slice(1);
        const roleCard = screen.getByRole('button', {
          name: new RegExp(`Select ${roleTitle} role`, 'i'),
        });

        expect(roleCard).toBeInTheDocument();

        // Click the role card
        act(() => {
          fireEvent.click(roleCard);
        });

        // Verify the callback was called with the correct role
        expect(mockOnRoleSelect).toHaveBeenCalledWith(role);

        // Advance timers to trigger navigation (RoleSelector has 200ms delay)
        act(() => {
          vi.advanceTimersByTime(200);
        });

        // Verify navigation occurred to the correct route
        const expectedRoute = roleRouteMapping[role];
        const locationDisplay = screen.getByTestId('location-display');
        expect(locationDisplay).toHaveTextContent(expectedRoute);

        unmount();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  it('should have correct route mapping for Admin role (Requirement 2.3)', () => {
    fc.assert(
      fc.property(fc.constant('admin' as const), (role) => {
        const mockOnRoleSelect = vi.fn();

        const { unmount } = render(
          <TestWrapper>
            <RoleSelector onRoleSelect={mockOnRoleSelect} />
          </TestWrapper>
        );

        const roleCard = screen.getByRole('button', {
          name: /Select Admin role/i,
        });

        act(() => {
          fireEvent.click(roleCard);
        });

        act(() => {
          vi.advanceTimersByTime(200);
        });

        const locationDisplay = screen.getByTestId('location-display');
        expect(locationDisplay).toHaveTextContent('/admin');

        unmount();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  it('should have correct route mapping for Investor role (Requirement 2.4)', () => {
    fc.assert(
      fc.property(fc.constant('investor' as const), (role) => {
        const mockOnRoleSelect = vi.fn();

        const { unmount } = render(
          <TestWrapper>
            <RoleSelector onRoleSelect={mockOnRoleSelect} />
          </TestWrapper>
        );

        const roleCard = screen.getByRole('button', {
          name: /Select Investor role/i,
        });

        act(() => {
          fireEvent.click(roleCard);
        });

        act(() => {
          vi.advanceTimersByTime(200);
        });

        const locationDisplay = screen.getByTestId('location-display');
        expect(locationDisplay).toHaveTextContent('/investor');

        unmount();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  it('should have correct route mapping for Innovator role (Requirement 2.5)', () => {
    fc.assert(
      fc.property(fc.constant('innovator' as const), (role) => {
        const mockOnRoleSelect = vi.fn();

        const { unmount } = render(
          <TestWrapper>
            <RoleSelector onRoleSelect={mockOnRoleSelect} />
          </TestWrapper>
        );

        const roleCard = screen.getByRole('button', {
          name: /Select Innovator role/i,
        });

        act(() => {
          fireEvent.click(roleCard);
        });

        act(() => {
          vi.advanceTimersByTime(200);
        });

        const locationDisplay = screen.getByTestId('location-display');
        expect(locationDisplay).toHaveTextContent('/innovator');

        unmount();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  it('should provide visual selection feedback before navigation', () => {
    const roleArbitrary = fc.constantFrom<'admin' | 'investor' | 'innovator'>(
      'admin',
      'investor',
      'innovator'
    );

    fc.assert(
      fc.property(roleArbitrary, (role) => {
        const mockOnRoleSelect = vi.fn();

        const { unmount } = render(
          <TestWrapper>
            <RoleSelector onRoleSelect={mockOnRoleSelect} />
          </TestWrapper>
        );

        const roleTitle = role.charAt(0).toUpperCase() + role.slice(1);
        const roleCard = screen.getByRole('button', {
          name: new RegExp(`Select ${roleTitle} role`, 'i'),
        });

        // Before click, should show "Click to select"
        expect(roleCard).toHaveAttribute('aria-pressed', 'false');

        // Click the role card
        act(() => {
          fireEvent.click(roleCard);
        });

        // After click, should show selection feedback
        expect(roleCard).toHaveAttribute('aria-pressed', 'true');

        unmount();
        return true;
      }),
      { numRuns: 100 }
    );
  });

  it('should support keyboard navigation for role selection', () => {
    const roleArbitrary = fc.constantFrom<'admin' | 'investor' | 'innovator'>(
      'admin',
      'investor',
      'innovator'
    );

    const keyArbitrary = fc.constantFrom('Enter', ' ');

    fc.assert(
      fc.property(roleArbitrary, keyArbitrary, (role, key) => {
        const mockOnRoleSelect = vi.fn();

        const { unmount } = render(
          <TestWrapper>
            <RoleSelector onRoleSelect={mockOnRoleSelect} />
          </TestWrapper>
        );

        const roleTitle = role.charAt(0).toUpperCase() + role.slice(1);
        const roleCard = screen.getByRole('button', {
          name: new RegExp(`Select ${roleTitle} role`, 'i'),
        });

        // Trigger keyboard event
        act(() => {
          fireEvent.keyDown(roleCard, { key });
        });

        // Verify the callback was called with the correct role
        expect(mockOnRoleSelect).toHaveBeenCalledWith(role);

        // Advance timers to trigger navigation
        act(() => {
          vi.advanceTimersByTime(200);
        });

        // Verify navigation occurred to the correct route
        const expectedRoute = roleRouteMapping[role];
        const locationDisplay = screen.getByTestId('location-display');
        expect(locationDisplay).toHaveTextContent(expectedRoute);

        unmount();
        return true;
      }),
      { numRuns: 100 }
    );
  });
});
