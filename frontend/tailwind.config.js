/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#0077BE',
          dark: '#005A8D',
          light: '#E6F4FB',
          pale: '#F2F9FC',
        },
        surface: '#FFFFFF',
        background: '#F8FAFC',
        border: '#E2E8F0',
        text: '#0F172A',
        muted: '#64748B',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        clay: '8px 8px 20px rgba(15,23,42,0.08), -6px -6px 16px rgba(255,255,255,0.9)',
        card: '0 8px 24px rgba(15,23,42,0.06), inset 1px 1px 2px rgba(255,255,255,0.8)',
        primaryBtn: '0 5px 12px rgba(0,119,190,0.22), inset 0 1px 1px rgba(255,255,255,0.25)',
      },
    },
  },
  plugins: [],
};