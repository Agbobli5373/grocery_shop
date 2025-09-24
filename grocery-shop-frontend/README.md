# Grocery Shop Frontend

A modern Next.js 15.1.8 application for an online grocery shopping experience, inspired by Picnic's server-driven UI architecture.

## Features

- **Next.js 15.1.8** with App Router and Turbopack
- **TypeScript** for type safety
- **Tailwind CSS 4.x** with custom design system
- **State Management**: Zustand (client) + React Query (server)
- **Real-time Updates**: Server-Sent Events for order tracking
- **Form Handling**: React Hook Form with Zod validation
- **Testing**: Jest, React Testing Library, Playwright E2E
- **Docker** deployment ready

## Tech Stack

- **Framework**: Next.js 15.1.8 (App Router)
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS 4.x
- **State**: Zustand + React Query
- **API**: Axios with JWT interceptors
- **Forms**: React Hook Form + Zod
- **Real-time**: Server-Sent Events
- **Testing**: Jest + Playwright

## Getting Started

### Prerequisites

- Node.js 20+
- npm or yarn

### Installation

1. Install dependencies:

```bash
npm install
```

2. Copy environment variables:

```bash
cp .env.local.example .env.local
```

3. Start the development server:

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) to view the application.

## Project Structure

```
src/
├── app/                          # Next.js App Router
│   ├── (auth)/                   # Auth routes
│   ├── (shop)/                   # Shop routes
│   └── (admin)/                  # Admin routes
├── components/                   # Reusable components
│   ├── ui/                       # Base UI components
│   ├── forms/                    # Form components
│   └── products/                 # Feature components
├── lib/                          # Utilities and configurations
│   ├── api/                      # API client
│   ├── auth/                     # Authentication
│   ├── store/                    # Zustand stores
│   └── validations/              # Zod schemas
└── middleware.ts                 # Route protection
```

## Available Scripts

- `npm run dev` - Start development server with Turbopack
- `npm run build` - Build for production
- `npm run start` - Start production server
- `npm run lint` - Run ESLint
- `npm test` - Run Jest tests
- `npm run test:e2e` - Run Playwright E2E tests

## Environment Variables

Create `.env.local` with:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_WS_URL=ws://localhost:8080
```

## Testing

### Unit Tests

```bash
npm test
```

### E2E Tests

```bash
npm run test:e2e
```

## Docker

Build and run with Docker:

```bash
docker build -t grocery-shop-frontend .
docker run -p 3000:3000 grocery-shop-frontend
```

## Deployment

The application is configured for deployment with:

- **Vercel**: Automatic deployments from main branch
- **Docker**: Multi-stage build for optimized images
- **Static Export**: Optional static generation

## Contributing

1. Follow the established patterns in the codebase
2. Write tests for new features
3. Update documentation as needed
4. Follow the commit message conventions

## License

This project is part of the Grocery Shop application suite.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.
