import { test, expect } from '@playwright/test';

test.describe('Pruebas de Paridad de Datos y Contenido (Vue 3 vs Nuxt 3)', () => {
  const SEED_USER_ID = '37007e45-fd4f-48a2-905a-f5b961c5e650';

  test('debe mostrar exactamente los mismos datos en el Perfil Público (/u/:id)', async ({ browser }) => {
    // 1. Abrir contexto Vue 3
    const vueContext = await browser.newContext({ baseURL: 'https://www.micache.es', ignoreHTTPSErrors: true });
    const vuePage = await vueContext.newPage();
    await vuePage.goto(`/u/${SEED_USER_ID}`);
    await vuePage.waitForLoadState('networkidle');

    // 2. Abrir contexto Nuxt 3
    const nuxtContext = await browser.newContext({ baseURL: 'https://www.micache.es:3000', ignoreHTTPSErrors: true });
    const nuxtPage = await nuxtContext.newPage();
    await nuxtPage.goto(`/u/${SEED_USER_ID}`);
    await nuxtPage.waitForLoadState('networkidle');

    // 3. Extraer elementos de datos clave
    const vueName = await vuePage.locator('h1, h2').first().innerText();
    const nuxtName = await nuxtPage.locator('h1, h2').first().innerText();

    const vueText = await vuePage.locator('body').innerText();
    const nuxtText = await nuxtPage.locator('body').innerText();

    // 4. Aserción de paridad de datos esenciales (Nombre, Título, Empresa)
    expect(vueName.trim()).toBe(nuxtName.trim());
    expect(vueText).toContain('Miguel');
    expect(nuxtText).toContain('Miguel');
    expect(vueText).toContain('Iberdrola');
    expect(nuxtText).toContain('Iberdrola');

    await vueContext.close();
    await nuxtContext.close();
  });
});
