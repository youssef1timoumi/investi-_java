// Design System Color Tokens
export const colors = {
  // Primary background for dark sections and dashboard layouts
  black: '#000501',
  // Main light background for pages, cards, and content areas
  lavenderMist: '#F7F0F5',
  // Primary UI elements: buttons, links, active states, navigation highlights
  balticBlue: '#456990',
  // Secondary accents: borders, icons, badges, subtle emphasis
  fadedCopper: '#9B7E46',
  // Critical actions only: errors, warnings, destructive buttons
  brownRed: '#A62639',
} as const;

// Responsive breakpoints
export const breakpoints = {
  mobile: '480px',
  tablet: '768px',
  desktop: '1024px',
  wide: '1280px',
} as const;

// Spacing scale
export const spacing = {
  xs: '0.25rem',
  sm: '0.5rem',
  md: '1rem',
  lg: '1.5rem',
  xl: '2rem',
  xxl: '3rem',
} as const;

// Typography
export const typography = {
  fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
  fontSize: {
    xs: '0.75rem',
    sm: '0.875rem',
    md: '1rem',
    lg: '1.25rem',
    xl: '1.5rem',
    xxl: '2rem',
    xxxl: '3rem',
  },
  fontWeight: {
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
  },
} as const;

export type ColorKey = keyof typeof colors;
export type BreakpointKey = keyof typeof breakpoints;
