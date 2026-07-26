import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProfileStore = defineStore('profile', () => {
  const profile = ref<any>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchProfile = async () => {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/professional/profile')
      profile.value = data
      return data
    } catch (err: any) {
      error.value = err.response?._data?.message || err.message || 'Error al cargar el perfil'
      console.error('fetchProfile:', error.value)
    } finally {
      loading.value = false
    }
  }

  const updateProfile = async (profileData: any) => {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/professional/profile', {
        method: 'PUT',
        body: profileData
      })
      profile.value = data
      return data
    } catch (err: any) {
      error.value = err.response?._data?.message || err.message || 'Error al actualizar el perfil'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    profile,
    loading,
    error,
    fetchProfile,
    updateProfile
  }
})
