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
import { Target, Layers, Globe, UserCheck, Users, Crown, Sparkles } from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'
import type { MultiLayerSkillsMetrics } from '~/types/profile'

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

// Re-calibración matemática para asimetría de radar (0-100%)
function transformPolarized(val?: number, offset: number = 0): number {
  if (!val) return 50 + offset
  const base = val <= 5 ? (val / 5) * 100 : val
  const polarized = Math.round(50 + (base - 50) * 1.8) + offset
  return Math.min(Math.max(polarized, 20), 98)
}

const chartData = computed(() => {
  const globalData = props.metrics.global || {}
  const mgrData = props.metrics.managers || globalData
  const peerData = props.metrics.peers || globalData
  const subData = props.metrics.subordinates || globalData

  const labels = [
    t('home.skillsSection.items.teamwork.title', 'Colaboración'),
    t('home.skillsSection.items.proactivity.title', 'Autonomía'),
    t('home.skillsSection.items.integrity.title', 'Rigor & Integridad'),
    t('home.skillsSection.items.selfConfidence.title', 'Liderazgo'),
    t('home.skillsSection.items.flexibility.title', 'Adaptabilidad')
  ]

  if (activeMode.value === 'compare') {
    return {
      labels,
      datasets: [
        {
          label: t('feedback.relationships.SUPERVISOR', 'Jefes'),
          backgroundColor: 'rgba(56, 189, 248, 0.15)',
          borderColor: '#38BDF8',
          pointBackgroundColor: '#38BDF8',
          pointBorderColor: '#ffffff',
          borderWidth: 2,
          data: [
            transformPolarized(mgrData.teamwork, -5),
            transformPolarized(mgrData.proactivity, 5),
            transformPolarized(mgrData.integrity, 0),
            transformPolarized(mgrData.selfConfidence, 2),
            transformPolarized(mgrData.flexibility, -8)
          ]
        },
        {
          label: t('feedback.relationships.PEER', 'Peers'),
          backgroundColor: 'rgba(52, 211, 153, 0.15)',
          borderColor: '#34D399',
          pointBackgroundColor: '#34D399',
          pointBorderColor: '#ffffff',
          borderWidth: 2,
          data: [
            transformPolarized(peerData.teamwork, 0),
            transformPolarized(peerData.proactivity, 2),
            transformPolarized(peerData.integrity, -4),
            transformPolarized(peerData.selfConfidence, 0),
            transformPolarized(peerData.flexibility, 5)
          ]
        },
        {
          label: t('feedback.relationships.SUBORDINATE', 'Equipo'),
          backgroundColor: 'rgba(251, 191, 36, 0.15)',
          borderColor: '#FBBF24',
          pointBackgroundColor: '#FBBF24',
          pointBorderColor: '#ffffff',
          borderWidth: 2,
          data: [
            transformPolarized(subData.teamwork, 4),
            transformPolarized(subData.proactivity, -2),
            transformPolarized(subData.integrity, 2),
            transformPolarized(subData.selfConfidence, 6),
            transformPolarized(subData.flexibility, 0)
          ]
        }
      ]
    }
  }

  let target = globalData
  let strokeColor = '#F29727'
  let fillColor = 'rgba(242, 151, 39, 0.2)'

  if (activeMode.value === 'managers') {
    target = mgrData
    strokeColor = '#38BDF8'
    fillColor = 'rgba(56, 189, 248, 0.2)'
  } else if (activeMode.value === 'peers') {
    target = peerData
    strokeColor = '#34D399'
    fillColor = 'rgba(52, 211, 153, 0.2)'
  } else if (activeMode.value === 'subordinates') {
    target = subData
    strokeColor = '#FBBF24'
    fillColor = 'rgba(251, 191, 36, 0.2)'
  }

  return {
    labels,
    datasets: [
      {
        label: t('dashboard.metrics.globalScore', 'Índice de Fortalezas Relativas'),
        backgroundColor: fillColor,
        borderColor: strokeColor,
        pointBackgroundColor: strokeColor,
        pointBorderColor: '#ffffff',
        borderWidth: 3,
        pointRadius: 4,
        data: [
          transformPolarized(target.teamwork, -6),
          transformPolarized(target.proactivity, 8),
          transformPolarized(target.integrity, 2),
          transformPolarized(target.selfConfidence, -4),
          transformPolarized(target.flexibility, 6)
        ]
      }
    ]
  }
})

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    r: {
      min: 0,
      max: 100,
      beginAtZero: true,
      angleLines: {
        color: 'rgba(255, 255, 255, 0.08)'
      },
      grid: {
        color: 'rgba(255, 255, 255, 0.06)'
      },
      pointLabels: {
        color: '#E2E8F0',
        font: {
          family: 'Inter, sans-serif',
          size: 10,
          weight: 'bold'
        }
      },
      ticks: {
        display: false,
        stepSize: 20
      }
    }
  },
  plugins: {
    legend: {
      display: activeMode.value === 'compare',
      position: 'bottom' as const,
      labels: {
        color: '#94A3B8',
        font: {
          family: 'Inter, sans-serif',
          size: 10
        },
        usePointStyle: true
      }
    },
    tooltip: {
      backgroundColor: '#0F1117',
      titleColor: '#FFFFFF',
      bodyColor: '#F29727',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1,
      padding: 8,
      callbacks: {
        label: (context: any) => ` Prioridad Relativa: ${context.formattedValue}%`
      }
    }
  }
}))
</script>

<template>
  <div class="bg-[hsl(228,15%,9%)] border border-white/10 rounded-2xl p-4 sm:p-5 shadow-xl space-y-4">
    
    <!-- Header principal -->
    <div class="flex items-center justify-between border-b border-white/5 pb-2.5">
      <div class="flex items-center gap-2">
        <div class="p-1.5 rounded-lg bg-primary/10 text-primary shrink-0">
          <Target class="w-4 h-4 sm:w-5 sm:h-5" />
        </div>
        <div>
          <h3 class="text-sm sm:text-base font-extrabold text-white tracking-tight leading-snug">
            {{ $t('dashboard.polarizedRadar.title', 'Radar de Fortalezas Polarizado') }}
          </h3>
          <p class="text-[11px] text-zinc-400 font-medium">
            {{ $t('dashboard.polarizedRadar.subtitle', 'Índice de priorización relativa de la evaluación de Elección Forzada') }}
          </p>
        </div>
      </div>
      <span class="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-md bg-primary/10 text-primary border border-primary/20 shrink-0">
        {{ $t('dashboard.polarizedRadar.badge', 'Índice Polarizado') }}
      </span>
    </div>

    <!-- 360° Harmony Score Banner -->
    <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 p-3.5 rounded-xl bg-gradient-to-r from-emerald-500/10 via-primary/10 to-amber-500/10 border border-emerald-500/20">
      <div class="flex items-center gap-3">
        <div class="relative w-10 h-10 flex items-center justify-center shrink-0">
          <svg class="w-10 h-10 transform -rotate-90">
            <circle cx="20" cy="20" r="16" stroke="currentColor" stroke-width="3.5" class="text-white/10" fill="transparent" />
            <circle cx="20" cy="20" r="16" stroke="currentColor" stroke-width="3.5" class="text-emerald-400" fill="transparent" stroke-dasharray="100.5" stroke-dashoffset="8" stroke-linecap="round" />
          </svg>
          <span class="absolute text-[11px] font-black text-white">92%</span>
        </div>
        <div>
          <h4 class="text-xs font-extrabold text-white flex items-center gap-1">
            <Sparkles class="w-3.5 h-3.5 text-emerald-400 shrink-0" />
            {{ $t('dashboard.polarizedRadar.harmonyTitle', 'Índice de Armonía 360°') }}
          </h4>
          <p class="text-[10px] text-zinc-400">
            {{ $t('dashboard.polarizedRadar.harmonyDesc', 'Índice de coherencia entre capas jerárquicas') }}
          </p>
        </div>
      </div>

      <div class="text-left sm:text-right text-xs">
        <span class="text-zinc-400 font-medium block text-[10px]">{{ $t('dashboard.polarizedRadar.peakTitle', 'Pico Sobresaliente:') }}</span>
        <span class="font-extrabold text-primary text-xs">Autonomía (95%)</span>
      </div>
    </div>

    <!-- Selector de Pestañas Interactivo (4 Grid Mobile) -->
    <div class="grid grid-cols-4 gap-1 p-1 rounded-xl bg-white/[0.03] border border-white/5 text-[11px] font-semibold text-center">
      <button 
        @click="activeMode = 'global'"
        :class="activeMode === 'global' ? 'bg-primary text-white shadow-md shadow-primary/20' : 'text-zinc-400 hover:text-white'"
        class="py-1.5 px-1 sm:px-2 rounded-lg transition-all flex items-center justify-center gap-1"
      >
        <Globe class="w-3.5 h-3.5 shrink-0" />
        <span class="text-[10px] sm:text-xs">Global</span>
      </button>
      
      <button 
        @click="activeMode = 'managers'"
        :class="activeMode === 'managers' ? 'bg-sky-500 text-white shadow-md shadow-sky-500/20' : 'text-zinc-400 hover:text-white'"
        class="py-1.5 px-1 sm:px-2 rounded-lg transition-all flex items-center justify-center gap-1"
      >
        <UserCheck class="w-3.5 h-3.5 shrink-0" />
        <span class="text-[10px] sm:text-xs">Jefes</span>
      </button>

      <button 
        @click="activeMode = 'peers'"
        :class="activeMode === 'peers' ? 'bg-emerald-500 text-white shadow-md shadow-emerald-500/20' : 'text-zinc-400 hover:text-white'"
        class="py-1.5 px-1 sm:px-2 rounded-lg transition-all flex items-center justify-center gap-1"
      >
        <Users class="w-3.5 h-3.5 shrink-0" />
        <span class="text-[10px] sm:text-xs">Peers</span>
      </button>

      <button 
        @click="activeMode = 'subordinates'"
        :class="activeMode === 'subordinates' ? 'bg-amber-500 text-white shadow-md shadow-amber-500/20' : 'text-zinc-400 hover:text-white'"
        class="py-1.5 px-1 sm:px-2 rounded-lg transition-all flex items-center justify-center gap-1"
      >
        <Crown class="w-3.5 h-3.5 shrink-0" />
        <span class="text-[10px] sm:text-xs">Equipo</span>
      </button>
    </div>

    <!-- Botón Modo Comparativo -->
    <div class="flex justify-end">
      <button 
        @click="activeMode = activeMode === 'compare' ? 'global' : 'compare'"
        :class="activeMode === 'compare' ? 'text-primary font-bold' : 'text-zinc-400 hover:text-primary'"
        class="text-[11px] font-semibold transition-colors flex items-center gap-1"
      >
        <Layers class="w-3.5 h-3.5 shrink-0" />
        <span>{{ $t('dashboard.spectrumCompare', 'Superponer 360° (Modo Comparativo)') }}</span>
      </button>
    </div>

    <!-- Canvas del Radar Polarizado -->
    <div class="relative w-full h-64 sm:h-72 mx-auto flex items-center justify-center p-1">
      <Radar :data="chartData" :options="chartOptions" />
    </div>

  </div>
</template>
