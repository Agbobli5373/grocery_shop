-- Inventory tracking table for audit and analytics
CREATE TABLE inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- STOCK_IN, STOCK_OUT, ADJUSTMENT
    quantity_change INT NOT NULL,
    previous_quantity INT NOT NULL,
    new_quantity INT NOT NULL,
    reason VARCHAR(255),
    created_by BIGINT, -- User who performed the transaction
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Indexes for inventory tracking
CREATE INDEX idx_inventory_product_id ON inventory_transactions(product_id);
CREATE INDEX idx_inventory_transaction_type ON inventory_transactions(transaction_type);
CREATE INDEX idx_inventory_created_at ON inventory_transactions(created_at);

-- Insert initial inventory records for existing products
INSERT INTO inventory_transactions (product_id, transaction_type, quantity_change, previous_quantity, new_quantity, reason, created_at)
SELECT id, 'INITIAL', stock_quantity, 0, stock_quantity, 'Initial inventory setup', NOW()
FROM products
WHERE stock_quantity > 0;
