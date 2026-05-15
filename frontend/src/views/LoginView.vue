<template>
  <div class="w-full space-y-6">
    <!-- Header -->
    <div class="space-y-1">
      <h1 class="text-2xl font-bold text-foreground tracking-tight">Bienvenido de nuevo</h1>
      <p class="text-sm text-muted-foreground">Accede a tu cuenta para continuar</p>
    </div>

    <form @submit.prevent="handleLogin" class="space-y-4">
      <div class="space-y-2">
        <label for="email" class="text-sm font-medium text-foreground">Email</label>
        <input
          id="email"
          v-model="email"
          type="email"
          placeholder="nombre@ejemplo.com"
          required
          class="w-full px-4 py-3 rounded-lg text-sm transition-all duration-200
                 bg-[hsl(226,13%,17%)] border border-[hsl(226,13%,24%)]
                 text-[hsl(220,14%,94%)] placeholder:text-[hsl(220,10%,42%)]
                 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/25
                 dark:bg-[hsl(226,13%,17%)] dark:border-[hsl(226,13%,24%)]
                 dark:text-[hsl(220,14%,94%)] dark:placeholder:text-[hsl(220,10%,42%)]"
        />
      </div>

      <div class="space-y-2">
        <div class="flex items-center justify-between">
          <label for="password" class="text-sm font-medium text-foreground">Contraseña</label>
          <a href="#" class="text-xs text-primary hover:text-primary/80 transition-colors">¿Olvidaste tu contraseña?</a>
        </div>
        <input
          id="password"
          v-model="password"
          type="password"
          placeholder="••••••••"
          required
          class="w-full px-4 py-3 rounded-lg text-sm transition-all duration-200
                 bg-[hsl(226,13%,17%)] border border-[hsl(226,13%,24%)]
                 text-[hsl(220,14%,94%)] placeholder:text-[hsl(220,10%,42%)]
                 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/25
                 dark:bg-[hsl(226,13%,17%)] dark:border-[hsl(226,13%,24%)]
                 dark:text-[hsl(220,14%,94%)] dark:placeholder:text-[hsl(220,10%,42%)]"
        />
      </div>

      <p v-if="error" class="text-sm text-red-400 bg-red-500/10 border border-red-500/20 px-3 py-2 rounded-lg">
        {{ error }}
      </p>

      <button
        type="submit"
        :disabled="loading"
        class="w-full py-3 px-4 rounded-lg font-semibold text-sm text-white transition-all duration-200
               bg-primary hover:bg-primary/90
               disabled:opacity-60 disabled:cursor-not-allowed
               focus:outline-none focus:ring-2 focus:ring-primary/40 focus:ring-offset-2 focus:ring-offset-[hsl(228,16%,7%)]
               hover:shadow-[0_0_20px_rgba(242,151,39,0.35)] hover:-translate-y-px active:translate-y-0"
      >
        <span v-if="loading" class="flex items-center justify-center gap-2">
          <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
          </svg>
          Entrando...
        </span>
        <span v-else>Iniciar Sesión</span>
      </button>
    </form>

    <!-- Divider -->
    <div class="relative flex items-center gap-3">
      <div class="flex-1 h-px bg-border"></div>
      <span class="text-xs text-muted-foreground font-medium uppercase tracking-wider">o continúa con</span>
      <div class="flex-1 h-px bg-border"></div>
    </div>

    <!-- Social Login -->
    <div class="flex flex-col gap-3">
      <a
        href="http://localhost:8080/oauth2/authorization/google"
        class="flex items-center justify-center gap-3 px-4 py-3 rounded-lg border border-border
               bg-card text-foreground text-sm font-medium
               hover:bg-muted transition-all duration-200 hover:border-border/80"
      >
        <svg viewBox="0 0 24 24" width="18" height="18" xmlns="http://www.w3.org/2000/svg">
          <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
          <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84A10.99 10.99 0 0012 23z" fill="#34A853"/>
          <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
          <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
        </svg>
        Continuar con Google
      </a>
      <a
        href="http://localhost:8080/oauth2/authorization/github"
        class="flex items-center justify-center gap-3 px-4 py-3 rounded-lg border border-border
               bg-[hsl(226,14%,13%)] text-[hsl(220,14%,90%)] text-sm font-medium
               hover:bg-[hsl(226,14%,16%)] transition-all duration-200"
      >
        <svg viewBox="0 0 16 16" width="18" height="18" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M8 0a8 8 0 00-2.53 15.59c.4.07.55-.17.55-.38l-.01-1.49c-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82A7.68 7.68 0 018 3.86c.69 0 1.39.09 2.06.26 1.53-1.03 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48l-.01 2.2c0 .21.15.46.55.38A8.01 8.01 0 0016 8a8 8 0 00-8-8z"/>
        </svg>
        Continuar con GitHub
      </a>
    </div>

    <!-- Redirect -->
    <p class="text-center text-sm text-muted-foreground">
      ¿No tienes cuenta?
      <router-link to="/register" class="text-primary font-semibold hover:text-primary/80 transition-colors ml-1">
        Regístrate
      </router-link>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)
const authStore = useAuthStore()

const handleLogin = async () => {
  error.value = ''
  loading.value = true
  try {
    await authStore.login(email.value, password.value)
  } catch (err) {
    error.value = err.response?.data?.message || 'Login fallido. Revisa tus credenciales.'
  } finally {
    loading.value = false
  }
}
</script>
