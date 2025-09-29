import { create } from 'zustand';
import { Order } from '../api/service';
import { OrderUpdate, orderSSEService } from '../events/sse';

interface OrderState {
  orders: Order[];
  loading: boolean;
  error: string | null;
  setOrders: (orders: Order[]) => void;
  addOrder: (order: Order) => void;
  updateOrder: (orderId: string, updates: Partial<Order>) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  initializeSSE: () => void;
  cleanupSSE: () => void;
}

export const useOrderStore = create<OrderState>((set, get) => ({
  orders: [],
  loading: false,
  error: null,

  setOrders: (orders) => set({ orders }),

  addOrder: (order) => set((state) => ({
    orders: [order, ...state.orders]
  })),

  updateOrder: (orderId, updates) => set((state) => ({
    orders: state.orders.map(order =>
      order.id === orderId ? { ...order, ...updates } : order
    )
  })),

  setLoading: (loading) => set({ loading }),

  setError: (error) => set({ error }),

  initializeSSE: () => {
    // Connect to SSE for real-time order updates
    orderSSEService.connect();

    // Set up callbacks for order updates
    orderSSEService.onOrderUpdateCallback((update: OrderUpdate) => {
      const { updateOrder } = get();
      updateOrder(update.orderId, {
        status: update.status as Order['status'],
        updatedAt: update.timestamp
      });
    });
  },

  cleanupSSE: () => {
    orderSSEService.disconnect();
  }
}));
