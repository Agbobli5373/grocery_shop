'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

import { apiService, InventoryStatus, Product } from '@/lib/api/service';
import { useUserStore } from '@/lib/store/user';
import { AlertTriangle, Package, TrendingDown, TrendingUp, Edit, Save, X } from 'lucide-react';

export default function InventoryManagement() {
  const { user } = useUserStore();
  const [inventoryStatus, setInventoryStatus] = useState<InventoryStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingStock, setEditingStock] = useState<{ [key: string]: number }>({});
  const [savingStock, setSavingStock] = useState<string | null>(null);

  useEffect(() => {
    const loadInventoryData = async () => {
      try {
        setLoading(true);
        const data = await apiService.getInventoryStatus();
        setInventoryStatus(data);
      } catch (err) {
        setError('Failed to load inventory data');
        console.error('Inventory error:', err);
      } finally {
        setLoading(false);
      }
    };

    if (user?.role === 'ADMIN') {
      loadInventoryData();
    }
  }, [user]);

  const handleStockEdit = (productId: string, currentStock: number) => {
    setEditingStock(prev => ({
      ...prev,
      [productId]: currentStock
    }));
  };

  const handleStockChange = (productId: string, newStock: number) => {
    setEditingStock(prev => ({
      ...prev,
      [productId]: newStock
    }));
  };

  const handleStockSave = async (productId: string) => {
    const newStock = editingStock[productId];
    if (newStock === undefined) return;

    try {
      setSavingStock(productId);
      await apiService.updateProductStock(productId, newStock);

      // Update local state
      setInventoryStatus(prev => {
        if (!prev) return prev;
        return {
          ...prev,
          lowStockItems: prev.lowStockItems.map(item =>
            item.id === productId ? { ...item, stockQuantity: newStock } : item
          )
        };
      });

      setEditingStock(prev => {
        const newState = { ...prev };
        delete newState[productId];
        return newState;
      });
    } catch (err) {
      console.error('Failed to update stock:', err);
      setError('Failed to update stock level');
    } finally {
      setSavingStock(null);
    }
  };

  const handleStockCancel = (productId: string) => {
    setEditingStock(prev => {
      const newState = { ...prev };
      delete newState[productId];
      return newState;
    });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <AlertTriangle className="mx-auto h-12 w-12 text-red-500" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">Error</h3>
          <p className="mt-1 text-sm text-gray-500">{error}</p>
        </div>
      </div>
    );
  }

  if (!inventoryStatus) {
    return null;
  }

  const getStockStatus = (stock: number, minStock: number = 10) => {
    if (stock === 0) return { status: 'Out of Stock', color: 'bg-red-100 text-red-800' };
    if (stock <= minStock) return { status: 'Low Stock', color: 'bg-yellow-100 text-yellow-800' };
    return { status: 'In Stock', color: 'bg-green-100 text-green-800' };
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight">Inventory Management</h1>
        <Button variant="outline">
          <Package className="mr-2 h-4 w-4" />
          Export Report
        </Button>
      </div>

      {/* Inventory Overview */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Products</CardTitle>
            <Package className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{inventoryStatus.totalProducts}</div>
            <p className="text-xs text-muted-foreground">
              Products in catalog
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">In Stock</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">{inventoryStatus.inStockProducts}</div>
            <p className="text-xs text-muted-foreground">
              Available for sale
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Low Stock</CardTitle>
            <AlertTriangle className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-600">{inventoryStatus.lowStockProducts}</div>
            <p className="text-xs text-muted-foreground">
              Need restocking
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Out of Stock</CardTitle>
            <TrendingDown className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">{inventoryStatus.outOfStockProducts}</div>
            <p className="text-xs text-muted-foreground">
              Unavailable
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Low Stock Alerts */}
      {inventoryStatus.lowStockProducts > 0 && (
        <Card className="border-yellow-200 bg-yellow-50">
          <CardHeader>
            <CardTitle className="text-yellow-800 flex items-center">
              <AlertTriangle className="mr-2 h-5 w-5" />
              Low Stock Alerts
            </CardTitle>
            <CardDescription className="text-yellow-700">
              These products are running low and may need restocking
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {inventoryStatus.lowStockItems.map((product) => {
                const isEditing = editingStock.hasOwnProperty(product.id);
                const editedStock = editingStock[product.id];
                const stockStatus = getStockStatus(product.stockQuantity);

                return (
                  <Card key={product.id} className="border-yellow-200">
                    <CardHeader className="pb-3">
                      <div className="flex items-center justify-between">
                        <CardTitle className="text-sm font-medium">{product.name}</CardTitle>
                        <Badge className={stockStatus.color}>
                          {stockStatus.status}
                        </Badge>
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-3">
                      <div className="flex items-center justify-between">
                        <span className="text-sm font-medium">Current Stock:</span>
                        {isEditing ? (
                          <div className="flex items-center space-x-2">
                            <Input
                              type="number"
                              value={editedStock}
                              onChange={(e) => handleStockChange(product.id, parseInt(e.target.value) || 0)}
                              className="w-20 h-8"
                              min="0"
                            />
                            <Button
                              size="sm"
                              onClick={() => handleStockSave(product.id)}
                              disabled={savingStock === product.id}
                            >
                              {savingStock === product.id ? (
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                              ) : (
                                <Save className="h-3 w-3" />
                              )}
                            </Button>
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => handleStockCancel(product.id)}
                            >
                              <X className="h-3 w-3" />
                            </Button>
                          </div>
                        ) : (
                          <div className="flex items-center space-x-2">
                            <span className="font-medium">{product.stockQuantity}</span>
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => handleStockEdit(product.id, product.stockQuantity)}
                            >
                              <Edit className="h-3 w-3" />
                            </Button>
                          </div>
                        )}
                      </div>
                      <div className="text-xs text-muted-foreground">
                        Category: {product.category}
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Inventory Actions */}
      <Card>
        <CardHeader>
          <CardTitle>Inventory Actions</CardTitle>
          <CardDescription>Manage your product inventory</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-3">
            <Button variant="outline" className="justify-start">
              <Package className="mr-2 h-4 w-4" />
              Bulk Update Stock
            </Button>
            <Button variant="outline" className="justify-start">
              <TrendingDown className="mr-2 h-4 w-4" />
              Generate Restock Report
            </Button>
            <Button variant="outline" className="justify-start">
              <AlertTriangle className="mr-2 h-4 w-4" />
              Set Low Stock Alerts
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
