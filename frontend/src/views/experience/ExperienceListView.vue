<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useExperienceStore } from '@/stores/experience.store';
import { Button } from '@/components/ui/button';
import { Plus, Building2, Calendar, Briefcase, Trash2, Edit2 } from 'lucide-vue-next';

const router = useRouter();
const experienceStore = useExperienceStore();

onMounted(async () => {
  await experienceStore.fetchExperiences();
});

const handleDelete = async (id) => {
  if (confirm('¿Estás seguro de que quieres eliminar esta experiencia?')) {
    await experienceStore.deleteExperience(id);
  }
};

const formatDate = (dateString) => {
  if (!dateString) return 'Presente';
  const options = { year: 'numeric', month: 'short' };
  return new Date(dateString).toLocaleDateString('es-ES', options);
};
</script>

<template>
  <div class="space-y-6 max-w-5xl mx-auto pb-12">
    
    <!-- Header Actions -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-3xl font-bold font-heading text-zinc-900 dark:text-white">Mis Experiencias</h2>
        <p class="text-zinc-500 dark:text-zinc-400 mt-1">Añade y gestiona tu trayectoria profesional.</p>
      </div>
      <Button @click="router.push('/experiences/new')" class="bg-primary hover:bg-primary/90 text-white gap-2">
        <Plus class="w-5 h-5" />
        <span class="hidden sm:inline">Añadir Experiencia</span>
      </Button>
    </div>

    <!-- Loading State -->
    <div v-if="experienceStore.loading && experienceStore.experiences.length === 0" class="flex flex-col items-center justify-center py-20 space-y-4">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      <p class="text-zinc-500">Cargando experiencias...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="experienceStore.error" class="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-4 rounded-lg flex items-center gap-3">
      <p>{{ experienceStore.error }}</p>
      <Button variant="outline" @click="experienceStore.fetchExperiences()" class="ml-auto">Reintentar</Button>
    </div>

    <!-- Empty State -->
    <div v-else-if="experienceStore.experiences.length === 0" class="bg-white dark:bg-zinc-900 rounded-2xl p-12 border border-dashed border-zinc-300 dark:border-zinc-800 flex flex-col items-center justify-center text-center">
      <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center text-primary mb-4">
        <Briefcase class="w-8 h-8" />
      </div>
      <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">Sin experiencias</h3>
      <p class="text-zinc-500 max-w-md mx-auto mb-6">Aún no has añadido ninguna experiencia laboral. Empieza a construir tu perfil para que otros puedan valorarte.</p>
      <Button @click="router.push('/experiences/new')" class="bg-primary hover:bg-primary/90 text-white gap-2">
        <Plus class="w-5 h-5" />
        Añadir mi primera experiencia
      </Button>
    </div>

    <!-- Experience List -->
    <div v-else class="space-y-4">
      <div 
        v-for="exp in experienceStore.sortedByDate" 
        :key="exp.id"
        class="bg-white dark:bg-zinc-900 rounded-2xl p-6 border border-zinc-200 dark:border-zinc-800 shadow-sm transition-all hover:shadow-md relative group"
      >
        <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
          
          <div class="flex items-start gap-4 flex-1">
            <div class="w-12 h-12 rounded-xl bg-zinc-100 dark:bg-zinc-800 flex items-center justify-center flex-shrink-0 text-zinc-500">
              <Building2 class="w-6 h-6" />
            </div>
            
            <div class="space-y-1">
              <h3 class="text-xl font-bold font-heading text-zinc-900 dark:text-white">{{ exp.position }}</h3>
              <div class="flex flex-wrap items-center gap-x-4 gap-y-2 text-sm text-zinc-600 dark:text-zinc-400">
                <span class="font-medium text-primary">{{ exp.companyName }}</span>
                <span v-if="exp.department" class="flex items-center gap-1">
                  <span class="w-1 h-1 rounded-full bg-zinc-300 dark:bg-zinc-700"></span>
                  {{ exp.department }}
                </span>
                <span class="flex items-center gap-1">
                  <span class="w-1 h-1 rounded-full bg-zinc-300 dark:bg-zinc-700"></span>
                  <Calendar class="w-4 h-4" />
                  {{ formatDate(exp.startDate) }} - {{ formatDate(exp.finishDate) }}
                </span>
              </div>
              <p v-if="exp.functions" class="text-zinc-600 dark:text-zinc-400 mt-3 text-sm leading-relaxed">
                {{ exp.functions }}
              </p>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex sm:flex-col gap-2 mt-4 sm:mt-0 opacity-100 sm:opacity-0 group-hover:opacity-100 transition-opacity">
            <Button variant="outline" size="sm" class="flex-1 sm:flex-none" @click="router.push(`/experiences/${exp.id}/edit`)">
              <Edit2 class="w-4 h-4 sm:mr-2" />
              <span class="hidden sm:inline">Editar</span>
            </Button>
            <Button variant="outline" size="sm" class="text-red-600 hover:bg-red-50 hover:text-red-700 dark:hover:bg-red-900/20 border-red-200 dark:border-red-900 flex-1 sm:flex-none" @click="handleDelete(exp.id)">
              <Trash2 class="w-4 h-4 sm:mr-2" />
              <span class="hidden sm:inline">Eliminar</span>
            </Button>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>
