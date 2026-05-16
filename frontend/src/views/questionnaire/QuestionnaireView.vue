<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { questionnaireApi } from '@/api/questionnaire.api';
import { Check, Star, AlertTriangle } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const route = useRoute();
const urlToken = route.params.token;

const questionnaire = ref(null);
const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);
const submitted = ref(false);

const answers = ref({});

onMounted(async () => {
  try {
    const response = await questionnaireApi.getQuestionnaire(urlToken);
    questionnaire.value = response.data;
    
    // Initialize answers
    questionnaire.value.categories.forEach(category => {
      category.questions.forEach(question => {
        answers.value[question.id] = null;
      });
    });
  } catch (err) {
    error.value = err.message || 'Error al cargar el cuestionario. Es posible que el enlace haya expirado o ya haya sido completado.';
  } finally {
    loading.value = false;
  }
});

const isFormValid = computed(() => {
  if (!questionnaire.value) return false;
  
  // Check if all questions have an answer
  for (const category of questionnaire.value.categories) {
    for (const question of category.questions) {
      if (answers.value[question.id] === null) {
        return false;
      }
    }
  }
  return true;
});

const setRating = (questionId, rating) => {
  answers.value[questionId] = rating;
};

const handleSubmit = async () => {
  if (!isFormValid.value) return;
  
  isSubmitting.value = true;
  error.value = null;
  
  try {
    const skillAnswers = Object.entries(answers.value).map(([questionId, rating]) => ({
      questionId,
      rating
    }));
    
    const data = {
      skillAnswers,
      extraAnswers: {} // Optional
    };
    
    await questionnaireApi.submitQuestionnaire(urlToken, data);
    submitted.value = true;
  } catch (err) {
    error.value = err.message || 'Error al enviar el cuestionario';
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- HEADER -->
    <div class="h-48 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdG09IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSJub25lIi8+PGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjA1KSIvPjwvc3ZnPg==')] opacity-30"></div>
      
      <div class="max-w-3xl mx-auto px-6 h-full flex flex-col justify-center relative z-10 text-white">
        <h1 class="text-3xl font-bold tracking-tight">Cuestionario de Validación</h1>
        <p class="text-white/80 text-sm mt-1">Tu opinión nos ayuda a validar las habilidades profesionales</p>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-3xl mx-auto px-6 -mt-12 relative z-20">
      
      <!-- Loading State -->
      <div v-if="loading" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-primary mx-auto"></div>
        <p class="text-zinc-500 dark:text-zinc-400 font-medium mt-4">Cargando cuestionario...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-red-200 dark:border-red-500/20 rounded-3xl p-8 text-center">
        <AlertTriangle class="w-12 h-12 text-red-500 mx-auto mb-4" />
        <h2 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">¡Ups! Algo salió mal</h2>
        <p class="text-zinc-600 dark:text-zinc-400 text-sm">{{ error }}</p>
      </div>

      <!-- Success State -->
      <div v-else-if="submitted" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center animate-fadeIn">
        <div class="w-16 h-16 bg-emerald-500/10 rounded-full flex items-center justify-center mx-auto mb-6">
          <Check class="w-8 h-8 text-emerald-500" />
        </div>
        <h2 class="text-2xl font-bold text-zinc-900 dark:text-white mb-2">¡Muchas gracias!</h2>
        <p class="text-zinc-600 dark:text-zinc-400 text-sm">Tus respuestas han sido registradas con éxito. Tu colaboración es muy valiosa.</p>
      </div>

      <!-- Questionnaire Form -->
      <div v-else class="space-y-6">
        
        <div v-for="category in questionnaire.categories" :key="category.id" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl shadow-sm p-8 transition-all duration-300">
          
          <div class="mb-6">
            <h2 class="text-xl font-bold text-zinc-900 dark:text-white">{{ category.name }}</h2>
            <p v-if="category.description" class="text-zinc-500 dark:text-zinc-400 text-sm mt-1">{{ category.description }}</p>
          </div>

          <div class="space-y-6">
            <div v-for="question in category.questions" :key="question.id" class="space-y-3">
              <p class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ question.text }}</p>
              
              <!-- Rating Blocks -->
              <div class="flex items-center space-x-2">
                <button 
                  v-for="rating in 5" 
                  :key="rating"
                  @click="setRating(question.id, rating)"
                  type="button"
                  class="w-11 h-11 rounded-xl flex items-center justify-center font-semibold text-sm transition-all duration-200"
                  :class="[
                    answers[question.id] === rating 
                      ? 'bg-primary text-white shadow-lg shadow-primary/20 scale-105' 
                      : 'bg-zinc-100 dark:bg-zinc-800 text-zinc-600 dark:text-zinc-400 hover:bg-zinc-200 dark:hover:bg-zinc-700'
                  ]"
                >
                  {{ rating }}
                </button>
                
                <span class="ml-2 text-xs text-zinc-400 dark:text-zinc-500 font-medium">
                  {{ answers[question.id] ? `${answers[question.id]} / 5` : 'Sin responder' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Submit Section -->
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-6 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div class="text-sm text-zinc-500 dark:text-zinc-400">
            <span v-if="!isFormValid" class="flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 bg-amber-500 rounded-full"></span>
              Por favor, responde a todas las preguntas para continuar.
            </span>
            <span v-else class="flex items-center gap-1.5 text-emerald-600 dark:text-emerald-400">
              <Check class="w-4 h-4" />
              ¡Todo listo! Ya puedes enviar tus respuestas.
            </span>
          </div>

          <Button 
            @click="handleSubmit"
            class="w-full sm:w-auto h-12 bg-primary hover:bg-primary-hover text-white font-medium rounded-xl transition-all duration-300 flex items-center justify-center space-x-2 shadow-lg shadow-primary/20 px-8"
            :disabled="!isFormValid || isSubmitting"
          >
            <span v-if="isSubmitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></span>
            <template v-else>
              <span>Enviar Valoración</span>
            </template>
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
