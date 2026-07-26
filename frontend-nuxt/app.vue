<script setup lang="ts">
import { Toaster } from 'vue-sonner'

onMounted(() => {
  if (import.meta.client) {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    document.documentElement.classList.toggle('dark', prefersDark)

    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      document.documentElement.classList.toggle('dark', e.matches)
    })
  }
})
</script>

<template>
  <div>
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
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
