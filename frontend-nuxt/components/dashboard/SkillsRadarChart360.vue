<script setup lang="ts">
import { ref, computed } from 'vue'
import { Radar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
} from 'chart.js'
import { Layers, Globe, UserCheck, Users, Crown } from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'
import type { MultiLayerSkillsMetrics, RoleSkillsMetrics } from '~/types/profile'

ChartJS.register(
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
)

const { t } = useI18n()

const props = defineProps<{
  metrics: MultiLayerSkillsMetrics
}>()

type RadarMode = 'global' | 'managers' | 'peers' | 'subordinates' | 'compare'

const activeMode = ref<RadarMode>('global')

const datasetGlobal = computed(() => {
  const data = props.metrics.global || {}
  return {
    label: t('dashboard.metrics.globalScore', 'Global Consolidado'),
    backgroundColor: 'rgba(242, 151, 39, 0.2)',
    borderColor: '#F29727',
    pointBackgroundColor: '#F29727',
    pointBorderColor: '#ffffff',
    borderWidth: 3,
    data: [
      data.teamwork || 0,
      data.proactivity || 0,
      data.integrity || 0,
      data.selfConfidence || 0,
      data.flexibility || 0
    ]
  }
})

const datasetManagers = computed(() => {
  const data = props.metrics.managers || {}
  return {
    label: t('feedback.relationships.SUPERVISOR', 'Jefes (Mánagers)'),
    backgroundColor: 'rgba(56, 189, 248, 0.2)',
    borderColor: '#38BDF8',
    pointBackgroundColor: '#38BDF8',
    pointBorderColor: '#ffffff',
    borderWidth: 3,
    data: [
      data.teamwork || 0,
      data.proactivity || 0,
      data.integrity || 0,
      data.selfConfidence || 0,
      data.flexibility || 0
    ]
  }
})

const datasetPeers = computed(() => {
  const data = props.metrics.peers || {}
  return {
    label: t('feedback.relationships.PEER', 'Compañeros (Peers)'),
    backgroundColor: 'rgba(52, 211, 153, 0.2)',
    borderColor: '#34D399',
    pointBackgroundColor: '#34D399',
    pointBorderColor: '#ffffff',
    borderWidth: 3,
    data: [
      data.teamwork || 0,
      data.proactivity || 0,
      data.integrity || 0,
      data.selfConfidence || 0,
      data.flexibility || 0
    ]
  }
})

const datasetSubordinates = computed(() => {
  const data = props.metrics.subordinates || {}
  return {
    label: t('feedback.relationships.SUBORDINATE', 'Subordinados (Equipo)'),
    backgroundColor: 'rgba(251, 191, 36, 0.2)',
    borderColor: '#FBBF24',
    pointBackgroundColor: '#FBBF24',
    pointBorderColor: '#ffffff',
    borderWidth: 3,
    data: [
      data.teamwork || 0,
      data.proactivity || 0,
      data.integrity || 0,
      data.selfConfidence || 0,
      data.flexibility || 0
    ]
  }
})

const activeDatasets = computed(() => {
  switch (activeMode.value) {
    case 'managers':
      return [datasetManagers.value]
    case 'peers':
      return [datasetPeers.value]
    case 'subordinates':
      return [datasetSubordinates.value]
    case 'compare':
      return [datasetManagers.value, datasetPeers.value, datasetSubordinates.value]
    case 'global':
    default:
      return [datasetGlobal.value]
  }
})

const chartData = computed(() => {
  return {
    labels: [
      t('questionnaire.categories.TEAMWORK.name', 'Colaboración'),
      t('questionnaire.categories.PROACTIVITY.name', 'Autonomía'),
      t('questionnaire.categories.INTEGRITY.name', 'Rigor e Integridad'),
      t('questionnaire.categories.SELF_CONFIDENCE.name', 'Liderazgo'),
      t('questionnaire.categories.FLEXIBILITY.name', 'Adaptabilidad')
    ],
    datasets: activeDatasets.value
  }
})

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    r: {
      angleLines: { color: 'rgba(255, 255, 255, 0.08)' },
      grid: { color: 'rgba(255, 255, 255, 0.08)' },
      pointLabels: {
        color: 'rgba(230, 235, 245, 0.85)',
        font: { family: 'Inter', size: 11, weight: '600' }
      },
      min: 0,
      max: 100,
      ticks: { display: false, stepSize: 20 }
    }
  },
  plugins: {
    legend: {
      display: activeMode.value === 'compare',
      position: 'bottom' as const,
      labels: {
        color: '#A1A1AA',
        font: { family: 'Inter', size: 11, weight: '500' },
        usePointStyle: true,
        padding: 12
      }
    },
    tooltip: {
      backgroundColor: 'rgba(15, 17, 23, 0.95)',
      titleFont: { family: 'Inter', size: 12, weight: 'bold' },
      bodyFont: { family: 'Inter', size: 12 },
      padding: 12,
      cornerRadius: 10,
      callbacks: {
        label: function (context: any) {
          return `${context.dataset.label}: ${Math.round(context.raw)}%`
        }
      }
    }
  }
}))
</script>

<template>
  <div class="w-full space-y-4">
    
    <!-- Header with Badge -->
    <div class="flex items-center justify-between border-b border-white/5 pb-3">
      <span class="text-xs font-bold text-zinc-300">
        {{ $t('dashboard.radarTitle', 'Radar 360° por Roles') }}
      </span>
      <span class="text-[10px] font-extrabold uppercase px-2.5 py-1 rounded-md bg-primary/10 text-primary border border-primary/20">
        {{ activeMode === 'compare' ? 'Modo Comparativo' : activeMode.toUpperCase() }}
      </span>
    </div>

    <!-- Tabs selector for Roles -->
    <div class="grid grid-cols-4 gap-1 p-1 rounded-xl bg-white/[0.03] border border-white/5 text-[11px] font-semibold text-center">
      <button 
        @click="activeMode = 'global'" 
        :class="['py-1.5 px-2 rounded-lg transition-all flex items-center justify-center gap-1', activeMode === 'global' ? 'bg-primary text-white font-bold shadow-sm' : 'text-zinc-400 hover:text-white']"
      >
        <Globe class="w-3 h-3 flex-shrink-0" />
        <span class="truncate">Global</span>
      </button>

      <button 
        @click="activeMode = 'managers'" 
        :class="['py-1.5 px-2 rounded-lg transition-all flex items-center justify-center gap-1', activeMode === 'managers' ? 'bg-sky-500 text-white font-bold shadow-sm' : 'text-zinc-400 hover:text-white']"
      >
        <UserCheck class="w-3 h-3 flex-shrink-0" />
        <span class="truncate">Jefes</span>
      </button>

      <button 
        @click="activeMode = 'peers'" 
        :class="['py-1.5 px-2 rounded-lg transition-all flex items-center justify-center gap-1', activeMode === 'peers' ? 'bg-emerald-500 text-white font-bold shadow-sm' : 'text-zinc-400 hover:text-white']"
      >
        <Users class="w-3 h-3 flex-shrink-0" />
        <span class="truncate">Peers</span>
      </button>

      <button 
        @click="activeMode = 'subordinates'" 
        :class="['py-1.5 px-2 rounded-lg transition-all flex items-center justify-center gap-1', activeMode === 'subordinates' ? 'bg-amber-500 text-white font-bold shadow-sm' : 'text-zinc-400 hover:text-white']"
      >
        <Crown class="w-3 h-3 flex-shrink-0" />
        <span class="truncate">Equipo</span>
      </button>
    </div>

    <!-- Toggle Compare Mode -->
    <div class="flex justify-end">
      <button 
        @click="activeMode = activeMode === 'compare' ? 'global' : 'compare'"
        class="text-[11px] font-semibold text-zinc-400 hover:text-primary transition-colors flex items-center gap-1.5"
      >
        <Layers class="w-3.5 h-3.5" />
        <span>{{ activeMode === 'compare' ? 'Ver Vista Consolidada' : 'Superponer 360° (Comparar)' }}</span>
      </button>
    </div>

    <!-- Radar Canvas -->
    <div class="relative w-full max-w-sm mx-auto aspect-square flex items-center justify-center p-2">
      <Radar :data="chartData" :options="chartOptions" />
    </div>

  </div>
</template>
