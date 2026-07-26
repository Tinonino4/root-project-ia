<script setup lang="ts">
import { ref } from 'vue'
import { Camera, Loader2, User } from 'lucide-vue-next'
import { toast } from 'vue-sonner'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const fileInput = ref<HTMLInputElement | null>(null)
const isUploading = ref(false)

const getFullUrl = (url?: string) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  const config = useRuntimeConfig()
  const base = config.public.apiBaseUrl || '/api'
  const host = base.replace(/\/api$/, '')
  return `${host}${url}`
}

const triggerFileInput = () => {
  if (isUploading.value) return
  if (fileInput.value) fileInput.value.click()
}

const onFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) return
  
  const file = files[0]
  
  if (file.size > 2 * 1024 * 1024) {
    toast.error('Archivo demasiado grande', {
      description: 'La foto de perfil no debe superar los 2 MB.'
    })
    return
  }
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    toast.error('Formato no compatible', {
      description: 'Por favor, sube una imagen en formato JPG, PNG o WEBP.'
    })
    return
  }
  
  const formData = new FormData()
  formData.append('file', file)
  
  isUploading.value = true
  
  try {
    const response: any = await $api('/professional/profile/avatar', {
      method: 'POST',
      body: formData
    })
    
    const newPhotoUrl = response.photoUrl
    emit('update:modelValue', newPhotoUrl)
    
    toast.success('Foto de perfil actualizada', {
      description: 'Tu avatar ha sido guardado exitosamente.'
    })
  } catch (error: any) {
    console.error('Error uploading avatar:', error)
    const serverMessage = error.response?._data?.message || 'No se pudo subir el archivo.'
    toast.error('Error al subir la imagen', {
      description: serverMessage
    })
  } finally {
    isUploading.value = false
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}
</script>

<template>
  <div class="flex flex-col items-center space-y-3">
    <div 
      @click="triggerFileInput"
      class="relative w-32 h-32 rounded-full overflow-hidden border-4 border-white dark:border-zinc-900 shadow-lg cursor-pointer group bg-zinc-100 dark:bg-zinc-800 transition-all duration-300 hover:border-primary/50 dark:hover:border-primary/50"
    >
      <img 
        v-if="modelValue" 
        :src="getFullUrl(modelValue)" 
        alt="Foto de Perfil" 
        class="w-full h-full object-cover" 
      />
      <div v-else class="w-full h-full flex items-center justify-center text-zinc-400 dark:text-zinc-600">
        <User class="w-16 h-16" />
      </div>

      <div 
        class="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col items-center justify-center text-white"
        :class="{ 'opacity-100 bg-black/50 pointer-events-none': isUploading }"
      >
        <template v-if="isUploading">
          <Loader2 class="w-7 h-7 animate-spin text-primary" />
          <span class="text-[10px] font-bold tracking-wider uppercase mt-1.5 text-primary">Subiendo...</span>
        </template>
        <template v-else>
          <Camera class="w-7 h-7 text-white/95 transition-transform duration-300 group-hover:scale-110" />
          <span class="text-[10px] font-bold tracking-wider uppercase mt-1 text-white/90">Cambiar Foto</span>
        </template>
      </div>
    </div>

    <input 
      ref="fileInput"
      type="file"
      class="hidden"
      accept="image/png, image/jpeg, image/webp"
      @change="onFileChange"
    />
    
    <p class="text-[10px] text-zinc-400 dark:text-zinc-500 uppercase tracking-widest font-bold">
      Formatos: JPG, PNG, WEBP (Max. 2MB)
    </p>
  </div>
</template>
