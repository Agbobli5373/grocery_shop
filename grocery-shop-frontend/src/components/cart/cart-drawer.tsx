"use client";

import { Fragment } from "react";
import Image from "next/image";
import Link from "next/link";
import { X, Plus, Minus, ShoppingBag } from "lucide-react";
import { Button } from "../ui/button";
import { useCartStore } from "@/lib/store/cart";
import { useUIStore } from "@/lib/store/ui";

export function CartDrawer() {
  const { items, total, updateItem, removeItem } = useCartStore();
  const { isCartOpen, toggleCart } = useUIStore();

  const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

  const handleUpdateQuantity = (itemId: string, newQuantity: number) => {
    if (newQuantity === 0) {
      removeItem(itemId);
    } else {
      updateItem(itemId, newQuantity);
    }
  };

  if (!isCartOpen) return null;

  return (
    <Fragment>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black bg-opacity-50 z-50"
        onClick={toggleCart}
      />

      {/* Cart Drawer */}
      <div className="fixed right-0 top-0 h-full w-full max-w-md bg-white z-50 shadow-xl transform transition-transform duration-300 ease-in-out">
        <div className="flex flex-col h-full">
          {/* Header */}
          <div className="flex items-center justify-between p-4 border-b">
            <h2 className="text-lg font-semibold text-gray-900">
              Shopping Cart ({itemCount})
            </h2>
            <Button
              variant="ghost"
              size="icon"
              onClick={toggleCart}
              className="text-gray-500 hover:text-gray-700"
            >
              <X className="h-5 w-5" />
            </Button>
          </div>

          {/* Cart Items */}
          <div className="flex-1 overflow-y-auto p-4">
            {items.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-full text-gray-500">
                <ShoppingBag className="h-16 w-16 mb-4" />
                <p className="text-lg font-medium mb-2">Your cart is empty</p>
                <p className="text-sm text-center">
                  Add some delicious items from our store to get started!
                </p>
              </div>
            ) : (
              <div className="space-y-4">
                {items.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg"
                  >
                    {item.product.image && (
                      <div className="relative w-16 h-16 bg-gray-200 rounded-md overflow-hidden">
                        <Image
                          src={item.product.image}
                          alt={item.product.name}
                          fill
                          className="object-cover"
                        />
                      </div>
                    )}

                    <div className="flex-1 min-w-0">
                      <h3 className="font-medium text-gray-900 truncate">
                        {item.product.name}
                      </h3>
                      <p className="text-sm text-gray-500">
                        ₵{item.price.toFixed(2)}{" "}
                        {item.product.unit && `per ${item.product.unit}`}
                      </p>
                      <div className="flex items-center justify-between mt-2">
                        <div className="flex items-center space-x-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() =>
                              handleUpdateQuantity(
                                item.id,
                                item.quantity - 1
                              )
                            }
                            className="h-7 w-7 p-0"
                          >
                            <Minus className="h-3 w-3" />
                          </Button>

                          <span className="font-medium text-sm min-w-[2rem] text-center">
                            {item.quantity}
                          </span>

                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() =>
                              handleUpdateQuantity(
                                item.id,
                                item.quantity + 1
                              )
                            }
                            className="h-7 w-7 p-0"
                          >
                            <Plus className="h-3 w-3" />
                          </Button>
                        </div>

                        <div className="text-right">
                          <p className="font-semibold text-green-600">
                            ₵{(item.price * item.quantity).toFixed(2)}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Footer */}
          {items.length > 0 && (
            <div className="border-t p-4 space-y-4">
              <div className="flex items-center justify-between text-lg font-semibold">
                <span>Total:</span>
                <span className="text-green-600">₵{total.toFixed(2)}</span>
              </div>

              <div className="space-y-2">
                <Link href="/checkout" onClick={toggleCart}>
                  <Button
                    className="w-full bg-green-600 hover:bg-green-700"
                    size="lg"
                  >
                    Proceed to Checkout
                  </Button>
                </Link>

                <Button
                  variant="outline"
                  className="w-full"
                  onClick={toggleCart}
                >
                  Continue Shopping
                </Button>
              </div>

              <p className="text-xs text-gray-500 text-center">
                Free delivery on orders over ₵100
              </p>
            </div>
          )}
        </div>
      </div>
    </Fragment>
  );
}
