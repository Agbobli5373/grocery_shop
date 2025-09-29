import { authService } from '../auth/service';

// Types
export interface OrderUpdate {
    orderId: string;
    status: string;
    timestamp: string;
    details?: Record<string, unknown>;
}

export interface DeliveryUpdate {
    orderId: string;
    location: {
        lat: number;
        lng: number;
    };
    eta: string;
    status: string;
}

export interface NotificationUpdate {
    id: string;
    type: 'ORDER_STATUS' | 'DELIVERY_UPDATE' | 'INVENTORY_ALERT' | 'PROMOTION';
    title: string;
    message: string;
    timestamp: string;
    read: boolean;
}

// Order tracking SSE service (inspired by Picnic's server-driven UI)
export class OrderSSEService {
    private eventSource: EventSource | null = null;
    private readonly baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    private onOrderUpdate?: (update: OrderUpdate) => void;
    private onDeliveryUpdate?: (update: DeliveryUpdate) => void;
    private onNotificationUpdate?: (update: NotificationUpdate) => void;

    connect(orderId?: string): void {
        const token = authService.getToken();
        if (!token) {
            console.error('No auth token available for SSE connection');
            return;
        }

        const url = orderId
            ? `${this.baseUrl}/orders/${orderId}/events?token=${token}`
            : `${this.baseUrl}/sse/events?token=${token}`;

        this.eventSource = new EventSource(url);

        this.eventSource.addEventListener('order-update', (event) => {
            const update: OrderUpdate = JSON.parse(event.data);
            this.handleOrderUpdate(update);
        });

        this.eventSource.addEventListener('delivery-update', (event) => {
            const update: DeliveryUpdate = JSON.parse(event.data);
            this.handleDeliveryUpdate(update);
        });

        this.eventSource.addEventListener('notification', (event) => {
            const update: NotificationUpdate = JSON.parse(event.data);
            this.handleNotificationUpdate(update);
        });

        this.eventSource.onerror = (error) => {
            console.error('SSE connection error:', error);
        };

        this.eventSource.onopen = () => {
            console.log('SSE connection established');
        };
    }

    disconnect(): void {
        this.eventSource?.close();
        this.eventSource = null;
    }

    onOrderUpdateCallback(callback: (update: OrderUpdate) => void): void {
        this.onOrderUpdate = callback;
    }

    onDeliveryUpdateCallback(callback: (update: DeliveryUpdate) => void): void {
        this.onDeliveryUpdate = callback;
    }

    onNotificationUpdateCallback(callback: (update: NotificationUpdate) => void): void {
        this.onNotificationUpdate = callback;
    }

    private handleOrderUpdate(update: OrderUpdate): void {
        console.log('Order update received:', update);
        this.onOrderUpdate?.(update);
    }

    private handleDeliveryUpdate(update: DeliveryUpdate): void {
        console.log('Delivery update received:', update);
        this.onDeliveryUpdate?.(update);
    }

    private handleNotificationUpdate(update: NotificationUpdate): void {
        console.log('Notification update received:', update);
        this.onNotificationUpdate?.(update);
    }
}

export const orderSSEService = new OrderSSEService();
