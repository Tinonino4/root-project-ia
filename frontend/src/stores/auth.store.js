import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authApi } from '@/api/auth.api';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null);
  const user = ref(JSON.parse(localStorage.getItem('user')) || null);
  const loading = ref(false);
  const error = ref(null);

  const isAuthenticated = computed(() => !!token.value);
  const userName = computed(() => user.value?.name || '');
  const userRole = computed(() => user.value?.role || '');

  function setAuth(userData, authToken) {
    token.value = authToken;
    user.value = userData;
    if (authToken) {
      localStorage.setItem('token', authToken);
    }
    if (userData) {
      localStorage.setItem('user', JSON.stringify(userData));
    }
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  async function login(email, password) {
    loading.value = true;
    error.value = null;
    try {
      const response = await authApi.login({ email, password });
      setAuth({ id: response.data.id, name: response.data.name, role: response.data.role }, response.data.token);
      return response.data;
    } catch (e) {
      error.value = e.response?.data?.message || 'Error de inicio de sesión';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  async function register(name, email, password, role) {
    loading.value = true;
    error.value = null;
    try {
      const response = await authApi.register({ name, email, password, role });
      return response.data;
    } catch (e) {
      error.value = e.response?.data?.message || 'Error en el registro';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  async function confirmAccount(email, code) {
    loading.value = true;
    error.value = null;
    try {
      const response = await authApi.confirm({ email, code });
      return response.data;
    } catch (e) {
      error.value = e.response?.data?.message || 'Error al confirmar la cuenta';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  async function forgotPassword(email) {
    loading.value = true;
    error.value = null;
    try {
      const response = await authApi.forgotPassword({ email });
      return response.data;
    } catch (e) {
      error.value = e.response?.data?.message || 'Error al solicitar recuperación';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  async function resetPassword(email, code, newPassword) {
    loading.value = true;
    error.value = null;
    try {
      const response = await authApi.resetPassword({ email, code, newPassword });
      return response.data;
    } catch (e) {
      error.value = e.response?.data?.message || 'Error al resetear la contraseña';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    userName,
    userRole,
    login,
    register,
    confirmAccount,
    forgotPassword,
    resetPassword,
    logout,
  };
});
