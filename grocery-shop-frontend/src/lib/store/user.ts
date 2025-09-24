import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

// Types
interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: 'customer' | 'admin';
}

interface LoginCredentials {
    email: string;
    password: string;
}

interface UserState {
    user: User | null;
    isAuthenticated: boolean;
    login: (credentials: LoginCredentials) => Promise<void>;
    logout: () => void;
    updateProfile: (profile: Partial<User>) => Promise<void>;
}

// User Store
export const useUserStore = create<UserState>()(
    devtools(
        (set) => ({
            user: null,
            isAuthenticated: false,

            login: async (credentials) => {
                // TODO: Implement API call
                // const response = await api.login(credentials);
                // set({ user: response.user, isAuthenticated: true });
                console.log('Login with:', credentials);
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