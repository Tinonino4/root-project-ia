<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import { toast } from 'vue-sonner';
import { Loader2 } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

onMounted(async () => {
  const token = route.query.token;
  const refreshToken = route.query.refreshToken;

  if (!token) {
    toast.error('Error de autenticación', {
      description: 'No se recibió ningún token de acceso. Por favor, intente de nuevo.',
    });
    router.push({ name: 'Login' });
    return;
  }

  try {
    // 1. Guardar tokens en la store de autenticación
    authStore.setAuth(null, token, refreshToken);

    // 2. Recuperar la información del usuario actual usando el token
    await authStore.fetchCurrentUser();

    // 3. Redirigir al Dashboard
    router.push({ name: 'Dashboard' });
  } catch (err) {
    console.error('Error in OAuth2 redirection handling:', err);
    toast.error('Error al iniciar sesión', {
      description: err.response?.data?.message || 'No se pudo recuperar tu perfil de usuario.',
    });
    router.push({ name: 'Login' });
  }
});
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[50vh] space-y-6 text-center">
    <div class="relative w-16 h-16 flex items-center justify-center">
      <!-- Outer spinning border -->
      <Loader2 class="w-12 h-12 text-primary animate-spin" />
    </div>
    
    <div class="space-y-2">
      <h2 class="text-xl font-bold tracking-tight text-zinc-900 dark:text-white font-heading">
        Procesando autenticación
      </h2>
      <p class="text-sm text-zinc-500 dark:text-zinc-400 max-w-xs">
        Estamos configurando tu sesión de forma segura. Un momento, por favor...
      </p>
    </div>
  </div>
</template>
