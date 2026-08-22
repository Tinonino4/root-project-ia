<script setup lang="ts">
import type { MultiLayerSkillsMetrics, ArchetypeData } from '~/types/profile'
import BehavioralDnaCard from './BehavioralDnaCard.vue'

defineProps<{
  metrics?: MultiLayerSkillsMetrics | any
  archetype?: ArchetypeData
}>()
</script>

<template>
  <div class="w-full">
    <BehavioralDnaCard 
      :metrics="metrics" 
      :archetype="archetype" 
    />
  </div>
</template>
