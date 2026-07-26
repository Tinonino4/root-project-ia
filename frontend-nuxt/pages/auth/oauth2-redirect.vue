<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '~/stores/auth.store'
import { toast } from 'vue-sonner'
import { Loader2 } from 'lucide-vue-next'

definePageMeta({
  layout: 'auth'
})

const route = useRoute()
const authStore = useAuthStore()

onMounted(async () => {
  if (import.meta.client) {
    const token = route.query.token as string
    const refreshToken = route.query.refreshToken as string

    if (!token) {
      toast.error('Error de autenticación', {
        description: 'No se recibió ningún token de acceso. Por favor, intente de nuevo.',
      })
      navigateTo('/login')
      return
    }

    try {
      authStore.setAuth(null, token, refreshToken)
      await authStore.fetchCurrentUser()
      navigateTo('/dashboard')
    } catch (err: any) {
      console.error('Error in OAuth2 redirection handling:', err)
      toast.error('Error al iniciar sesión', {
        description: err.response?._data?.message || 'No se pudo recuperar tu perfil de usuario.',
      })
      navigateTo('/login')
    }
  }
})
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[50vh] space-y-6 text-center">
    <div class="relative w-16 h-16 flex items-center justify-center">
      <Loader2 class="w-12 h-12 text-primary animate-spin" />
    </div>
    
    <div class="space-y-2">
      <h2 class="text-xl font-bold tracking-tight text-zinc-900 dark:text-white font-heading">
        {{ $t('auth.processing') }}
      </h2>
      <p class="text-sm text-zinc-500 dark:text-zinc-400 max-w-xs">
        {{ $t('auth.processingDesc') }}
      </p>
    </div>
  </div>
</template>
