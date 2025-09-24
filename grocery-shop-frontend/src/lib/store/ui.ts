import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

// Types
interface Notification {
    id: string;
    type: 'success' | 'error' | 'warning' | 'info';
    message: string;
    duration?: number;
}

interface UIState {
    isCartOpen: boolean;
    isSearchOpen: boolean;
    notifications: Notification[];
    toggleCart: () => void;
    toggleSearch: () => void;
    addNotification: (notification: Omit<Notification, 'id'>) => void;
    removeNotification: (id: string) => void;
}

// UI Store
export const useUIStore = create<UIState>()(
    devtools(
        (set) => ({
            isCartOpen: false,
            isSearchOpen: false,
            notifications: [],

            toggleCart: () =>
                set((state) => ({ isCartOpen: !state.isCartOpen })),

            toggleSearch: () =>
                set((state) => ({ isSearchOpen: !state.isSearchOpen })),

            addNotification: (notification) => {
                const id = Date.now().toString();
                set((state) => ({
                    notifications: [...state.notifications, { ...notification, id }],
                }));
            },

            removeNotification: (id) =>
                set((state) => ({
                    notifications: state.notifications.filter((n) => n.id !== id),
                })),
        }),
        { name: 'ui-store' }
    )
);