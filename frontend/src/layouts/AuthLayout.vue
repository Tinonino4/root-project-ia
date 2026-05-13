<script setup>
import { computed } from 'vue';
import logoUrl from '@/assets/images/PNGs/Logo e Iconos/Caché-02.png';
</script>

<template>
  <div class="min-h-screen flex flex-col md:flex-row bg-zinc-50 dark:bg-zinc-950 font-sans selection:bg-primary/30">
    
    <!-- LEFT PANEL: Dynamic Branding & Visuals -->
    <div class="hidden md:flex md:w-1/2 lg:w-3/5 relative overflow-hidden bg-zinc-900 border-r border-zinc-800/50 flex-col justify-between p-12">
      <!-- Animated Background Gradients -->
      <div class="absolute -top-[20%] -left-[10%] w-[70%] h-[70%] rounded-full bg-primary/20 blur-[120px] mix-blend-screen animate-pulse duration-1000"></div>
      <div class="absolute bottom-[10%] -right-[20%] w-[60%] h-[60%] rounded-full bg-secondary/20 blur-[100px] mix-blend-screen"></div>
      
      <!-- Content -->
      <div class="relative z-10">
        <div class="flex items-center gap-3">
          <!-- Logo Image -->
          <img :src="logoUrl" alt="Caché Logo" class="h-12 w-auto object-contain" />
        </div>
      </div>

      <div class="relative z-10 max-w-lg mt-auto">
        <h2 class="text-4xl lg:text-5xl font-heading text-white mb-6 leading-tight">
          Aumenta tu <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary to-secondary">valor</span>.
        </h2>
        <p class="text-zinc-400 text-lg leading-relaxed mb-8">
          Consigue las referencias profesionales de tu perfil con una visión 360. Compañeros, proveedores o clientes podrán valorar tu forma de trabajar.
        </p>
        
        <div class="flex items-center gap-4 text-sm text-zinc-500 font-medium">
          <div class="flex -space-x-3">
            <div class="w-10 h-10 rounded-full border-2 border-zinc-900 bg-zinc-800"></div>
            <div class="w-10 h-10 rounded-full border-2 border-zinc-900 bg-zinc-700"></div>
            <div class="w-10 h-10 rounded-full border-2 border-zinc-900 bg-zinc-600"></div>
          </div>
          <p>Únete a la plataforma líder de referencias</p>
        </div>
      </div>
    </div>

    <!-- RIGHT PANEL: Forms Area -->
    <div class="w-full md:w-1/2 lg:w-2/5 flex items-center justify-center p-6 sm:p-12 relative">
      <!-- Mobile Header -->
      <div class="absolute top-8 left-8 md:hidden flex items-center gap-2">
        <img :src="logoUrl" alt="Caché Logo" class="h-8 w-auto object-contain" />
      </div>

      <!-- The router view will render the login/register/forgot forms here -->
      <div class="w-full max-w-[420px] mt-12 md:mt-0 animate-in fade-in slide-in-from-bottom-4 duration-700">
        <router-view />
      </div>
    </div>
    
  </div>
</template>

<style scoped>
</style>
