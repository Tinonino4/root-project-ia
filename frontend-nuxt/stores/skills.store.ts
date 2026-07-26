import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useSkillsStore = defineStore('skills', () => {
  const metrics = ref<any>(null)
  const hasMetrics = ref(false)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const categoryScores = computed(() => {
    return metrics.value?.categoryScores || []
  })

  const averageScore = computed(() => {
    return metrics.value?.averageScore || 0
  })

  async function fetchMetrics() {
    loading.value = true
    error.value = null
    try {
      const data = await $api('/skills/metrics')
      metrics.value = data
      hasMetrics.value = !!data
    } catch (err: any) {
      error.value = err.message || 'Error al obtener métricas'
    } finally {
      loading.value = false
    }
  }

  return {
    metrics,
    hasMetrics,
    loading,
    error,
    categoryScores,
    averageScore,
    fetchMetrics,
  }
})
