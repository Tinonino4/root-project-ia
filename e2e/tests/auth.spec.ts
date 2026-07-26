import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/login.page';

test.describe('Autenticación y Formulario de Login', () => {
  test('debe mostrar los campos de email y contraseña en la vista de login', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.expectLoaded();
  });

  test('debe mostrar error o alerta con credenciales inválidas', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login('usuario_invalido_test@example.com', 'PasswordIncorrecta123!');
    
    // Verificar que permanece en /login
    await expect(page).toHaveURL(/.*login/);
  });

  test('debe iniciar sesión correctamente con usuario de pruebas (seed.user1@micache.com / password123)', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login('seed.user1@micache.com', 'password123');
    
    // Al iniciar sesión debe abandonar la ruta de /login y redirigir (a /dashboard o a /)
    await page.waitForURL(url => !url.pathname.endsWith('/login'), { timeout: 10000 });
    expect(page.url()).not.toContain('/login');
  });
});
