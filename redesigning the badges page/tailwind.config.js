/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ["class"],
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "#9B7E46",
          light: "#B8983E",
          lighter: "#C8A84E",
          dark: "#8A6E3A",
          foreground: "#FFFFFF",
        },
        secondary: {
          DEFAULT: "#456990",
          light: "#5179A0",
          lighter: "#6189B0",
          dark: "#3A5A7D",
          foreground: "#FFFFFF",
        },
        dark: {
          DEFAULT: "#1A1A2E",
          light: "#2A2A3E",
          lighter: "#3A3A4E",
          foreground: "#FFFFFF",
        },
        surface: "#FFFFFF",
        background: "#F4F6FA",
        muted: {
          DEFAULT: "#6B7280",
          foreground: "#6B7280",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        destructive: {
          DEFAULT: "#DC3545",
          foreground: "#FFFFFF",
        },
        success: {
          DEFAULT: "#28A745",
          foreground: "#FFFFFF",
        },
        warning: {
          DEFAULT: "#FFC107",
          foreground: "#23272F",
        },
        info: {
          DEFAULT: "#17A2B8",
          foreground: "#FFFFFF",
        },
        border: "#E2E6ED",
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
      },
      fontFamily: {
        sans: ['"Segoe UI"', '"Helvetica Neue"', 'Arial', 'sans-serif'],
      },
      borderRadius: {
        xl: "calc(var(--radius) + 4px)",
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
        xs: "calc(var(--radius) - 6px)",
        '10': '10px',
        '12': '12px',
      },
      boxShadow: {
        xs: "0 1px 2px 0 rgb(0 0 0 / 0.05)",
        'card': '0 2px 8px rgba(0,0,0,0.04)',
        'card-hover': '0 8px 24px rgba(155,126,70,0.15)',
        'input': '0 4px 12px rgba(0,0,0,0.03)',
        'input-focus': '0 8px 24px rgba(155,126,70,0.18)',
        'button': '0 4px 15px rgba(0,0,0,0.08)',
        'button-hover': '0 8px 24px rgba(0,0,0,0.16)',
        'gold': '0 4px 20px rgba(155,126,70,0.3)',
        'gold-hover': '0 8px 30px rgba(155,126,70,0.4)',
      },
      keyframes: {
        "accordion-down": {
          from: { height: "0" },
          to: { height: "var(--radix-accordion-content-height)" },
        },
        "accordion-up": {
          from: { height: "var(--radix-accordion-content-height)" },
          to: { height: "0" },
        },
        "caret-blink": {
          "0%,70%,100%": { opacity: "1" },
          "20%,50%": { opacity: "0" },
        },
        "float": {
          "0%, 100%": { transform: "translateY(0)" },
          "50%": { transform: "translateY(-10px)" },
        },
        "pulse-glow": {
          "0%, 100%": { boxShadow: "0 4px 15px rgba(155,126,70,0.3)" },
          "50%": { boxShadow: "0 8px 30px rgba(155,126,70,0.5)" },
        },
        "shimmer": {
          "0%": { backgroundPosition: "-200% 0" },
          "100%": { backgroundPosition: "200% 0" },
        },
        "slide-up": {
          "0%": { transform: "translateY(40px)", opacity: "0" },
          "100%": { transform: "translateY(0)", opacity: "1" },
        },
        "slide-left": {
          "0%": { transform: "translateX(80px)", opacity: "0" },
          "100%": { transform: "translateX(0)", opacity: "1" },
        },
        "slide-right": {
          "0%": { transform: "translateX(-80px)", opacity: "0" },
          "100%": { transform: "translateX(0)", opacity: "1" },
        },
        "scale-in": {
          "0%": { transform: "scale(0.8)", opacity: "0" },
          "100%": { transform: "scale(1)", opacity: "1" },
        },
        "flip-in": {
          "0%": { transform: "rotateY(-90deg)", opacity: "0" },
          "100%": { transform: "rotateY(0)", opacity: "1" },
        },
        "pop-up": {
          "0%": { transform: "translateY(60px) scale(0.5) rotateX(45deg)", opacity: "0" },
          "100%": { transform: "translateY(0) scale(1) rotateX(0)", opacity: "1" },
        },
        "shake": {
          "0%, 100%": { transform: "translateX(0)" },
          "25%": { transform: "translateX(-5px)" },
          "75%": { transform: "translateX(5px)" },
        },
        "icon-pulse": {
          "0%, 100%": { transform: "scale(1)" },
          "50%": { transform: "scale(1.05)" },
        },
        "rotate-subtle": {
          "0%, 100%": { transform: "rotate(0deg)" },
          "50%": { transform: "rotate(5deg)" },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
        "caret-blink": "caret-blink 1.25s ease-out infinite",
        "float": "float 6s ease-in-out infinite",
        "pulse-glow": "pulse-glow 3s ease-in-out infinite",
        "shimmer": "shimmer 2s linear infinite",
        "slide-up": "slide-up 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards",
        "slide-left": "slide-left 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards",
        "slide-right": "slide-right 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards",
        "scale-in": "scale-in 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards",
        "flip-in": "flip-in 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards",
        "pop-up": "pop-up 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards",
        "shake": "shake 0.4s ease-in-out",
        "icon-pulse": "icon-pulse 3s ease-in-out infinite",
        "rotate-subtle": "rotate-subtle 4s ease-in-out infinite",
      },
    },
  },
  plugins: [require("tailwindcss-animate")],
}
