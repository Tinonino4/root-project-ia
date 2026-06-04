<script setup>
import logoUrl from '@/assets/images/logo-cache.png';

// Avatares para social proof con iniciales
const testimonials = [
  { initials: 'AM', gradient: 'from-primary to-accent' },
  { initials: 'CR', gradient: 'from-accent to-orange-400' },
  { initials: 'LG', gradient: 'from-secondary to-primary' },
];
</script>

<template>
  <a href="#main-content" class="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-white focus:rounded-lg focus:font-bold focus:shadow-lg focus:outline-none focus:ring-2 focus:ring-primary">
    Saltar al contenido principal
  </a>
  <div class="min-h-screen flex flex-col bg-[hsl(228,16%,7%)] font-sans selection:bg-primary/30 text-[hsl(220,14%,94%)]">

    <!-- ─── MOBILE HEADER ─────────────────────────────────────────────────── -->
    <header class="md:hidden flex items-center justify-center px-6 pt-8 pb-4">
      <img :src="logoUrl" alt="Caché Logo" class="h-10 w-auto object-contain" />
    </header>

    <!-- ─── MOBILE HERO — visible solo en mobile ─────────────────────────── -->
    <div class="md:hidden relative overflow-hidden mx-4 mb-6 rounded-2xl p-6 bg-[hsl(228,15%,11%)] border border-[hsl(226,13%,20%)]">
      <!-- Blobs decorativos -->
      <div class="absolute -top-8 -right-8 w-40 h-40 rounded-full bg-primary/20 blur-3xl pointer-events-none"></div>
      <div class="absolute -bottom-8 -left-8 w-32 h-32 rounded-full bg-secondary/15 blur-3xl pointer-events-none"></div>

      <div class="relative z-10">
        <p class="text-xs font-semibold text-primary uppercase tracking-widest mb-3">Plataforma profesional</p>
        <h2 class="text-xl font-bold text-white leading-snug mb-3">
          Aumenta tu <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary to-secondary">valor</span> profesional.
        </h2>
        <p class="text-sm text-[hsl(220,10%,58%)] leading-relaxed mb-4">
          Consigue referencias profesionales con una visión 360 de tu trabajo.
        </p>

        <!-- Social proof mobile -->
        <div class="flex items-center gap-3">
          <div class="flex -space-x-2">
            <div
              v-for="t in testimonials"
              :key="t.initials"
              :class="`w-8 h-8 rounded-full border-2 border-[hsl(228,16%,7%)] bg-gradient-to-br ${t.gradient} flex items-center justify-center text-white text-[10px] font-bold`"
            >
              {{ t.initials }}
            </div>
          </div>
          <p class="text-xs text-[hsl(220,10%,58%)]">+2.000 profesionales ya confían en Caché</p>
        </div>
      </div>
    </div>

    <!-- ─── MAIN LAYOUT SPLIT ────────────────────────────────────────────── -->
    <div class="flex flex-1 flex-col md:flex-row">

      <!-- ─── LEFT PANEL: Desktop Branding ─────────────────────────────── -->
      <div class="hidden md:flex md:w-1/2 lg:w-3/5 relative overflow-hidden bg-[hsl(228,15%,9%)] border-r border-[hsl(226,13%,18%)] flex-col justify-between p-12">

        <!-- Blobs de fondo animados -->
        <div class="absolute -top-[25%] -left-[15%] w-[65%] h-[65%] rounded-full bg-primary/15 blur-[140px] mix-blend-screen animate-pulse" style="animation-duration: 4s;"></div>
        <div class="absolute bottom-[5%] -right-[20%] w-[55%] h-[55%] rounded-full bg-secondary/12 blur-[120px] mix-blend-screen"></div>
        <div class="absolute top-[40%] left-[30%] w-[40%] h-[40%] rounded-full bg-accent/8 blur-[100px] mix-blend-screen"></div>

        <!-- Logo desktop -->
        <div class="relative z-10 animate-in fade-in slide-in-from-top-4 duration-700">
          <img :src="logoUrl" alt="Caché Logo" class="h-11 w-auto object-contain" />
        </div>

        <!-- Copy hero desktop -->
        <div class="relative z-10 max-w-lg animate-in fade-in slide-in-from-bottom-6 duration-700 delay-200">
          <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-primary/10 border border-primary/20 mb-6">
            <span class="w-2 h-2 rounded-full bg-primary animate-pulse"></span>
            <span class="text-xs font-semibold text-primary uppercase tracking-wider">Plataforma de referencias</span>
          </div>

          <h2 class="text-4xl lg:text-5xl font-bold text-white mb-5 leading-tight">
            Aumenta tu<br />
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-accent to-secondary">
              valor profesional.
            </span>
          </h2>
          <p class="text-[hsl(220,10%,58%)] text-base leading-relaxed mb-8 max-w-md">
            Consigue las referencias profesionales de tu perfil con una visión 360. Compañeros, proveedores o clientes podrán valorar tu forma de trabajar.
          </p>

          <!-- Social proof con avatares reales (iniciales) -->
          <div class="flex items-center gap-4">
            <div class="flex -space-x-3">
              <div
                v-for="t in testimonials"
                :key="t.initials"
                :class="`w-10 h-10 rounded-full border-2 border-[hsl(228,15%,9%)] bg-gradient-to-br ${t.gradient} flex items-center justify-center text-white text-xs font-bold shadow-lg`"
              >
                {{ t.initials }}
              </div>
            </div>
            <div>
              <p class="text-sm font-medium text-[hsl(220,14%,88%)]">+2.000 profesionales</p>
              <p class="text-xs text-[hsl(220,10%,50%)]">ya confían en la plataforma</p>
            </div>
          </div>

          <!-- Feature pills -->
          <div class="flex flex-wrap gap-2 mt-6">
            <span class="px-3 py-1 rounded-full text-xs font-medium bg-white/5 border border-white/10 text-[hsl(220,14%,75%)]">✓ Referencias verificadas</span>
            <span class="px-3 py-1 rounded-full text-xs font-medium bg-white/5 border border-white/10 text-[hsl(220,14%,75%)]">✓ Visión 360°</span>
            <span class="px-3 py-1 rounded-full text-xs font-medium bg-white/5 border border-white/10 text-[hsl(220,14%,75%)]">✓ Perfil profesional</span>
          </div>
        </div>
      </div>

      <!-- ─── RIGHT PANEL: Forms Area ──────────────────────────────────── -->
      <main id="main-content" class="w-full md:w-1/2 lg:w-2/5 flex items-center justify-center px-6 py-8 sm:p-12 relative bg-[hsl(228,16%,7%)]">
        <div class="w-full max-w-[420px] relative">
          <router-view v-slot="{ Component, route: viewRoute }">
            <Transition name="route-fade" mode="out-in">
              <component :is="Component" :key="viewRoute.path" />
            </Transition>
          </router-view>
        </div>
      </main>

    </div>
  </div>
</template>

<style scoped>
</style>
