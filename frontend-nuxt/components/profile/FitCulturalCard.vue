<script setup lang="ts">
import { Sparkles, Target, Zap, ShieldCheck } from 'lucide-vue-next'
import type { ArchetypeData } from '~/types/profile'

defineProps<{
  data: ArchetypeData
}>()
</script>

<template>
  <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-5 backdrop-blur-xl shadow-xl space-y-5">
    
    <!-- Header -->
    <div class="flex items-center justify-between border-b border-white/5 pb-3">
      <h3 class="text-base font-bold text-white flex items-center gap-2">
        <Sparkles class="w-4 h-4 text-primary" />
        {{ $t('profile.archetypeTitle', 'Arquetipo & Fit Cultural') }}
      </h3>
      <span class="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-primary/10 text-primary border border-primary/20">
        {{ $t('profile.archetypeBadge', 'Conductual') }}
      </span>
    </div>

    <!-- Estilos Dominantes (Tags) -->
    <div class="space-y-2" v-if="data.tags?.length">
      <span class="text-xs font-semibold text-zinc-400">
        {{ $t('profile.dominantStyle', 'Estilo de Trabajo Dominante:') }}
      </span>
      <div class="flex flex-wrap gap-2">
        <span 
          v-for="tag in data.tags" 
          :key="tag"
          class="px-3 py-1 rounded-xl bg-white/[0.04] border border-white/10 text-xs font-semibold text-zinc-200 hover:bg-white/[0.08] transition-colors"
        >
          🏷️ {{ $t('profile.archetypeTags.' + tag, tag) }}
        </span>
      </div>
    </div>

    <!-- Top Fortalezas (Elección Forzada) -->
    <div class="space-y-2.5" v-if="data.topStrengths?.length">
      <span class="text-xs font-semibold text-zinc-400 flex items-center gap-1.5">
        <Zap class="w-3.5 h-3.5 text-amber-400" />
        {{ $t('profile.topStrengthsTitle', 'Fortalezas Principales (Elección Forzada):') }}
      </span>
      <ul class="space-y-1.5">
        <li 
          v-for="strength in data.topStrengths" 
          :key="strength"
          class="flex items-center gap-2 text-xs font-medium text-zinc-300 bg-emerald-500/5 border border-emerald-500/10 px-3 py-2 rounded-xl"
        >
          <ShieldCheck class="w-3.5 h-3.5 text-emerald-400 flex-shrink-0" />
          <span>{{ $t('profile.strengths.' + strength, strength) }}</span>
        </li>
      </ul>
    </div>

    <!-- Entorno de Máximo Rendimiento -->
    <div class="space-y-2 pt-2 border-t border-white/5" v-if="data.idealEnvironment">
      <div class="flex items-center justify-between text-xs">
        <span class="font-semibold text-zinc-400 flex items-center gap-1.5">
          <Target class="w-3.5 h-3.5 text-sky-400" />
          {{ $t('profile.idealEnvironmentTitle', 'Entorno Ideal de Trabajo:') }}
        </span>
        <span class="font-bold text-primary">
          {{ $t('profile.matchPercentage', { percent: data.idealEnvironment.fitPercentage }, `${data.idealEnvironment.fitPercentage}% Match`) }}
        </span>
      </div>
      <p class="text-xs font-bold text-white">
        {{ $t('profile.environments.' + data.idealEnvironment.name, data.idealEnvironment.name) }}
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
