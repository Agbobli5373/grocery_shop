import { authService } from '../auth/service';

// Types
interface OrderUpdate {
    orderId: string;
    status: string;
    timestamp: string;
    details?: Record<string, unknown>;
}

interface DeliveryUpdate {
    orderId: string;
    location: {
        lat: number;
        lng: number;
    };
    eta: string;
    status: string;
}

// Order tracking SSE service (inspired by Picnic's server-driven UI)
export class OrderSSEService {
    private eventSource: EventSource | null = null;
    private readonly baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

    connect(orderId: string): void {
        const token = authService.getToken();
        if (!token) {
            console.error('No auth token available for SSE connection');
            return;
        }

        this.eventSource = new EventSource(
            `${this.baseUrl}/orders/${orderId}/events?token=${token}`
        );

        this.eventSource.addEventListener('order-update', (event) => {
            const update: OrderUpdate = JSON.parse(event.data);
            // Update order status in store (server-driven UI approach)
            this.handleOrderUpdate(update);
        });

        this.eventSource.addEventListener('delivery-update', (event) => {
            const update: DeliveryUpdate = JSON.parse(event.data);
            // Update delivery tracking
            this.handleDeliveryUpdate(update);
        });

        this.eventSource.onerror = () => {
            // SSE automatically reconnects, but we can add custom logic
            console.log('SSE connection error, will auto-reconnect');
        };
    }

    disconnect(): void {
        this.eventSource?.close();
        this.eventSource = null;
    }

    private handleOrderUpdate(update: OrderUpdate): void {
        // TODO: Update order status in store
        console.log('Order update received:', update);
    }

    private handleDeliveryUpdate(update: DeliveryUpdate): void {
        // TODO: Update delivery status in store
        console.log('Delivery update received:', update);
    }
}

export const orderSSEService = new OrderSSEService();