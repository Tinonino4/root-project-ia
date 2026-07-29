import { test, expect } from '@playwright/test';

test.describe('Pruebas E2E - Radar 360° y Arquetipos Conductuales', () => {

  test('debe cargar la vista del perfil 360 y permitir interactuar con las pestañas por rol', async ({ page }) => {
    // Abrir la demo HTML interactiva del perfil 360°
    await page.goto(`file://${process.cwd()}/preview_perfil_360.html`);

    // Verificar que el título y el avatar se muestran
    await expect(page.locator('h1')).toContainText('Alejandro Rivera');
    await expect(page.locator('#roleBadge')).toContainText('Vista: Global Consolidado');

    // Comprobar que existe el canvas del gráfico Radar
    const radarCanvas = page.locator('#radarChart');
    await expect(radarCanvas).toBeVisible();

    // Hacer clic en la pestaña "Jefes"
    await page.click('#tabManagers');
    await expect(page.locator('#roleBadge')).toContainText('Vista: Solo Jefes');

    // Hacer clic en la pestaña "Peers"
    await page.click('#tabPeers');
    await expect(page.locator('#roleBadge')).toContainText('Vista: Solo Compañeros');

    // Hacer clic en la pestaña "Equipo"
    await page.click('#tabSubordinates');
    await expect(page.locator('#roleBadge')).toContainText('Vista: Solo Subordinados');

    // Probar el botón de Modo Comparativo
    await page.click('#btnCompare');
    await expect(page.locator('#roleBadge')).toContainText('Vista: Comparativa 360°');

    // Volver a la vista Global
    await page.click('#tabGlobal');
    await expect(page.locator('#roleBadge')).toContainText('Vista: Global Consolidado');
  });

  test('debe mostrar la tarjeta de Arquetipo & Fit Cultural con sus componentes', async ({ page }) => {
    await page.goto(`file://${process.cwd()}/preview_perfil_360.html`);

    // Comprobar la tarjeta de Arquetipo
    await expect(page.locator('text=Arquetipo & Fit Cultural')).toBeVisible();
    await expect(page.locator('text=Respondedor Pragmático')).toBeVisible();
    await expect(page.locator('text=85% Match')).toBeVisible();
    await expect(page.locator('text=Startup / Scaleup')).toBeVisible();
  });

});
