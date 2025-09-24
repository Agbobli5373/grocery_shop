import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  experimental: {
    optimizePackageImports: ['lucide-react'],
  },
  images: {
    domains: [
      'api.grocery-shop.com',
      'localhost',
      // Allow popular remote image hosts we will use on the homepage
      'images.unsplash.com',
      'plus.unsplash.com',
      'res.cloudinary.com',
    ],
    formats: ['image/webp', 'image/avif'],
  },
};

export default nextConfig;
