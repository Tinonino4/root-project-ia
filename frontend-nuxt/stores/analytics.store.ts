import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAnalyticsStore = defineStore('analytics', () => {
  const metrics = ref<any>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchMetrics = async () => {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/skills/metrics')
      metrics.value = data
    } catch (err: any) {
      error.value = err.message || 'Error al obtener métricas'
      console.error(error.value)
    } finally {
      loading.value = false
    }
  }

  return {
    metrics,
    loading,
    error,
    fetchMetrics
  }
})
