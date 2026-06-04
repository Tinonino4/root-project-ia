<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useFeedbackStore } from '@/stores/feedback.store';
import { useExperienceStore } from '@/stores/experience.store';
import { ArrowLeft, Send, User, Mail, Phone, Briefcase, Heart } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

const router = useRouter();
const route = useRoute();
const feedbackStore = useFeedbackStore();
const experienceStore = useExperienceStore();

const form = ref({
  targetName: '',
  targetSurname: '',
  targetEmail: '',
  targetPhone: '',
  relationshipId: '',
  experienceId: '',
  stillWorksThere: true,
  extraAnswers: false
});

const isSubmitting = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

onMounted(async () => {
  await Promise.all([
    feedbackStore.fetchRelationships(),
    experienceStore.fetchExperiences()
  ]);
  
  // If experienceId is in query, use it. Otherwise use the first one.
  const queryExperienceId = route.query.experienceId;
  if (queryExperienceId) {
    form.value.experienceId = queryExperienceId;
  } else if (experienceStore.experiences.length > 0) {
    form.value.experienceId = experienceStore.experiences[0].id;
  }
});

const relationships = computed(() => feedbackStore.relationships);
const experiences = computed(() => experienceStore.experiences);

const handleSubmit = async () => {
  isSubmitting.value = true;
  successMessage.value = '';
  errorMessage.value = '';
  
  try {
    const data = {
      ...form.value,
      relationshipId: parseInt(form.value.relationshipId),
      experienceId: form.value.experienceId
    };
    
    await feedbackStore.createRequest(data);
    successMessage.value = '¡Solicitud de feedback enviada con éxito!';
    
    // Reset form or redirect after a delay
    setTimeout(() => {
      router.push('/dashboard');
    }, 2000);
  } catch (err) {
    errorMessage.value = err.message || 'Error al enviar la solicitud';
  } finally {
    isSubmitting.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- HEADER -->
    <div class="h-40 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdG09IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSJub25lIi8+PGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjA1KSIvPjwvc3ZnPg==')] opacity-30"></div>
      
      <div class="max-w-4xl mx-auto px-6 h-full flex items-center justify-between relative z-10">
        <div class="flex items-center space-x-4">
          <button 
            @click="goBack" 
            class="p-2.5 rounded-xl bg-white/10 backdrop-blur-md border border-white/20 text-white hover:bg-white/20 transition-all duration-300 shadow-lg group"
            aria-label="Volver"
          >
            <ArrowLeft class="w-5 h-5 group-hover:-translate-x-1 transition-transform" />
          </button>
          <div>
            <h1 class="text-3xl font-bold text-white tracking-tight">Solicitar Feedback</h1>
            <p class="text-white/80 text-sm mt-0.5">Pide a tus contactos que validen tus habilidades</p>
          </div>
        </div>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-4xl mx-auto px-6 -mt-10 relative z-20">
      
      <!-- Form Card (Glassmorphism) -->
      <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl shadow-[0_20px_50px_rgba(0,0,0,0.05)] dark:shadow-[0_20px_50px_rgba(0,0,0,0.3)] p-8 transition-all duration-500">
        
        <form @submit.prevent="handleSubmit" class="space-y-6">
          
          <!-- Alert Success -->
          <div v-if="successMessage" class="p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl text-emerald-600 dark:text-emerald-400 text-sm font-medium flex items-center space-x-2 animate-fadeIn">
            <span class="w-2 h-2 bg-emerald-500 rounded-full animate-pulse"></span>
            <span>{{ successMessage }}</span>
          </div>

          <!-- Alert Error -->
          <div v-if="errorMessage" class="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-red-600 dark:text-red-400 text-sm font-medium flex items-center space-x-2 animate-fadeIn">
            <span class="w-2 h-2 bg-red-500 rounded-full animate-pulse"></span>
            <span>{{ errorMessage }}</span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            
            <!-- Target Name -->
            <div class="space-y-2">
              <Label for="targetName" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">Nombre del Validador</Label>
              <div class="relative">
                <User class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <Input 
                  id="targetName" 
                  v-model="form.targetName" 
                  placeholder="Ej. Juan" 
                  class="pl-10 h-11 bg-white dark:bg-zinc-800/50 border-zinc-200 dark:border-zinc-700 focus:ring-primary focus:border-primary rounded-xl"
                  required 
                />
              </div>
            </div>

            <!-- Target Surname -->
            <div class="space-y-2">
              <Label for="targetSurname" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">Apellidos</Label>
              <div class="relative">
                <User class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <Input 
                  id="targetSurname" 
                  v-model="form.targetSurname" 
                  placeholder="Ej. Pérez" 
                  class="pl-10 h-11 bg-white dark:bg-zinc-800/50 border-zinc-200 dark:border-zinc-700 focus:ring-primary focus:border-primary rounded-xl"
                  required 
                />
              </div>
            </div>

            <!-- Target Email -->
            <div class="space-y-2">
              <Label for="targetEmail" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">Email</Label>
              <div class="relative">
                <Mail class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <Input 
                  id="targetEmail" 
                  type="email" 
                  v-model="form.targetEmail" 
                  placeholder="ejemplo@email.com" 
                  class="pl-10 h-11 bg-white dark:bg-zinc-800/50 border-zinc-200 dark:border-zinc-700 focus:ring-primary focus:border-primary rounded-xl"
                  required 
                />
              </div>
            </div>

            <!-- Target Phone -->
            <div class="space-y-2">
              <Label for="targetPhone" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">Teléfono (Opcional)</Label>
              <div class="relative">
                <Phone class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <Input 
                  id="targetPhone" 
                  v-model="form.targetPhone" 
                  placeholder="+34 600 000 000" 
                  class="pl-10 h-11 bg-white dark:bg-zinc-800/50 border-zinc-200 dark:border-zinc-700 focus:ring-primary focus:border-primary rounded-xl"
                />
              </div>
            </div>

            <!-- Relationship -->
            <div class="space-y-2">
              <Label for="relationship" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">Relación</Label>
              <div class="relative">
                <Heart class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <select 
                  id="relationship" 
                  v-model="form.relationshipId" 
                  class="w-full pl-10 h-11 bg-white dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700 rounded-xl text-zinc-900 dark:text-zinc-100 focus:ring-primary focus:border-primary text-sm"
                  required
                >
                  <option value="" disabled>Selecciona una relación</option>
                  <option v-for="rel in relationships" :key="rel.id" :value="rel.id">
                    {{ rel.name }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Experience -->
            <div class="space-y-2">
              <Label for="experience" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">Experiencia Asociada</Label>
              <div class="relative">
                <Briefcase class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <select 
                  id="experience" 
                  v-model="form.experienceId" 
                  class="w-full pl-10 h-11 bg-white dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700 rounded-xl text-zinc-900 dark:text-zinc-100 focus:ring-primary focus:border-primary text-sm"
                  required
                >
                  <option value="" disabled>Selecciona una experiencia</option>
                  <option v-for="exp in experiences" :key="exp.id" :value="exp.id">
                    {{ exp.company }} - {{ exp.position }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <!-- Checkboxes -->
          <div class="space-y-4 pt-2">
            <div class="flex items-center space-x-3">
              <input 
                id="stillWorksThere" 
                type="checkbox" 
                v-model="form.stillWorksThere" 
                class="w-5 h-5 rounded-md border-zinc-300 dark:border-zinc-700 text-primary focus:ring-primary dark:bg-zinc-800"
              />
              <Label for="stillWorksThere" class="text-sm font-medium text-zinc-700 dark:text-zinc-300 cursor-pointer">
                ¿Sigue trabajando ahí?
              </Label>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="pt-4">
            <Button 
              type="submit" 
              class="w-full h-12 bg-primary hover:bg-primary-hover text-white font-medium rounded-xl transition-all duration-300 flex items-center justify-center space-x-2 shadow-lg shadow-primary/20"
              :disabled="isSubmitting"
            >
              <span v-if="isSubmitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></span>
              <template v-else>
                <Send class="w-5 h-5" />
                <span>Enviar Solicitud</span>
              </template>
            </Button>
          </div>

        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
