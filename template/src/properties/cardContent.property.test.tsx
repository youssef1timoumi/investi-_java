/**
 * Property 2: Card Content Completeness
 * For any data card component (ServiceCard, IdeaCard, ProposalCard, ProjectCard),
 * the rendered output should contain all required fields as defined by its data model.
 * 
 * **Feature: startup-platform-template, Property 2: Card Content Completeness**
 * **Validates: Requirements 1.3, 4.4, 8.2, 9.2**
 */

import { describe, it, expect } from 'vitest';
import { render, cleanup } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import * as fc from 'fast-check';
import ServiceCard from '../components/ServiceCard';

describe('Property 2: Card Content Completeness', () => {
  /**
   * **Validates: Requirements 1.3**
   * Tests that ServiceCards render all required fields (icon, title, description)
   */
  it('ServiceCard should render all required fields (icon, title, description)', () => {
    fc.assert(
      fc.property(
        fc.record({
          icon: fc.stringMatching(/^[a-zA-Z0-9]{1,5}$/),
          title: fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9 ]{2,20}$/),
          description: fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9 ]{5,50}$/),
          href: fc.constantFrom('/forum', '/events', '/projects', '/achievements'),
        }),
        (serviceData) => {
          cleanup();
          
          const { container } = render(
            <MemoryRouter>
              <ServiceCard
                icon={serviceData.icon}
                title={serviceData.title}
                description={serviceData.description}
                href={serviceData.href}
              />
            </MemoryRouter>
          );

          // Icon should be rendered (within span with icon class)
          const iconElement = container.querySelector('span[aria-hidden="true"]');
          expect(iconElement).not.toBeNull();
          expect(iconElement?.textContent).toBe(serviceData.icon);
          
          // Title should be rendered (h3 element)
          const titleElement = container.querySelector('h3');
          expect(titleElement).not.toBeNull();
          expect(titleElement?.textContent).toBe(serviceData.title);
          
          // Description should be rendered (p element)
          const descElement = container.querySelector('p');
          expect(descElement).not.toBeNull();
          expect(descElement?.textContent).toBe(serviceData.description);
          
          // Link should have correct href
          const linkElement = container.querySelector('a');
          expect(linkElement).not.toBeNull();
          expect(linkElement?.getAttribute('href')).toBe(serviceData.href);

          cleanup();
        }
      ),
      { numRuns: 100 }
    );
  });
});
