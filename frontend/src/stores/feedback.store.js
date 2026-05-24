import { defineStore } from 'pinia';
import { ref } from 'vue';
import { feedbackApi } from '@/api/feedback.api';

export const useFeedbackStore = defineStore('feedback', () => {
  const requests = ref([]);
  const categories = ref([]);
  const relationships = ref([]);
  const loading = ref(false);
  const error = ref(null);

  // Actions
  async function fetchCategories() {
    loading.value = true;
    error.value = null;
    try {
      const response = await feedbackApi.getCategories();
      categories.value = response.data;
    } catch (err) {
      error.value = err.message || 'Error al cargar categorías';
    } finally {
      loading.value = false;
    }
  }

  async function fetchRelationships() {
    loading.value = true;
    error.value = null;
    try {
      const response = await feedbackApi.getRelationships();
      relationships.value = response.data;
    } catch (err) {
      error.value = err.message || 'Error al cargar relaciones';
    } finally {
      loading.value = false;
    }
  }

  async function fetchRequests() {
    loading.value = true;
    error.value = null;
    try {
      const response = await feedbackApi.getRequests();
      requests.value = response.data;
    } catch (err) {
      error.value = err.message || 'Error al cargar solicitudes';
    } finally {
      loading.value = false;
    }
  }

  async function fetchRequestsByExperience(experienceId) {
    loading.value = true;
    error.value = null;
    try {
      const response = await feedbackApi.getRequestsByExperience(experienceId);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Error al cargar solicitudes de la experiencia';
      return [];
    } finally {
      loading.value = false;
    }
  }

  async function createRequest(data) {
    loading.value = true;
    error.value = null;
    try {
      const response = await feedbackApi.createRequest(data);
      requests.value.push(response.data);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Error al crear solicitud';
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function getCompletedCount(expId) {
    try {
      const response = await feedbackApi.getCompletedCountByExperience(expId);
      return response.data;
    } catch (err) {
      console.error('Error getting completed count', err);
      return 0;
    }
  }

  async function toggleRequestVisibility(requestId, visible) {
    loading.value = true;
    error.value = null;
    try {
      const response = await feedbackApi.toggleVisibility(requestId, visible);
      const index = requests.value.findIndex(r => r.id === requestId);
      if (index !== -1) {
        requests.value[index] = response.data;
      }
      return response.data;
    } catch (err) {
      error.value = err.message || 'Error al cambiar la visibilidad';
      throw err;
    } finally {
      loading.value = false;
    }
  }

  return {
    requests,
    categories,
    relationships,
    loading,
    error,
    fetchCategories,
    fetchRelationships,
    fetchRequests,
    fetchRequestsByExperience,
    createRequest,
    getCompletedCount,
    toggleRequestVisibility,
  };
});
