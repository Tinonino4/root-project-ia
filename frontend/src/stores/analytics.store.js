import { defineStore } from 'pinia';
import { ref } from 'vue';
import { analyticsApi } from '@/api/analytics.api';

export const useAnalyticsStore = defineStore('analytics', () => {
  const metrics = ref(null);
  const loading = ref(false);
  const error = ref(null);

  const fetchMetrics = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await analyticsApi.getMetrics();
      // Si el endpoint devuelve 204 No Content, la data será vacía/null
      metrics.value = response.status === 204 ? null : response.data;
    } catch (err) {
      error.value = err.message || 'Error al obtener métricas';
      console.error(error.value);
    } finally {
      loading.value = false;
    }
  };

  return {
    metrics,
    loading,
    error,
    fetchMetrics
  };
});
