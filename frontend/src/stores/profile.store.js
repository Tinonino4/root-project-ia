import { defineStore } from 'pinia';
import { ref } from 'vue';
import { professionalApi } from '@/api/professional.api';

export const useProfileStore = defineStore('profile', () => {
  const profile = ref(null);
  const loading = ref(false);
  const error = ref(null);

  const fetchProfile = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await professionalApi.getProfile();
      profile.value = response.data;
      return profile.value;
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar el perfil';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const updateProfile = async (profileData) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await professionalApi.updateProfile(profileData);
      profile.value = response.data;
      return profile.value;
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al actualizar el perfil';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    profile,
    loading,
    error,
    fetchProfile,
    updateProfile
  };
});
