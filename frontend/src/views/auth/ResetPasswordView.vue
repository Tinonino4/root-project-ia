<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

const email = ref('');
const code = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const successMessage = ref('');
const passwordError = ref('');

onMounted(() => {
  if (route.query.email) {
    email.value = route.query.email;
  }
});

const handleResetPassword = async () => {
  passwordError.value = '';
  successMessage.value = '';

  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = 'Las contraseñas no coinciden';
    return;
  }

  if (newPassword.value.length < 8) {
    passwordError.value = 'La contraseña debe tener al menos 8 caracteres';
    return;
  }

  try {
    await authStore.resetPassword(email.value, code.value, newPassword.value);
    successMessage.value = '¡Contraseña actualizada correctamente!';
    setTimeout(() => {
      router.push({ name: 'Login' });
    }, 2000);
  } catch (error) {
    console.error('Reset password failed:', error);
  }
};
</script>

<template>
  <div class="space-y-6">
    <div class="space-y-2 text-center md:text-left">
      <!-- Icono decorativo -->
      <div class="flex justify-center md:justify-start mb-2">
        <div class="w-12 h-12 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-primary">
            <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
          </svg>
        </div>
      </div>
      <h1 class="text-3xl font-bold font-heading tracking-tight text-zinc-900 dark:text-white">Nueva contraseña</h1>
      <p class="text-zinc-500 dark:text-zinc-400">Introduce el código que has recibido por email y elige tu nueva contraseña</p>
    </div>

    <form @submit.prevent="handleResetPassword" class="space-y-4">
      <div class="space-y-2">
        <Label for="reset-email">Correo electrónico</Label>
        <Input 
          id="reset-email" 
          type="email" 
          v-model="email" 
          required 
          :disabled="!!route.query.email"
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm transition-all focus:ring-primary"
        />
      </div>

      <div class="space-y-2">
        <Label for="reset-code">Código de verificación (6 dígitos)</Label>
        <Input 
          id="reset-code" 
          type="text" 
          placeholder="123456" 
          v-model="code" 
          required 
          maxlength="6" 
          pattern="\d{6}" 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm text-center text-2xl tracking-[0.5em] h-14 font-mono focus:ring-primary transition-all" 
        />
      </div>

      <div class="space-y-2">
        <Label for="new-password">Nueva contraseña</Label>
        <Input 
          id="new-password" 
          type="password" 
          placeholder="••••••••"
          v-model="newPassword" 
          required 
          minlength="8" 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm transition-all focus:ring-primary"
        />
        <p class="text-xs text-zinc-500 mt-1">Mínimo 8 caracteres</p>
      </div>

      <div class="space-y-2">
        <Label for="confirm-password">Confirmar contraseña</Label>
        <Input 
          id="confirm-password" 
          type="password" 
          placeholder="••••••••"
          v-model="confirmPassword" 
          required 
          minlength="8" 
          class="bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm transition-all focus:ring-primary"
        />
      </div>
      
      <!-- Validation error -->
      <div v-if="passwordError" class="text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 p-3 rounded-lg flex items-start gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="shrink-0 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <span>{{ passwordError }}</span>
      </div>

      <!-- API error -->
      <div v-if="authStore.error" class="text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 p-3 rounded-lg flex items-start gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="shrink-0 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <span>{{ authStore.error }}</span>
      </div>

      <!-- Success message -->
      <div v-if="successMessage" class="text-sm text-emerald-700 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-900/20 border border-emerald-200 dark:border-emerald-800 p-3 rounded-lg flex items-center gap-2 font-medium">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="shrink-0"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        <span>{{ successMessage }}</span>
      </div>
      
      <Button 
        type="submit" 
        class="w-full bg-primary hover:bg-primary/90 text-primary-foreground shadow-[0_4px_14px_0_hsl(var(--primary)/0.39)] hover:shadow-[0_6px_20px_hsl(var(--primary)/0.23)] hover:-translate-y-0.5 transition-all duration-200" 
        :disabled="authStore.loading || !!successMessage"
      >
        <svg v-if="authStore.loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-primary-foreground" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        <span v-if="authStore.loading">Restableciendo...</span>
        <span v-else>Restablecer contraseña</span>
      </Button>
    </form>

    <div class="text-center text-sm text-zinc-500 dark:text-zinc-400 mt-6">
      ¿No recibiste el código?
      <RouterLink to="/forgot-password" class="font-medium text-primary hover:text-primary/80 hover:underline transition-all">
        Reenviar código
      </RouterLink>
    </div>
  </div>
</template>
