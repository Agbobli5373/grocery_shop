import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import type { CartItem } from '@/types';

type CartState = {
    items: CartItem[];
    total: number;
    addItem: (product: Omit<CartItem, 'quantity'>, quantity: number) => void;
    removeItem: (productId: string) => void;
    updateQuantity: (productId: string, quantity: number) => void;
    clearCart: () => void;
}

// Cart Store
export const useCartStore = create<CartState>()(
    devtools(
        (set, get) => ({
            items: [],
            total: 0,

            addItem: (product, quantity) => {
                const { items } = get();
                const existingItem = items.find(item => item.productId === product.productId);

                if (existingItem) {
                    existingItem.quantity += quantity;
                } else {
                    items.push({ ...product, quantity });
                }

                const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
                set({ items: [...items], total });
            },

            removeItem: (productId) => {
                const { items } = get();
                const filteredItems = items.filter(item => item.productId !== productId);
                const total = filteredItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
                set({ items: filteredItems, total });
            },

            updateQuantity: (productId, quantity) => {
                const { items } = get();
                const item = items.find(item => item.productId === productId);
                if (item) {
                    item.quantity = quantity;
                    const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
                    set({ items: [...items], total });
                }
            },

            clearCart: () => set({ items: [], total: 0 }),
        }),
        { name: 'cart-store' }
    )
);