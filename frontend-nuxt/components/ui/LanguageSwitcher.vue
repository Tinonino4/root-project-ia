<script setup lang="ts">
import { computed } from 'vue'
import { Globe } from 'lucide-vue-next'

interface Props {
  variant?: 'pill' | 'compact'
  theme?: 'dark' | 'light' | 'auto'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'pill',
  theme: 'auto'
})

const { locale, setLocale } = useI18n()
const currentLocale = computed(() => locale.value)

const changeLang = (lang: 'es' | 'en') => {
  setLocale(lang)
}

const themeClasses = computed(() => {
  if (props.theme === 'dark') {
    return 'bg-white/5 border-white/10 text-zinc-300'
  }
  if (props.theme === 'light') {
    return 'bg-zinc-100 dark:bg-zinc-800 border-zinc-200 dark:border-zinc-700 text-zinc-600 dark:text-zinc-300'
  }
  return 'bg-white/5 dark:bg-zinc-800/80 border-white/10 dark:border-zinc-700/60 text-zinc-300 dark:text-zinc-200'
})
</script>

<template>
  <div
    :class="[
      'inline-flex items-center gap-1.5 border rounded-lg p-0.5 backdrop-blur-md transition-all duration-200 select-none',
      themeClasses
    ]"
  >
    <Globe class="w-3.5 h-3.5 ml-1 opacity-70 flex-shrink-0" />

    <div class="flex items-center gap-0.5">
      <button
        @click="changeLang('es')"
        type="button"
        :class="[
          'font-bold rounded transition-all duration-200',
          variant === 'compact' ? 'px-1.5 py-0.5 text-[10px]' : 'px-2 py-0.5 text-xs',
          currentLocale === 'es'
            ? 'bg-primary text-white shadow-sm shadow-primary/20 scale-[1.02]'
            : 'text-zinc-400 hover:text-white dark:hover:text-zinc-100'
        ]"
        aria-label="Cambiar idioma a Español"
      >
        ES
      </button>

      <button
        @click="changeLang('en')"
        type="button"
        :class="[
          'font-bold rounded transition-all duration-200',
          variant === 'compact' ? 'px-1.5 py-0.5 text-[10px]' : 'px-2 py-0.5 text-xs',
          currentLocale === 'en'
            ? 'bg-primary text-white shadow-sm shadow-primary/20 scale-[1.02]'
            : 'text-zinc-400 hover:text-white dark:hover:text-zinc-100'
        ]"
        aria-label="Switch language to English"
      >
        EN
      </button>
    </div>
  </div>
</template>
