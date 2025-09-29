import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import { apiService, Product, ProductFilters } from '../api/service';

interface ProductsState {
    products: Product[];
    featuredProducts: Product[];
    categories: string[];
    loading: boolean;
    error: string | null;

    // Actions
    fetchProducts: (filters?: ProductFilters) => Promise<void>;
    fetchFeaturedProducts: () => Promise<void>;
    searchProducts: (query: string) => Promise<Product[]>;
    clearError: () => void;
}

export const useProductsStore = create<ProductsState>()(
    devtools(
        (set, get) => ({
            products: [],
            featuredProducts: [],
            categories: [],
            loading: false,
            error: null,

            fetchProducts: async (filters?: ProductFilters) => {
                set({ loading: true, error: null });
                try {
                    const products = await apiService.getProducts(filters);
                    set({ products, loading: false });

                    // Extract unique categories
                    const categories = [...new Set(products.map(p => p.category))];
                    set({ categories });
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to fetch products',
                        loading: false
                    });
                }
            },

            fetchFeaturedProducts: async () => {
                set({ loading: true, error: null });
                try {
                    // For now, fetch all products and take first 8 as featured
                    // In a real app, backend would have a featured endpoint
                    const products = await apiService.getProducts({ size: 8 });
                    set({ featuredProducts: products, loading: false });
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to fetch featured products',
                        loading: false
                    });
                }
            },

            searchProducts: async (query: string) => {
                set({ loading: true, error: null });
                try {
                    const products = await apiService.searchProducts(query);
                    set({ loading: false });
                    return products;
                } catch (error) {
                    set({
                        error: error instanceof Error ? error.message : 'Failed to search products',
                        loading: false
                    });
                    return [];
                }
            },

            clearError: () => set({ error: null }),
        }),
        { name: 'products-store' }
    )
);
