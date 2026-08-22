<script setup lang="ts">
import { Sparkles, Target, Zap, ShieldCheck } from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'
import type { ArchetypeData } from '~/types/profile'

defineProps<{
  data: ArchetypeData
}>()

const { locale } = useI18n()

const tagMap: Record<string, { en: string; es: string }> = {
  "Respondedor Pragmático": { en: "Pragmatic Responder", es: "Respondedor Pragmático" },
  "Comunicador Sintético": { en: "Concise Communicator", es: "Comunicador Sintético" },
  "Liderazgo Coach": { en: "Coach Leadership", es: "Liderazgo Coach" }
}

const strengthMap: Record<string, { en: string; es: string }> = {
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

const environmentMap: Record<string, { en: string; es: string }> = {
  "Equipos multidisciplinares muy unidos que trabajan en constante lluvia de ideas.": {
    en: "Tight-knit multidisciplinary teams working in continuous brainstorming.",
    es: "Equipos multidisciplinares muy unidos que trabajan en constante lluvia de ideas."
  },
  "Startup / Scaleup (Ritmo ágil y alta autonomía)": {
    en: "Startup / Scaleup (Agile pace & high autonomy)",
    es: "Startup / Scaleup (Ritmo ágil y alta autonomía)"
  },
  "Equipos autónomos donde cada especialista tiene su área de responsabilidad clara.": {
    en: "Autonomous teams where each specialist has a clear area of ownership.",
    es: "Equipos autónomos donde cada especialista tiene su área de responsabilidad clara."
  },
  "Equipos altamente técnicos donde se premia la excelencia y el rigor por encima de la velocidad.": {
    en: "Highly technical teams prioritizing excellence and rigor over speed.",
    es: "Equipos altamente técnicos donde se premia la excelencia y el rigor por encima de la velocidad."
  }
}

function translateTag(tag: string): string {
  const isEn = locale.value?.startsWith('en')
  if (tagMap[tag]) return isEn ? tagMap[tag].en : tagMap[tag].es
  return tag
}

function translateStrength(str: string): string {
  const isEn = locale.value?.startsWith('en')
  if (strengthMap[str]) return isEn ? strengthMap[str].en : strengthMap[str].es
  return str
}

function translateEnv(env: string): string {
  const isEn = locale.value?.startsWith('en')
  if (environmentMap[env]) return isEn ? environmentMap[env].en : environmentMap[env].es
  return env
}
</script>

<template>
  <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-5 backdrop-blur-xl shadow-xl space-y-5">
    
    <!-- Header -->
    <div class="flex items-center justify-between border-b border-white/5 pb-3">
      <h3 class="text-base font-bold text-white flex items-center gap-2">
        <Sparkles class="w-4 h-4 text-primary" />
        {{ $t('profile.archetypeTitle') }}
      </h3>
      <span class="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-primary/10 text-primary border border-primary/20">
        {{ $t('profile.archetypeBadge') }}
      </span>
    </div>

    <!-- Estilos Dominantes (Tags) -->
    <div class="space-y-2" v-if="data.tags?.length">
      <span class="text-xs font-semibold text-zinc-400">
        {{ $t('profile.dominantStyle') }}
      </span>
      <div class="flex flex-wrap gap-2">
        <span 
          v-for="tag in data.tags" 
          :key="tag"
          class="px-3 py-1 rounded-xl bg-white/[0.04] border border-white/10 text-xs font-semibold text-zinc-200 hover:bg-white/[0.08] transition-colors"
        >
          🏷️ {{ translateTag(tag) }}
        </span>
      </div>
    </div>

    <!-- Top Fortalezas (Elección Forzada) -->
    <div class="space-y-2.5" v-if="data.topStrengths?.length">
      <span class="text-xs font-semibold text-zinc-400 flex items-center gap-1.5">
        <Zap class="w-3.5 h-3.5 text-amber-400" />
        {{ $t('profile.topStrengthsTitle') }}
      </span>
      <ul class="space-y-1.5">
        <li 
          v-for="strength in data.topStrengths" 
          :key="strength"
          class="flex items-center gap-2 text-xs font-medium text-zinc-300 bg-emerald-500/5 border border-emerald-500/10 px-3 py-2 rounded-xl"
        >
          <ShieldCheck class="w-3.5 h-3.5 text-emerald-400 flex-shrink-0" />
          <span>{{ translateStrength(strength) }}</span>
        </li>
      </ul>
    </div>

    <!-- Entorno de Máximo Rendimiento -->
    <div class="space-y-2 pt-2 border-t border-white/5" v-if="data.idealEnvironment">
      <div class="flex items-center justify-between text-xs">
        <span class="font-semibold text-zinc-400 flex items-center gap-1.5">
          <Target class="w-3.5 h-3.5 text-sky-400" />
          {{ $t('profile.idealEnvironmentTitle') }}
        </span>
        <span class="font-bold text-primary">
          {{ $t('profile.matchPercentage', { percent: data.idealEnvironment.fitPercentage }) }}
        </span>
      </div>
      <p class="text-xs font-bold text-white">
        {{ translateEnv(data.idealEnvironment.name) }}
      </p>
      
      <!-- Progress bar -->
      <div class="w-full h-2 rounded-full bg-white/5 overflow-hidden">
        <div 
          class="h-full bg-gradient-to-r from-primary via-amber-400 to-emerald-400 rounded-full transition-all duration-500"
          :style="{ width: `${data.idealEnvironment.fitPercentage}%` }"
        ></div>
      </div>
    </div>

  </div>
</template>
