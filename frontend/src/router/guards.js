import { useAuthStore } from '@/stores/auth.store';

export function setupGuards(router) {
  router.beforeEach((to) => {
    const authStore = useAuthStore();
    const isAuthenticated = authStore.isAuthenticated;

    // Check inactivity timeout (12 hours)
    const lastActivity = localStorage.getItem('lastActivity');
    const now = Date.now();
    const INACTIVITY_TIMEOUT = 12 * 60 * 60 * 1000; // 12 hours in milliseconds

    if (isAuthenticated && lastActivity && (now - parseInt(lastActivity) > INACTIVITY_TIMEOUT)) {
      authStore.logout();
      localStorage.removeItem('lastActivity');
      return { name: 'Login' };
    }

    if (isAuthenticated) {
      localStorage.setItem('lastActivity', now.toString());
    }

    if (to.meta.auth && !isAuthenticated) {
      return { name: 'Login' };
    }
    if (to.meta.guest && isAuthenticated) {
      return { name: 'Dashboard' };
    }
  });
}
