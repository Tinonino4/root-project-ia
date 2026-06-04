<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile.store';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ArrowLeft, Save } from 'lucide-vue-next';
import AvatarUploader from '@/components/profile/AvatarUploader.vue';

const router = useRouter();
const profileStore = useProfileStore();

const formData = ref({
  name: '',
  surname: '',
  contactEmail: '',
  aboutMe: '',
  city: '',
  birthday: '',
  zipcode: '',
  phoneNumber: '',
  photoUrl: '',
  jobTitle: '',
  education: ''
});

const successMessage = ref('');

onMounted(async () => {
  if (!profileStore.profile) {
    await profileStore.fetchProfile();
  }
  if (profileStore.profile) {
    // Populate form data
    Object.keys(formData.value).forEach(key => {
      if (profileStore.profile[key] !== undefined) {
        formData.value[key] = profileStore.profile[key];
      }
    });
  }
});

const handleSave = async () => {
  successMessage.value = '';
  try {
    // Clean up empty fields if necessary, or just send all
    await profileStore.updateProfile(formData.value);
    successMessage.value = 'Perfil actualizado exitosamente.';
    setTimeout(() => {
      router.push({ name: 'Profile' });
    }, 1500);
  } catch (error) {
    console.error('Error updating profile:', error);
  }
};
</script>

<template>
  <div class="space-y-6 max-w-4xl mx-auto pb-12">
    
    <!-- Header Actions -->
    <div class="flex items-center gap-4">
      <Button variant="outline" size="icon" @click="router.push('/profile')" class="rounded-full">
        <ArrowLeft class="w-5 h-5" />
      </Button>
      <div>
        <h2 class="text-3xl font-bold font-heading text-zinc-900 dark:text-white">Editar Perfil</h2>
        <p class="text-zinc-500 dark:text-zinc-400 mt-1">Actualiza tu información personal y profesional.</p>
      </div>
    </div>

    <!-- Loading State with Shimmer Skeletons -->
    <div v-if="profileStore.loading && !profileStore.profile" class="bg-white dark:bg-zinc-900 rounded-2xl p-6 md:p-8 border border-zinc-200 dark:border-zinc-800 shadow-sm space-y-8 animate-pulse">
      <!-- Personal Info Section Shimmer -->
      <div class="space-y-4">
        <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/4 mb-4"></div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="i in 6" :key="i" class="space-y-2">
            <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-1/5"></div>
            <div class="h-10 bg-zinc-200 dark:bg-zinc-800 rounded w-full"></div>
          </div>
        </div>
      </div>
      
      <!-- Location Section Shimmer -->
      <div class="space-y-4">
        <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/4 mb-4"></div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="i in 2" :key="i" class="space-y-2">
            <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-1/5"></div>
            <div class="h-10 bg-zinc-200 dark:bg-zinc-800 rounded w-full"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Form -->
    <div v-else class="bg-white dark:bg-zinc-900 rounded-2xl p-6 md:p-8 border border-zinc-200 dark:border-zinc-800 shadow-sm">
      <form @submit.prevent="handleSave" class="space-y-8">
        
        <!-- Avatar Uploader Section -->
        <div class="flex justify-center pb-6 border-b border-zinc-100 dark:border-zinc-800">
          <AvatarUploader v-model="formData.photoUrl" />
        </div>
        
        <!-- Personal Information Section -->
        <div class="space-y-4">
          <h3 class="text-lg font-bold font-heading text-zinc-900 dark:text-white border-b border-zinc-100 dark:border-zinc-800 pb-2">
            Información Personal
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-2">
              <Label for="name">Nombre</Label>
              <Input id="name" v-model="formData.name" required class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="surname">Apellidos</Label>
              <Input id="surname" v-model="formData.surname" class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="contactEmail">Email de Contacto</Label>
              <Input id="contactEmail" type="email" v-model="formData.contactEmail" required class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="phoneNumber">Teléfono</Label>
              <Input id="phoneNumber" type="tel" v-model="formData.phoneNumber" class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="birthday">Fecha de Nacimiento</Label>
              <Input id="birthday" type="date" v-model="formData.birthday" class="focus:ring-primary" />
            </div>
          </div>
        </div>

        <!-- Location Section -->
        <div class="space-y-4">
          <h3 class="text-lg font-bold font-heading text-zinc-900 dark:text-white border-b border-zinc-100 dark:border-zinc-800 pb-2">
            Ubicación
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-2">
              <Label for="city">Ciudad</Label>
              <Input id="city" v-model="formData.city" class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="zipcode">Código Postal</Label>
              <Input id="zipcode" v-model="formData.zipcode" class="focus:ring-primary" />
            </div>
          </div>
        </div>

        <!-- Professional Information Section -->
        <div class="space-y-4">
          <h3 class="text-lg font-bold font-heading text-zinc-900 dark:text-white border-b border-zinc-100 dark:border-zinc-800 pb-2">
            Información Profesional
          </h3>
          <div class="grid grid-cols-1 gap-6">
            <div class="space-y-2">
              <Label for="jobTitle">Puesto Actual</Label>
              <Input id="jobTitle" v-model="formData.jobTitle" placeholder="Ej: Senior Developer" class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="education">Formación / Educación</Label>
              <Input id="education" v-model="formData.education" placeholder="Ej: Ingeniería Informática" class="focus:ring-primary" />
            </div>
            <div class="space-y-2">
              <Label for="aboutMe">Sobre Mí (Biografía)</Label>
              <textarea 
                id="aboutMe" 
                v-model="formData.aboutMe" 
                rows="4" 
                class="flex w-full rounded-md border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-800/60 px-3 py-2.5 text-sm text-foreground shadow-sm transition-all duration-200 placeholder:text-zinc-400 dark:placeholder:text-zinc-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/30 focus-visible:border-primary disabled:cursor-not-allowed disabled:opacity-50 resize-none"
                placeholder="Escribe algo sobre tu experiencia y habilidades..."
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Feedback Messages -->
        <div v-if="profileStore.error" class="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-3 rounded-lg text-sm">
          {{ profileStore.error }}
        </div>
        <div v-if="successMessage" class="bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400 p-3 rounded-lg text-sm font-medium">
          {{ successMessage }}
        </div>

        <!-- Submit Actions -->
        <div class="flex justify-end gap-4 pt-4 border-t border-zinc-100 dark:border-zinc-800">
          <Button type="button" variant="outline" @click="router.push('/profile')" :disabled="profileStore.loading">
            Cancelar
          </Button>
          <Button type="submit" class="bg-primary hover:bg-primary/90 text-primary-foreground gap-2" :disabled="profileStore.loading">
            <svg v-if="profileStore.loading" class="animate-spin -ml-1 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            <Save v-else class="w-4 h-4" />
            Guardar Cambios
          </Button>
        </div>
      </form>
    </div>
  </div>
</template>
