<script setup>
import { ref } from 'vue';
import { useRouter, RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

const email = ref('');
const password = ref('');
const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  try {
    await authStore.login(email.value, password.value);
    router.push({ name: 'Dashboard' });
  } catch (error) {
    console.error('Login failed:', error);
  }
};
</script>

<template>
  <div class="space-y-6">
    <div class="space-y-2 text-center md:text-left">
      <h1 class="text-3xl font-bold font-heading tracking-tight text-zinc-900 dark:text-white">Bienvenido de nuevo</h1>
      <p class="text-zinc-500 dark:text-zinc-400">Ingresa tus credenciales para acceder a tu cuenta</p>
    </div>

    <form @submit.prevent="handleLogin" class="space-y-4">
      <div class="space-y-2">
        <Label for="email">Correo electrónico</Label>
        <Input 
          id="email" 
          type="email" 
          placeholder="nombre@ejemplo.com" 
          v-model="email" 
          required 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm transition-all focus:ring-primary"
        />
      </div>
      
      <div class="space-y-2">
        <div class="flex items-center justify-between">
          <Label for="password">Contraseña</Label>
          <RouterLink to="/forgot-password" class="text-sm font-medium text-primary hover:text-primary/80 transition-colors">
            ¿Olvidaste tu contraseña?
          </RouterLink>
        </div>
        <Input 
          id="password" 
          type="password" 
          placeholder="••••••••"
          v-model="password" 
          required 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm transition-all focus:ring-primary"
        />
      </div>
      
      <div v-if="authStore.error" class="text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 p-3 rounded-lg flex items-start gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <span>{{ authStore.error }}</span>
      </div>
      
      <Button type="submit" class="w-full bg-primary hover:bg-primary/90 text-primary-foreground shadow-[0_4px_14px_0_hsl(var(--primary)/0.39)] hover:shadow-[0_6px_20px_hsl(var(--primary)/0.23)] hover:-translate-y-0.5 transition-all duration-200" :disabled="authStore.loading">
        <svg v-if="authStore.loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-primary-foreground" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        <span v-if="authStore.loading">Iniciando sesión...</span>
        <span v-else>Iniciar sesión</span>
      </Button>
    </form>

    <div class="text-center text-sm text-zinc-500 dark:text-zinc-400 mt-6">
      ¿No tienes una cuenta?
      <RouterLink to="/register" class="font-medium text-primary hover:text-primary/80 hover:underline transition-all">
        Regístrate aquí
      </RouterLink>
    </div>
  </div>
</template>
