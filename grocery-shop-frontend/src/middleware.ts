import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    const token = request.cookies.get('auth_token')?.value;
    const isAuthPage = request.nextUrl.pathname.startsWith('/login') ||
        request.nextUrl.pathname.startsWith('/register');
    const isAdminPage = request.nextUrl.pathname.startsWith('/admin');
    const isPublicPage = request.nextUrl.pathname === '/' ||
        request.nextUrl.pathname.startsWith('/categories') ||
        request.nextUrl.pathname.startsWith('/products');

    // Only require auth for admin pages and account pages
    if (!token && !isAuthPage && !isPublicPage && (isAdminPage || request.nextUrl.pathname.startsWith('/account'))) {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    if (token && isAuthPage) {
        return NextResponse.redirect(new URL('/', request.url));
    }

    // Additional admin role checking would go here
    if (isAdminPage && token) {
        // TODO: Check if user has admin role
        // For now, allow access
    }

    return NextResponse.next();
}

export const config = {
    matcher: [
        '/((?!api|_next/static|_next/image|favicon.ico).*)',
    ],
};