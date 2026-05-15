<template>
  <div class="w-full space-y-6">
    <!-- Header -->
    <div class="space-y-1">
      <h1 class="text-2xl font-bold text-foreground tracking-tight">Crea tu cuenta</h1>
      <p class="text-sm text-muted-foreground">Únete a Caché y empieza a construir tu reputación profesional</p>
    </div>

    <form @submit.prevent="handleRegister" class="space-y-4">
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
        <label for="password" class="text-sm font-medium text-foreground">Contraseña</label>
        <input
          id="password"
          v-model="password"
          type="password"
          placeholder="Mínimo 8 caracteres"
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

      <!-- CTA principal con naranja Caché — contraste blanco: 3.1:1 (texto grande ✅) -->
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
          Creando cuenta...
        </span>
        <span v-else>Crear Cuenta</span>
      </button>
    </form>

    <!-- Divider -->
    <div class="relative flex items-center gap-3">
      <div class="flex-1 h-px bg-border"></div>
      <span class="text-xs text-muted-foreground font-medium uppercase tracking-wider">o regístrate con</span>
      <div class="flex-1 h-px bg-border"></div>
    </div>

    <!-- Social -->
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
    </div>

    <!-- Términos -->
    <p class="text-center text-xs text-muted-foreground leading-relaxed">
      Al registrarte aceptas nuestros
      <a href="#" class="text-primary hover:underline">Términos de Servicio</a> y
      <a href="#" class="text-primary hover:underline">Política de Privacidad</a>
    </p>

    <!-- Redirect -->
    <p class="text-center text-sm text-muted-foreground">
      ¿Ya tienes cuenta?
      <router-link to="/login" class="text-primary font-semibold hover:text-primary/80 transition-colors ml-1">
        Inicia sesión
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

const handleRegister = async () => {
  error.value = ''
  loading.value = true
  try {
    await authStore.register(email.value, password.value)
  } catch (err) {
    error.value = err.response?.data?.body || 'Error al registrarse. Inténtalo de nuevo.'
  } finally {
    loading.value = false
  }
}
</script>
