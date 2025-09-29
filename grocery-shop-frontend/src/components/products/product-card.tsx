"use client";

import Image from "next/image";
import { Star, Plus, Minus } from "lucide-react";
import { Button } from "../ui/button";
import { Card, CardContent } from "../ui/card";
import { useCartStore } from "@/lib/store/cart";
import type { Product } from "@/lib/api/service";

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const { addItem, items, updateItem, removeItem } = useCartStore();

  const cartItem = items.find((item) => item.productId === product.id);
  const quantity = cartItem?.quantity || 0;

  const handleAddToCart = () => {
    addItem(product.id, 1);
  };

  const handleUpdateQuantity = (newQuantity: number) => {
    if (newQuantity === 0 && cartItem) {
      // Remove from cart
      removeItem(cartItem.id);
    } else if (cartItem) {
      updateItem(cartItem.id, newQuantity);
    }
  };

  const discountPercentage =
    product.originalPrice && product.originalPrice > product.price
      ? Math.round(
          ((product.originalPrice - product.price) / product.originalPrice) *
            100
        )
      : 0;

  return (
    <Card className="group hover:shadow-lg transition-shadow duration-200 border border-gray-200 hover:border-green-200">
      <CardContent className="p-0">
        {/* Product Image */}
        <div className="relative aspect-square overflow-hidden rounded-t-lg bg-gray-100">
          <Image
            src={product.image || "https://images.unsplash.com/photo-1542831371-29b0f74f9713?q=80&w=400&auto=format&fit=crop"}
            alt={product.name}
            fill
            className="object-cover group-hover:scale-105 transition-transform duration-200"
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          />

          {/* Discount Badge */}
          {discountPercentage > 0 && (
            <div className="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
              -{discountPercentage}%
            </div>
          )}

          {/* Stock Status */}
          {!product.inStock && (
            <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
              <span className="text-white font-semibold">Out of Stock</span>
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="p-4">
          <div className="mb-2">
            <h3 className="font-semibold text-sm line-clamp-2 text-gray-900 group-hover:text-green-600">
              {product.name}
            </h3>
            {product.unit && (
              <p className="text-xs text-gray-500 mt-1">{product.unit}</p>
            )}
          </div>

          {/* Rating */}
          {product.rating && (
            <div className="flex items-center mb-2">
              <div className="flex items-center">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className={`h-3 w-3 ${
                      i < Math.floor(product.rating!)
                        ? "text-yellow-400 fill-current"
                        : "text-gray-300"
                    }`}
                  />
                ))}
              </div>
              <span className="text-xs text-gray-500 ml-1">
                ({product.reviewCount || 0})
              </span>
            </div>
          )}

          {/* Price */}
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center space-x-2">
              <span className="font-bold text-green-600">
                ₵{product.price.toFixed(2)}
              </span>
              {product.originalPrice &&
                product.originalPrice > product.price && (
                  <span className="text-xs text-gray-500 line-through">
                    ₵{product.originalPrice.toFixed(2)}
                  </span>
                )}
            </div>
          </div>

          {/* Add to Cart */}
          {product.inStock && (
            <div className="flex items-center justify-between">
              {quantity === 0 ? (
                <Button
                  onClick={handleAddToCart}
                  className="w-full bg-green-600 hover:bg-green-700 text-white text-sm py-2"
                  size="sm"
                >
                  <Plus className="h-4 w-4 mr-1" />
                  Add to Cart
                </Button>
              ) : (
                <div className="flex items-center justify-between w-full">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleUpdateQuantity(quantity - 1)}
                    className="h-8 w-8 p-0 border-green-600 text-green-600 hover:bg-green-50"
                  >
                    <Minus className="h-3 w-3" />
                  </Button>

                  <span className="mx-3 font-semibold text-green-600">
                    {quantity}
                  </span>

                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleUpdateQuantity(quantity + 1)}
                    className="h-8 w-8 p-0 border-green-600 text-green-600 hover:bg-green-50"
                  >
                    <Plus className="h-3 w-3" />
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
