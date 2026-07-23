<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useFeedbackStore } from '@/stores/feedback.store';
import { useExperienceStore } from '@/stores/experience.store';
import { ArrowLeft, Send, User, Mail, Phone, Briefcase, ChevronDown } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

const router = useRouter();
const route = useRoute();
const feedbackStore = useFeedbackStore();
const experienceStore = useExperienceStore();
const { t } = useI18n();

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

const showRelationshipDropdown = ref(false);
const showExperienceDropdown = ref(false);
const relationshipContainer = ref(null);
const experienceContainer = ref(null);

onMounted(async () => {
  await Promise.all([
    feedbackStore.fetchRelationships(),
    experienceStore.fetchExperiences()
  ]);
  
  const queryExperienceId = route.query.experienceId;
  if (queryExperienceId) {
    form.value.experienceId = queryExperienceId;
  } else if (experienceStore.experiences.length > 0) {
    form.value.experienceId = experienceStore.experiences[0].id;
  }
});

const relationships = computed(() => feedbackStore.relationships);
const experiences = computed(() => experienceStore.experiences);

const selectedRelationship = computed(() => {
  return relationships.value.find(r => r.id.toString() === form.value.relationshipId) || null;
});

const selectedExperience = computed(() => {
  return experiences.value.find(e => e.id === form.value.experienceId) || null;
});

const selectRelationship = (id) => {
  form.value.relationshipId = id.toString();
  showRelationshipDropdown.value = false;
};

const selectExperience = (id) => {
  form.value.experienceId = id;
  showExperienceDropdown.value = false;
};

const closeDropdowns = (e) => {
  if (relationshipContainer.value && !relationshipContainer.value.contains(e.target)) {
    showRelationshipDropdown.value = false;
  }
  if (experienceContainer.value && !experienceContainer.value.contains(e.target)) {
    showExperienceDropdown.value = false;
  }
};

onMounted(() => {
  window.addEventListener('click', closeDropdowns);
});

onBeforeUnmount(() => {
  window.removeEventListener('click', closeDropdowns);
});

const handleSubmit = async () => {
  if (!form.value.relationshipId) {
    errorMessage.value = t('feedback.relationship');
    return;
  }
  if (!form.value.experienceId) {
    errorMessage.value = t('feedback.selectExperience');
    return;
  }

  isSubmitting.value = true;
  successMessage.value = '';
  errorMessage.value = '';
  
  try {
    await feedbackStore.createRequest({
      targetName: form.value.targetName,
      targetSurname: form.value.targetSurname,
      targetEmail: form.value.targetEmail,
      targetPhone: form.value.targetPhone,
      relationshipId: parseInt(form.value.relationshipId, 10),
      experienceId: form.value.experienceId,
      stillWorksThere: form.value.stillWorksThere,
      extraAnswers: form.value.extraAnswers
    });
    
    successMessage.value = t('feedback.status.COMPLETED');
    
    setTimeout(() => {
      router.push('/feedback');
    }, 1500);
  } catch (error) {
    console.error('Error creating feedback request:', error);
    errorMessage.value = error.response?.data?.message || 'Error sending request';
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
      
      <div class="max-w-4xl mx-auto px-4 sm:px-6 h-full flex items-center justify-between relative z-10">
        <div class="flex items-center space-x-3 sm:space-x-4">
          <button 
            @click="goBack" 
            class="p-2 sm:p-2.5 rounded-xl bg-white/10 backdrop-blur-md border border-white/20 text-white hover:bg-white/20 transition-all duration-300 shadow-lg group"
            aria-label="Volver"
          >
            <ArrowLeft class="w-4.5 h-4.5 sm:w-5 sm:h-5 group-hover:-translate-x-1 transition-transform" />
          </button>
          <div>
            <h1 class="text-2xl sm:text-3xl font-bold text-white tracking-tight">{{ $t('feedback.createTitle') }}</h1>
            <p class="text-white/80 text-xs sm:text-sm mt-0.5">{{ $t('feedback.createSubtitle') }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-4xl mx-auto px-3 sm:px-6 -mt-10 relative z-20">
      
      <!-- Form Card (Glassmorphism) -->
      <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl shadow-[0_20px_50px_rgba(0,0,0,0.05)] dark:shadow-[0_20px_50px_rgba(0,0,0,0.3)] p-4 sm:p-8 transition-all duration-500">
        
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
              <Label for="targetName" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ $t('feedback.refereeName') }}</Label>
              <div class="relative">
                <User class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <Input 
                  id="targetName" 
                  v-model="form.targetName" 
                  placeholder="Juan" 
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
                  placeholder="Pérez" 
                  class="pl-10 h-11 bg-white dark:bg-zinc-800/50 border-zinc-200 dark:border-zinc-700 focus:ring-primary focus:border-primary rounded-xl"
                  required 
                />
              </div>
            </div>

            <!-- Target Email -->
            <div class="space-y-2">
              <Label for="targetEmail" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ $t('feedback.refereeEmail') }}</Label>
              <div class="relative">
                <Mail class="w-4 h-4 absolute left-3.5 top-3 text-zinc-400" />
                <Input 
                  id="targetEmail" 
                  type="email" 
                  v-model="form.targetEmail" 
                  placeholder="example@email.com" 
                  class="pl-10 h-11 bg-white dark:bg-zinc-800/50 border-zinc-200 dark:border-zinc-700 focus:ring-primary focus:border-primary rounded-xl"
                  required 
                />
              </div>
            </div>

            <!-- Target Phone -->
            <div class="space-y-2">
              <Label for="targetPhone" class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ $t('feedback.refereePhone') }}</Label>
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
            <div class="space-y-2" ref="relationshipContainer">
              <Label class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ $t('feedback.relationship') }}</Label>
              <div class="relative">
                <button 
                  type="button"
                  @click.stop="showRelationshipDropdown = !showRelationshipDropdown; showExperienceDropdown = false;"
                  class="w-full px-4 h-11 bg-white dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700 rounded-xl text-left text-zinc-900 dark:text-zinc-100 focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary text-sm flex items-center justify-between transition-all duration-200"
                >
                  <span :class="{ 'text-zinc-400 dark:text-zinc-500': !selectedRelationship }">
                    {{ selectedRelationship ? selectedRelationship.name : $t('feedback.relationship') }}
                  </span>
                  <ChevronDown class="w-4 h-4 text-zinc-400 dark:text-zinc-500 transition-transform duration-200" :class="{ 'rotate-180': showRelationshipDropdown }" />
                </button>
                <div 
                  v-if="showRelationshipDropdown" 
                  class="absolute z-50 w-full mt-1.5 bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-xl shadow-xl overflow-hidden animate-in fade-in slide-in-from-top-2 duration-150"
                >
                  <button
                    v-for="rel in relationships"
                    :key="rel.id"
                    type="button"
                    @click="selectRelationship(rel.id)"
                    class="w-full px-4 py-2.5 text-left text-sm text-zinc-700 dark:text-zinc-300 hover:bg-zinc-50 dark:hover:bg-zinc-800/80 transition-colors flex items-center justify-between"
                    :class="{ 'bg-primary/5 text-primary dark:bg-primary/10 dark:text-primary font-semibold': form.relationshipId === rel.id.toString() }"
                  >
                    <span>{{ rel.name }}</span>
                    <span v-if="form.relationshipId === rel.id.toString()" class="w-1.5 h-1.5 rounded-full bg-primary"></span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Experience -->
            <div class="space-y-2" ref="experienceContainer">
              <Label class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ $t('feedback.selectExperience') }}</Label>
              <div class="relative">
                <button 
                  type="button"
                  @click.stop="showExperienceDropdown = !showExperienceDropdown; showRelationshipDropdown = false;"
                  class="w-full px-4 h-11 bg-white dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700 rounded-xl text-left text-zinc-900 dark:text-zinc-100 focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary text-sm flex items-center justify-between transition-all duration-200"
                >
                  <span :class="{ 'text-zinc-400 dark:text-zinc-500': !selectedExperience }">
                    {{ selectedExperience ? `${selectedExperience.position} - ${selectedExperience.companyName}` : $t('feedback.selectExperience') }}
                  </span>
                  <ChevronDown class="w-4 h-4 text-zinc-400 dark:text-zinc-500 transition-transform duration-200" :class="{ 'rotate-180': showExperienceDropdown }" />
                </button>
                <div 
                  v-if="showExperienceDropdown" 
                  class="absolute z-50 w-full mt-1.5 bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-xl shadow-xl overflow-hidden animate-in fade-in slide-in-from-top-2 duration-150"
                >
                  <button
                    v-for="exp in experiences"
                    :key="exp.id"
                    type="button"
                    @click="selectExperience(exp.id)"
                    class="w-full px-4 py-2.5 text-left text-sm text-zinc-700 dark:text-zinc-300 hover:bg-zinc-50 dark:hover:bg-zinc-800/80 transition-colors flex items-center justify-between"
                    :class="{ 'bg-primary/5 text-primary dark:bg-primary/10 dark:text-primary font-semibold': form.experienceId === exp.id }"
                  >
                    <span class="truncate">{{ exp.position }} - {{ exp.companyName }}</span>
                    <span v-if="form.experienceId === exp.id" class="w-1.5 h-1.5 rounded-full bg-primary flex-shrink-0 ml-2"></span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Email Language Selector -->
            <div class="space-y-2">
              <Label class="text-sm font-medium text-zinc-700 dark:text-zinc-300">{{ $t('extraFeedback.emailLanguage') }}</Label>
              <div class="flex items-center gap-2 h-11">
                <button
                  type="button"
                  @click="form.emailLanguage = 'en'"
                  class="flex-1 h-full rounded-xl border text-xs font-bold transition-all flex items-center justify-center gap-1.5"
                  :class="form.emailLanguage === 'en' 
                    ? 'bg-primary text-white border-primary shadow-sm' 
                    : 'bg-white dark:bg-zinc-800/50 text-zinc-700 dark:text-zinc-300 border-zinc-200 dark:border-zinc-700 hover:bg-zinc-50 dark:hover:bg-zinc-800'"
                >
                  <span>🇬🇧 EN</span>
                </button>
                <button
                  type="button"
                  @click="form.emailLanguage = 'es'"
                  class="flex-1 h-full rounded-xl border text-xs font-bold transition-all flex items-center justify-center gap-1.5"
                  :class="form.emailLanguage === 'es' 
                    ? 'bg-primary text-white border-primary shadow-sm' 
                    : 'bg-white dark:bg-zinc-800/50 text-zinc-700 dark:text-zinc-300 border-zinc-200 dark:border-zinc-700 hover:bg-zinc-50 dark:hover:bg-zinc-800'"
                >
                  <span>🇪🇸 ES</span>
                </button>
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
                {{ $t('extraFeedback.stillWorksThere') }}
              </Label>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="pt-4">
            <Button 
              type="submit" 
              class="w-full h-12 bg-primary hover:bg-primary/90 text-white font-medium rounded-xl transition-all duration-300 flex items-center justify-center space-x-2 shadow-lg shadow-primary/20"
              :disabled="isSubmitting"
            >
              <span v-if="isSubmitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></span>
              <template v-else>
                <Send class="w-5 h-5" />
                <span>{{ isSubmitting ? $t('feedback.sending') : $t('feedback.submitRequest') }}</span>
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
