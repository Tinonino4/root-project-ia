// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

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
  ],

  site: {
    url: process.env.NUXT_PUBLIC_SITE_URL || 'http://localhost:3000',
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
