"use client";

import { useState } from "react";
import Link from "next/link";
import { ShoppingCart, Search, Menu, X, User, MapPin } from "lucide-react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { useCartStore } from "@/lib/store/cart";
import { useUIStore } from "@/lib/store/ui";

export function Header() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const { items, total } = useCartStore();
  const { toggleCart } = useUIStore();

  const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

  const categories = [
    { name: "Fresh Produce", href: "/categories/fresh-produce" },
    { name: "Grains & Cereals", href: "/categories/grains-cereals" },
    { name: "Protein & Meat", href: "/categories/protein-meat" },
    { name: "Pantry Staples", href: "/categories/pantry-staples" },
    { name: "Beverages", href: "/categories/beverages" },
    { name: "Spices & Seasonings", href: "/categories/spices-seasonings" },
  ];

  return (
    <header className="bg-white shadow-sm border-b sticky top-0 z-50">
      {/* Top bar with location and contact */}
      <div className="bg-green-600 text-white text-sm">
        <div className="container mx-auto px-4 py-2 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1">
              <MapPin className="h-4 w-4" />
              <span>Delivering to Accra, Ghana</span>
            </div>
          </div>
          <div className="hidden md:flex items-center gap-4">
            <span>📞 +233 24 123 4567</span>
            <span>🕒 Mon-Sun: 7AM - 9PM</span>
          </div>
        </div>
      </div>

      {/* Main header */}
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-2">
            <div className="bg-green-600 text-white p-2 rounded-lg">
              <span className="font-bold text-xl">🛒</span>
            </div>
            <div>
              <h1 className="font-bold text-xl text-green-600">GhanaMart</h1>
              <p className="text-xs text-gray-500">Fresh & Local</p>
            </div>
          </Link>

          {/* Search bar - Desktop */}
          <div className="hidden md:flex flex-1 max-w-2xl mx-8">
            <div className="relative w-full">
              <Input
                type="text"
                placeholder="Search for yam, plantain, tomatoes, gari..."
                className="pr-12 h-12 border-2 border-green-100 focus:border-green-300"
              />
              <Button
                size="icon"
                className="absolute right-1 top-1 h-10 w-10 bg-green-600 hover:bg-green-700"
              >
                <Search className="h-4 w-4" />
              </Button>
            </div>
          </div>

          {/* Desktop menu */}
          <div className="hidden md:flex items-center space-x-4">
            <Link
              href="/account"
              className="flex items-center space-x-1 text-gray-700 hover:text-green-600"
            >
              <User className="h-5 w-5" />
              <span>Account</span>
            </Link>

            <Button
              variant="ghost"
              className="relative flex items-center space-x-1 text-gray-700 hover:text-green-600"
              onClick={toggleCart}
            >
              <ShoppingCart className="h-5 w-5" />
              <span>Cart</span>
              {itemCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-6 w-6 flex items-center justify-center">
                  {itemCount}
                </span>
              )}
            </Button>

            {total > 0 && (
              <div className="text-sm font-semibold text-green-600">
                ₵{total.toFixed(2)}
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          >
            {isMobileMenuOpen ? (
              <X className="h-6 w-6" />
            ) : (
              <Menu className="h-6 w-6" />
            )}
          </Button>
        </div>

        {/* Categories nav - Desktop */}
        <nav className="hidden md:block border-t border-gray-100">
          <div className="flex space-x-8 py-3">
            {categories.map((category) => (
              <Link
                key={category.name}
                href={category.href}
                className="text-sm text-gray-600 hover:text-green-600 transition-colors"
              >
                {category.name}
              </Link>
            ))}
          </div>
        </nav>
      </div>

      {/* Mobile menu */}
      {isMobileMenuOpen && (
        <div className="md:hidden bg-white border-t">
          <div className="px-4 py-4 space-y-4">
            {/* Mobile search */}
            <div className="relative">
              <Input
                type="text"
                placeholder="Search for yam, plantain, tomatoes..."
                className="pr-12 h-12"
              />
              <Button
                size="icon"
                className="absolute right-1 top-1 h-10 w-10 bg-green-600 hover:bg-green-700"
              >
                <Search className="h-4 w-4" />
              </Button>
            </div>

            {/* Mobile navigation */}
            <nav className="space-y-2">
              {categories.map((category) => (
                <Link
                  key={category.name}
                  href={category.href}
                  className="block py-2 text-gray-700 hover:text-green-600"
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  {category.name}
                </Link>
              ))}
            </nav>

            <div className="flex items-center justify-between pt-4 border-t">
              <Link
                href="/account"
                className="flex items-center space-x-2 text-gray-700"
              >
                <User className="h-5 w-5" />
                <span>Account</span>
              </Link>

              <Button
                variant="ghost"
                className="relative flex items-center space-x-2"
                onClick={() => {
                  toggleCart();
                  setIsMobileMenuOpen(false);
                }}
              >
                <ShoppingCart className="h-5 w-5" />
                <span>Cart ({itemCount})</span>
                {total > 0 && (
                  <span className="text-green-600 font-semibold ml-2">
                    ₵{total.toFixed(2)}
                  </span>
                )}
              </Button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
