import Link from "next/link";
import {
  Facebook,
  Twitter,
  Instagram,
  Phone,
  Mail,
  MapPin,
} from "lucide-react";

export function Footer() {
  return (
    <footer className="bg-green-800 text-white relative overflow-hidden">
      {/* Background Pattern */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-10 left-10 w-20 h-20 rounded-full bg-white/20"></div>
        <div className="absolute top-32 right-20 w-16 h-16 rounded-full bg-white/15"></div>
        <div className="absolute bottom-20 left-1/4 w-12 h-12 rounded-full bg-white/10"></div>
        <div className="absolute bottom-10 right-10 w-24 h-24 rounded-full bg-white/20"></div>
      </div>

      <div className="container mx-auto px-4 py-12 relative z-10">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Company Info */}
          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <div className="bg-white text-green-800 p-2 rounded-lg">
                <span className="font-bold text-xl">🛒</span>
              </div>
              <div>
                <h1 className="font-bold text-xl text-white">GhanaMart</h1>
                <p className="text-xs text-green-200">Fresh & Local</p>
              </div>
            </div>
            <p className="text-green-100 text-sm">
              Ghana&apos;s premier online grocery store, bringing fresh local
              produce and quality groceries right to your doorstep across Accra
              and beyond.
            </p>
            <div className="flex space-x-4">
              <div className="bg-white/20 p-2 rounded-full hover:bg-white/30 cursor-pointer transition-colors">
                <Facebook className="h-5 w-5 text-white" />
              </div>
              <div className="bg-white/20 p-2 rounded-full hover:bg-white/30 cursor-pointer transition-colors">
                <Twitter className="h-5 w-5 text-white" />
              </div>
              <div className="bg-white/20 p-2 rounded-full hover:bg-white/30 cursor-pointer transition-colors">
                <Instagram className="h-5 w-5 text-white" />
              </div>
            </div>
          </div>

          {/* Quick Links */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">Quick Links</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  href="/about"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  About Us
                </Link>
              </li>
              <li>
                <Link
                  href="/how-it-works"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  How It Works
                </Link>
              </li>
              <li>
                <Link
                  href="/delivery-areas"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Delivery Areas
                </Link>
              </li>
              <li>
                <Link
                  href="/bulk-orders"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Bulk Orders
                </Link>
              </li>
              <li>
                <Link
                  href="/become-supplier"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Become a Supplier
                </Link>
              </li>
            </ul>
          </div>

          {/* Categories */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">Categories</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  href="/categories/fresh-produce"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Fresh Produce
                </Link>
              </li>
              <li>
                <Link
                  href="/categories/grains-cereals"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Grains & Cereals
                </Link>
              </li>
              <li>
                <Link
                  href="/categories/protein-meat"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Protein & Meat
                </Link>
              </li>
              <li>
                <Link
                  href="/categories/pantry-staples"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Pantry Staples
                </Link>
              </li>
              <li>
                <Link
                  href="/categories/spices-seasonings"
                  className="text-green-200 hover:text-white transition-colors"
                >
                  Spices & Seasonings
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact Info */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">Contact Us</h3>
            <div className="space-y-3 text-sm">
              <div className="flex items-center space-x-2">
                <div className="bg-white/20 p-1.5 rounded-full">
                  <Phone className="h-4 w-4 text-white" />
                </div>
                <span className="text-green-200">+233 24 123 4567</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="bg-white/20 p-1.5 rounded-full">
                  <Mail className="h-4 w-4 text-white" />
                </div>
                <span className="text-green-200">support@ghanamart.com</span>
              </div>
              <div className="flex items-start space-x-2">
                <div className="bg-white/20 p-1.5 rounded-full mt-0.5">
                  <MapPin className="h-4 w-4 text-white" />
                </div>
                <div className="text-green-200">
                  <p>East Legon, Accra</p>
                  <p>Greater Accra Region, Ghana</p>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <h4 className="font-medium text-white">Business Hours</h4>
              <p className="text-green-200 text-sm">
                Monday - Sunday: 7:00 AM - 9:00 PM
              </p>
            </div>
          </div>
        </div>

        {/* Bottom bar */}
        <div className="border-t border-green-600 mt-12 pt-8 flex flex-col md:flex-row justify-between items-center">
          <p className="text-green-200 text-sm">
            © 2024 GhanaMart. All rights reserved.
          </p>
          <div className="flex space-x-6 mt-4 md:mt-0">
            <Link
              href="/privacy"
              className="text-green-200 hover:text-white text-sm transition-colors"
            >
              Privacy Policy
            </Link>
            <Link
              href="/terms"
              className="text-green-200 hover:text-white text-sm transition-colors"
            >
              Terms of Service
            </Link>
            <Link
              href="/support"
              className="text-green-200 hover:text-white text-sm transition-colors"
            >
              Support
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
