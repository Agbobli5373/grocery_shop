-- Seed data for integration tests
-- This file is executed after migrations to populate test data

-- Insert test products
INSERT INTO products (name, description, price, image_url, category, status, stock_quantity, created_at, updated_at) VALUES
('Apple', 'Fresh red apple', 2.50, 'https://example.com/apple.jpg', 'FRUITS', 'ACTIVE', 100, NOW(), NOW()),
('Banana', 'Yellow banana', 1.20, 'https://example.com/banana.jpg', 'FRUITS', 'ACTIVE', 150, NOW(), NOW()),
('Milk', 'Whole milk 1L', 3.50, 'https://example.com/milk.jpg', 'DAIRY', 'ACTIVE', 50, NOW(), NOW()),
('Bread', 'Whole grain bread', 2.00, 'https://example.com/bread.jpg', 'PANTRY', 'ACTIVE', 75, NOW(), NOW()),
('Orange Juice', 'Fresh orange juice 500ml', 4.00, 'https://example.com/orange-juice.jpg', 'BEVERAGES', 'ACTIVE', 30, NOW(), NOW());

-- Insert test admin user (password: admin123)
INSERT INTO users (email, password_hash, first_name, last_name, role, status, created_at, updated_at) VALUES
('admin@groceryshop.com', '$2a$10$8K3dsH8zJcQkJcQkJcQkJeQkJcQkJcQkJcQkJcQkJcQkJcQkJcQkJ', 'Admin', 'User', 'ADMIN', 'ACTIVE', NOW(), NOW());
