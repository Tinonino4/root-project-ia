import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useFeedbackStore = defineStore('feedback', () => {
  const requests = ref<any[]>([])
  const categories = ref<any[]>([])
  const relationships = ref<any[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchCategories() {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/feedback/categories')
      categories.value = data
      return data
    } catch (err: any) {
      error.value = err.message || 'Error al cargar categorías'
    } finally {
      loading.value = false
    }
  }

  async function fetchRelationships() {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/feedback/relationships')
      relationships.value = data
      return data
    } catch (err: any) {
      error.value = err.message || 'Error al cargar relaciones'
    } finally {
      loading.value = false
    }
  }

  async function fetchRequests() {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/feedback/requests')
      requests.value = data
      return data
    } catch (err: any) {
      error.value = err.message || 'Error al cargar solicitudes'
    } finally {
      loading.value = false
    }
  }

  async function fetchRequestsByExperience(experienceId: number | string) {
    loading.value = true
    error.value = null
    try {
      const data = await $api(`/feedback/requests/experience/${experienceId}`)
      return data
    } catch (err: any) {
      error.value = err.message || 'Error al cargar solicitudes de la experiencia'
      return []
    } finally {
      loading.value = false
    }
  }

  async function createRequest(requestData: any) {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/feedback/requests', {
        method: 'POST',
        body: requestData
      })
      requests.value.push(data)
      return data
    } catch (err: any) {
      error.value = err.message || 'Error al crear solicitud'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function getCompletedCount(expId: number | string) {
    try {
      return await $api(`/feedback/requests/experience/${expId}/count`)
    } catch (err) {
      console.error('Error getting completed count', err)
      return 0
    }
  }

  async function toggleRequestVisibility(requestId: number | string, visible: boolean) {
    loading.value = true
    error.value = null
    try {
      const data = await $api(`/feedback/requests/${requestId}/visibility`, {
        method: 'PATCH',
        body: { visible }
      })
      const index = requests.value.findIndex(r => r.id === requestId)
      if (index !== -1) {
        requests.value[index] = data
      }
      return data
    } catch (err: any) {
      error.value = err.message || 'Error al cambiar la visibilidad'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function remindRequest(requestId: number | string) {
    loading.value = true
    error.value = null
    try {
      await $api(`/feedback/requests/${requestId}/remind`, {
        method: 'POST'
      })
    } catch (err: any) {
      error.value = err.message || 'Error al enviar recordatorio'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteRequest(requestId: number | string) {
    loading.value = true
    error.value = null
    try {
      await $api(`/feedback/requests/${requestId}`, {
        method: 'DELETE'
      })
      requests.value = requests.value.filter(r => r.id !== requestId)
    } catch (err: any) {
      error.value = err.message || 'Error al eliminar solicitud'
      throw err
    } finally {
      loading.value = false
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
    remindRequest,
    deleteRequest,
  }
})
