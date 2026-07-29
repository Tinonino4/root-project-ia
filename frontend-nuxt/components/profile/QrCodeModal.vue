<script setup lang="ts">
import { ref, computed } from 'vue'
import { QrCode, X, Copy, Check, ExternalLink } from 'lucide-vue-next'

interface Props {
  isOpen: boolean
  url: string
  profileName?: string
}

const props = defineProps<Props>()
const emit = defineEmits(['close'])

const copied = ref(false)

const qrCodeApiUrl = computed(() => {
  if (!props.url) return ''
  const encodedUrl = encodeURIComponent(props.url)
  return `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodedUrl}&color=18181b&bgcolor=ffffff&margin=1`
})

const copyLink = async () => {
  if (!props.url || !import.meta.client) return
  try {
    await navigator.clipboard.writeText(props.url)
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2500)
  } catch (err) {
    console.error('Error al copiar enlace:', err)
  }
}
</script>

<template>
  <Teleport to="body">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-md animate-in fade-in duration-200"
    >
      <div
        class="bg-zinc-900 border border-zinc-800 rounded-2xl p-6 max-w-sm w-full shadow-2xl space-y-5 relative text-center"
        @click.stop
      >
        <!-- Close Button -->
        <button
          @click="emit('close')"
          class="absolute top-4 right-4 p-1.5 text-zinc-400 hover:text-white rounded-lg hover:bg-zinc-800 transition-colors"
          aria-label="Cerrar modal"
        >
          <X class="w-5 h-5" />
        </button>

        <!-- Header -->
        <div class="space-y-1">
          <div class="inline-flex p-3 rounded-full bg-primary/10 text-primary mb-1">
            <QrCode class="w-6 h-6" />
          </div>
          <h3 class="text-lg font-bold text-white">Código QR de Perfil</h3>
          <p class="text-xs text-zinc-400">
            Escanea para ver las referencias verificadas de <span class="text-white font-semibold">{{ profileName || 'este profesional' }}</span>.
          </p>
        </div>

        <!-- QR Image Display -->
        <div class="bg-white p-4 rounded-xl inline-block shadow-inner border border-zinc-200">
          <img
            v-if="qrCodeApiUrl"
            :src="qrCodeApiUrl"
            alt="Código QR de Perfil Público"
            class="w-48 h-48 mx-auto object-contain select-none"
            loading="eager"
          />
          <div v-else class="w-48 h-48 flex items-center justify-center text-zinc-400 text-xs">
            Generando QR...
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="space-y-2 pt-2">
          <button
            @click="copyLink"
            class="w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl bg-primary text-white font-semibold text-sm hover:bg-primary/90 btn-glow transition-all"
          >
            <Check v-if="copied" class="w-4 h-4 text-emerald-300" />
            <Copy v-else class="w-4 h-4" />
            <span>{{ copied ? '¡Enlace Copiado!' : 'Copiar Enlace Directo' }}</span>
          </button>

          <a
            :href="url"
            target="_blank"
            rel="noopener noreferrer"
            class="w-full flex items-center justify-center gap-1.5 py-2 px-4 rounded-xl bg-zinc-800/80 text-zinc-300 hover:text-white hover:bg-zinc-800 text-xs font-medium transition-colors"
          >
            <span>Abrir en nueva pestaña</span>
            <ExternalLink class="w-3.5 h-3.5" />
          </a>
        </div>
      </div>
    </div>
  </Teleport>
</template>
