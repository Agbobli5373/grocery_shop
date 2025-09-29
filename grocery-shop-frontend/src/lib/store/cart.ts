import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import { apiService, CartItem, Cart } from '../api/service';

interface CartState {
    items: CartItem[];
    total: number;
    loading: boolean;
    error: string | null;

    // Actions
    fetchCart: () => Promise<void>;
    addItem: (productId: string, quantity: number) => Promise<void>;
    updateItem: (itemId: string, quantity: number) => Promise<void>;
    removeItem: (itemId: string) => Promise<void>;
    clearCart: () => void;
    clearError: () => void;
}

export const useCartStore = create<CartState>()(
    devtools(
        (set, get) => ({
            items: [],
            total: 0,
            loading: false,
            error: null,

            fetchCart: async () => {
                set({ loading: true, error: null });
                try {
                    const cart = await apiService.getCart();
                    set({
                        items: cart.items,
                        total: cart.total,
                        loading: false
                    });
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to fetch cart',
                        loading: false
                    });
                }
            },

            addItem: async (productId: string, quantity: number) => {
                set({ loading: true, error: null });
                try {
                    const cart = await apiService.addToCart({ productId, quantity });
                    set({
                        items: cart.items,
                        total: cart.total,
                        loading: false
                    });
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to add item to cart',
                        loading: false
                    });
                }
            },

            updateItem: async (itemId: string, quantity: number) => {
                set({ loading: true, error: null });
                try {
                    const cart = await apiService.updateCartItem(itemId, { quantity });
                    set({
                        items: cart.items,
                        total: cart.total,
                        loading: false
                    });
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to update cart item',
                        loading: false
                    });
                }
            },

            removeItem: async (itemId: string) => {
                set({ loading: true, error: null });
                try {
                    const cart = await apiService.removeFromCart(itemId);
                    set({
                        items: cart.items,
                        total: cart.total,
                        loading: false
                    });
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to remove item from cart',
                        loading: false
                    });
                }
            },

            clearCart: () => set({ items: [], total: 0, error: null }),

            clearError: () => set({ error: null }),
        }),
        { name: 'cart-store' }
    )
);
