<script setup>
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import logoUrl from '@/assets/images/logo-cache.png';

const mobileMenuOpen = ref(false);

const navLinks = [
  { label: 'Cómo funciona', href: '#como-funciona' },
  { label: 'Características', href: '#caracteristicas' },
  { label: 'Para quién', href: '#para-quien' },
];
</script>

<template>
  <a href="#main-content" class="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-white focus:rounded-lg focus:font-bold focus:shadow-lg focus:outline-none focus:ring-2 focus:ring-primary">
    Saltar al contenido principal
  </a>
  <div class="min-h-screen bg-[hsl(228,16%,7%)] text-[hsl(220,14%,94%)] font-sans">
    
    <!-- ─── NAVBAR ──────────────────────────────────────────────────────── -->
    <header class="fixed top-0 inset-x-0 z-50 border-b border-white/5 backdrop-blur-xl bg-[hsl(228,16%,7%)]/80">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          
          <!-- Logo -->
          <RouterLink to="/" class="flex items-center">
            <img :src="logoUrl" alt="Caché" class="h-9 w-auto object-contain" />
          </RouterLink>

          <!-- Nav links (desktop) -->
          <nav class="hidden md:flex items-center gap-8">
            <a
              v-for="link in navLinks"
              :key="link.label"
              :href="link.href"
              class="text-sm text-[hsl(220,10%,60%)] hover:text-white transition-colors duration-200"
            >
              {{ link.label }}
            </a>
          </nav>

          <!-- CTA actions (desktop) -->
          <div class="hidden md:flex items-center gap-3">
            <RouterLink
              to="/login"
              class="text-sm font-medium text-[hsl(220,10%,65%)] hover:text-white transition-colors px-3 py-2"
            >
              Iniciar sesión
            </RouterLink>
            <RouterLink
              to="/register"
              class="text-sm font-semibold text-white bg-primary hover:bg-primary/90 transition-all duration-200
                     px-4 py-2 rounded-lg hover:shadow-[0_0_16px_rgba(242,151,39,0.4)] hover:-translate-y-px"
            >
              Empieza gratis
            </RouterLink>
          </div>

          <!-- Mobile menu toggle -->
          <button
            @click="mobileMenuOpen = !mobileMenuOpen"
            class="md:hidden p-2 text-[hsl(220,10%,60%)] hover:text-white transition-colors"
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

      <!-- Mobile menu -->
      <div
        v-if="mobileMenuOpen"
        class="md:hidden border-t border-white/5 bg-[hsl(228,15%,9%)] px-4 py-4 space-y-3"
      >
        <a
          v-for="link in navLinks"
          :key="link.label"
          :href="link.href"
          @click="mobileMenuOpen = false"
          class="block text-sm text-[hsl(220,10%,65%)] hover:text-white py-2 transition-colors"
        >
          {{ link.label }}
        </a>
        <div class="pt-3 flex flex-col gap-2 border-t border-white/5">
          <RouterLink to="/login" class="text-sm text-center font-medium text-[hsl(220,10%,65%)] hover:text-white py-2">
            Iniciar sesión
          </RouterLink>
          <RouterLink to="/register" class="text-sm text-center font-semibold text-white bg-primary hover:bg-primary/90 py-2.5 rounded-lg">
            Empieza gratis
          </RouterLink>
        </div>
      </div>
    </header>

    <!-- ─── PAGE CONTENT ────────────────────────────────────────────────── -->
    <main id="main-content">
      <router-view />
    </main>

    <!-- ─── FOOTER ──────────────────────────────────────────────────────── -->
    <footer class="border-t border-white/5 py-10 mt-20">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-4">
        <img :src="logoUrl" alt="Caché" class="h-8 w-auto object-contain opacity-70" />
        <p class="text-xs text-[hsl(220,10%,40%)] text-center">
          © 2025 Caché. La plataforma de referencias profesionales. Todos los derechos reservados.
        </p>
        <div class="flex gap-4 text-xs text-[hsl(220,10%,45%)]">
          <a href="#" class="hover:text-white transition-colors">Privacidad</a>
          <a href="#" class="hover:text-white transition-colors">Términos</a>
          <a href="#" class="hover:text-white transition-colors">Contacto</a>
        </div>
      </div>
    </footer>

  </div>
</template>
