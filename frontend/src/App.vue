<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Toaster } from 'vue-sonner'
import { useAuthStore } from '@/stores/auth.store'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import PublicLayout from '@/layouts/PublicLayout.vue'

const layouts = {
  DefaultLayout,
  AuthLayout,
  PublicLayout
}

const route = useRoute()
const currentLayout = computed(() => {
  const authStore = useAuthStore()
  if (route.name === 'PublicProfile' && authStore.isAuthenticated) {
    return DefaultLayout
  }
  return layouts[route.meta.layout || 'DefaultLayout'] || DefaultLayout
})

// Aplica la clase .dark al <html> según la preferencia del sistema operativo.
// Tailwind JIT usa la clase .dark en el elemento raíz para activar los tokens dark mode.
onMounted(() => {
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  document.documentElement.classList.toggle('dark', prefersDark)

  // Escucha cambios en tiempo real si el usuario cambia el tema del OS
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    document.documentElement.classList.toggle('dark', e.matches)
  })
})
</script>

<template>
  <Toaster
    position="top-right"
    :duration="4000"
    rich-colors
    close-button
    theme="system"
    :toast-options="{
      class: 'font-sans',
      style: { fontFamily: 'Inter, sans-serif' },
    }"
  />
  <component :is="currentLayout" />
</template>
