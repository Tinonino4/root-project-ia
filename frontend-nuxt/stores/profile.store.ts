import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserProfile, UpdateProfilePayload } from '~/types'

export const useProfileStore = defineStore('profile', () => {
  const profile = ref<UserProfile | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchProfile = async () => {
    loading.value = true
    error.value = null
    try {
      const data = await $api<UserProfile>('/professional/profile')
      profile.value = data
      return data
    } catch (err: unknown) {
      const errorObj = err as { response?: { _data?: { message?: string } }; message?: string }
      error.value = errorObj.response?._data?.message || errorObj.message || 'Error al cargar el perfil'
      console.error('fetchProfile:', error.value)
    } finally {
      loading.value = false
    }
  }

  const updateProfile = async (profileData: UpdateProfilePayload) => {
    loading.value = true
    error.value = null
    try {
      const data = await $api<UserProfile>('/professional/profile', {
        method: 'PUT',
        body: profileData
      })
      profile.value = data
      return data
    } catch (err: unknown) {
      const errorObj = err as { response?: { _data?: { message?: string } }; message?: string }
      error.value = errorObj.response?._data?.message || errorObj.message || 'Error al actualizar el perfil'
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
