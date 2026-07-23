<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { questionnaireApi } from '@/api/questionnaire.api';
import { Check, Star, AlertTriangle, ArrowLeft, ArrowRight, Award, MessageSquare } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const route = useRoute();
const urlToken = route.params.token;
const { t } = useI18n();

const questionnaire = ref(null);
const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);
const submitted = ref(false);

const answers = ref({});
const currentStep = ref(0);
const additionalComments = ref('');

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
    error.value = err.message || t('questionnaire.errorMsg');
  } finally {
    loading.value = false;
  }
});

const isCurrentStepValid = computed(() => {
  if (!questionnaire.value) return false;
  if (currentStep.value === questionnaire.value.categories.length) {
    return true; // El paso de comentarios adicionales es opcional
  }
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
    if (isCurrentStepValid.value && currentStep.value < questionnaire.value.categories.length) {
      currentStep.value++;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, 450);
};

const nextStep = () => {
  if (currentStep.value < questionnaire.value.categories.length) {
    currentStep.value++;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

const handleRadioKeyDown = (event, questionId, ratingValue) => {
  const key = event.key;
  let nextRating = null;

  if (key === 'ArrowRight' || key === 'ArrowDown') {
    event.preventDefault();
    nextRating = ratingValue < 5 ? ratingValue + 1 : 1;
  } else if (key === 'ArrowLeft' || key === 'ArrowUp') {
    event.preventDefault();
    nextRating = ratingValue > 1 ? ratingValue - 1 : 5;
  }

  if (nextRating !== null) {
    answers.value[questionId] = nextRating;
    // Focus the next button using dynamic element ID after Vue renders
    setTimeout(() => {
      const btn = document.getElementById(`btn-${questionId}-${nextRating}`);
      if (btn) btn.focus();
    }, 10);
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
      extraAnswers: {
        comments: additionalComments.value.trim()
      }
    };
    
    await questionnaireApi.submitQuestionnaire(urlToken, data);
    submitted.value = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  } catch (err) {
    error.value = err.message || t('questionnaire.submitError');
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
        <h1 class="text-3xl font-bold tracking-tight">{{ $t('questionnaire.title') }}</h1>
        <p class="text-white/80 text-sm mt-1">{{ $t('questionnaire.subtitle') }}</p>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-3xl mx-auto px-6 -mt-12 relative z-20">
      
      <!-- Loading State with Shimmer Skeletons -->
      <div v-if="loading" class="space-y-6 animate-pulse">
        <!-- Progress Stepper Header Shimmer -->
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div class="flex items-center space-x-3 w-full md:w-auto">
            <div class="w-10 h-10 bg-zinc-200 dark:bg-zinc-800 rounded-xl"></div>
            <div class="space-y-2 flex-1">
              <div class="h-5 bg-zinc-200 dark:bg-zinc-800 rounded w-24"></div>
              <div class="h-3.5 bg-zinc-200 dark:bg-zinc-800 rounded w-32"></div>
            </div>
          </div>
          <div class="w-full md:w-48 h-2 bg-zinc-200 dark:bg-zinc-800 rounded"></div>
        </div>

        <!-- Question Card Shimmer -->
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-6 md:p-8 shadow-md space-y-8">
          <div class="space-y-3">
            <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/4"></div>
            <div class="h-8 bg-zinc-200 dark:bg-zinc-800 rounded w-3/4"></div>
          </div>

          <!-- Slider or Radio Choices Shimmer -->
          <div class="space-y-4 pt-4">
            <div v-for="i in 5" :key="i" class="flex items-center justify-between p-4 rounded-xl border border-zinc-100 dark:border-white/[0.02]">
              <div class="h-5 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3"></div>
              <div class="w-5 h-5 rounded-full bg-zinc-200 dark:bg-zinc-800"></div>
            </div>
          </div>

          <!-- Bottom Actions Shimmer -->
          <div class="flex justify-between items-center pt-6 border-t border-zinc-100 dark:border-white/5">
            <div class="w-24 h-10 bg-zinc-200 dark:bg-zinc-800 rounded-xl"></div>
            <div class="w-32 h-10 bg-zinc-200 dark:bg-zinc-800 rounded-xl"></div>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-red-200 dark:border-red-500/20 rounded-3xl p-8 text-center animate-fadeIn">
        <AlertTriangle class="w-12 h-12 text-red-500 mx-auto mb-4" />
        <h2 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">{{ $t('questionnaire.errorTitle') }}</h2>
        <p class="text-zinc-600 dark:text-zinc-400 text-sm">{{ error }}</p>
      </div>

      <!-- Success State -->
      <div v-else-if="submitted" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center animate-fadeIn shadow-xl">
        <div class="w-16 h-16 bg-emerald-500/10 rounded-full flex items-center justify-center mx-auto mb-6">
          <Check class="w-8 h-8 text-emerald-500" />
        </div>
        <h2 class="text-2xl font-bold text-zinc-900 dark:text-white mb-2">{{ $t('questionnaire.successTitle') }}</h2>
        <p class="text-zinc-600 dark:text-zinc-400 text-sm max-w-md mx-auto">{{ $t('questionnaire.successMsg') }}</p>
        
        <!-- Acciones Post-Envio -->
        <div class="mt-8 flex flex-col sm:flex-row justify-center gap-4">
          <RouterLink 
            to="/login" 
            class="inline-flex items-center justify-center px-6 py-3 rounded-xl bg-primary text-white font-semibold hover:bg-primary/95 shadow-lg shadow-primary/20 hover:-translate-y-0.5 transition-all duration-200"
          >
            {{ $t('questionnaire.goToLogin') }}
          </RouterLink>
          <a 
            href="/" 
            class="inline-flex items-center justify-center px-6 py-3 rounded-xl bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-700 dark:text-zinc-300 font-semibold hover:bg-zinc-100 dark:hover:bg-zinc-700/80 hover:-translate-y-0.5 transition-all duration-200"
          >
            {{ $t('questionnaire.goHome') }}
          </a>
        </div>
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
              <p class="text-xs text-zinc-400 dark:text-zinc-500 uppercase tracking-wider font-semibold">{{ $t('questionnaire.progress') }}</p>
              <h2 class="text-sm font-bold text-zinc-800 dark:text-zinc-200">
                {{ $t('questionnaire.section', { current: currentStep + 1, total: questionnaire.categories.length + 1 }) }}
              </h2>
            </div>
          </div>

          <!-- Stepper Dots -->
          <div class="flex items-center space-x-2">
            <button
              v-for="(cat, idx) in (questionnaire.categories.length + 1)"
              :key="idx"
              @click="currentStep = idx"
              class="w-3.5 h-3.5 rounded-full transition-all duration-300"
              :class="[
                idx === currentStep 
                  ? 'bg-primary scale-110 shadow-lg shadow-primary/20' 
                  : idx < currentStep 
                    ? 'bg-emerald-500/80 dark:bg-emerald-500/60'
                    : 'bg-zinc-200 dark:bg-zinc-800'
              ]"
              :aria-label="$t('questionnaire.ariaGoToSection', { index: idx + 1 })"
              :aria-current="idx === currentStep ? 'step' : undefined"
            ></button>
          </div>
        </div>

        <!-- Step Containers -->
        <div class="relative overflow-hidden min-h-[400px]">
          
          <!-- Category Steps -->
          <div 
            v-for="(category, catIdx) in questionnaire.categories" 
            :key="category.id"
            v-show="catIdx === currentStep"
            class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl shadow-sm p-8 transition-all duration-500 animate-fadeIn"
          >
            <div class="mb-8 border-b border-zinc-100 dark:border-zinc-800/80 pb-4">
              <div class="flex items-center gap-2">
                <Award class="w-6 h-6 text-primary" />
                <h2 class="text-2xl font-bold text-zinc-900 dark:text-white">
                  {{ $t('questionnaire.categories.' + category.code + '.name', category.name) }}
                </h2>
              </div>
              <p v-if="category.description" class="text-zinc-500 dark:text-zinc-400 text-sm mt-2 leading-relaxed">
                {{ $t('questionnaire.categories.' + category.code + '.description', category.description) }}
              </p>
            </div>

            <div class="space-y-8">
              <div 
                v-for="question in category.questions" 
                :key="question.id" 
                class="space-y-4"
              >
                <label :for="'radiogroup-' + question.id" class="text-sm font-semibold text-zinc-700 dark:text-zinc-300 leading-normal block">
                  {{ $t('questionnaire.questions.' + question.text, question.text) }}
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
                    :id="'btn-' + question.id + '-' + rating"
                    @click="setRating(question.id, rating)"
                    @keydown="handleRadioKeyDown($event, question.id, rating)"
                    :tabindex="(answers[question.id] === rating || (answers[question.id] === null && rating === 1)) ? 0 : -1"
                    type="button"
                    role="radio"
                    :aria-checked="answers[question.id] === rating"
                    :aria-label="$t('questionnaire.ariaRate', { rating: rating })"
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
                    {{ answers[question.id] ? $t('questionnaire.ariaScore', { score: answers[question.id] }) : $t('questionnaire.unanswered') }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Final step: Comments -->
          <div 
            v-show="currentStep === questionnaire.categories.length"
            class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-8 shadow-sm transition-all duration-500 animate-fadeIn"
          >
            <div class="mb-8 border-b border-zinc-100 dark:border-zinc-800/80 pb-4">
              <div class="flex items-center gap-2">
                <MessageSquare class="w-6 h-6 text-primary" />
                <h2 class="text-2xl font-bold text-zinc-900 dark:text-white">{{ $t('questionnaire.finishTitle') }}</h2>
              </div>
              <p class="text-zinc-500 dark:text-zinc-400 text-sm mt-2 leading-relaxed">
                {{ $t('questionnaire.finishDesc') }}
              </p>
            </div>

            <div class="space-y-4">
              <label for="additional-comments" class="text-sm font-semibold text-zinc-700 dark:text-zinc-300 leading-normal block">
                {{ $t('questionnaire.optionalComments') }}
              </label>
              <p class="text-zinc-400 dark:text-zinc-500 text-xs">
                {{ $t('questionnaire.commentsDesc') }}
              </p>
              <textarea
                id="additional-comments"
                v-model="additionalComments"
                rows="5"
                :placeholder="$t('questionnaire.commentsPlaceholder')"
                class="w-full p-4 bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700 rounded-2xl text-sm text-zinc-900 dark:text-white placeholder-zinc-400 dark:placeholder-zinc-500 focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200 resize-none"
              ></textarea>
            </div>
          </div>

        </div>

        <!-- Navigation & Submit Footer -->
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-6 flex flex-row items-center justify-between gap-4 shadow-sm">
          <!-- Left: Previous button -->
          <div :class="currentStep > 0 ? 'flex-1 sm:flex-initial' : 'hidden sm:block text-xs text-zinc-400 dark:text-zinc-500'">
            <Button
              v-if="currentStep > 0"
              @click="prevStep"
              variant="outline"
              class="w-full sm:w-auto h-11 border-zinc-200 dark:border-zinc-800 rounded-xl px-6 flex items-center justify-center gap-2 hover:bg-zinc-100 dark:hover:bg-zinc-800 text-zinc-700 dark:text-zinc-300 font-medium"
            >
              <ArrowLeft class="w-4 h-4" />
              <span>{{ $t('questionnaire.back') }}</span>
            </Button>
            <div v-else class="text-xs text-zinc-400 dark:text-zinc-500 font-medium select-none">
              {{ $t('questionnaire.instruction') }}
            </div>
          </div>

          <!-- Right: Next / Submit button -->
          <div :class="currentStep > 0 ? 'flex-1 sm:flex-initial text-right' : 'w-full sm:w-auto text-right'">
            <Button
              v-if="currentStep < questionnaire.categories.length"
              @click="nextStep"
              :disabled="!isCurrentStepValid"
              class="w-full sm:w-auto h-11 bg-primary hover:bg-primary/90 text-white rounded-xl px-8 flex items-center justify-center gap-2 shadow-lg shadow-primary/10 disabled:opacity-50"
            >
              <span>{{ $t('questionnaire.next') }}</span>
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
                <span>{{ $t('questionnaire.submit') }}</span>
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
