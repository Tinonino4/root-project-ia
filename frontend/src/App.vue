<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
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
  <component :is="currentLayout" />
</template>
