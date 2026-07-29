<script setup lang="ts">
import { computed } from 'vue'
import { Check, Sparkles } from 'lucide-vue-next'
import type { BehavioralQuestionDTO } from '~/types'

const props = withDefaults(
  defineProps<{
    question: BehavioralQuestionDTO
    modelValue?: string[]
  }>(),
  {
    modelValue: () => []
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
}>()

const safeValue = computed(() => Array.isArray(props.modelValue) ? props.modelValue : [])
const selectedCount = computed(() => safeValue.value.length)

function toggleOption(optionId: string) {
  const current = [...safeValue.value]
  const index = current.indexOf(optionId)

  if (index > -1) {
    current.splice(index, 1)
  } else {
    if (current.length < 2) {
      current.push(optionId)
    }
  }

  emit('update:modelValue', current)
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <div class="space-y-1">
        <span class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-600 dark:text-amber-400">
          <Sparkles class="w-3 h-3" /> Elección Forzada
        </span>
        <h3 class="text-lg font-bold text-zinc-900 dark:text-white leading-snug">
          {{ question.text }}
        </h3>
      </div>
      
      <div 
        class="px-3 py-1 rounded-full text-xs font-bold transition-colors"
        :class="[
          selectedCount === 2 
            ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/30' 
            : 'bg-zinc-100 dark:bg-zinc-800 text-zinc-500 dark:text-zinc-400'
        ]"
      >
        {{ selectedCount }} / 2 seleccionadas
      </div>
    </div>

    <p class="text-xs text-zinc-500 dark:text-zinc-400">
      Selecciona exactamente las 2 cualidades más representativas del candidato.
    </p>

    <div class="grid grid-cols-1 gap-3 pt-2">
      <button
        v-for="option in question.options"
        :key="option.id"
        type="button"
        @click="toggleOption(option.id)"
        :disabled="selectedCount >= 2 && !safeValue.includes(option.id)"
        class="text-left p-4 rounded-2xl border transition-all duration-200 flex items-start space-x-3.5 group cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        :class="[
          safeValue.includes(option.id)
            ? 'bg-amber-500/5 dark:bg-amber-500/10 border-amber-500 ring-2 ring-amber-500/20 shadow-md'
            : 'bg-white dark:bg-zinc-800/80 border-zinc-200/80 dark:border-zinc-700/60 hover:bg-zinc-50 dark:hover:bg-zinc-800'
        ]"
      >
        <div
          class="w-5 h-5 rounded-lg border-2 flex items-center justify-center mt-0.5 flex-shrink-0 transition-colors"
          :class="[
            safeValue.includes(option.id)
              ? 'border-amber-500 bg-amber-500 text-white'
              : 'border-zinc-300 dark:border-zinc-600'
          ]"
        >
          <Check v-if="safeValue.includes(option.id)" class="w-3.5 h-3.5 text-white" />
        </div>

        <div class="flex-1">
          <p class="text-sm font-medium text-zinc-800 dark:text-zinc-200 leading-relaxed">
            {{ option.text }}
          </p>
        </div>
      </button>
    </div>
  </div>
</template>
