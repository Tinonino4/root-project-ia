<script setup>
import { ref } from 'vue';
import { Camera, Loader2, User } from 'lucide-vue-next';
import { toast } from 'vue-sonner';
import client from '@/api/client';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue']);

const fileInput = ref(null);
const isUploading = ref(false);

const getFullUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  
  // If relative path, prefix with backend host
  const base = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  // Remove "/api" suffix from base if it's there, to serve static files from "/uploads"
  const host = base.replace(/\/api$/, '');
  return `${host}${url}`;
};

const triggerFileInput = () => {
  if (isUploading.value) return;
  fileInput.value.click();
};

const onFileChange = async (event) => {
  const files = event.target.files;
  if (!files || files.length === 0) return;
  
  const file = files[0];
  
  // Client-side validations
  if (file.size > 2 * 1024 * 1024) {
    toast.error('Archivo demasiado grande', {
      description: 'La foto de perfil no debe superar los 2 MB.'
    });
    return;
  }
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
  if (!allowedTypes.includes(file.type)) {
    toast.error('Formato no compatible', {
      description: 'Por favor, sube una imagen en formato JPG, PNG o WEBP.'
    });
    return;
  }
  
  // Prepare FormData for Multipart submittal
  const formData = new FormData();
  formData.append('file', file);
  
  isUploading.value = true;
  
  try {
    const response = await client.post('/professional/profile/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    const newPhotoUrl = response.data.photoUrl;
    emit('update:modelValue', newPhotoUrl);
    
    toast.success('Foto de perfil actualizada', {
      description: 'Tu avatar ha sido guardado exitosamente.'
    });
  } catch (error) {
    console.error('Error uploading avatar:', error);
    const serverMessage = error.response?.data?.message || 'No se pudo subir el archivo.';
    toast.error('Error al subir la imagen', {
      description: serverMessage
    });
  } finally {
    isUploading.value = false;
    // Clear input so same file can be uploaded again if needed
    if (fileInput.value) {
      fileInput.value.value = '';
    }
  }
};
</script>

<template>
  <div class="flex flex-col items-center space-y-3">
    <!-- Avatar circle -->
    <div 
      @click="triggerFileInput"
      class="relative w-32 h-32 rounded-full overflow-hidden border-4 border-white dark:border-zinc-900 shadow-lg cursor-pointer group bg-zinc-100 dark:bg-zinc-800 transition-all duration-300 hover:border-primary/50 dark:hover:border-primary/50"
    >
      <!-- Image or fallback -->
      <img 
        v-if="modelValue" 
        :src="getFullUrl(modelValue)" 
        alt="Foto de Perfil" 
        class="w-full h-full object-cover" 
      />
      <div v-else class="w-full h-full flex items-center justify-center text-zinc-400 dark:text-zinc-600">
        <User class="w-16 h-16" />
      </div>

      <!-- Hover camera overlay -->
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

    <!-- Hidden Input File -->
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
