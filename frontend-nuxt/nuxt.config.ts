// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: process.env.NODE_ENV !== 'production' },

  app: {
    head: {
      title: 'Caché - Aumenta tu valor | Referencias Profesionales Verificadas',
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ],
      meta: [
        { name: 'description', content: 'MiCaché es la plataforma digital de referencias profesionales verificadas de 360° para potenciar tu carrera y validar tus soft-skills reales.' }
      ]
    }
  },

  modules: [
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss',
    '@nuxtjs/i18n',
    '@vueuse/nuxt',
    '@nuxtjs/sitemap',
    '@nuxt/image',
    '@nuxt/fonts',
    '@nuxtjs/color-mode'
  ],

  colorMode: {
    classSuffix: '',
    preference: 'system',
    fallback: 'light'
  },

  site: {
    url: process.env.NUXT_PUBLIC_SITE_URL || 'https://www.micache.es',
    name: 'Caché - Referencias Profesionales Verificadas',
  },

  sitemap: {
    exclude: [
      '/dashboard/**',
      '/profile/edit',
      '/experiences/**',
      '/feedback/**',
      '/recruiter/**',
      '/auth/**'
    ]
  },

  components: [
    {
      path: '~/components',
      pattern: '**/*.vue',
      pathPrefix: false
    }
  ],

  css: [
    '~/assets/main.css'
  ],

  i18n: {
    compilation: {
      strictMessage: false,
      escapeHtml: false
    },
    bundle: {
      optimizeTranslationDirective: false
    },
    locales: [
      { code: 'es', file: 'es.json', name: 'Español' },
      { code: 'en', file: 'en.json', name: 'English' }
    ],
    lazy: false,
    langDir: 'locales',
    defaultLocale: 'en',
    strategy: 'no_prefix',
    detectBrowserLanguage: false
  },

  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || process.env.VITE_API_BASE_URL || '/api'
    }
  },

  nitro: {
    routeRules: {
      '/**': {
        headers: {
          'X-Frame-Options': 'DENY',
          'X-Content-Type-Options': 'nosniff',
          'Referrer-Policy': 'strict-origin-when-cross-origin',
          'Strict-Transport-Security': 'max-age=31536000; includeSubDomains'
        }
      },
      '/api/**': {
        proxy: 'http://localhost:8080/api/**'
      },
      '/uploads/**': {
        proxy: 'http://localhost:8080/uploads/**'
      }
    },
    devProxy: {
      '/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080/uploads',
        changeOrigin: true
      }
    }
  }
})
