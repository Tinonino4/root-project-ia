import { createI18n } from 'vue-i18n';
import esTranslations from './locales/es.json';
import enTranslations from './locales/en.json';

const savedLocale = localStorage.getItem('locale');
const defaultLocale = savedLocale || 'en';

const i18n = createI18n({
  legacy: false,
  locale: defaultLocale,
  fallbackLocale: 'en',
  globalInjection: true,
  messages: {
    es: esTranslations,
    en: enTranslations
  }
});

export function switchLanguage(newLocale) {
  i18n.global.locale.value = newLocale;
  localStorage.setItem('locale', newLocale);
  document.documentElement.setAttribute('lang', newLocale);
}

document.documentElement.setAttribute('lang', defaultLocale);

export default i18n;
