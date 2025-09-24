import { z } from 'zod';

// Authentication schemas
export const loginSchema = z.object({
    email: z.string().email('Invalid email address'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
});

export const registerSchema = z.object({
    firstName: z.string().min(2, 'First name is required'),
    lastName: z.string().min(2, 'Last name is required'),
    email: z.string().email('Invalid email address'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
});

// Product schemas
export const productFilterSchema = z.object({
    category: z.string().optional(),
    minPrice: z.number().min(0).optional(),
    maxPrice: z.number().min(0).optional(),
    search: z.string().optional(),
    sortBy: z.enum(['name', 'price', 'rating']).optional(),
    sortOrder: z.enum(['asc', 'desc']).optional(),
});

// Cart schemas
export const addToCartSchema = z.object({
    productId: z.string(),
    quantity: z.number().min(1),
});

// Order schemas
export const createOrderSchema = z.object({
    items: z.array(z.object({
        productId: z.string(),
        quantity: z.number().min(1),
    })),
    shippingAddress: z.object({
        street: z.string().min(1),
        city: z.string().min(1),
        state: z.string().min(1),
        zipCode: z.string().min(5),
        country: z.string().min(1),
    }),
    paymentMethod: z.enum(['credit_card', 'paypal', 'apple_pay']),
});