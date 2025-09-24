export interface Product {
    id: string;
    name: string;
    description: string;
    price: number;
    originalPrice?: number;
    image: string;
    category: string;
    inStock: boolean;
    rating?: number;
    reviewCount?: number;
    unit?: string;
    discount?: number;
}

export interface Category {
    id: string;
    name: string;
    slug: string;
    image: string;
    itemCount?: number;
}

export interface CartItem {
    id: string;
    productId: string;
    name: string;
    price: number;
    quantity: number;
    image?: string;
    unit?: string;
}

export interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: 'customer' | 'admin';
    phone?: string;
    avatar?: string;
}

export interface Address {
    id: string;
    street: string;
    city: string;
    region: string;
    country: string;
    postalCode?: string;
    isDefault: boolean;
}

export interface Order {
    id: string;
    userId: string;
    items: CartItem[];
    total: number;
    status: 'pending' | 'confirmed' | 'preparing' | 'out_for_delivery' | 'delivered' | 'cancelled';
    createdAt: string;
    deliveryAddress: Address;
    estimatedDelivery?: string;
}

export interface Notification {
    id: string;
    type: 'success' | 'error' | 'warning' | 'info';
    message: string;
    duration?: number;
}