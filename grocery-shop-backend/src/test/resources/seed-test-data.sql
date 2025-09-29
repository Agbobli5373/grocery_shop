-- Seed data for integration tests
-- This file is executed after migrations to populate test data

-- Insert test products
INSERT INTO products (name, description, price, image_url, category, status, stock_quantity, created_at, updated_at) VALUES
('Apple', 'Fresh red apple', 2.50, 'https://images.unsplash.com/photo-1623815242959-fb20354f9b8d?fm=jpg&q=60&w=400&h=400&fit=crop', 'FRUITS', 'ACTIVE', 100, NOW(), NOW()),
('Banana', 'Yellow banana', 1.20, 'https://images.unsplash.com/photo-1668762924635-a3683caf32bf?fm=jpg&q=60&w=400&h=400&fit=crop', 'FRUITS', 'ACTIVE', 150, NOW(), NOW()),
('Milk', 'Whole milk 1L', 3.50, 'https://images.unsplash.com/photo-1553301803-768cd4a59b9c?fm=jpg&q=60&w=400&h=400&fit=crop', 'DAIRY', 'ACTIVE', 50, NOW(), NOW()),
('Bread', 'Whole grain bread', 2.00, 'https://images.unsplash.com/photo-1626423642268-24cc183cbacb?fm=jpg&q=60&w=400&h=400&fit=crop', 'PANTRY', 'ACTIVE', 75, NOW(), NOW()),
('Orange Juice', 'Fresh orange juice 500ml', 4.00, 'https://images.unsplash.com/photo-1577680716097-9a565ddc2007?fm=jpg&q=60&w=400&h=400&fit=crop', 'BEVERAGES', 'ACTIVE', 30, NOW(), NOW());

-- Insert test admin user (password: admin123)
INSERT INTO users (email, password_hash, first_name, last_name, role, status, created_at, updated_at) VALUES
('admin@groceryshop.com', '$2a$10$8K3dsH8zJcQkJcQkJcQkJeQkJcQkJcQkJcQkJcQkJcQkJcQkJcQkJ', 'Admin', 'User', 'ADMIN', 'ACTIVE', NOW(), NOW());
