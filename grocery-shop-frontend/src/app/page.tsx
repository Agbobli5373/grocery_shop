"use client";

import Image from "next/image";
import Link from "next/link";
import { ArrowRight, Truck, Shield, Clock, Star } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { ProductCard } from "@/components/products/product-card";
import type { Product, Category } from "@/types";

// Lightweight seed data for the homepage. In a real app this will come from the API.
const categories: Category[] = [
  {
    id: "produce",
    name: "Fresh Produce",
    slug: "fresh-produce",
    image:
      "https://images.unsplash.com/photo-1542831371-29b0f74f9713?q=80&w=1280&auto=format&fit=crop",
    itemCount: 120,
  },
  {
    id: "grains",
    name: "Grains & Cereals",
    slug: "grains-cereals",
    image:
      "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?q=80&w=1280&auto=format&fit=crop",
    itemCount: 80,
  },
  {
    id: "protein",
    name: "Protein & Meat",
    slug: "protein-meat",
    image:
      "https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=1280&auto=format&fit=crop",
    itemCount: 65,
  },
  {
    id: "spices",
    name: "Spices & Seasonings",
    slug: "spices-seasonings",
    image:
      "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?q=80&w=1280&auto=format&fit=crop",
    itemCount: 54,
  },
];

const featured: Product[] = [
  {
    id: "yam",
    name: "Pona Yam (Large)",
    description: "Premium Ghanaian yam, perfect for ampesi & fufu",
    price: 35,
    originalPrice: 42,
    image:
      "https://images.unsplash.com/photo-1597733336794-12d05021d510?q=80&w=1200&auto=format&fit=crop",
    category: "Fresh Produce",
    inStock: true,
    rating: 4.7,
    reviewCount: 210,
    unit: "per tuber",
  },
  {
    id: "plantain",
    name: "Ripe Plantain",
    description: "Sweet plantain for kelewele & ampesi",
    price: 18,
    image:
      "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=1200&auto=format&fit=crop",
    category: "Fresh Produce",
    inStock: true,
    rating: 4.6,
    reviewCount: 160,
    unit: "bundle",
  },
  {
    id: "tomato",
    name: "Fresh Tomatoes",
    description: "Vine-ripened tomatoes from local farms",
    price: 22,
    originalPrice: 25,
    image:
      "https://images.unsplash.com/photo-1506806732259-39c2d0268443?q=80&w=1200&auto=format&fit=crop",
    category: "Fresh Produce",
    inStock: true,
    rating: 4.5,
    reviewCount: 98,
    unit: "per kg",
  },
  {
    id: "gari",
    name: "Gari (Ijebu)",
    description: "Crunchy gari – perfect with beans or soaked",
    price: 28,
    image:
      "https://images.unsplash.com/photo-1566478989037-eec170784d0b?q=80&w=1200&auto=format&fit=crop",
    category: "Grains & Cereals",
    inStock: true,
    rating: 4.2,
    reviewCount: 45,
    unit: "2kg bag",
  },
];

const bestSellers: Product[] = [
  {
    id: "eggs",
    name: "Farm Fresh Eggs",
    description: "Free-range eggs from local farms",
    price: 30,
    image:
      "https://images.unsplash.com/photo-1548550023-2bdb3c5beed7?q=80&w=1200&auto=format&fit=crop",
    category: "Protein",
    inStock: true,
    rating: 4.8,
    reviewCount: 320,
    unit: "crate (30)",
  },
  {
    id: "tilapia",
    name: "Fresh Tilapia",
    description: "Cleaned and ready to grill",
    price: 48,
    image:
      "https://images.unsplash.com/photo-1559847844-5315695dadae?q=80&w=1200&auto=format&fit=crop",
    category: "Protein",
    inStock: true,
    rating: 4.4,
    reviewCount: 90,
    unit: "per kg",
  },
  {
    id: "rice",
    name: "Jasmine Rice",
    description: "Premium long-grain jasmine rice",
    price: 85,
    image:
      "https://images.unsplash.com/photo-1586201375761-83865001e31c?q=80&w=1600&auto=format&fit=crop",
    category: "Grains",
    inStock: true,
    rating: 4.3,
    reviewCount: 140,
    unit: "5kg bag",
  },
  {
    id: "shito",
    name: "Homemade Shito",
    description: "Spicy Ghanaian pepper sauce",
    price: 25,
    image:
      "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?q=80&w=1200&auto=format&fit=crop",
    category: "Spices",
    inStock: true,
    rating: 4.6,
    reviewCount: 200,
    unit: "300ml jar",
  },
];

export default function HomePage() {
  return (
    <main className="min-h-screen bg-white">
      {/* HERO */}
      <section className="relative overflow-hidden">
        <div className="container mx-auto px-4 grid grid-cols-1 md:grid-cols-2 gap-8 items-center py-10 md:py-16">
          {/* Copy */}
          <div>
            <span className="inline-flex items-center text-xs font-semibold uppercase tracking-wider text-green-700 bg-green-50 border border-green-100 px-3 py-1 rounded-full">
              The Real Ghana Grocery Store
            </span>
            <h1 className="mt-4 text-4xl md:text-6xl font-extrabold leading-tight text-gray-900">
              Your One‑Stop Shop
              <br className="hidden md:block" /> for Quality Groceries
            </h1>
            <p className="mt-4 text-gray-600 md:text-lg">
              From fresh produce to pantry staples, get authentic Ghanaian
              ingredients delivered fast across Accra and beyond.
            </p>
            <div className="mt-6 flex flex-wrap gap-3">
              <Button className="bg-green-600 hover:bg-green-700">
                Shop Now
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
              <Link href="#featured" className="text-green-700 font-medium">
                Browse Categories
              </Link>
            </div>

            {/* Trust badges */}
            <div className="mt-8 grid grid-cols-3 gap-4 text-sm">
              <div className="flex items-center gap-2">
                <Truck className="h-5 w-5 text-green-600" />
                <span>Same‑day delivery</span>
              </div>
              <div className="flex items-center gap-2">
                <Shield className="h-5 w-5 text-green-600" />
                <span>Quality assured</span>
              </div>
              <div className="flex items-center gap-2">
                <Clock className="h-5 w-5 text-green-600" />
                <span>7AM–9PM daily</span>
              </div>
            </div>
          </div>

          {/* Visual */}
          <div className="relative aspect-[4/3] md:aspect-square rounded-2xl overflow-hidden bg-green-50">
            <Image
              src="https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?q=80&w=1600&auto=format&fit=crop"
              alt="Smiling Ghanaian woman holding fresh vegetables"
              fill
              className="object-cover"
              priority
            />
          </div>
        </div>
      </section>

      {/* CATEGORIES */}
      <section className="py-12 md:py-16 bg-gray-50" id="categories">
        <div className="container mx-auto px-4">
          <div className="flex items-end justify-between mb-6">
            <div>
              <h2 className="text-2xl md:text-3xl font-bold text-gray-900">
                Featured Categories
              </h2>
              <p className="text-gray-600 mt-1">
                Shop essentials from trusted local suppliers
              </p>
            </div>
            <Link href="/categories" className="text-green-700 font-medium">
              View all
            </Link>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {categories.map((c) => (
              <Link
                key={c.id}
                href={`/categories/${c.slug}`}
                className="group relative overflow-hidden rounded-xl border bg-white hover:shadow-md"
              >
                <div className="relative h-36">
                  <Image
                    src={c.image}
                    alt={c.name}
                    fill
                    sizes="(max-width:768px) 50vw, 25vw"
                    className="object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                </div>
                <div className="p-3">
                  <div className="font-semibold text-gray-900">{c.name}</div>
                  <div className="text-xs text-gray-500">
                    {c.itemCount}+ items
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* PROMOTIONAL BANNERS */}
      <section className="py-8">
        <div className="container mx-auto px-4">
          <div className="grid md:grid-cols-2 gap-6">
            {/* Fresh Vegetables Banner */}
            <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-orange-400 to-orange-500 p-6 md:p-8">
              <div className="relative z-10">
                <span className="inline-block px-3 py-1 rounded-full bg-white/20 text-white text-sm font-medium mb-3">
                  Purely Fresh
                </span>
                <h3 className="text-2xl md:text-3xl font-bold text-white mb-2">
                  Vegetables
                </h3>
                <p className="text-white/90 mb-4">
                  Farm fresh produce delivered daily
                </p>
                <Button className="bg-white text-orange-600 hover:bg-gray-100">
                  Shop Now
                </Button>
              </div>
              <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -translate-y-16 translate-x-16"></div>
              <div className="absolute bottom-0 right-0 w-24 h-24 bg-white/10 rounded-full translate-y-12 translate-x-12"></div>
            </div>

            {/* Fresh Fruits Banner */}
            <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-green-400 to-green-500 p-6 md:p-8">
              <div className="relative z-10">
                <span className="inline-block px-3 py-1 rounded-full bg-white/20 text-white text-sm font-medium mb-3">
                  Fresh Fruits
                </span>
                <h3 className="text-2xl md:text-3xl font-bold text-white mb-2">
                  Pure Quality
                </h3>
                <p className="text-white/90 mb-4">Handpicked seasonal fruits</p>
                <Button className="bg-white text-green-600 hover:bg-gray-100">
                  Shop Now
                </Button>
              </div>
              <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -translate-y-16 translate-x-16"></div>
              <div className="absolute bottom-0 right-0 w-24 h-24 bg-white/10 rounded-full translate-y-12 translate-x-12"></div>
            </div>
          </div>
        </div>
      </section>

      {/* FEATURED PRODUCTS */}
      <section className="py-12 md:py-16" id="featured">
        <div className="container mx-auto px-4">
          <div className="flex items-end justify-between mb-6">
            <div>
              <h2 className="text-2xl md:text-3xl font-bold text-gray-900">
                Featured Products
              </h2>
              <p className="text-gray-600 mt-1">
                Hand-picked favourites this week
              </p>
            </div>
            <Link href="/shop" className="text-green-700 font-medium">
              Shop all
            </Link>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {featured.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </div>
      </section>

      {/* DEALS STRIP */}
      <section className="py-8">
        <div className="container mx-auto px-4">
          <Card className="overflow-hidden border-green-100 bg-green-50">
            <div className="grid md:grid-cols-2 items-center">
              <div className="p-6 md:p-8">
                <h3 className="text-2xl font-bold text-green-800">
                  Unbeatable Offers: Your Weekly Grocery Specials
                </h3>
                <p className="text-green-700 mt-2">
                  Save more on fresh produce and pantry essentials every week.
                </p>
                <Button className="mt-4 bg-green-600 hover:bg-green-700">
                  View Deals
                </Button>
              </div>
              <div className="relative h-44 md:h-56">
                <Image
                  src="https://images.unsplash.com/photo-1542831371-29b0f74f9713?q=80&w=1600&auto=format&fit=crop"
                  alt="Basket of fresh produce"
                  fill
                  className="object-cover"
                />
              </div>
            </div>
          </Card>
        </div>
      </section>

      {/* DEALS OF THE DAY */}
      <section className="py-12 md:py-16 bg-gradient-to-r from-red-50 to-orange-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-8">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Deals of the Day
            </h2>
            <p className="text-gray-600 mb-6">
              Limited time offers on selected items
            </p>

            {/* Countdown Timer */}
            <div className="flex justify-center items-center gap-4 mb-8">
              <div className="text-center">
                <div className="bg-white rounded-lg shadow-lg p-3 min-w-[60px]">
                  <div className="text-2xl font-bold text-red-600">04</div>
                  <div className="text-xs text-gray-500">Days</div>
                </div>
              </div>
              <div className="text-2xl font-bold text-gray-400">:</div>
              <div className="text-center">
                <div className="bg-white rounded-lg shadow-lg p-3 min-w-[60px]">
                  <div className="text-2xl font-bold text-red-600">15</div>
                  <div className="text-xs text-gray-500">Hours</div>
                </div>
              </div>
              <div className="text-2xl font-bold text-gray-400">:</div>
              <div className="text-center">
                <div className="bg-white rounded-lg shadow-lg p-3 min-w-[60px]">
                  <div className="text-2xl font-bold text-red-600">42</div>
                  <div className="text-xs text-gray-500">Minutes</div>
                </div>
              </div>
              <div className="text-2xl font-bold text-gray-400">:</div>
              <div className="text-center">
                <div className="bg-white rounded-lg shadow-lg p-3 min-w-[60px]">
                  <div className="text-2xl font-bold text-red-600">30</div>
                  <div className="text-xs text-gray-500">Seconds</div>
                </div>
              </div>
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {/* Daily Cleaning Essentials */}
            <Card className="overflow-hidden bg-white shadow-lg">
              <div className="relative">
                <div className="absolute top-4 left-4 z-10">
                  <span className="bg-red-500 text-white px-3 py-1 rounded-full text-sm font-bold">
                    30% OFF
                  </span>
                </div>
                <div className="relative h-48">
                  <Image
                    src="https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?q=80&w=1600&auto=format&fit=crop"
                    alt="Cleaning Supplies"
                    fill
                    className="object-cover"
                  />
                </div>
              </div>
              <CardContent className="p-6">
                <h3 className="text-xl font-bold text-gray-900 mb-2">
                  Daily Cleaning Essentials
                </h3>
                <p className="text-gray-600 mb-4">
                  Premium household cleaning products
                </p>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="text-2xl font-bold text-green-600">
                      ₵45
                    </span>
                    <span className="text-lg line-through text-gray-400">
                      ₵65
                    </span>
                  </div>
                  <Button className="bg-red-500 hover:bg-red-600">
                    Shop Now
                  </Button>
                </div>
              </CardContent>
            </Card>

            {/* Rice & Water Bundle */}
            <Card className="overflow-hidden bg-white shadow-lg">
              <div className="relative">
                <div className="absolute top-4 left-4 z-10">
                  <span className="bg-green-500 text-white px-3 py-1 rounded-full text-sm font-bold">
                    25% OFF
                  </span>
                </div>
                <div className="relative h-48">
                  <Image
                    src="https://images.unsplash.com/photo-1586201375761-83865001e31c?q=80&w=1600&auto=format&fit=crop"
                    alt="Rice and Water"
                    fill
                    className="object-cover"
                  />
                </div>
              </div>
              <CardContent className="p-6">
                <h3 className="text-xl font-bold text-gray-900 mb-2">
                  Rice & Water Bundle
                </h3>
                <p className="text-gray-600 mb-4">
                  5kg Premium Rice + 6L Pure Water
                </p>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="text-2xl font-bold text-green-600">
                      ₵95
                    </span>
                    <span className="text-lg line-through text-gray-400">
                      ₵125
                    </span>
                  </div>
                  <Button className="bg-green-500 hover:bg-green-600">
                    Shop Now
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* BEST SELLERS */}
      <section className="py-12 md:py-16 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="flex items-end justify-between mb-6">
            <div>
              <h2 className="text-2xl md:text-3xl font-bold text-gray-900">
                Best Seller Products
              </h2>
              <p className="text-gray-600 mt-1">
                Customers can’t get enough of these
              </p>
            </div>
            <Link href="/best-sellers" className="text-green-700 font-medium">
              View more
            </Link>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {bestSellers.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </div>
      </section>

      {/* TESTIMONIALS */}
      <section className="py-12 md:py-16">
        <div className="container mx-auto px-4">
          <div className="text-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold text-gray-900">
              Testimonials from Our Loyal Customers
            </h2>
            <p className="text-gray-600 mt-1">
              Reliable service, fresh produce, and fast delivery—see what people
              say
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-4">
            {[1, 2, 3].map((i) => (
              <Card key={i} className="border-gray-200">
                <CardContent className="p-6">
                  <div className="flex items-center gap-2 text-yellow-500">
                    {Array.from({ length: 5 }).map((_, idx) => (
                      <Star key={idx} className="h-4 w-4 fill-yellow-400" />
                    ))}
                  </div>
                  <p className="mt-3 text-gray-700">
                    “I ordered in the morning and got everything before lunch.
                    The tomatoes were so fresh and the yam quality was
                    excellent.”
                  </p>
                  <div className="mt-4 flex items-center gap-3">
                    <div className="relative h-10 w-10 rounded-full overflow-hidden">
                      <Image
                        src={`https://images.unsplash.com/photo-1527980965255-d3b416303d12?q=80&w=200&auto=format&fit=crop`}
                        alt="Customer avatar"
                        fill
                        className="object-cover"
                      />
                    </div>
                    <div>
                      <div className="font-semibold">Ama B.</div>
                      <div className="text-xs text-gray-500">East Legon</div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* BLOG + FAQ */}
      <section className="py-12 md:py-16 bg-gray-50">
        <div className="container mx-auto px-4 grid lg:grid-cols-2 gap-8">
          {/* Blog cards */}
          <div>
            <div className="flex items-end justify-between mb-6">
              <h2 className="text-2xl md:text-3xl font-bold text-gray-900">
                Our Latest News & Blogs
              </h2>
              <Link href="/blog" className="text-green-700 font-medium">
                View all
              </Link>
            </div>
            <div className="grid sm:grid-cols-2 gap-4">
              {["okra", "waakye", "grill"].map((k) => (
                <Link key={k} href="/blog/ghana-food" className="group">
                  <Card className="overflow-hidden">
                    <div className="relative h-40">
                      <Image
                        src={
                          k === "okra"
                            ? "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?q=80&w=1200&auto=format&fit=crop"
                            : k === "waakye"
                            ? "https://images.unsplash.com/photo-1544025162-d76694265947?q=80&w=1200&auto=format&fit=crop"
                            : "https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?q=80&w=1200&auto=format&fit=crop"
                        }
                        alt="Ghanaian food"
                        fill
                        className="object-cover group-hover:scale-105 transition-transform duration-300"
                      />
                    </div>
                    <CardContent className="p-4">
                      <h3 className="font-semibold text-gray-900">
                        {k === "okra"
                          ? "How to pick the freshest okro in the market"
                          : k === "waakye"
                          ? "Meal prep ideas: Waakye for the week"
                          : "The perfect grilled tilapia at home"}
                      </h3>
                      <p className="text-sm text-gray-600 mt-1">
                        Tips from local vendors and chefs to upgrade your
                        kitchen.
                      </p>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </div>
          </div>

          {/* FAQ */}
          <div>
            <h2 className="text-2xl md:text-3xl font-bold text-gray-900 mb-6">
              Frequently Asked Questions
            </h2>
            <div className="space-y-3">
              {[
                {
                  q: "Are the products fresh and of high quality?",
                  a: "Yes. We source directly from trusted local farmers and suppliers every morning.",
                },
                {
                  q: "What are your delivery times?",
                  a: "Same‑day delivery in Accra for orders before 4PM. You can also schedule a time.",
                },
                {
                  q: "Do you offer secure payment options?",
                  a: "We accept Mobile Money, Visa/Mastercard, and cash on delivery in select areas.",
                },
                {
                  q: "Can I place a bulk/office order?",
                  a: "Absolutely. Contact us for wholesale pricing and recurring deliveries.",
                },
              ].map((item, idx) => (
                <details
                  key={idx}
                  className="group rounded-lg border border-gray-200 bg-white p-4 open:shadow-sm"
                >
                  <summary className="cursor-pointer font-medium text-gray-900">
                    {item.q}
                  </summary>
                  <p className="mt-2 text-gray-600 text-sm">{item.a}</p>
                </details>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* NEWSLETTER */}
      <section className="py-12 md:py-16">
        <div className="container mx-auto px-4">
          <Card className="border-green-100 bg-green-50">
            <CardContent className="p-6 md:p-10 text-center">
              <h3 className="text-2xl md:text-3xl font-bold text-gray-900">
                Subscribe to our Newsletter to Get Updates on Our Latest Offers
              </h3>
              <p className="text-gray-600 mt-2 max-w-2xl mx-auto">
                Be the first to know about special discounts, seasonal produce,
                and new arrivals.
              </p>
              <form
                className="mt-6 grid sm:grid-cols-[1fr_auto] gap-3 max-w-xl mx-auto"
                onSubmit={(e) => e.preventDefault()}
              >
                <input
                  type="email"
                  required
                  placeholder="Enter your email address"
                  className="h-12 rounded-md border border-green-200 px-4 focus:outline-none focus:ring-2 focus:ring-green-300"
                />
                <Button
                  type="submit"
                  className="h-12 bg-green-600 hover:bg-green-700"
                >
                  Subscribe
                </Button>
              </form>
            </CardContent>
          </Card>
        </div>
      </section>
    </main>
  );
}
