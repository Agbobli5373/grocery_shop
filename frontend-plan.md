# Grocery Shop Frontend - Next.js Architecture Plan

## Overview
This document outlines the implementation plan for a responsive Next.js frontend application that provides a seamless user experience for the Picnic-inspired grocery online shop. The frontend integrates with the event-driven Spring Boot backend via REST APIs and Server-Sent Events (SSE) for real-time updates, following Picnic's server-driven UI architecture.

## Architecture Overview

### Core Principles
- **Component-Based**: Modular, reusable React components
- **Server-Side Rendering**: Optimized performance with Next.js App Router
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Real-Time Updates**: Server-Sent Events (SSE) integration for order tracking
- **Progressive Enhancement**: Works without JavaScript for core functionality

### Technology Stack
- **Framework**: Next.js 15.1.8 (App Router)
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS 4.x with custom design system
- **State Management**: Zustand for client state, React Query for server state
- **API Integration**: Axios with interceptors for JWT handling
- **Forms**: React Hook Form with Zod validation
- **Real-Time**: Server-Sent Events (SSE) for order tracking updates (bridged from RabbitMQ events)
- **Testing**: Jest, React Testing Library, Playwright for E2E

### Next.js 15 Features to Leverage
- **App Router**: File-based routing with layouts and loading states
- **Server Components**: Optimized rendering and data fetching
- **Streaming**: Progressive loading of UI components
- **Turbopack**: Fast development builds
- **Partial Prerendering**: Hybrid static/dynamic rendering

## Application Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Next.js App   │    │   API Routes    │    │   External APIs │
│   (Pages)       │◄──►│   (Backend)     │◄──►│   (Spring Boot) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Components    │    │   State Mgmt    │    │   SSE           │
│   (UI Library)  │    │   (Zustand)     │    │   (Real-time)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Project Structure

```
frontend/
├── app/                          # Next.js App Router
│   ├── (auth)/                   # Route groups
│   │   ├── login/
│   │   ├── register/
│   │   └── layout.tsx
│   ├── (shop)/
│   │   ├── products/
│   │   ├── cart/
│   │   ├── checkout/
│   │   └── layout.tsx
│   ├── (admin)/
│   │   ├── dashboard/
│   │   ├── inventory/
│   │   └── layout.tsx
│   ├── api/                      # API routes for SSR
│   ├── globals.css
│   ├── layout.tsx
│   └── page.tsx
├── components/                   # Reusable components
│   ├── ui/                       # Base UI components
│   ├── forms/                    # Form components
│   ├── products/                 # Product-related components
│   ├── cart/                     # Shopping cart components
│   ├── orders/                   # Order components
│   └── layout/                   # Layout components
├── lib/                          # Utilities and configurations
│   ├── api/                      # API client and hooks
│   ├── auth/                     # Authentication utilities
│   ├── store/                    # State management
│   ├── utils/                    # Helper functions
│   ├── validations/              # Zod schemas
│   └── events/                   # Real-time connections (SSE)
├── hooks/                        # Custom React hooks
├── types/                        # TypeScript type definitions
├── styles/                       # Additional styles
├── public/                       # Static assets
└── middleware.ts                 # Next.js middleware
```

## Core Pages & Routes

### Public Routes
```
GET    /                     # Home/Landing page
GET    /products             # Product catalog
GET    /products/[id]        # Product details
GET    /search               # Search results
GET    /categories/[slug]    # Category pages
GET    /login                # Authentication
GET    /register             # User registration
```

### Protected Routes (Customer)
```
GET    /cart                 # Shopping cart
GET    /checkout             # Checkout process
GET    /orders               # Order history
GET    /orders/[id]          # Order details
GET    /profile              # User profile
GET    /profile/addresses    # Address management
```

### Admin Routes
```
GET    /admin/dashboard      # Admin overview
GET    /admin/products       # Product management
GET    /admin/orders         # Order management
GET    /admin/inventory      # Stock management
GET    /admin/analytics      # Analytics dashboard
GET    /admin/users          # User management
```

## Component Architecture

### UI Component Library
```typescript
// Base Components
export { Button } from './ui/button'
export { Input } from './ui/input'
export { Card } from './ui/card'
export { Modal } from './ui/modal'
export { Badge } from './ui/badge'
export { Spinner } from './ui/spinner'
export { Alert } from './ui/alert'

// Layout Components
export { Header } from './layout/header'
export { Footer } from './layout/footer'
export { Sidebar } from './layout/sidebar'
export { Breadcrumbs } from './layout/breadcrumbs'
```

### Feature Components

#### Product Components
```typescript
export { ProductCard } from './products/product-card'
export { ProductGrid } from './products/product-grid'
export { ProductFilters } from './products/product-filters'
export { ProductSearch } from './products/product-search'
export { ProductRecommendations } from './products/recommendations'
```

#### Cart Components
```typescript
export { CartItem } from './cart/cart-item'
export { CartSummary } from './cart/cart-summary'
export { AddToCartButton } from './cart/add-to-cart-button'
export { CartDrawer } from './cart/cart-drawer'
```

#### Order Components
```typescript
export { OrderList } from './orders/order-list'
export { OrderDetails } from './orders/order-details'
export { OrderTracking } from './orders/order-tracking'
export { OrderStatusBadge } from './orders/order-status-badge'
```

## State Management

### Client State (Zustand)
```typescript
// Cart Store
interface CartState {
  items: CartItem[]
  total: number
  addItem: (product: Product, quantity: number) => void
  removeItem: (productId: string) => void
  updateQuantity: (productId: string, quantity: number) => void
  clearCart: () => void
}

// User Store
interface UserState {
  user: User | null
  isAuthenticated: boolean
  login: (credentials: LoginCredentials) => Promise<void>
  logout: () => void
  updateProfile: (profile: Partial<User>) => Promise<void>
}

// UI Store
interface UIState {
  isCartOpen: boolean
  isSearchOpen: boolean
  notifications: Notification[]
  toggleCart: () => void
  toggleSearch: () => void
  addNotification: (notification: Notification) => void
}
```

### Server State (React Query)
```typescript
// API Hooks
export const useProducts = (filters: ProductFilters) =>
  useQuery(['products', filters], () => api.getProducts(filters))

export const useProduct = (id: string) =>
  useQuery(['product', id], () => api.getProduct(id), {
    enabled: !!id
  })

export const useCart = () =>
  useQuery(['cart'], api.getCart, {
    staleTime: 1000 * 60 * 5 // 5 minutes
  })

export const useOrders = () =>
  useQuery(['orders'], api.getOrders, {
    staleTime: 1000 * 60 * 10 // 10 minutes
  })
```

## API Integration

### HTTP Client Configuration
```typescript
// Axios instance with interceptors
const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 10000,
})

// Request interceptor for JWT
apiClient.interceptors.request.use((config) => {
  const token = getAuthToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle token refresh or redirect to login
      redirectToLogin()
    }
    return Promise.reject(error)
  }
)
```

### API Service Layer
```typescript
export class ApiService {
  // Authentication
  async login(credentials: LoginRequest): Promise<AuthResponse>
  async register(userData: RegisterRequest): Promise<User>
  async refreshToken(): Promise<AuthResponse>
  async logout(): Promise<void>

  // Products
  async getProducts(filters: ProductFilters): Promise<Product[]>
  async getProduct(id: string): Promise<Product>
  async searchProducts(query: string): Promise<Product[]>
  async getRecommendations(userId: string): Promise<Product[]>

  // Cart
  async getCart(): Promise<Cart>
  async addToCart(item: AddToCartRequest): Promise<Cart>
  async updateCartItem(itemId: string, quantity: number): Promise<Cart>
  async removeFromCart(itemId: string): Promise<Cart>

  // Orders
  async createOrder(orderData: CreateOrderRequest): Promise<Order>
  async getOrders(): Promise<Order[]>
  async getOrder(id: string): Promise<Order>
  async cancelOrder(id: string): Promise<Order>
}
```

## Real-Time Features

### Server-Sent Events (SSE) Integration
```typescript
// Order tracking SSE service (inspired by Picnic's server-driven UI)
export class OrderSSEService {
  private eventSource: EventSource | null = null

  connect(orderId: string): void {
    const token = getAuthToken()
    this.eventSource = new EventSource(
      `${API_URL}/orders/${orderId}/events?token=${token}`
    )

    this.eventSource.addEventListener('order-update', (event) => {
      const update: OrderUpdate = JSON.parse(event.data)
      // Update order status in store (server-driven UI approach)
      updateOrderStatus(update)
    })

    this.eventSource.addEventListener('delivery-update', (event) => {
      const update: DeliveryUpdate = JSON.parse(event.data)
      // Update delivery tracking
      updateDeliveryStatus(update)
    })

    this.eventSource.onerror = () => {
      // SSE automatically reconnects, but we can add custom logic
      console.log('SSE connection error, will auto-reconnect')
    }
  }

  disconnect(): void {
    this.eventSource?.close()
    this.eventSource = null
  }
}
```

## Form Management & Validation

### Zod Schemas
```typescript
// Authentication schemas
export const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
})

export const registerSchema = z.object({
  firstName: z.string().min(2, 'First name is required'),
  lastName: z.string().min(2, 'Last name is required'),
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
})

// Product schemas
export const productFilterSchema = z.object({
  category: z.string().optional(),
  minPrice: z.number().min(0).optional(),
  maxPrice: z.number().min(0).optional(),
  search: z.string().optional(),
  sortBy: z.enum(['name', 'price', 'rating']).optional(),
  sortOrder: z.enum(['asc', 'desc']).optional(),
})
```

### Form Components
```typescript
interface FormFieldProps {
  name: string
  label: string
  type?: string
  placeholder?: string
  required?: boolean
  error?: string
}

export function FormField({ name, label, type = 'text', ...props }: FormFieldProps) {
  const { register, formState: { errors } } = useFormContext()

  return (
    <div className="space-y-2">
      <Label htmlFor={name}>{label}</Label>
      <Input
        id={name}
        type={type}
        {...register(name)}
        {...props}
      />
      {errors[name] && (
        <p className="text-sm text-red-600">{errors[name].message}</p>
      )}
    </div>
  )
}
```

## Styling & Design System

### Tailwind Configuration
```javascript
// tailwind.config.js
module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
    './app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f9ff',
          500: '#3b82f6',
          600: '#2563eb',
          900: '#1e3a8a',
        },
        secondary: {
          50: '#f8fafc',
          500: '#64748b',
          600: '#475569',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
    require('@tailwindcss/aspect-ratio'),
  ],
}
```

### Design Tokens
```typescript
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
}
```

## Authentication Flow

### JWT Token Management
```typescript
export class AuthService {
  private tokenKey = 'auth_token'
  private refreshTokenKey = 'refresh_token'

  getToken(): string | null {
    if (typeof window === 'undefined') return null
    return localStorage.getItem(this.tokenKey)
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token)
  }

  getRefreshToken(): string | null {
    if (typeof window === 'undefined') return null
    return localStorage.getItem(this.refreshTokenKey)
  }

  setRefreshToken(token: string): void {
    localStorage.setItem(this.refreshTokenKey, token)
  }

  clearTokens(): void {
    localStorage.removeItem(this.tokenKey)
    localStorage.removeItem(this.refreshTokenKey)
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.exp * 1000 < Date.now()
    } catch {
      return true
    }
  }
}
```

### Route Protection
```typescript
// middleware.ts
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
  const token = request.cookies.get('auth_token')?.value
  const isAuthPage = request.nextUrl.pathname.startsWith('/login') ||
                     request.nextUrl.pathname.startsWith('/register')
  const isAdminPage = request.nextUrl.pathname.startsWith('/admin')

  if (!token && !isAuthPage) {
    return NextResponse.redirect(new URL('/login', request.url))
  }

  if (token && isAuthPage) {
    return NextResponse.redirect(new URL('/', request.url))
  }

  // Additional admin role checking would go here

  return NextResponse.next()
}

export const config = {
  matcher: [
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
}
```

## Performance Optimization

### Image Optimization
```typescript
import Image from 'next/image'

export function ProductImage({ src, alt, ...props }) {
  return (
    <Image
      src={src}
      alt={alt}
      width={400}
      height={400}
      sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
      placeholder="blur"
      blurDataURL="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ..."
      {...props}
    />
  )
}
```

### Code Splitting & Lazy Loading
```typescript
import dynamic from 'next/dynamic'

const AdminDashboard = dynamic(() => import('../components/admin/dashboard'), {
  loading: () => <div>Loading...</div>,
  ssr: false, // Only load on client side
})

const OrderTracking = dynamic(() => import('../components/orders/tracking'), {
  loading: () => <Spinner />,
})
```

## Testing Strategy

### Unit Tests
```typescript
// Component testing
describe('ProductCard', () => {
  it('renders product information correctly', () => {
    const product = { id: '1', name: 'Apple', price: 1.99 }
    render(<ProductCard product={product} />)

    expect(screen.getByText('Apple')).toBeInTheDocument()
    expect(screen.getByText('$1.99')).toBeInTheDocument()
  })

  it('calls onAddToCart when add button is clicked', () => {
    const mockOnAddToCart = jest.fn()
    const product = { id: '1', name: 'Apple', price: 1.99 }

    render(<ProductCard product={product} onAddToCart={mockOnAddToCart} />)

    fireEvent.click(screen.getByRole('button', { name: /add to cart/i }))
    expect(mockOnAddToCart).toHaveBeenCalledWith(product)
  })
})
```

### Integration Tests
```typescript
// API integration testing
describe('Cart API', () => {
  it('adds item to cart successfully', async () => {
    const cartItem = { productId: '1', quantity: 2 }

    const response = await api.addToCart(cartItem)

    expect(response.status).toBe(200)
    expect(response.data.items).toContainEqual(
      expect.objectContaining(cartItem)
    )
  })
})
```

### E2E Tests (Playwright)
```typescript
test('complete purchase flow', async ({ page }) => {
  // Navigate to product page
  await page.goto('/products/1')

  // Add to cart
  await page.click('[data-testid="add-to-cart"]')

  // Go to cart
  await page.click('[data-testid="cart-link"]')

  // Proceed to checkout
  await page.click('[data-testid="checkout-button"]')

  // Fill checkout form
  await page.fill('[data-testid="email"]', 'test@example.com')
  await page.fill('[data-testid="address"]', '123 Main St')

  // Complete purchase
  await page.click('[data-testid="place-order"]')

  // Verify success
  await expect(page.locator('[data-testid="order-confirmation"]')).toBeVisible()
})
```

## Deployment & DevOps

### Build Configuration
```javascript
// next.config.js
module.exports = {
  experimental: {
    optimizePackageImports: ['@heroicons/react', 'lucide-react'],
  },
  images: {
    domains: ['api.grocery-shop.com'],
    formats: ['image/webp', 'image/avif'],
  },
}
```

### Environment Variables
```bash
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_WS_URL=ws://localhost:8080

# .env.production
NEXT_PUBLIC_API_URL=https://api.grocery-shop.com
NEXT_PUBLIC_WS_URL=wss://api.grocery-shop.com
```

### Docker Configuration
```dockerfile
FROM node:20-alpine AS base

# Install dependencies only when needed
FROM base AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci --only=production

# Rebuild the source code only when needed
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

RUN npm run build

# Production image, copy all the files and run next
FROM base AS runner
WORKDIR /app

ENV NODE_ENV production

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public

# Set the correct permission for prerender cache
RUN mkdir .next
RUN chown nextjs:nodejs .next

# Automatically leverage output traces to reduce image size
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT 3000

CMD ["node", "server.js"]
```

## Implementation Phases

### Phase 1: Foundation (Week 1)
- [ ] Set up Next.js project with TypeScript and Tailwind
- [ ] Configure project structure and routing
- [ ] Implement basic layout components (Header, Footer, Navigation)
- [ ] Set up authentication pages (Login, Register)
- [ ] Configure API client and error handling

### Phase 2: Product Catalog (Week 2)
- [ ] Build product listing and detail pages
- [ ] Implement search and filtering functionality
- [ ] Create product card and grid components
- [ ] Add shopping cart functionality
- [ ] Implement state management for cart

### Phase 3: Checkout & Orders (Week 3)
- [ ] Build checkout flow with form validation
- [ ] Implement order creation and confirmation
- [ ] Add order history and tracking pages
- [ ] Integrate Server-Sent Events (SSE) for real-time updates
- [ ] Implement user profile management

### Phase 4: Admin & Optimization (Week 4)
- [ ] Build admin dashboard with product management
- [ ] Implement inventory management interface
- [ ] Add analytics and reporting features
- [ ] Performance optimization and SEO
- [ ] Comprehensive testing and deployment

## Success Metrics

### Performance
- **First Contentful Paint**: < 1.5 seconds
- **Largest Contentful Paint**: < 2.5 seconds
- **Time to Interactive**: < 3 seconds
- **Lighthouse Score**: > 90
- **Bundle Size**: < 200KB (initial load)

### User Experience
- **Mobile Responsiveness**: 100% mobile-friendly
- **Accessibility**: WCAG 2.1 AA compliance
- **Cross-browser Support**: Chrome, Firefox, Safari, Edge
- **Error Rate**: < 1% of user sessions

### Business Metrics
- **Conversion Rate**: > 3% (visitors to orders)
- **Cart Abandonment**: < 60%
- **User Retention**: > 40% returning users
- **Search Success Rate**: > 85%

This frontend plan provides a comprehensive blueprint for building a modern, performant, and user-friendly grocery shopping experience that seamlessly integrates with the event-driven backend architecture.
