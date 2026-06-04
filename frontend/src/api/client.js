import axios from 'axios';
import { toast } from 'vue-sonner';
import { useAuthStore } from '@/stores/auth.store';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

client.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const lastActivity = localStorage.getItem('lastActivity');
    const now = Date.now();
    const INACTIVITY_TIMEOUT = 12 * 60 * 60 * 1000; // 12 hours in milliseconds

    if (authStore.token && lastActivity && (now - parseInt(lastActivity) > INACTIVITY_TIMEOUT)) {
      authStore.logout();
      localStorage.removeItem('lastActivity');
      window.location.href = '/login';
      return Promise.reject(new Error('Sesión expirada por inactividad.'));
    }

    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
      localStorage.setItem('lastActivity', now.toString());
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Queue to hold requests that are waiting for token renewal
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

/**
 * Maps HTTP error status codes to user-friendly toast messages.
 * Returns null for statuses handled elsewhere (e.g., 401 → refresh flow).
 */
const getErrorToast = (status, serverMessage) => {
  const messages = {
    400: {
      title: 'Solicitud incorrecta',
      description: serverMessage || 'Revisa los datos enviados e inténtalo de nuevo.',
    },
    403: {
      title: 'Acceso denegado',
      description: 'No tienes permisos para realizar esta acción.',
    },
    404: {
      title: 'Recurso no encontrado',
      description: serverMessage || 'El recurso que buscas no existe o ha sido eliminado.',
    },
    409: {
      title: 'Conflicto',
      description: serverMessage || 'La operación no se pudo completar por un conflicto de datos.',
    },
    422: {
      title: 'Datos inválidos',
      description: serverMessage || 'Revisa los campos del formulario e inténtalo de nuevo.',
    },
    429: {
      title: 'Demasiadas solicitudes',
      description: 'Has realizado muchas peticiones. Espera un momento e inténtalo de nuevo.',
    },
  };

  if (messages[status]) return messages[status];

  if (status >= 500) {
    return {
      title: 'Error del servidor',
      description: 'Ha ocurrido un error inesperado. Nuestro equipo ha sido notificado.',
    };
  }

  return null;
};

client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Check if error is 401 and this request wasn't already retried
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      // Do not try to refresh if the request was to login or refresh-token itself
      if (originalRequest.url.includes('/auth/login') || originalRequest.url.includes('/auth/refresh-token')) {
        const authStore = useAuthStore();
        authStore.logout();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        // Queue the request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return client(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const authStore = useAuthStore();
      try {
        const data = await authStore.refreshToken();
        const newToken = data.accessToken;
        processQueue(null, newToken);
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return client(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        authStore.logout();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // ─── Global error toast handling (non-401 errors) ──────────────
    if (error.response) {
      const status = error.response.status;
      // Skip toast for 401 (handled above) and for auth endpoints that show their own errors
      const isAuthEndpoint = originalRequest.url?.includes('/auth/');
      if (status !== 401 && !isAuthEndpoint) {
        const serverMessage =
          error.response.data?.message || error.response.data?.error || null;
        const toastInfo = getErrorToast(status, serverMessage);
        if (toastInfo) {
          toast.error(toastInfo.title, { description: toastInfo.description });
        }
      }
    } else if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      toast.error('Tiempo de espera agotado', {
        description: 'El servidor tardó demasiado en responder. Inténtalo de nuevo.',
      });
    } else if (!error.response) {
      toast.error('Error de conexión', {
        description: 'No se pudo conectar con el servidor. Verifica tu conexión a internet.',
      });
    }

    return Promise.reject(error);
  }
);

export default client;
