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
}

export const authService = new AuthService();