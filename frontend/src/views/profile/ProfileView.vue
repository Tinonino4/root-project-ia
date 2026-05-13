<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile.store';
import { Button } from '@/components/ui/button';
import { Mail, MapPin, Phone, Briefcase, GraduationCap, Edit, User as UserIcon } from 'lucide-vue-next';

const router = useRouter();
const profileStore = useProfileStore();

onMounted(async () => {
  await profileStore.fetchProfile();
});
</script>

<template>
  <div class="space-y-6 max-w-4xl mx-auto">
    
    <!-- Header Actions -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-3xl font-bold font-heading text-zinc-900 dark:text-white">Mi Perfil</h2>
        <p class="text-zinc-500 dark:text-zinc-400 mt-1">Gestiona tu información personal y profesional.</p>
      </div>
      <Button @click="router.push('/profile/edit')" class="bg-primary hover:bg-primary/90 text-white gap-2">
        <Edit class="w-4 h-4" />
        Editar Perfil
      </Button>
    </div>

    <!-- Loading State -->
    <div v-if="profileStore.loading" class="flex flex-col items-center justify-center py-20 space-y-4">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      <p class="text-zinc-500">Cargando perfil...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="profileStore.error" class="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-4 rounded-lg flex items-center gap-3">
      <p>{{ profileStore.error }}</p>
      <Button variant="outline" @click="profileStore.fetchProfile()" class="ml-auto">Reintentar</Button>
    </div>

    <!-- Profile Content -->
    <div v-else-if="profileStore.profile" class="grid grid-cols-1 md:grid-cols-3 gap-6">
      
      <!-- Left Column: Avatar & Basic Info -->
      <div class="md:col-span-1 space-y-6">
        <div class="bg-white dark:bg-zinc-900 rounded-2xl p-6 border border-zinc-200 dark:border-zinc-800 shadow-sm flex flex-col items-center text-center">
          <div class="w-32 h-32 rounded-full overflow-hidden mb-4 bg-zinc-100 dark:bg-zinc-800 flex items-center justify-center border-4 border-white dark:border-zinc-900 shadow-md">
            <img v-if="profileStore.profile.photoUrl" :src="profileStore.profile.photoUrl" alt="Foto de Perfil" class="w-full h-full object-cover" />
            <UserIcon v-else class="w-16 h-16 text-zinc-400" />
          </div>
          <h3 class="text-xl font-bold text-zinc-900 dark:text-white">
            {{ profileStore.profile.name }} {{ profileStore.profile.surname || '' }}
          </h3>
          <p class="text-primary font-medium mt-1">{{ profileStore.profile.jobTitle || 'Puesto no especificado' }}</p>
          <p class="text-zinc-500 text-sm mt-2 flex items-center gap-1 justify-center">
            <MapPin class="w-4 h-4" />
            {{ profileStore.profile.city || 'Ciudad no especificada' }}
            <span v-if="profileStore.profile.zipcode">({{ profileStore.profile.zipcode }})</span>
          </p>
        </div>

        <!-- Contact Info -->
        <div class="bg-white dark:bg-zinc-900 rounded-2xl p-6 border border-zinc-200 dark:border-zinc-800 shadow-sm space-y-4">
          <h4 class="font-bold text-zinc-900 dark:text-white border-b border-zinc-100 dark:border-zinc-800 pb-2">Contacto</h4>
          
          <div class="flex items-center gap-3 text-zinc-600 dark:text-zinc-400">
            <Mail class="w-5 h-5 text-zinc-400" />
            <span class="text-sm break-all">{{ profileStore.profile.contactEmail || 'No especificado' }}</span>
          </div>
          
          <div class="flex items-center gap-3 text-zinc-600 dark:text-zinc-400">
            <Phone class="w-5 h-5 text-zinc-400" />
            <span class="text-sm">{{ profileStore.profile.phoneNumber || 'No especificado' }}</span>
          </div>
        </div>
      </div>

      <!-- Right Column: Details -->
      <div class="md:col-span-2 space-y-6">
        
        <!-- About Me -->
        <div class="bg-white dark:bg-zinc-900 rounded-2xl p-6 border border-zinc-200 dark:border-zinc-800 shadow-sm">
          <h4 class="text-lg font-bold text-zinc-900 dark:text-white mb-4">Sobre Mí</h4>
          <p v-if="profileStore.profile.aboutMe" class="text-zinc-600 dark:text-zinc-400 leading-relaxed whitespace-pre-line">
            {{ profileStore.profile.aboutMe }}
          </p>
          <p v-else class="text-zinc-500 italic text-sm">Aún no has añadido una descripción personal. Edita tu perfil para completarla.</p>
        </div>

        <!-- Education & Job Title Summary -->
        <div class="bg-white dark:bg-zinc-900 rounded-2xl p-6 border border-zinc-200 dark:border-zinc-800 shadow-sm space-y-6">
          <h4 class="text-lg font-bold text-zinc-900 dark:text-white mb-2">Formación e Información Profesional</h4>
          
          <div class="flex gap-4 items-start">
            <div class="mt-1 w-10 h-10 rounded-full bg-secondary/20 flex items-center justify-center text-secondary flex-shrink-0">
              <Briefcase class="w-5 h-5" />
            </div>
            <div>
              <p class="font-semibold text-zinc-900 dark:text-white">Puesto Actual</p>
              <p class="text-zinc-600 dark:text-zinc-400">{{ profileStore.profile.jobTitle || 'No especificado' }}</p>
            </div>
          </div>

          <div class="flex gap-4 items-start">
            <div class="mt-1 w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center text-primary flex-shrink-0">
              <GraduationCap class="w-5 h-5" />
            </div>
            <div>
              <p class="font-semibold text-zinc-900 dark:text-white">Educación</p>
              <p class="text-zinc-600 dark:text-zinc-400">{{ profileStore.profile.education || 'No especificada' }}</p>
            </div>
          </div>

          <div class="flex gap-4 items-start" v-if="profileStore.profile.birthday">
            <div class="mt-1 w-10 h-10 rounded-full bg-zinc-100 dark:bg-zinc-800 flex items-center justify-center text-zinc-500 flex-shrink-0">
              <UserIcon class="w-5 h-5" />
            </div>
            <div>
              <p class="font-semibold text-zinc-900 dark:text-white">Fecha de Nacimiento</p>
              <p class="text-zinc-600 dark:text-zinc-400">{{ profileStore.profile.birthday }}</p>
            </div>
          </div>

        </div>

      </div>

    </div>
  </div>
</template>
