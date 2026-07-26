import { Page, Locator, expect } from '@playwright/test';

export class FeedbackPage {
  readonly page: Page;
  readonly heading: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.locator('h1, h2').first();
  }

  async goto() {
    await this.page.goto('/feedback');
  }

  async expectLoaded() {
    await expect(this.page).toHaveURL(/.*feedback/);
    await expect(this.page.locator('body')).toBeVisible();
  }
}
