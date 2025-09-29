import { test, expect } from '@playwright/test';

test.describe('Authentication', () => {
  test('should login successfully with valid credentials', async ({ page }) => {
    // Navigate to login page
    await page.goto('/login');

    // Fill login form
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');

    // Click login button
    await page.click('button[type="submit"]');

    // Wait for navigation to home page
    await page.waitForURL('/');

    // Verify we're logged in (check for user info or logout button)
    await expect(page.locator('text=Welcome')).toBeVisible();
  });

  test('should show error for invalid credentials', async ({ page }) => {
    // Navigate to login page
    await page.goto('/login');

    // Fill login form with invalid credentials
    await page.fill('input[name="email"]', 'invalid@example.com');
    await page.fill('input[name="password"]', 'wrongpassword');

    // Click login button
    await page.click('button[type="submit"]');

    // Verify error message appears
    await expect(page.locator('text=Invalid credentials')).toBeVisible();
  });

  test('should navigate to register page', async ({ page }) => {
    // Navigate to login page
    await page.goto('/login');

    // Click register link
    await page.click('text=Create an account');

    // Verify navigation to register page
    await expect(page).toHaveURL('/register');
  });
});
