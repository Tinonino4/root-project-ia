<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { questionnaireApi } from '@/api/questionnaire.api';
import { Check, Star, AlertTriangle, ArrowLeft, ArrowRight, Award } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const route = useRoute();
const urlToken = route.params.token;

const questionnaire = ref(null);
const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);
const submitted = ref(false);

const answers = ref({});
const currentStep = ref(0);

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

const isCurrentStepValid = computed(() => {
  if (!questionnaire.value) return false;
  const currentCategory = questionnaire.value.categories[currentStep.value];
  for (const question of currentCategory.questions) {
    if (answers.value[question.id] === null) {
      return false;
    }
  }
  return true;
});

const isFormValid = computed(() => {
  if (!questionnaire.value) return false;
  
  // Check if all questions of all categories have an answer
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
  
  // Check if current step is fully answered to auto-advance
  setTimeout(() => {
    if (isCurrentStepValid.value && currentStep.value < questionnaire.value.categories.length - 1) {
      currentStep.value++;
    }
  }, 400);
};

const nextStep = () => {
  if (currentStep.value < questionnaire.value.categories.length - 1) {
    currentStep.value++;
  }
};

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
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
      <div v-else-if="error" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-red-200 dark:border-red-500/20 rounded-3xl p-8 text-center animate-fadeIn">
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

      <!-- Questionnaire Stepper Form -->
      <div v-else class="space-y-6">
        
        <!-- Progress Stepper Header -->
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-primary/10 rounded-xl flex items-center justify-center text-primary font-bold">
              {{ currentStep + 1 }}
            </div>
            <div>
              <p class="text-xs text-zinc-400 dark:text-zinc-500 uppercase tracking-wider font-semibold">Progreso Cuestionario</p>
              <h2 class="text-sm font-bold text-zinc-800 dark:text-zinc-200">
                Sección {{ currentStep + 1 }} de {{ questionnaire.categories.length }}
              </h2>
            </div>
          </div>

          <!-- Stepper Dots -->
          <div class="flex items-center space-x-2">
            <button
              v-for="(cat, idx) in questionnaire.categories"
              :key="cat.id"
              @click="currentStep = idx"
              class="w-3.5 h-3.5 rounded-full transition-all duration-300"
              :class="[
                idx === currentStep 
                  ? 'bg-primary scale-110 shadow-lg shadow-primary/20' 
                  : idx < currentStep 
                    ? 'bg-emerald-500/80 dark:bg-emerald-500/60'
                    : 'bg-zinc-200 dark:bg-zinc-800'
              ]"
              :aria-label="'Ir a sección ' + (idx + 1)"
              :aria-current="idx === currentStep ? 'step' : undefined"
            ></button>
          </div>
        </div>

        <!-- Category Step Container (Single Step Visible) -->
        <div class="relative overflow-hidden min-h-[400px]">
          <div 
            v-for="(category, catIdx) in questionnaire.categories" 
            :key="category.id"
            v-show="catIdx === currentStep"
            class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl shadow-sm p-8 transition-all duration-500 animate-fadeIn"
          >
            <div class="mb-8 border-b border-zinc-100 dark:border-zinc-800/80 pb-4">
              <div class="flex items-center gap-2">
                <Award class="w-6 h-6 text-primary" />
                <h2 class="text-2xl font-bold text-zinc-900 dark:text-white">{{ category.name }}</h2>
              </div>
              <p v-if="category.description" class="text-zinc-500 dark:text-zinc-400 text-sm mt-2 leading-relaxed">
                {{ category.description }}
              </p>
            </div>

            <div class="space-y-8">
              <div 
                v-for="question in category.questions" 
                :key="question.id" 
                class="space-y-4"
              >
                <label :for="'radiogroup-' + question.id" class="text-sm font-semibold text-zinc-700 dark:text-zinc-300 leading-normal block">
                  {{ question.text }}
                </label>
                
                <!-- Rating Blocks -->
                <div 
                  :id="'radiogroup-' + question.id" 
                  role="radiogroup" 
                  :aria-label="question.text" 
                  class="flex items-center space-x-2.5"
                >
                  <button 
                    v-for="rating in 5" 
                    :key="rating"
                    @click="setRating(question.id, rating)"
                    type="button"
                    role="radio"
                    :aria-checked="answers[question.id] === rating"
                    :aria-label="'Puntuar ' + rating + ' sobre 5'"
                    class="w-12 h-12 rounded-2xl flex items-center justify-center font-bold text-sm transition-all duration-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
                    :class="[
                      answers[question.id] === rating 
                        ? 'bg-primary text-white shadow-xl shadow-primary/30 scale-105 border border-primary' 
                        : 'bg-zinc-100 dark:bg-zinc-800/80 border border-zinc-200/10 text-zinc-700 dark:text-zinc-300 hover:bg-zinc-200 dark:hover:bg-zinc-700 hover:scale-105'
                    ]"
                  >
                    {{ rating }}
                  </button>
                  
                  <span class="ml-2 text-xs text-zinc-400 dark:text-zinc-500 font-bold" aria-live="polite">
                    {{ answers[question.id] ? `${answers[question.id]} / 5` : 'Sin responder' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Navigation & Submit Footer -->
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-6 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-sm">
          <!-- Left: Previous button / Validation message -->
          <div class="w-full sm:w-auto">
            <Button
              v-if="currentStep > 0"
              @click="prevStep"
              variant="outline"
              class="w-full sm:w-auto h-11 border-zinc-200 dark:border-zinc-800 rounded-xl px-6 flex items-center justify-center gap-2 hover:bg-zinc-100 dark:hover:bg-zinc-800 text-zinc-700 dark:text-zinc-300 font-medium"
            >
              <ArrowLeft class="w-4 h-4" />
              <span>Anterior</span>
            </Button>
            <div v-else class="text-xs text-zinc-400 dark:text-zinc-500 font-medium text-center sm:text-left select-none">
              Responde todas las preguntas de cada sección para avanzar.
            </div>
          </div>

          <!-- Right: Next / Submit button -->
          <div class="w-full sm:w-auto">
            <Button
              v-if="currentStep < questionnaire.categories.length - 1"
              @click="nextStep"
              :disabled="!isCurrentStepValid"
              class="w-full sm:w-auto h-11 bg-primary hover:bg-primary/90 text-white rounded-xl px-8 flex items-center justify-center gap-2 shadow-lg shadow-primary/10 disabled:opacity-50"
            >
              <span>Siguiente</span>
              <ArrowRight class="w-4 h-4" />
            </Button>

            <Button
              v-else
              @click="handleSubmit"
              :disabled="!isFormValid || isSubmitting"
              class="w-full sm:w-auto h-12 bg-primary hover:bg-primary/95 text-white font-bold rounded-xl px-10 flex items-center justify-center space-x-2 shadow-xl shadow-primary/30"
            >
              <span v-if="isSubmitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></span>
              <template v-else>
                <Check class="w-5 h-5" />
                <span>Enviar Valoración</span>
              </template>
            </Button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
