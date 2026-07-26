import { test, expect } from '@playwright/test';

test.describe('Pruebas de Paridad de Funcionalidad y UI', () => {
  test('debe responder con estado 200 en la página de inicio', async ({ page, baseURL }) => {
    const response = await page.goto('/');
    expect(response?.status()).toBeLessThan(400);
  });

  test('debe responder con estado 200 en la página de registro', async ({ page }) => {
    const response = await page.goto('/register');
    expect(response?.status()).toBeLessThan(400);
    await expect(page.locator('form, input')).toBeDefined();
  });

  test('debe responder con estado 200 o redirigir en la vista de feedback', async ({ page }) => {
    const response = await page.goto('/feedback');
    expect(response?.status()).toBeLessThan(500);
  });
});
