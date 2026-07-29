<script setup lang="ts">
import type { BehavioralQuestionDTO } from '~/types'

const props = withDefaults(
  defineProps<{
    question: BehavioralQuestionDTO
    modelValue?: string | null
  }>(),
  {
    modelValue: null
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

function selectOption(optionId: string) {
  emit('update:modelValue', optionId)
}
</script>

<template>
  <div class="space-y-4">
    <div class="space-y-1">
      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary/10 text-primary">
        Estilo Conductual
      </span>
      <h3 class="text-lg font-bold text-zinc-900 dark:text-white leading-snug">
        {{ question.text }}
      </h3>
    </div>

    <div class="grid grid-cols-1 gap-3.5 pt-2">
      <button
        v-for="option in question.options"
        :key="option.id"
        type="button"
        @click="selectOption(option.id)"
        class="text-left p-4 rounded-2xl border transition-all duration-200 flex items-start space-x-3.5 group cursor-pointer"
        :class="[
          modelValue === option.id
            ? 'bg-primary/5 dark:bg-primary/10 border-primary ring-2 ring-primary/20 shadow-md'
            : 'bg-white dark:bg-zinc-800/80 border-zinc-200/80 dark:border-zinc-700/60 hover:bg-zinc-50 dark:hover:bg-zinc-800 hover:border-zinc-300 dark:hover:border-zinc-600'
        ]"
      >
        <div
          class="w-5 h-5 rounded-full border-2 flex items-center justify-center mt-0.5 flex-shrink-0 transition-colors"
          :class="[
            modelValue === option.id
              ? 'border-primary bg-primary text-white'
              : 'border-zinc-300 dark:border-zinc-600 group-hover:border-primary/50'
          ]"
        >
          <div v-if="modelValue === option.id" class="w-2 h-2 rounded-full bg-white"></div>
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
