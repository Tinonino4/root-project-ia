import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { professionalApi } from '@/api/professional.api';

export const useExperienceStore = defineStore('experience', () => {
  const experiences = ref([]);
  const loading = ref(false);
  const error = ref(null);

  const sortedByDate = computed(() => {
    return [...experiences.value].sort((a, b) => {
      // Sort by finishDate (nulls first = present), then by startDate
      if (a.finishDate === null && b.finishDate !== null) return -1;
      if (a.finishDate !== null && b.finishDate === null) return 1;
      if (a.finishDate === b.finishDate) {
         return new Date(b.startDate) - new Date(a.startDate);
      }
      return new Date(b.finishDate) - new Date(a.finishDate);
    });
  });

  const getExperienceById = (id) => {
    return experiences.value.find(exp => exp.id === id);
  };

  const fetchExperiences = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await professionalApi.getExperiences();
      experiences.value = response.data;
      return experiences.value;
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar las experiencias';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const addExperience = async (experienceData) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await professionalApi.createExperience(experienceData);
      experiences.value.push(response.data);
      return response.data;
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al añadir la experiencia';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const updateExperience = async (id, experienceData) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await professionalApi.updateExperience(id, experienceData);
      const index = experiences.value.findIndex(exp => exp.id === id);
      if (index !== -1) {
        experiences.value[index] = response.data;
      }
      return response.data;
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al actualizar la experiencia';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const deleteExperience = async (id) => {
    loading.value = true;
    error.value = null;
    try {
      await professionalApi.deleteExperience(id);
      experiences.value = experiences.value.filter(exp => exp.id !== id);
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al eliminar la experiencia';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    experiences,
    loading,
    error,
    sortedByDate,
    getExperienceById,
    fetchExperiences,
    addExperience,
    updateExperience,
    deleteExperience
  };
});
