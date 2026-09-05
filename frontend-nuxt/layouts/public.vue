<script setup lang="ts">
import { ref } from 'vue'
import Logo from '~/components/ui/Logo.vue'
import LanguageSwitcher from '~/components/ui/LanguageSwitcher.vue'
import { useAuthStore } from '~/stores/auth.store'

const mobileMenuOpen = ref(false)
const authStore = useAuthStore()

const navLinks = [
  { translationKey: 'navbar.howItWorks', to: '/#como-funciona' },
  { translationKey: 'navbar.skills', to: '/#habilidades' },
  { translationKey: 'navbar.verification', to: '/#verificacion' },
  { translationKey: 'navbar.useCases', to: '/#casos-de-uso' },
  { translationKey: 'navbar.forWhom', to: '/#para-quien' },
  { translationKey: 'navbar.forRecruiters', to: '/empresas' },
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
            <Logo variant="full" size="md" interactive />
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
            <LanguageSwitcher theme="dark" variant="pill" />

            <template v-if="!authStore.isAuthenticated">
              <NuxtLink
                to="/login"
                class="text-sm font-medium text-[hsl(220,10%,65%)] hover:text-white transition-colors px-3 py-2"
              >
                {{ $t('navbar.login') }}
              </NuxtLink>
              <NuxtLink
                to="/register"
                class="text-sm font-semibold text-white bg-primary hover:bg-primary/90 btn-glow
                       px-4 py-2 rounded-lg"
              >
                {{ $t('navbar.startFree') }}
              </NuxtLink>
            </template>
            <template v-else>
              <NuxtLink
                to="/dashboard"
                class="text-sm font-semibold text-white bg-primary hover:bg-primary/90 btn-glow
                       px-4 py-2 rounded-lg"
              >
                {{ $t('navbar.dashboard') }}
              </NuxtLink>
            </template>
          </div>

          <div class="flex md:hidden items-center gap-3">
            <LanguageSwitcher theme="dark" variant="compact" />

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
        <Logo variant="full" size="sm" class="opacity-80" />
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
