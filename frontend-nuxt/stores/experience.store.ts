import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useExperienceStore = defineStore('experience', () => {
  const experiences = ref<any[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const sortedByDate = computed(() => {
    return [...experiences.value].sort((a, b) => {
      if (a.finishDate === null && b.finishDate !== null) return -1
      if (a.finishDate !== null && b.finishDate === null) return 1
      if (a.finishDate === b.finishDate) {
        return new Date(b.startDate).getTime() - new Date(a.startDate).getTime()
      }
      return new Date(b.finishDate).getTime() - new Date(a.finishDate).getTime()
    })
  })

  const getExperienceById = (id: number | string) => {
    return experiences.value.find(exp => exp.id === id)
  }

  const fetchExperiences = async () => {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/professional/experiences')
      experiences.value = data
      return data
    } catch (err: any) {
      error.value = err.response?._data?.message || err.message || 'Error al cargar las experiencias'
      console.error('fetchExperiences:', error.value)
    } finally {
      loading.value = false
    }
  }

  const addExperience = async (experienceData: any) => {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/professional/experiences', {
        method: 'POST',
        body: experienceData
      })
      experiences.value.push(data)
      return data
    } catch (err: any) {
      error.value = err.response?._data?.message || err.message || 'Error al añadir la experiencia'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateExperience = async (id: number | string, experienceData: any) => {
    loading.value = true
    error.value = null
    try {
      const data = await $api(`/professional/experiences/${id}`, {
        method: 'PUT',
        body: experienceData
      })
      const index = experiences.value.findIndex(exp => exp.id === id)
      if (index !== -1) {
        experiences.value[index] = data
      }
      return data
    } catch (err: any) {
      error.value = err.response?._data?.message || err.message || 'Error al actualizar la experiencia'
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteExperience = async (id: number | string) => {
    loading.value = true
    error.value = null
    try {
      await $api(`/professional/experiences/${id}`, {
        method: 'DELETE'
      })
      experiences.value = experiences.value.filter(exp => exp.id !== id)
    } catch (err: any) {
      error.value = err.response?._data?.message || err.message || 'Error al eliminar la experiencia'
      throw err
    } finally {
      loading.value = false
    }
  }

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
  }
})
