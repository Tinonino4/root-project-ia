<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '~/stores/auth.store'
import { Button } from '~/components/ui/button'
import { Input } from '~/components/ui/input'
import { Label } from '~/components/ui/label'

definePageMeta({
  layout: 'auth'
})

const route = useRoute()
const email = ref('')
const code = ref('')
const authStore = useAuthStore()
const successMessage = ref('')

onMounted(() => {
  if (route.query.email) {
    email.value = route.query.email as string
  }
})

const handleConfirm = async () => {
  try {
    await authStore.confirmAccount(email.value, code.value)
    successMessage.value = '¡Cuenta confirmada exitosamente!'
    setTimeout(() => {
      navigateTo('/login')
    }, 2000)
  } catch (error) {
    console.error('Confirmation failed:', error)
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="space-y-2 text-center md:text-left">
      <h1 class="text-3xl font-bold font-heading tracking-tight text-zinc-900 dark:text-white">Verifica tu cuenta</h1>
      <p class="text-zinc-500 dark:text-zinc-400">Hemos enviado un código a tu correo electrónico</p>
    </div>

    <form @submit.prevent="handleConfirm" class="space-y-4">
      <div class="space-y-2">
        <Label for="email">Correo electrónico</Label>
        <Input 
          id="email" 
          type="email" 
          v-model="email" 
          required 
          :disabled="!!route.query.email" 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm"
        />
      </div>

      <div class="space-y-2">
        <Label for="code">Código de confirmación (6 dígitos)</Label>
        <Input 
          id="code" 
          type="text" 
          placeholder="123456" 
          v-model="code" 
          required 
          maxlength="6" 
          pattern="\d{6}" 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm text-center text-2xl tracking-[0.5em] h-14 font-mono focus:ring-primary transition-all" 
        />
      </div>
      
      <div v-if="authStore.error" class="text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 p-3 rounded-lg flex items-start gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <span>{{ authStore.error }}</span>
      </div>

      <div v-if="successMessage" class="text-sm text-emerald-700 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-900/20 border border-emerald-200 dark:border-emerald-800 p-3 rounded-lg flex items-center gap-2 font-medium">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        <span>{{ successMessage }}</span>
      </div>
      
      <Button type="submit" class="w-full bg-primary hover:bg-primary/90 text-primary-foreground shadow-[0_4px_14px_0_hsl(var(--primary)/0.39)] hover:shadow-[0_6px_20px_hsl(var(--primary)/0.23)] hover:-translate-y-0.5 transition-all duration-200" :disabled="authStore.loading || !!successMessage">
        <svg v-if="authStore.loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-primary-foreground" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        <span v-if="authStore.loading">Verificando...</span>
        <span v-else>Verificar código</span>
      </Button>
    </form>
  </div>
</template>
