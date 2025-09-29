import { apiService, LoginRequest, RegisterRequest, AuthResponse, User } from '../api/service';

export class AuthService {
    private tokenKey = 'auth_token';
    private refreshTokenKey = 'refresh_token';

    getToken(): string | null {
        if (typeof window === 'undefined') return null;
        return localStorage.getItem(this.tokenKey);
    }

    setToken(token: string): void {
        localStorage.setItem(this.tokenKey, token);
    }

    getRefreshToken(): string | null {
        if (typeof window === 'undefined') return null;
        return localStorage.getItem(this.refreshTokenKey);
    }

    setRefreshToken(token: string): void {
        localStorage.setItem(this.refreshTokenKey, token);
    }

    clearTokens(): void {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.refreshTokenKey);
    }

    isTokenExpired(token: string): boolean {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch {
            return true;
        }
    }

    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const response = await apiService.login(credentials);
        if (response.token && response.refreshToken) {
            this.setToken(response.token);
            this.setRefreshToken(response.refreshToken);
        }
        return response;
    }

    async register(userData: RegisterRequest): Promise<AuthResponse> {
        const response = await apiService.register(userData);
        // After registration, user needs to login
        return response;
    }

    async refreshToken(): Promise<AuthResponse> {
        const response = await apiService.refreshToken();
        if (response.token && response.refreshToken) {
            this.setToken(response.token);
            this.setRefreshToken(response.refreshToken);
        }
        return response;
    }

    logout(): void {
        this.clearTokens();
        // Redirect to login page
        if (typeof window !== 'undefined') {
            window.location.href = '/login';
        }
    }
}

export const authService = new AuthService();
