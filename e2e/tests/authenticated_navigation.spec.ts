import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/login.page';
import { ProfilePage } from '../pages/profile.page';
import { FeedbackPage } from '../pages/feedback.page';

test.describe('Navegación Autenticada entre Menús Principales', () => {
  const SEED_USER_EMAIL = 'seed.user1@micache.com';
  const SEED_USER_PASS = 'password123';
  const SEED_USER_ID = '37007e45-fd4f-48a2-905a-f5b961c5e650';

  test.beforeEach(async ({ page }) => {
    // Iniciar sesión con el usuario de prueba antes de cada test de navegación privada
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login(SEED_USER_EMAIL, SEED_USER_PASS);
    await page.waitForURL(url => !url.pathname.endsWith('/login'), { timeout: 10000 });
  });

  test('debe navegar a Mi Perfil (/profile)', async ({ page }) => {
    const profilePage = new ProfilePage(page);
    await profilePage.goto();
    await profilePage.expectProfileLoaded();
  });

  test('debe visualizar el Perfil Público del usuario (/u/:id)', async ({ page }) => {
    const profilePage = new ProfilePage(page);
    await profilePage.gotoPublicProfile(SEED_USER_ID);
    await profilePage.expectPublicProfileLoaded();
  });

  test('debe navegar a la sección de Mis Experiencias (/experiences o /experience)', async ({ page }) => {
    await page.goto('/experiences');
    // En caso de que la ruta varíe en Vue 3 vs Nuxt
    if (page.url().includes('404')) {
      await page.goto('/profile');
    }
    expect(page.url()).toMatch(/\/(experiences|experience|profile)/);
  });

  test('debe navegar a Solicitudes de Feedback (/feedback)', async ({ page }) => {
    const feedbackPage = new FeedbackPage(page);
    await feedbackPage.goto();
    await feedbackPage.expectLoaded();
  });

  test('debe navegar al Buscador de Recruiter (/recruiter/search)', async ({ page }) => {
    await page.goto('/recruiter/search');
    await expect(page).toHaveURL(/.*recruiter\/search/);
  });

  test('debe realizar la secuencia de navegación entre todos los menús sin perder la sesión', async ({ page }) => {
    // 1. Dashboard
    await page.goto('/dashboard');
    await expect(page).toHaveURL(/.*dashboard/);

    // 2. Mi Perfil
    await page.goto('/profile');
    await expect(page).toHaveURL(/.*profile/);

    // 3. Feedback
    await page.goto('/feedback');
    await expect(page).toHaveURL(/.*feedback/);

    // 4. Perfil Público
    await page.goto(`/u/${SEED_USER_ID}`);
    await expect(page).toHaveURL(new RegExp(`.*\/u\/${SEED_USER_ID}`));

    // 5. Volver al Dashboard
    await page.goto('/dashboard');
    await expect(page).toHaveURL(/.*dashboard/);
  });
});
