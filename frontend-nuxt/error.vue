<script setup lang="ts">
import { computed } from 'vue'
import { AlertTriangle, FileQuestion, ServerCrash, Home, RefreshCw, ArrowLeft } from 'lucide-vue-next'

const props = defineProps({
  error: {
    type: Object,
    default: null
  }
})

const statusCode = computed(() => Number(props.error?.statusCode || props.error?.status || 500))
const is404 = computed(() => statusCode.value === 404)

const errorTitle = computed(() => {
  if (is404.value) return 'Página no encontrada'
  if (statusCode.value >= 500) return 'Error interno del servidor'
  return 'Ocurrió un error inesperado'
})

const errorDescription = computed(() => {
  if (is404.value) {
    return 'La página o recurso que buscas no existe, ha sido movida o no está disponible públicamente.'
  }
  return props.error?.message || props.error?.statusMessage || 'Estamos experimentando problemas técnicos. Por favor, inténtalo de nuevo más tarde.'
})

const handleClearError = () => {
  clearError({ redirect: '/' })
}

const handleGoBack = () => {
  if (import.meta.client && window.history.length > 1) {
    window.history.back()
  } else {
    clearError({ redirect: '/' })
  }
}
</script>

<template>
  <div class="min-h-screen bg-[hsl(228,16%,7%)] text-white flex items-center justify-center p-4 sm:p-6 lg:p-8 font-sans relative overflow-hidden">
    <!-- Ambient Background Glows -->
    <div class="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-primary/10 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute bottom-10 right-10 w-72 h-72 bg-orange-500/5 rounded-full blur-2xl pointer-events-none"></div>

    <div class="max-w-md w-full text-center space-y-8 relative z-10">
      
      <!-- Brand Logo Header -->
      <div class="flex justify-center items-center gap-2">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-primary to-orange-500 flex items-center justify-center font-bold text-white shadow-lg shadow-primary/20">
          C
        </div>
        <span class="text-2xl font-black tracking-tight text-white">Caché</span>
      </div>

      <!-- Main Error Card -->
      <div class="bg-[hsl(228,15%,9%)] border border-white/10 rounded-3xl p-8 sm:p-10 shadow-2xl backdrop-blur-xl space-y-6">
        
        <!-- Icon Badge -->
        <div class="w-20 h-20 rounded-2xl mx-auto flex items-center justify-center shadow-inner"
          :class="is404 ? 'bg-amber-500/10 border border-amber-500/20 text-amber-400' : 'bg-rose-500/10 border border-rose-500/20 text-rose-400'"
        >
          <FileQuestion v-if="is404" class="w-10 h-10" />
          <ServerCrash v-else-if="statusCode >= 500" class="w-10 h-10" />
          <AlertTriangle v-else class="w-10 h-10" />
        </div>

        <!-- Status Code Badge -->
        <div>
          <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold uppercase tracking-widest bg-white/5 border border-white/10 text-zinc-400">
            Error {{ statusCode }}
          </span>
        </div>

        <!-- Text Info -->
        <div class="space-y-2">
          <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight text-white">
            {{ errorTitle }}
          </h1>
          <p class="text-sm text-zinc-400 leading-relaxed text-balance">
            {{ errorDescription }}
          </p>
        </div>

        <!-- Actions -->
        <div class="pt-4 flex flex-col sm:flex-row gap-3 justify-center">
          <button
            @click="handleClearError"
            class="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3 rounded-xl bg-primary hover:bg-primary/90 text-white font-bold text-sm transition-all duration-200 shadow-lg shadow-primary/20"
          >
            <Home class="w-4 h-4" />
            <span>Ir al inicio</span>
          </button>
          
          <button
            @click="handleGoBack"
            class="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-5 py-3 rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 text-zinc-300 font-semibold text-sm transition-all duration-200"
          >
            <ArrowLeft class="w-4 h-4" />
            <span>Volver atrás</span>
          </button>
        </div>

      </div>

      <!-- Footer Info -->
      <p class="text-xs text-zinc-500">
        Si crees que esto es un error, por favor ponte en contacto con nuestro equipo de soporte.
      </p>

    </div>
  </div>
</template>
