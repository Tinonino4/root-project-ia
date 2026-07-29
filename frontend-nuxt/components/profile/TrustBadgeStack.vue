<script setup lang="ts">
import { computed } from 'vue'
import { ShieldCheck, Building2, Users, Star } from 'lucide-vue-next'

interface Props {
  totalReferences?: number
  trustScore?: number
  verifiedCorporateCount?: number
  relationshipTypes?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  totalReferences: 0,
  trustScore: 0,
  verifiedCorporateCount: 0,
  relationshipTypes: () => []
})

const formattedScore = computed(() => {
  if (!props.trustScore) return '0.0'
  const scoreOnFive = props.trustScore > 5 ? props.trustScore / 20 : props.trustScore
  return scoreOnFive.toFixed(1)
})

const isMultiSource = computed(() => {
  return props.relationshipTypes && props.relationshipTypes.length >= 2
})
</script>

<template>
  <div class="flex flex-wrap items-center gap-2 select-none">
    <!-- Badge: Trust Rating Score -->
    <div
      v-if="totalReferences > 0"
      class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-500/10 text-amber-500 border border-amber-500/20 shadow-sm"
    >
      <Star class="w-3.5 h-3.5 fill-amber-500 text-amber-500" />
      <span>{{ formattedScore }} / 5.0</span>
      <span class="text-amber-500/60 font-normal">({{ totalReferences }})</span>
    </div>

    <!-- Badge: Verified Corporate Email References -->
    <div
      v-if="verifiedCorporateCount > 0"
      class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 shadow-sm"
    >
      <Building2 class="w-3.5 h-3.5 text-emerald-400" />
      <span>Email Corporativo Verificado</span>
    </div>

    <!-- Badge: Multi-Source 360 Evaluation -->
    <div
      v-if="isMultiSource"
      class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 shadow-sm"
    >
      <Users class="w-3.5 h-3.5 text-indigo-400" />
      <span>Evaluación 360° Multi-Fuente</span>
    </div>

    <!-- Badge: General Certified References Badge -->
    <div
      v-if="totalReferences > 0 && verifiedCorporateCount === 0 && !isMultiSource"
      class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 shadow-sm"
    >
      <ShieldCheck class="w-3.5 h-3.5 text-emerald-400" />
      <span>{{ totalReferences }} {{ totalReferences === 1 ? 'Referencia Certificada' : 'Referencias Certificadas' }}</span>
    </div>
  </div>
</template>
