import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

// Types
interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: 'CUSTOMER' | 'ADMIN';
}

interface LoginCredentials {
    email: string;
    password: string;
}

interface UserState {
    user: User | null;
    isAuthenticated: boolean;
    login: (user: User) => void;
    logout: () => void;
    updateProfile: (profile: Partial<User>) => Promise<void>;
}

// User Store
export const useUserStore = create<UserState>()(
    devtools(
        (set) => ({
            user: null,
            isAuthenticated: false,

            login: (user) => {
                set({ user, isAuthenticated: true });
            },

            logout: () => {
                set({ user: null, isAuthenticated: false });
            },

            updateProfile: async (profile) => {
                // TODO: Implement API call
                // const response = await api.updateProfile(profile);
                // set({ user: response.user });
                console.log('Update profile:', profile);
            },
        }),
        { name: 'user-store' }
    )
);
