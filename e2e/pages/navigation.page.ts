import { Page, Locator, expect } from '@playwright/test';

export class NavigationPage {
  readonly page: Page;
  readonly logo: Locator;
  readonly homeLink: Locator;
  readonly loginLink: Locator;
  readonly registerLink: Locator;

  constructor(page: Page) {
    this.page = page;
    this.logo = page.locator('a[href="/"], .logo, svg').first();
    this.homeLink = page.locator('a[href="/"]').first();
    this.loginLink = page.locator('a[href="/login"]').first();
    this.registerLink = page.locator('a[href="/register"]').first();
  }

  async gotoHome() {
    await this.page.goto('/');
  }

  async expectHomeLoaded() {
    await expect(this.page).toHaveTitle(/.*/);
    await expect(this.homeLink).toBeVisible();
  }
}
