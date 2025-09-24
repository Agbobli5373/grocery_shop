import { test, expect } from '@playwright/test';

test('complete purchase flow', async ({ page }) => {
    // Navigate to product page
    await page.goto('/products/1');

    // Add to cart
    await page.click('[data-testid="add-to-cart"]');

    // Go to cart
    await page.click('[data-testid="cart-link"]');

    // Proceed to checkout
    await page.click('[data-testid="checkout-button"]');

    // Fill checkout form
    await page.fill('[data-testid="email"]', 'test@example.com');
    await page.fill('[data-testid="address"]', '123 Main St');

    // Complete purchase
    await page.click('[data-testid="place-order"]');

    // Verify success
    await expect(page.locator('[data-testid="order-confirmation"]')).toBeVisible();
});