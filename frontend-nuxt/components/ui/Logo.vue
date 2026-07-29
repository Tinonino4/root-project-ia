<script setup lang="ts">
import { computed } from 'vue'
import LogoMark from './LogoMark.vue'

interface Props {
  variant?: 'full' | 'mark'
  size?: 'sm' | 'md' | 'lg'
  interactive?: boolean
  class?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'full',
  size: 'md',
  interactive: false,
})

const markSizeClasses = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'h-5.5 w-5.5 min-w-[22px]'
    case 'lg':
      return 'h-9 w-9 min-w-[36px]'
    case 'md':
    default:
      return 'h-7 w-7 min-w-[28px]'
  }
})

const textSizeClasses = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'text-base'
    case 'lg':
      return 'text-2xl'
    case 'md':
    default:
      return 'text-lg'
  }
})

const gapClasses = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'gap-1.5'
    case 'lg':
      return 'gap-2.5'
    case 'md':
    default:
      return 'gap-2'
  }
})
</script>

<template>
  <div
    :class="[
      'inline-flex items-center group select-none shrink-0',
      gapClasses,
      interactive ? 'cursor-pointer' : '',
      props.class
    ]"
  >
    <LogoMark
      :class="[
        markSizeClasses,
        interactive ? 'group-hover:scale-105 group-hover:drop-shadow-[0_0_10px_rgba(242,151,39,0.45)] transition-all duration-300' : ''
      ]"
    />
    <div v-if="variant === 'full'" class="flex items-center gap-0.5">
      <span
        :class="[
          'font-extrabold tracking-tight text-zinc-900 dark:text-white transition-colors duration-200',
          textSizeClasses
        ]"
      >
        Caché
      </span>
      <span class="w-1.5 h-1.5 rounded-full bg-primary animate-pulse"></span>
    </div>
  </div>
</template>
