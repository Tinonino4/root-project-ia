import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authApi } from '@/api/auth.api';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null);
  const refreshToken = ref(localStorage.getItem('refreshToken') || null);
  const user = ref(JSON.parse(localStorage.getItem('user')) || null);
  const loading = ref(false);
  const error = ref(null);

  const isAuthenticated = computed(() => !!token.value);
  const userName = computed(() => user.value?.name || '');
  const userRole = computed(() => user.value?.role || '');

  function setAuth(userData, authToken, refreshAuthToken) {
    token.value = authToken;
    user.value = userData;
    if (authToken) {
      localStorage.setItem('token', authToken);
    }
    if (refreshAuthToken) {
      refreshToken.value = refreshAuthToken;
      localStorage.setItem('refreshToken', refreshAuthToken);
    }
    if (userData) {
      localStorage.setItem('user', JSON.stringify(userData));
    }
  }

  function logout() {
    token.value = null;
    refreshToken.value = null;
    user.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }

  async function login(email, password) {
    loading.value = true;
    error.value = null;
    try {
      const response = await authApi.login({ email, password });
      setAuth(
        { id: response.data.id, name: response.data.name, role: response.data.role },
        response.data.token,
        response.data.refreshToken
      );
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

  async function refreshSession() {
    if (!refreshToken.value) {
      logout();
      throw new Error('No refresh token available');
    }
    try {
      const response = await authApi.refreshToken({ refreshToken: refreshToken.value });
      const newAccessToken = response.data.accessToken;
      const newRefreshToken = response.data.refreshToken;
      
      token.value = newAccessToken;
      refreshToken.value = newRefreshToken;
      localStorage.setItem('token', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
      
      return response.data;
    } catch (e) {
      logout();
      throw e;
    }
  }

  return {
    token,
    refreshToken,
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
    refreshToken: refreshSession,
    logout,
  };
});
