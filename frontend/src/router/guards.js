import { useAuthStore } from '@/stores/auth.store';

export function setupGuards(router) {
  router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const isAuthenticated = authStore.isAuthenticated;

    if (to.meta.auth && !isAuthenticated) {
      next({ name: 'Login' });
    } else if (to.meta.guest && isAuthenticated) {
      next({ name: 'Dashboard' });
    } else {
      next();
    }
  });
}
