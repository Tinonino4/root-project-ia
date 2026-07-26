import { Page, Locator, expect } from '@playwright/test';

export class ProfilePage {
  readonly page: Page;
  readonly heading: Locator;
  readonly emailField: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.locator('h1, h2').first();
    this.emailField = page.locator('input[type="email"], text="seed.user1@micache.com", text="Email"').first();
  }

  async goto() {
    await this.page.goto('/profile');
  }

  async gotoPublicProfile(userIdOrSlug: string = '37007e45-fd4f-48a2-905a-f5b961c5e650') {
    await this.page.goto(`/u/${userIdOrSlug}`);
  }

  async expectProfileLoaded() {
    await expect(this.page).toHaveURL(/.*profile/);
    await expect(this.heading).toBeVisible();
  }

  async expectPublicProfileLoaded() {
    await expect(this.page).toHaveURL(/.*\/u\/.*/);
    await expect(this.page.locator('body')).toBeVisible();
  }
}
