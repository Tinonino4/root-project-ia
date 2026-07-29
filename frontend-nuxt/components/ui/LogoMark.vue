<script setup lang="ts">
import { useId } from 'vue'

interface Props {
  class?: string
}

const props = defineProps<Props>()

const primaryGradId = useId()
const ringGradId = useId()
</script>

<template>
  <svg
    viewBox="0 0 36 36"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="['select-none transition-transform duration-300 flex-shrink-0 shrink-0 block', props.class]"
  >
    <defs>
      <!-- Gradient para el Isotipo de Marca (Amber / Gold) -->
      <linearGradient :id="primaryGradId" x1="2" y1="2" x2="34" y2="34" gradientUnits="userSpaceOnUse">
        <stop offset="0%" stop-color="#F59E0B" />
        <stop offset="50%" stop-color="#F29727" />
        <stop offset="100%" stop-color="#EA580C" />
      </linearGradient>

      <!-- Gradient sutil de respaldo -->
      <linearGradient :id="ringGradId" x1="0" y1="0" x2="36" y2="36" gradientUnits="userSpaceOnUse">
        <stop offset="0%" stop-color="#F29727" stop-opacity="0.95" />
        <stop offset="100%" stop-color="#EA580C" stop-opacity="0.75" />
      </linearGradient>
    </defs>

    <!-- Órbita Exterior 360° (Shield Loop) -->
    <path
      d="M18 4C10.268 4 4 10.268 4 18C4 23.52 7.19 28.29 11.83 30.5"
      :stroke="`url(#${ringGradId})`"
      stroke-width="3"
      stroke-linecap="round"
    />
    <path
      d="M18 32C25.732 32 32 25.732 32 18C32 12.48 28.81 7.71 24.17 5.5"
      :stroke="`url(#${ringGradId})`"
      stroke-width="3"
      stroke-linecap="round"
      stroke-dasharray="2 4"
    />

    <!-- Monograma Central Estilizado C + Nodo 360° -->
    <path
      d="M23 12.5C21.6 11 19.8 10 17.5 10C13.08 10 9.5 13.58 9.5 18C9.5 22.42 13.08 26 17.5 26C19.8 26 21.6 25 23 23.5"
      :stroke="`url(#${primaryGradId})`"
      stroke-width="3.5"
      stroke-linecap="round"
    />

    <!-- Checkmark de Verificación Integrado -->
    <path
      d="M21 17.5L23.5 20L28.5 14.5"
      :stroke="`url(#${primaryGradId})`"
      stroke-width="3"
      stroke-linecap="round"
      stroke-linejoin="round"
    />

    <!-- Nodo Central de Confianza -->
    <circle cx="17.5" cy="18" r="2" :fill="`url(#${primaryGradId})`" />
  </svg>
</template>
