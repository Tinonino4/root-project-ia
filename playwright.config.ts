import { defineConfig, devices } from '@playwright/test';

/**
 * Configuración de Playwright E2E orientada al despliegue en VPS (Producción).
 * - Nuxt 3: https://www.micache.es
 */
export default defineConfig({
  testDir: './e2e/tests',
  timeout: 30 * 1000,
  expect: {
    timeout: 5000,
  },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 1,
  workers: 4,
  reporter: [
    ['html', { open: 'never' }],
    ['list']
  ],

  use: {
    actionTimeout: 10000,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    ignoreHTTPSErrors: true,
  },

  projects: [
    {
      name: 'nuxt',
      use: {
        ...devices['Desktop Chrome'],
        baseURL: process.env.NUXT_URL || 'https://www.micache.es',
      },
    },
  ],
});
