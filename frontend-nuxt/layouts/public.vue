<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '~/stores/auth.store'

const { locale, setLocale } = useI18n()
const mobileMenuOpen = ref(false)
const authStore = useAuthStore()

const currentLocale = computed(() => locale.value)

const changeLang = (lang: 'es' | 'en') => {
  setLocale(lang)
}

const navLinks = [
  { translationKey: 'navbar.howItWorks', to: '/#como-funciona' },
  { translationKey: 'navbar.skills', to: '/#habilidades' },
  { translationKey: 'navbar.verification', to: '/#verificacion' },
  { translationKey: 'navbar.useCases', to: '/#casos-de-uso' },
  { translationKey: 'navbar.forWhom', to: '/#para-quien' },
]
</script>

<template>
  <a href="#main-content" class="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-white focus:rounded-lg focus:font-bold focus:shadow-lg focus:outline-none focus:ring-2 focus:ring-primary">
    {{ $t('navbar.skipToContent') }}
  </a>
  <div class="min-h-screen bg-[hsl(228,16%,7%)] text-[hsl(220,14%,94%)] font-sans">
    
    <!-- Navbar -->
    <header class="fixed top-0 inset-x-0 z-50 border-b border-white/5 backdrop-blur-xl bg-[hsl(228,16%,7%)]/80">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          
          <NuxtLink to="/" class="flex items-center">
            <img src="/logo-cache.png" alt="Caché" class="h-9 w-auto object-contain" />
          </NuxtLink>

          <nav class="hidden md:flex items-center gap-6">
            <NuxtLink
              v-for="link in navLinks"
              :key="link.translationKey"
              :to="link.to"
              class="text-sm text-[hsl(220,10%,60%)] hover:text-white transition-colors duration-200"
            >
              {{ $t(link.translationKey) }}
            </NuxtLink>
          </nav>

          <div class="hidden md:flex items-center gap-4">
            <div class="flex items-center gap-1 bg-white/5 border border-white/10 rounded-lg p-0.5">
              <button
                @click="changeLang('es')"
                class="px-2 py-0.5 text-xs font-bold rounded transition-all duration-200"
                :class="currentLocale === 'es' ? 'bg-primary text-white shadow-sm' : 'text-zinc-400 hover:text-white'"
              >
                ES
              </button>
              <button
                @click="changeLang('en')"
                class="px-2 py-0.5 text-xs font-bold rounded transition-all duration-200"
                :class="currentLocale === 'en' ? 'bg-primary text-white shadow-sm' : 'text-zinc-400 hover:text-white'"
              >
                EN
              </button>
            </div>

            <template v-if="!authStore.isAuthenticated">
              <NuxtLink
                to="/login"
                class="text-sm font-medium text-[hsl(220,10%,65%)] hover:text-white transition-colors px-3 py-2"
              >
                {{ $t('navbar.login') }}
              </NuxtLink>
              <NuxtLink
                to="/register"
                class="text-sm font-semibold text-white bg-primary hover:bg-primary/90 transition-all duration-200
                       px-4 py-2 rounded-lg hover:shadow-[0_0_16px_rgba(242,151,39,0.4)] hover:-translate-y-px"
              >
                {{ $t('navbar.startFree') }}
              </NuxtLink>
            </template>
            <template v-else>
              <NuxtLink
                to="/dashboard"
                class="text-sm font-semibold text-white bg-primary hover:bg-primary/90 transition-all duration-200
                       px-4 py-2 rounded-lg hover:shadow-[0_0_16px_rgba(242,151,39,0.4)] hover:-translate-y-px"
              >
                {{ $t('navbar.dashboard') }}
              </NuxtLink>
            </template>
          </div>

          <div class="flex md:hidden items-center gap-3">
            <div class="flex items-center gap-0.5 bg-white/5 border border-white/10 rounded-lg p-0.5">
              <button
                @click="changeLang('es')"
                class="px-1.5 py-0.5 text-[10px] font-bold rounded"
                :class="currentLocale === 'es' ? 'bg-primary text-white' : 'text-zinc-400'"
              >
                ES
              </button>
              <button
                @click="changeLang('en')"
                class="px-1.5 py-0.5 text-[10px] font-bold rounded"
                :class="currentLocale === 'en' ? 'bg-primary text-white' : 'text-zinc-400'"
              >
                EN
              </button>
            </div>

            <button
              @click="mobileMenuOpen = !mobileMenuOpen"
              class="p-2 text-[hsl(220,10%,60%)] hover:text-white transition-colors"
              aria-label="Toggle menu"
            >
              <svg v-if="!mobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
              </svg>
              <svg v-else class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <div
        v-if="mobileMenuOpen"
        class="md:hidden border-t border-white/5 bg-[hsl(228,15%,9%)] px-4 py-4 space-y-3"
      >
        <NuxtLink
          v-for="link in navLinks"
          :key="link.translationKey"
          :to="link.to"
          @click="mobileMenuOpen = false"
          class="block text-sm text-[hsl(220,10%,65%)] hover:text-white py-2 transition-colors"
        >
          {{ $t(link.translationKey) }}
        </NuxtLink>
        <div class="pt-3 flex flex-col gap-2 border-t border-white/5">
          <template v-if="!authStore.isAuthenticated">
            <NuxtLink to="/login" @click="mobileMenuOpen = false" class="text-sm text-center font-medium text-[hsl(220,10%,65%)] hover:text-white py-2">
              {{ $t('navbar.login') }}
            </NuxtLink>
            <NuxtLink to="/register" @click="mobileMenuOpen = false" class="text-sm text-center font-semibold text-white bg-primary hover:bg-primary/90 py-2.5 rounded-lg">
              {{ $t('navbar.startFree') }}
            </NuxtLink>
          </template>
          <template v-else>
            <NuxtLink to="/dashboard" @click="mobileMenuOpen = false" class="text-sm text-center font-semibold text-white bg-primary hover:bg-primary/90 py-2.5 rounded-lg">
              {{ $t('navbar.dashboard') }}
            </NuxtLink>
          </template>
        </div>
      </div>
    </header>

    <main id="main-content" class="relative pt-16">
      <slot />
    </main>

    <footer class="border-t border-white/5 py-10 mt-20">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-4">
        <img src="/logo-cache.png" alt="Caché" class="h-8 w-auto object-contain opacity-70" />
        <p class="text-xs text-[hsl(220,10%,40%)] text-center">
          © 2025 Caché. {{ $t('footer.tagline') }}
        </p>
        <div class="flex gap-4 text-xs text-[hsl(220,10%,45%)]">
          <a href="#" class="hover:text-white transition-colors">{{ $t('footer.privacy') }}</a>
          <a href="#" class="hover:text-white transition-colors">{{ $t('footer.terms') }}</a>
          <a href="#" class="hover:text-white transition-colors">{{ $t('footer.contact') }}</a>
        </div>
      </div>
    </footer>

  </div>
</template>
