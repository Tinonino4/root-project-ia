<script setup lang="ts">
import { computed } from 'vue'
import { Sparkles, Dna, ShieldCheck, Zap, Crosshair, Award } from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'
import type { MultiLayerSkillsMetrics, ArchetypeData } from '~/types/profile'

const { t, locale } = useI18n()

const props = defineProps<{
  metrics?: MultiLayerSkillsMetrics
  archetype?: ArchetypeData
}>()

// Coordenadas calculadas para la matriz 2D (con margen seguro 22%-78%)
const coordinateX = computed(() => {
  const flex = props.metrics?.global?.flexibility ?? 81
  return Math.min(Math.max(Math.round(flex), 22), 78)
})

const coordinateY = computed(() => {
  const proact = props.metrics?.global?.proactivity ?? 84
  // Invertir para que 100% quede arriba
  return Math.min(Math.max(100 - Math.round(proact), 22), 78)
})

const activeQuadrant = computed<'leader' | 'builder' | 'collaborator' | 'specialist'>(() => {
  const isRight = coordinateX.value >= 50
  const isTop = coordinateY.value <= 50
  if (isTop && isRight) return 'builder'
  if (isTop && !isRight) return 'leader'
  if (!isTop && isRight) return 'specialist'
  return 'collaborator'
})

const strengthTranslations: Record<string, { en: string; es: string }> = {
  "Genera un clima de confianza y buen humor en el equipo incluso bajo presión.": {
    en: "Generates a climate of trust and good humor in the team even under pressure.",
    es: "Genera un clima de confianza y buen humor en el equipo incluso bajo presión."
  },
  "Tiene una capacidad de trabajo y volumen de ejecución muy alto.": {
    en: "Has a very high work capacity and execution volume.",
    es: "Tiene una capacidad de trabajo y volumen de ejecución muy alto."
  },
  "Calma y resolución bajo picos de alta presión": {
    en: "Calmness and resolution under high-pressure peaks",
    es: "Calma y resolución bajo picos de alta presión"
  },
  "Alta autonomía en proyectos desde cero (0 a 1)": {
    en: "High autonomy in greenfield projects (0 to 1)",
    es: "Alta autonomía en proyectos desde cero (0 a 1)"
  },
  "Es una referencia técnica/profesional a la que acudir cuando algo se complica.": {
    en: "Is a technical/professional benchmark to turn to when things get complicated.",
    es: "Es una referencia técnica/profesional a la que acudir cuando algo se complica."
  },
  "Es extremadamente riguroso/a con la calidad del producto final.": {
    en: "Is extremely rigorous regarding final product quality.",
    es: "Es extremadamente riguroso/a con la calidad del producto final."
  },
  "Se adapta sin quejarse cuando cambian las reglas del juego a mitad de camino.": {
    en: "Adapts smoothly when project goals change mid-way.",
    es: "Se adapta sin quejarse cuando cambian las reglas del juego a mitad de camino."
  }
}

function translateStrength(str: string): string {
  const isEn = locale.value?.startsWith('en')
  if (strengthTranslations[str]) {
    return isEn ? strengthTranslations[str].en : strengthTranslations[str].es
  }
  return str
}

const topStrengths = computed(() => {
  if (props.archetype?.topStrengths?.length) {
    return props.archetype.topStrengths
  }
  return [
    "Genera un clima de confianza y buen humor en el equipo incluso bajo presión.",
    "Tiene una capacidad de trabajo y volumen de ejecución muy alto.",
    "Calma y resolución bajo picos de alta presión"
  ]
})
</script>

<template>
  <div class="space-y-4 w-full">
    
    <!-- Hero Banner Card (Identidad de Talent ADN) -->
    <div class="relative overflow-hidden rounded-2xl bg-gradient-to-br from-amber-500/20 via-violet-900/20 to-indigo-950/40 border border-amber-500/30 p-4 sm:p-5 shadow-2xl">
      <div class="absolute -top-10 -right-10 w-36 h-36 bg-amber-500/10 rounded-full blur-2xl pointer-events-none"></div>
      
      <div class="relative z-10 space-y-3">
        <!-- Badges Bar -->
        <div class="flex flex-wrap items-center gap-2">
          <span class="inline-flex items-center gap-1 text-[10px] font-extrabold uppercase px-2.5 py-1 rounded-full bg-amber-500/20 text-amber-300 border border-amber-500/30">
            <Dna class="w-3 h-3 shrink-0" />
            {{ $t('dashboard.behavioralDna.badge') }}
          </span>
          <span class="inline-flex items-center gap-1 text-[10px] font-extrabold uppercase px-2.5 py-1 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
            <Award class="w-3 h-3 shrink-0" />
            94% {{ $t('dashboard.behavioralDna.alignmentTitle') }}
          </span>
        </div>

        <!-- Title & Subtitle -->
        <div class="space-y-1">
          <h3 class="text-base sm:text-xl font-black text-white tracking-tight leading-snug flex items-center gap-2">
            <Sparkles class="w-5 h-5 text-amber-400 shrink-0" />
            <span>{{ $t('dashboard.behavioralDna.heroTag') }}</span>
          </h3>
          <p class="text-xs sm:text-sm text-zinc-300 leading-relaxed">
            {{ $t('dashboard.behavioralDna.heroDesc') }}
          </p>
        </div>
      </div>
    </div>

    <!-- Matriz 2D de Posicionamiento Conductual -->
    <div class="bg-[hsl(228,15%,9%)] border border-white/10 rounded-2xl p-4 sm:p-5 shadow-xl space-y-3">
      <!-- Section Title Bar -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-white/5 pb-2.5">
        <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 flex items-center gap-1.5">
          <Crosshair class="w-4 h-4 text-primary shrink-0" />
          <span>{{ $t('dashboard.behavioralDna.matrixTitle') }}</span>
        </h4>
        <span class="inline-flex items-center gap-1.5 text-[10px] font-mono font-bold text-amber-300 bg-amber-500/10 border border-amber-500/20 px-2 py-0.5 rounded-lg self-start sm:self-auto shadow-sm">
          <span class="w-1.5 h-1.5 rounded-full bg-amber-400"></span>
          <span>X: {{ coordinateX }}%</span>
          <span class="text-zinc-600">•</span>
          <span>Y: {{ 100 - coordinateY }}%</span>
        </span>
      </div>

      <!-- Plano Cartesiano 2D -->
      <div class="relative w-full min-h-[240px] sm:min-h-[260px] rounded-xl bg-black/70 border border-white/15 p-2.5 sm:p-3 flex flex-col justify-between overflow-hidden shadow-inner select-none">
        <!-- Rejilla de Fondo -->
        <div class="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:1.25rem_1.25rem] pointer-events-none"></div>

        <!-- Ejes Divisorios Centrales (Cruz en 50%) -->
        <div class="absolute left-1/2 top-0 bottom-0 w-[1px] bg-white/10 pointer-events-none -translate-x-1/2"></div>
        <div class="absolute top-1/2 left-0 right-0 h-[1px] bg-white/10 pointer-events-none -translate-y-1/2"></div>

        <!-- 4 Cuadrantes de Talento con Iluminación Reactiva -->
        <div class="absolute inset-2 sm:inset-3 grid grid-cols-2 grid-rows-2 gap-1.5 pointer-events-none z-0">
          <!-- Cuadrante 2 (Top-Left): Líder & Facilitador -->
          <div 
            :class="activeQuadrant === 'leader' ? 'bg-sky-500/15 border-sky-500/40 shadow-[inset_0_0_20px_rgba(56,189,248,0.15)] ring-1 ring-sky-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
            class="rounded-xl border p-2 flex flex-col justify-between transition-all duration-500"
          >
            <div class="flex items-center gap-1">
              <span class="text-xs">👑</span>
              <span 
                :class="activeQuadrant === 'leader' ? 'text-sky-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                class="text-[9px] sm:text-[10px] tracking-tight uppercase"
              >
                {{ $t('home.hero.quadrants.leader', 'Líder Facilitador') }}
              </span>
            </div>
            <span class="text-[8px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.leaderSub', 'Autonomía + Equipo') }}</span>
          </div>

          <!-- Cuadrante 1 (Top-Right): Builder / Innovador -->
          <div 
            :class="activeQuadrant === 'builder' ? 'bg-amber-500/15 border-amber-500/40 shadow-[inset_0_0_20px_rgba(245,158,11,0.15)] ring-1 ring-amber-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
            class="rounded-xl border p-2 flex flex-col justify-between items-end text-right transition-all duration-500"
          >
            <div class="flex items-center gap-1">
              <span 
                :class="activeQuadrant === 'builder' ? 'text-amber-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                class="text-[9px] sm:text-[10px] tracking-tight uppercase"
              >
                {{ $t('home.hero.quadrants.builder', 'Builder (0 a 1)') }}
              </span>
              <span class="text-xs">🚀</span>
            </div>
            <span class="text-[8px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.builderSub', 'Autonomía + Rigor') }}</span>
          </div>

          <!-- Cuadrante 3 (Bottom-Left): Colaborador Ágil -->
          <div 
            :class="activeQuadrant === 'collaborator' ? 'bg-indigo-500/15 border-indigo-500/40 shadow-[inset_0_0_20px_rgba(99,102,241,0.15)] ring-1 ring-indigo-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
            class="rounded-xl border p-2 flex flex-col justify-between transition-all duration-500"
          >
            <span class="text-[8px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.collaboratorSub', 'Consenso + Ejecución') }}</span>
            <div class="flex items-center gap-1">
              <span class="text-xs">🤝</span>
              <span 
                :class="activeQuadrant === 'collaborator' ? 'text-indigo-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                class="text-[9px] sm:text-[10px] tracking-tight uppercase"
              >
                {{ $t('home.hero.quadrants.collaborator', 'Colaborador Ágil') }}
              </span>
            </div>
          </div>

          <!-- Cuadrante 4 (Bottom-Right): Especialista Técnico -->
          <div 
            :class="activeQuadrant === 'specialist' ? 'bg-emerald-500/15 border-emerald-500/40 shadow-[inset_0_0_20px_rgba(16,185,129,0.15)] ring-1 ring-emerald-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
            class="rounded-xl border p-2 flex flex-col justify-between items-end text-right transition-all duration-500"
          >
            <span class="text-[8px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.specialistSub', 'Rigor + Foco Técnico') }}</span>
            <div class="flex items-center gap-1">
              <span 
                :class="activeQuadrant === 'specialist' ? 'text-emerald-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                class="text-[9px] sm:text-[10px] tracking-tight uppercase"
              >
                {{ $t('home.hero.quadrants.specialist', 'Especialista Técnico') }}
              </span>
              <span class="text-xs">🎯</span>
            </div>
          </div>
        </div>

        <!-- Eje Y Superior -->
        <div class="text-center text-[9px] sm:text-[10px] font-black text-amber-400 uppercase tracking-widest relative z-10 pt-0.5 pointer-events-none">
          ▲ {{ $t('dashboard.behavioralDna.matrixYTop') }}
        </div>

        <!-- Eje X Central Minimalista -->
        <div class="flex items-center justify-between text-[8px] sm:text-[10px] font-bold text-zinc-300 relative z-10 px-1 gap-1 pointer-events-none">
          <span class="bg-black/85 backdrop-blur-md px-1.5 sm:px-2 py-0.5 rounded-lg border border-white/10 shadow-md">◄ {{ $t('dashboard.behavioralDna.matrixXLeft') }}</span>
          <span class="bg-black/85 backdrop-blur-md px-1.5 sm:px-2 py-0.5 rounded-lg border border-white/10 text-zinc-200 font-extrabold shadow-md">{{ $t('dashboard.behavioralDna.matrixXRight') }} ►</span>
        </div>

        <!-- Eje Y Inferior -->
        <div class="text-center text-[9px] sm:text-[10px] font-black text-sky-400 uppercase tracking-widest relative z-10 pb-0.5 pointer-events-none">
          ▼ {{ $t('dashboard.behavioralDna.matrixYBottom') }}
        </div>

        <!-- Líneas Láser Proyectadas (Crosshair) -->
        <div 
          class="absolute top-0 bottom-0 w-[1px] border-l border-dashed border-amber-400/40 transition-all duration-700 ease-out z-10 pointer-events-none"
          :style="{ left: `${coordinateX}%` }"
        ></div>
        <div 
          class="absolute left-0 right-0 h-[1px] border-t border-dashed border-amber-400/40 transition-all duration-700 ease-out z-10 pointer-events-none"
          :style="{ top: `${coordinateY}%` }"
        ></div>

        <!-- Punto Posicionado con Efecto Neón Pulsante y Tooltip -->
        <div 
          class="absolute z-30 -translate-x-1/2 -translate-y-1/2 transition-all duration-700 ease-out flex flex-col items-center pointer-events-none"
          :style="{ left: `${coordinateX}%`, top: `${coordinateY}%` }"
        >
          <!-- Tooltip flotante -->
          <div class="mb-1 px-1.5 py-0.5 rounded-md bg-zinc-950/95 border border-amber-500/40 text-[8px] font-mono font-bold text-amber-300 shadow-xl whitespace-nowrap backdrop-blur-md flex items-center gap-1">
            <span class="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span>
            <span>X: {{ coordinateX }}%</span>
            <span class="text-zinc-600">•</span>
            <span>Y: {{ 100 - coordinateY }}%</span>
          </div>

          <div class="relative w-4 h-4 sm:w-5 sm:h-5 rounded-full bg-primary border-2 border-white shadow-[0_0_20px_rgba(242,151,39,1)]">
            <span class="absolute inset-0 rounded-full bg-primary animate-ping opacity-75"></span>
          </div>
        </div>
      </div>

      <!-- Leyenda de Ejes -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-2 text-[10px] font-medium text-zinc-400 pt-1">
        <div class="p-2.5 rounded-xl bg-white/[0.02] border border-white/5 space-y-0.5">
          <strong class="text-amber-400 block text-[11px] font-bold">{{ $t('dashboard.behavioralDna.legendYTitle') }}</strong>
          <span class="leading-normal block">{{ $t('dashboard.behavioralDna.legendYDesc') }}</span>
        </div>
        <div class="p-2.5 rounded-xl bg-white/[0.02] border border-white/5 space-y-0.5">
          <strong class="text-primary block text-[11px] font-bold">{{ $t('dashboard.behavioralDna.legendXTitle') }}</strong>
          <span class="leading-normal block">{{ $t('dashboard.behavioralDna.legendXDesc') }}</span>
        </div>
      </div>
    </div>

    <!-- Top 3 Superpotencias Verificadas -->
    <div class="bg-[hsl(228,15%,9%)] border border-white/10 rounded-2xl p-4 sm:p-5 shadow-xl space-y-3">
      <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 flex items-center gap-1.5 border-b border-white/5 pb-2.5">
        <Zap class="w-4 h-4 text-amber-400 shrink-0" />
        <span>{{ $t('dashboard.behavioralDna.superpowersTitle') }}</span>
      </h4>

      <div class="space-y-2.5">
        <div 
          v-for="(strength, idx) in topStrengths.slice(0, 3)" 
          :key="idx"
          class="p-3 rounded-xl bg-white/[0.03] border border-white/10 hover:border-amber-500/30 transition-all space-y-2 group"
        >
          <div class="flex items-start gap-2.5">
            <span class="w-5 h-5 rounded-md bg-amber-500/10 text-amber-400 border border-amber-500/20 font-black text-[11px] flex items-center justify-center shrink-0 mt-0.5">
              0{{ idx + 1 }}
            </span>
            <span class="text-xs font-semibold text-zinc-200 group-hover:text-white transition-colors leading-relaxed">
              {{ translateStrength(strength) }}
            </span>
          </div>

          <div class="flex justify-end pt-0.5">
            <span class="text-[10px] font-bold text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-2.5 py-0.5 rounded-md flex items-center gap-1">
              <ShieldCheck class="w-3 h-3 shrink-0 text-emerald-400" />
              {{ $t('dashboard.behavioralDna.validatedBy', { count: 5 - idx }) }}
            </span>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
