import apiClient from './client';

// Types based on backend DTOs
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string | null;
  refreshToken: string | null;
  email: string;
  firstName: string;
  lastName: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE';
}

export interface UserResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'CUSTOMER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
  fullName: string;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  originalPrice?: number;
  image: string;
  category: string;
  inStock: boolean;
  stockQuantity: number;
  rating: number;
  reviewCount: number;
  unit: string;
}

export interface ProductFilters {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  search?: string;
  sortBy?: 'name' | 'price' | 'rating';
  sortOrder?: 'asc' | 'desc';
  page?: number;
  size?: number;
}

export interface CartItem {
  id: string;
  productId: string;
  product: Product;
  quantity: number;
  price: number;
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
  total: number;
  itemCount: number;
}

export interface AddToCartRequest {
  productId: string;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

export interface OrderItem {
  id: string;
  productId: string;
  product: Product;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  userId: string;
  items: OrderItem[];
  total: number;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
  shippingAddress: Address;
}

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}

export interface CheckoutRequest {
  shippingAddress: Address;
  paymentMethod: 'credit_card' | 'paypal' | 'apple_pay';
}

export interface OrderUpdate {
  orderId: string;
  status: string;
  timestamp: string;
}

export interface InventoryItem {
  productId: string;
  productName: string;
  currentStock: number;
  minStock: number;
  maxStock: number;
  lastUpdated: string;
}

export interface DashboardMetrics {
  totalOrders: number;
  totalCustomers: number;
  totalProducts: number;
  totalRevenue: number;
  pendingOrders: number;
  lowStockProducts: number;
  averageOrderValue: number;
  ordersToday: number;
}

export interface SalesAnalytics {
  totalRevenue: number;
  totalOrders: number;
  revenueByCategory: Record<string, number>;
  ordersByStatus: Record<string, number>;
  dailySales: DailySalesData[];
}

export interface DailySalesData {
  date: string;
  revenue: number;
  orderCount: number;
}

export interface InventoryStatus {
  totalProducts: number;
  inStockProducts: number;
  outOfStockProducts: number;
  lowStockProducts: number;
  lowStockItems: Product[];
}

export interface ProductSalesData {
  product: Product;
  totalSold: number;
  totalRevenue: number;
}

export interface CustomerAnalytics {
  totalCustomers: number;
  activeCustomers: number;
  newCustomersThisMonth: number;
  averageOrdersPerCustomer: number;
  topCustomers: TopCustomerData[];
}

export interface TopCustomerData {
  customerName: string;
  customerEmail: string;
  orderCount: number;
  totalSpent: number;
}

export interface SystemHealth {
  status: string;
  services: Record<string, string>;
  uptimeSeconds: number;
  memoryUsagePercent: number;
  cpuUsagePercent: number;
}

export class ApiService {
  // Authentication
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/register', userData);
    return response.data;
  }

  async refreshToken(): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/refresh');
    return response.data;
  }

  async getCurrentUser(): Promise<UserResponse> {
    const response = await apiClient.get<UserResponse>('/auth/me');
    return response.data;
  }

  // Products
  async getProducts(filters?: ProductFilters): Promise<Product[]> {
    const response = await apiClient.get<Product[]>('/products', { params: filters });
    return response.data;
  }

  async getProduct(id: string): Promise<Product> {
    const response = await apiClient.get<Product>(`/products/${id}`);
    return response.data;
  }

  async searchProducts(query: string): Promise<Product[]> {
    const response = await apiClient.get<Product[]>('/products/search', {
      params: { q: query }
    });
    return response.data;
  }

  async getRecommendations(userId: string): Promise<Product[]> {
    const response = await apiClient.get<Product[]>(`/recommendations/${userId}`);
    return response.data;
  }

  // Cart
  async getCart(): Promise<Cart> {
    const response = await apiClient.get<Cart>('/cart');
    return response.data;
  }

  async addToCart(item: AddToCartRequest): Promise<Cart> {
    const response = await apiClient.post<Cart>('/cart/items', item);
    return response.data;
  }

  async updateCartItem(itemId: string, update: UpdateCartItemRequest): Promise<Cart> {
    const response = await apiClient.put<Cart>(`/cart/items/${itemId}`, update);
    return response.data;
  }

  async removeFromCart(itemId: string): Promise<Cart> {
    const response = await apiClient.delete<Cart>(`/cart/items/${itemId}`);
    return response.data;
  }

  // Orders
  async createOrder(orderData: CheckoutRequest): Promise<Order> {
    const response = await apiClient.post<Order>('/orders', orderData);
    return response.data;
  }

  async getOrders(): Promise<Order[]> {
    const response = await apiClient.get<Order[]>('/orders');
    return response.data;
  }

  async getOrder(id: string): Promise<Order> {
    const response = await apiClient.get<Order>(`/orders/${id}`);
    return response.data;
  }

  async cancelOrder(id: string): Promise<Order> {
    const response = await apiClient.put<Order>(`/orders/${id}/cancel`);
    return response.data;
  }

  // Admin endpoints
  async getAllProducts(filters?: ProductFilters): Promise<Product[]> {
    const response = await apiClient.get<Product[]>('/admin/products', { params: filters });
    return response.data;
  }

  async createProduct(product: Omit<Product, 'id' | 'rating' | 'reviewCount'>): Promise<Product> {
    const response = await apiClient.post<Product>('/admin/products', product);
    return response.data;
  }

  async updateProduct(id: string, product: Partial<Product>): Promise<Product> {
    const response = await apiClient.put<Product>(`/admin/products/${id}`, product);
    return response.data;
  }

  async deleteProduct(id: string): Promise<void> {
    await apiClient.delete(`/admin/products/${id}`);
  }

  async getAllOrders(): Promise<Order[]> {
    const response = await apiClient.get<Order[]>('/admin/orders');
    return response.data;
  }

  async updateOrderStatus(id: string, status: string): Promise<Order> {
    const response = await apiClient.put<Order>(`/admin/orders/${id}/status`, { status });
    return response.data;
  }

  async getInventory(): Promise<InventoryItem[]> {
    const response = await apiClient.get<InventoryItem[]>('/admin/inventory');
    return response.data;
  }

  // Admin Dashboard
  async getDashboardMetrics(): Promise<DashboardMetrics> {
    const response = await apiClient.get<DashboardMetrics>('/admin/dashboard');
    return response.data;
  }

  async getSalesAnalytics(startDate: string, endDate: string): Promise<SalesAnalytics> {
    const response = await apiClient.get<SalesAnalytics>('/admin/analytics/sales', {
      params: { startDate, endDate }
    });
    return response.data;
  }

  async getInventoryStatus(): Promise<InventoryStatus> {
    const response = await apiClient.get<InventoryStatus>('/admin/inventory/status');
    return response.data;
  }

  async getRecentOrders(limit: number = 10): Promise<Order[]> {
    const response = await apiClient.get<Order[]>('/admin/orders/recent', {
      params: { limit }
    });
    return response.data;
  }

  async getTopSellingProducts(limit: number = 10): Promise<ProductSalesData[]> {
    const response = await apiClient.get<ProductSalesData[]>('/admin/products/top-selling', {
      params: { limit }
    });
    return response.data;
  }

  async getCustomerAnalytics(): Promise<CustomerAnalytics> {
    const response = await apiClient.get<CustomerAnalytics>('/admin/analytics/customers');
    return response.data;
  }

  async updateProductStock(productId: string, newStockQuantity: number): Promise<void> {
    await apiClient.put(`/admin/inventory/stock/${productId}`, null, {
      params: { newStockQuantity }
    });
  }

  async getSystemHealth(): Promise<SystemHealth> {
    const response = await apiClient.get<SystemHealth>('/admin/system/health');
    return response.data;
  }
}

export const apiService = new ApiService();
