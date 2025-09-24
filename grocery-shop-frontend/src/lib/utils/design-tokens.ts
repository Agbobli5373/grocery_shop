// Design system constants
export const DESIGN_TOKENS = {
    borderRadius: {
        sm: '0.25rem',
        md: '0.375rem',
        lg: '0.5rem',
        xl: '0.75rem',
    },
    shadows: {
        sm: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
        md: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
        lg: '0 10px 15px -3px rgb(0 0 0 / 0.1)',
    },
    transitions: {
        fast: '150ms ease-in-out',
        normal: '250ms ease-in-out',
        slow: '350ms ease-in-out',
    },
} as const;