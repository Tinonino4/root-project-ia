<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useExperienceStore } from '@/stores/experience.store';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ArrowLeft, Save } from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();
const experienceStore = useExperienceStore();

const isEditing = computed(() => !!route.params.id);
const experienceId = computed(() => route.params.id);

const formData = ref({
  companyName: '',
  department: '',
  position: '',
  startDate: '',
  finishDate: '',
  functions: ''
});

const isCurrentJob = ref(false);

const handleCurrentJobChange = () => {
  if (isCurrentJob.value) {
    formData.value.finishDate = '';
  }
};

onMounted(async () => {
  if (isEditing.value) {
    let exp = experienceStore.getExperienceById(experienceId.value);
    
    // If not in store, fetch all (or we could have a fetchOne, but fetchAll is fine here)
    if (!exp) {
      await experienceStore.fetchExperiences();
      exp = experienceStore.getExperienceById(experienceId.value);
    }
    
    if (exp) {
      formData.value = {
        companyName: exp.companyName || '',
        department: exp.department || '',
        position: exp.position || '',
        startDate: exp.startDate || '',
        finishDate: exp.finishDate || '',
        functions: exp.functions || ''
      };
      isCurrentJob.value = exp.finishDate === null;
    } else {
      // Experience not found
      router.push('/experiences');
    }
  }
});

const handleSave = async () => {
  try {
    const payload = { ...formData.value };
    if (isCurrentJob.value || payload.finishDate === '') {
      payload.finishDate = null;
    }

    if (isEditing.value) {
      await experienceStore.updateExperience(experienceId.value, payload);
    } else {
      await experienceStore.addExperience(payload);
    }
    router.push('/experiences');
  } catch (error) {
    console.error('Error saving experience:', error);
  }
};
</script>

<template>
  <div class="space-y-6 max-w-3xl mx-auto pb-12">
    
    <!-- Header Actions -->
    <div class="flex items-center gap-4">
      <Button variant="outline" size="icon" @click="router.push('/experiences')" class="rounded-full">
        <ArrowLeft class="w-5 h-5" />
      </Button>
      <div>
        <h2 class="text-3xl font-bold font-heading text-zinc-900 dark:text-white">
          {{ isEditing ? 'Editar Experiencia' : 'Nueva Experiencia' }}
        </h2>
        <p class="text-zinc-500 dark:text-zinc-400 mt-1">
          {{ isEditing ? 'Modifica los detalles de tu experiencia.' : 'Añade una nueva etapa a tu trayectoria profesional.' }}
        </p>
      </div>
    </div>

    <!-- Form -->
    <div class="bg-white dark:bg-zinc-900 rounded-2xl p-6 md:p-8 border border-zinc-200 dark:border-zinc-800 shadow-sm">
      <form @submit.prevent="handleSave" class="space-y-6">
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="space-y-2">
            <Label for="companyName">Empresa *</Label>
            <Input id="companyName" v-model="formData.companyName" required placeholder="Ej: Acme Corp" class="focus:ring-primary" />
          </div>
          <div class="space-y-2">
            <Label for="department">Departamento</Label>
            <Input id="department" v-model="formData.department" placeholder="Ej: Tecnología" class="focus:ring-primary" />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="position">Puesto o Cargo *</Label>
          <Input id="position" v-model="formData.position" required placeholder="Ej: Senior Developer" class="focus:ring-primary" />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="space-y-2">
            <Label for="startDate">Fecha de Inicio *</Label>
            <Input id="startDate" type="date" v-model="formData.startDate" required class="focus:ring-primary" />
          </div>
          
          <div class="space-y-2">
            <Label for="finishDate">Fecha de Fin</Label>
            <Input 
              id="finishDate" 
              type="date" 
              v-model="formData.finishDate" 
              :disabled="isCurrentJob" 
              class="focus:ring-primary disabled:opacity-50" 
            />
            <div class="flex items-center gap-2 mt-2">
              <input 
                type="checkbox" 
                id="currentJob" 
                v-model="isCurrentJob" 
                @change="handleCurrentJobChange"
                class="rounded border-zinc-300 text-primary focus:ring-primary"
              />
              <Label for="currentJob" class="text-sm font-normal text-zinc-600 dark:text-zinc-400 cursor-pointer">Actualmente trabajo aquí</Label>
            </div>
          </div>
        </div>

        <div class="space-y-2">
          <Label for="functions">Descripción de Funciones</Label>
          <textarea 
            id="functions" 
            v-model="formData.functions" 
            rows="12" 
            class="flex w-full min-h-[320px] rounded-md border border-zinc-200 dark:border-zinc-800 bg-transparent px-3 py-2 text-sm shadow-sm transition-colors placeholder:text-zinc-500 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-primary disabled:cursor-not-allowed disabled:opacity-50 dark:placeholder:text-zinc-400"
            placeholder="Describe tus responsabilidades y logros clave..."
          ></textarea>
        </div>

        <!-- Error Feedback -->
        <div v-if="experienceStore.error" class="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-3 rounded-lg text-sm">
          {{ experienceStore.error }}
        </div>

        <!-- Submit Actions -->
        <div class="flex justify-end gap-4 pt-6 border-t border-zinc-100 dark:border-zinc-800">
          <Button type="button" variant="outline" @click="router.push('/experiences')" :disabled="experienceStore.loading">
            Cancelar
          </Button>
          <Button type="submit" class="bg-primary hover:bg-primary/90 text-primary-foreground gap-2" :disabled="experienceStore.loading">
            <svg v-if="experienceStore.loading" class="animate-spin -ml-1 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            <Save v-else class="w-4 h-4" />
            {{ isEditing ? 'Guardar Cambios' : 'Añadir Experiencia' }}
          </Button>
        </div>

      </form>
    </div>

  </div>
</template>
