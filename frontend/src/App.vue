<script setup>
import { computed } from 'vue'
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
</script>

<template>
  <component :is="currentLayout" />
</template>
