import { useAuthStore } from '@/stores/auth.store';

export function setupGuards(router) {
  router.beforeEach((to) => {
    const authStore = useAuthStore();
    const isAuthenticated = authStore.isAuthenticated;

    if (to.meta.auth && !isAuthenticated) {
      return { name: 'Login' };
    }
    if (to.meta.guest && isAuthenticated) {
      return { name: 'Dashboard' };
    }
  });
}
