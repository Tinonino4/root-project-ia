<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile.store';
import { useExperienceStore } from '@/stores/experience.store';
import { useAnalyticsStore } from '@/stores/analytics.store';
import { 
  ChevronDown, 
  Menu, 
  User as UserIcon, 
  Briefcase, 
  Star, 
  Download, 
  Sparkles, 
  ArrowRight, 
  CheckCircle2, 
  Circle, 
  ShieldCheck, 
  UserPlus, 
  Plus, 
  Award,
  HelpCircle
} from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import SkillsRadarChart from '@/components/dashboard/SkillsRadarChart.vue';
import html2pdf from 'html2pdf.js';

const router = useRouter();
const profileStore = useProfileStore();
const experienceStore = useExperienceStore();
const analyticsStore = useAnalyticsStore();

onMounted(async () => {
  await Promise.all([
    profileStore.fetchProfile(),
    experienceStore.fetchExperiences(),
    analyticsStore.fetchMetrics()
  ]);
});

const metrics = computed(() => analyticsStore.metrics);
const averageScore = computed(() => metrics.value?.averageScore || 0);

const formatDate = (dateString) => {
  if (!dateString) return 'Actualidad';
  const options = { year: 'numeric', month: 'long' };
  const date = new Date(dateString);
  const formatted = date.toLocaleDateString('es-ES', options);
  return formatted.charAt(0).toUpperCase() + formatted.slice(1);
};

const profile = computed(() => profileStore.profile);
const experiences = computed(() => experienceStore.sortedByDate);
const loading = computed(() => profileStore.loading || experienceStore.loading || analyticsStore.loading);

// Onboarding states
const isNewUser = computed(() => !profile.value);
const hasExperiences = computed(() => experiences.value.length > 0);
const hasFeedback = computed(() => !!metrics.value);
const showOnboarding = computed(() => !profile.value || experiences.value.length === 0);

const mockMetrics = {
  teamwork: 4.8,
  proactivity: 4.4,
  integrity: 4.7,
  selfConfidence: 4.2,
  flexibility: 4.5
};

const isExporting = ref(false);
const isGeneratingPDF = ref(false);

const exportToPDF = () => {
  if (isExporting.value || !profile.value) return;

  isExporting.value = true;
  isGeneratingPDF.value = true;

  // Esperar a que Vue monte el elemento en el DOM usando la clase de posicionamiento absoluto
  setTimeout(() => {
    const element = document.getElementById('pdf-template');
    if (!element) {
      console.error('El elemento PDF no se encontró en el DOM.');
      isExporting.value = false;
      isGeneratingPDF.value = false;
      return;
    }

    const opt = {
      margin:       12,
      filename:     `${profile.value?.name || 'perfil'}_cache.pdf`,
      image:        { type: 'jpeg', quality: 0.98 },
      html2canvas:  { 
        scale: 2.0, 
        useCORS: true, 
        logging: false,
        scrollY: 0,
        scrollX: 0
      },
      jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    html2pdf()
      .from(element)
      .set(opt)
      .save()
      .then(() => {
        isExporting.value = false;
        isGeneratingPDF.value = false;
      })
      .catch((err) => {
        console.error('Error al exportar PDF:', err);
        isExporting.value = false;
        isGeneratingPDF.value = false;
      });
  }, 200);
};
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    <!-- Main Container: Adaptable Light/Premium Dark -->
    
    <!-- Loading State with Shimmer Skeletons -->
    <div v-if="loading && (!profile || experiences.length === 0)" class="max-w-5xl mx-auto px-4 py-8 space-y-12 animate-pulse">
      <!-- Welcome Hero Banner Shimmer -->
      <div class="h-44 bg-zinc-200/50 dark:bg-white/[0.02] border border-zinc-200/50 dark:border-white/5 rounded-3xl p-8 shadow-sm">
        <div class="space-y-4 max-w-3xl">
          <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/6"></div>
          <div class="h-8 bg-zinc-200 dark:bg-zinc-800 rounded w-1/2"></div>
          <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-3/4"></div>
        </div>
      </div>

      <!-- 2 Column Layout Shimmer -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        <!-- Steps Road Shimmer (Left Col) -->
        <div class="lg:col-span-7 space-y-6">
          <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3 mb-6"></div>
          
          <div class="space-y-6">
            <div v-for="i in 3" :key="i" class="bg-zinc-200/30 dark:bg-white/[0.01] border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 space-y-3">
              <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-1/12"></div>
              <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3"></div>
              <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-full"></div>
            </div>
          </div>
        </div>

        <!-- Radar Demo Preview Shimmer (Right Col) -->
        <div class="lg:col-span-5 bg-zinc-200/40 dark:bg-white/[0.02] border border-zinc-200/50 dark:border-white/5 rounded-3xl p-8 space-y-6">
          <div class="space-y-3">
            <div class="h-5 bg-zinc-200 dark:bg-zinc-800 rounded w-1/4"></div>
            <div class="h-7 bg-zinc-200 dark:bg-zinc-800 rounded w-2/3"></div>
            <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-full"></div>
          </div>
          <div class="aspect-square bg-zinc-200 dark:bg-zinc-800/50 rounded-2xl flex items-center justify-center">
            <div class="w-48 h-48 rounded-full border-4 border-zinc-300 dark:border-zinc-700/50 flex items-center justify-center">
              <div class="w-32 h-32 rounded-full border-4 border-zinc-300 dark:border-zinc-700/50"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template v-else>
      <!-- ─── ONBOARDING DASHBOARD (NEW USER / NO DATA) ───────────────────── -->
      <div v-if="showOnboarding" class="max-w-5xl mx-auto px-4 py-8 space-y-12 animate-in fade-in slide-in-from-bottom-4 duration-500">
        
        <!-- Welcome Hero Banner -->
        <div class="relative overflow-hidden bg-gradient-to-tr from-primary/10 via-primary/[0.05] to-transparent dark:from-primary/20 dark:via-primary/[0.05] border border-primary/20 dark:border-primary/10 rounded-3xl p-8 md:p-10 shadow-sm backdrop-blur-md">
          <div class="absolute top-0 right-0 w-80 h-80 bg-primary/10 dark:bg-primary/5 rounded-full blur-3xl -translate-y-1/3 translate-x-1/3 pointer-events-none"></div>
          <div class="relative z-10 max-w-3xl space-y-4 text-center md:text-left">
            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-primary/20 text-primary uppercase tracking-wider">
              <Sparkles class="w-3.5 h-3.5" />
              Primeros Pasos
            </span>
            <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight text-zinc-900 dark:text-white font-heading">
              Tu reputación no se cuenta, <span class="text-primary">se demuestra.</span>
            </h1>
            <p class="text-zinc-600 dark:text-zinc-300 text-base md:text-lg leading-relaxed max-w-2xl">
              ¡Bienvenido a <span class="font-bold text-zinc-900 dark:text-white">Mi Caché</span>! Estás a muy pocos pasos de desbloquear un radar interactivo de habilidades blandas basado en valoraciones reales y 100% anónimas de tus colegas. Cero spam, puro valor profesional.
            </p>
          </div>
        </div>

        <!-- 2 Column Layout: Steps and Preview -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          
          <!-- Steps Road (Left Col) -->
          <div class="lg:col-span-7 space-y-6">
            <h2 class="text-xl font-bold tracking-wide text-zinc-900 dark:text-white uppercase font-heading flex items-center gap-2">
              <Award class="w-5 h-5 text-primary" />
              Tu Plan de Reputación
            </h2>
            
            <div class="relative pl-6 border-l-2 border-zinc-200 dark:border-zinc-800 space-y-8">
              
              <!-- STEP 1 -->
              <div class="relative group">
                <!-- Status Node -->
                <div class="absolute -left-[31px] top-1.5 p-1 rounded-full bg-zinc-50 dark:bg-[hsl(228,16%,7%)] z-10">
                  <CheckCircle2 v-if="!isNewUser" class="w-6 h-6 text-emerald-500 fill-emerald-500/10" />
                  <Circle v-else class="w-6 h-6 text-primary animate-pulse" />
                </div>
                
                <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-6 transition-all duration-300 hover:shadow-md dark:hover:bg-white/[0.04]">
                  <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <div>
                      <div class="flex items-center gap-2 mb-1">
                        <span class="text-xs font-bold text-primary uppercase tracking-wider">Paso 1</span>
                        <span v-if="!isNewUser" class="text-xs font-medium text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">Completado</span>
                        <span v-else class="text-xs font-medium text-amber-500 bg-amber-500/10 px-2 py-0.5 rounded-full">Requerido</span>
                      </div>
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white">Completa tu Perfil Profesional</h3>
                      <p class="text-sm text-zinc-500 dark:text-zinc-400 mt-1 max-w-md">
                        Define tu cargo actual, educación e imagen. Así tus colegas sabrán exactamente a quién están valorando.
                      </p>
                    </div>
                    <Button 
                      @click="router.push('/profile/edit')"
                      :variant="!isNewUser ? 'outline' : 'default'"
                      size="sm"
                      class="flex-shrink-0"
                    >
                      <UserPlus class="w-4 h-4 mr-2" />
                      {{ !isNewUser ? 'Editar Perfil' : 'Crear Perfil' }}
                    </Button>
                  </div>
                </div>
              </div>

              <!-- STEP 2 -->
              <div class="relative group" :class="{ 'opacity-60': isNewUser }">
                <!-- Status Node -->
                <div class="absolute -left-[31px] top-1.5 p-1 rounded-full bg-zinc-50 dark:bg-[hsl(228,16%,7%)] z-10">
                  <CheckCircle2 v-if="hasExperiences" class="w-6 h-6 text-emerald-500 fill-emerald-500/10" />
                  <Circle v-else class="w-6 h-6 text-zinc-400" />
                </div>
                
                <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-6 transition-all duration-300 hover:shadow-md dark:hover:bg-white/[0.04]">
                  <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <div>
                      <div class="flex items-center gap-2 mb-1">
                        <span class="text-xs font-bold text-primary uppercase tracking-wider">Paso 2</span>
                        <span v-if="hasExperiences" class="text-xs font-medium text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">Completado</span>
                        <span v-else class="text-xs font-medium text-zinc-400 bg-zinc-100 dark:bg-white/5 px-2 py-0.5 rounded-full">Pendiente</span>
                      </div>
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white">Añade tu Trayectoria Laboral</h3>
                      <p class="text-sm text-zinc-500 dark:text-zinc-400 mt-1 max-w-md">
                        Registra tus trabajos previos o actuales. El feedback de tus compañeros irá asociado a estas experiencias.
                      </p>
                    </div>
                    <Button 
                      @click="router.push('/experiences/new')"
                      :disabled="isNewUser"
                      :variant="hasExperiences ? 'outline' : 'default'"
                      size="sm"
                      class="flex-shrink-0"
                    >
                      <Plus class="w-4 h-4 mr-2" />
                      Añadir Experiencia
                    </Button>
                  </div>
                </div>
              </div>

              <!-- STEP 3 -->
              <div class="relative group" :class="{ 'opacity-60': !hasExperiences }">
                <!-- Status Node -->
                <div class="absolute -left-[31px] top-1.5 p-1 rounded-full bg-zinc-50 dark:bg-[hsl(228,16%,7%)] z-10">
                  <CheckCircle2 v-if="hasFeedback" class="w-6 h-6 text-emerald-500 fill-emerald-500/10" />
                  <Circle v-else class="w-6 h-6 text-zinc-400" />
                </div>
                
                <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-6 transition-all duration-300 hover:shadow-md dark:hover:bg-white/[0.04]">
                  <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <div>
                      <div class="flex items-center gap-2 mb-1">
                        <span class="text-xs font-bold text-primary uppercase tracking-wider">Paso 3</span>
                        <span v-if="hasFeedback" class="text-xs font-medium text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">Completado</span>
                        <span v-else class="text-xs font-medium text-zinc-400 bg-zinc-100 dark:bg-white/5 px-2 py-0.5 rounded-full">Pendiente</span>
                      </div>
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white">Solicita tu Primer Feedback</h3>
                      <p class="text-sm text-zinc-500 dark:text-zinc-400 mt-1 max-w-md">
                        Envía solicitudes dinámicas a tus excompañeros para que valoren de forma anónima tus habilidades interpersonales en un par de clics.
                      </p>
                    </div>
                    <Button 
                      @click="router.push('/feedback/new')"
                      :disabled="!hasExperiences"
                      variant="default"
                      size="sm"
                      class="flex-shrink-0 bg-primary hover:bg-primary/90 text-white"
                    >
                      <ArrowRight class="w-4 h-4 mr-2" />
                      Solicitar Feedback
                    </Button>
                  </div>
                </div>
              </div>

            </div>
          </div>

          <!-- Radar Demo Preview (Right Col) -->
          <div class="lg:col-span-5 bg-white/40 dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 sm:p-8 backdrop-blur-md relative overflow-hidden shadow-sm">
            <div class="absolute -top-12 -left-12 w-40 h-40 bg-primary/10 dark:bg-primary/5 rounded-full blur-2xl pointer-events-none"></div>
            
            <div class="space-y-4 mb-6">
              <div class="flex items-center gap-2">
                <span class="p-1.5 rounded-lg bg-primary/10 text-primary">
                  <ShieldCheck class="w-5 h-5" />
                </span>
                <span class="text-sm font-bold tracking-wide uppercase text-zinc-500 dark:text-zinc-400">Demostración en Vivo</span>
              </div>
              <h3 class="text-xl font-bold text-zinc-900 dark:text-white font-heading">
                Tu Futuro Caché Profesional
              </h3>
              <p class="text-xs text-zinc-500 dark:text-zinc-400 leading-relaxed">
                Una vez recibas valoraciones, tus 5 pilares de Habilidades Blandas se consolidarán en este radar interactivo y exportable en PDF de forma instantánea.
              </p>
            </div>

            <!-- Skills Radar Chart Component using Mock Data -->
            <div class="w-full relative aspect-square flex items-center justify-center p-2 rounded-2xl bg-zinc-50/50 dark:bg-zinc-900/30 border border-zinc-100 dark:border-white/[0.02]">
              <SkillsRadarChart :metrics="mockMetrics" />
              <div class="absolute top-3 right-3 bg-zinc-900/80 dark:bg-black/80 backdrop-blur-md px-2.5 py-1 rounded-full border border-white/10 flex items-center gap-1 shadow-lg">
                <Star class="w-3 h-3 text-amber-400 fill-current" />
                <span class="text-xs font-bold text-white">4.6 Avg</span>
              </div>
            </div>

            <div class="mt-6 flex items-start gap-2.5 bg-primary/5 dark:bg-primary/[0.03] border border-primary/10 p-3 rounded-xl">
              <HelpCircle class="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
              <p class="text-xs text-zinc-600 dark:text-zinc-400 leading-relaxed">
                <strong>¿Sabías qué?</strong> El radar blinda tu privacidad. Jamás revelamos quién te dio qué puntuación para garantizar respuestas honestas y de calidad.
              </p>
            </div>
          </div>

        </div>

      </div>

      <!-- ─── STANDARD DASHBOARD (ACTIVE USER WITH PROFILE AND DATA) ──────── -->
      <div v-else class="animate-in fade-in duration-500">
        <!-- Hero Header -->
        <div class="h-56 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
          <div class="absolute top-0 right-0 w-96 h-96 bg-white/10 dark:bg-white/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3"></div>
          <div class="absolute bottom-0 left-0 w-64 h-64 bg-primary/20 rounded-full blur-2xl translate-y-1/2 -translate-x-1/4"></div>
        </div>

        <!-- Profile Section -->
        <div class="max-w-3xl mx-auto px-4 -mt-24 relative z-10 flex flex-col items-center text-center">
          <!-- Avatar (Glowing) -->
          <div class="w-36 h-36 rounded-full overflow-hidden bg-white dark:bg-zinc-900 border-4 border-white dark:border-[hsl(228,16%,7%)] shadow-xl dark:shadow-[0_0_40px_rgba(242,151,39,0.15)] mb-6 flex items-center justify-center relative group transition-transform duration-300 hover:scale-105">
            <img v-if="profile?.photoUrl" :src="profile.photoUrl" alt="Foto de Perfil" class="w-full h-full object-cover" />
            <UserIcon v-else class="w-16 h-16 text-zinc-300 dark:text-zinc-600" />
            <div class="absolute inset-0 rounded-full ring-1 ring-inset ring-black/5 dark:ring-white/10 pointer-events-none"></div>
          </div>

          <!-- Profile Details -->
          <h1 class="text-3xl font-bold uppercase tracking-tight text-zinc-900 dark:text-white mb-2 font-heading">
            {{ profile?.name }} {{ profile?.surname || '' }}
          </h1>
          <div class="text-zinc-600 dark:text-zinc-400 font-medium text-[15px] space-y-1.5">
            <p class="text-primary dark:text-primary/90 font-semibold tracking-wide">{{ profile?.jobTitle || 'Profesional en Mi Caché' }}</p>
            <p v-if="profile?.education" class="text-sm opacity-90">{{ profile?.education }}</p>
          </div>

          <!-- Rating Score Blocks -->
          <div class="mt-8 flex items-center justify-center gap-3 bg-white/50 dark:bg-white/[0.02] py-2.5 px-5 rounded-2xl backdrop-blur-sm border border-zinc-200 dark:border-white/5 shadow-sm">
            <div class="flex gap-2">
              <div 
                v-for="i in 5" 
                :key="i"
                class="w-6 h-10 rounded-[3px] transition-colors"
                :class="i <= Math.round(averageScore) ? 'bg-primary dark:bg-primary/90 shadow-[0_0_10px_rgba(242,151,39,0.3)]' : 'bg-zinc-200/50 dark:bg-white/5 border border-zinc-300 dark:border-white/10'"
              ></div>
            </div>
            <ChevronDown class="w-5 h-5 text-zinc-400 ml-2" />
          </div>
        </div>

        <!-- Skills Radar Chart Component -->
        <div class="max-w-3xl mx-auto px-4 mt-12 relative z-10 flex flex-col items-center">
          <div v-if="metrics" class="w-full bg-white/50 dark:bg-white/[0.02] p-6 rounded-3xl backdrop-blur-md border border-zinc-200 dark:border-white/5 shadow-sm">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold tracking-wide text-zinc-900 dark:text-white uppercase font-heading">
                Mi Caché Profesional
              </h2>
              <Button @click="exportToPDF" variant="outline" size="sm" class="flex items-center gap-2">
                <Download class="w-4 h-4" />
                PDF
              </Button>
            </div>
            <SkillsRadarChart :metrics="metrics" />
          </div>
          <!-- Empty State for Skills -->
          <div v-else class="w-full bg-white/50 dark:bg-white/[0.02] p-6 rounded-3xl backdrop-blur-md border border-zinc-200 dark:border-white/5 shadow-sm text-center">
            <Star class="w-10 h-10 text-primary/40 mx-auto mb-3" />
            <h2 class="text-lg font-bold text-zinc-900 dark:text-white mb-2">Aún no tienes valoraciones</h2>
            <p class="text-sm text-zinc-500 dark:text-zinc-400">Solicita feedback en tus experiencias para descubrir tu gráfico de habilidades.</p>
          </div>
        </div>

        <!-- Experience Timeline Section -->
        <div class="max-w-3xl mx-auto mt-20 relative px-4 sm:px-6">
          <div class="flex items-center mb-10 relative z-10 justify-center sm:justify-start">
            <div class="inline-flex items-center gap-2.5 px-5 py-2 rounded-full bg-primary/10 dark:bg-primary/[0.08] border border-primary/20 dark:border-primary/10 shadow-sm backdrop-blur-md">
              <div class="w-6 h-6 rounded-full bg-primary flex items-center justify-center text-white shadow-[0_0_10px_rgba(242,151,39,0.3)]">
                <Briefcase class="w-3.5 h-3.5" />
              </div>
              <span class="text-lg font-bold tracking-wide text-primary dark:text-primary/90 uppercase text-sm">Experiencia</span>
            </div>
          </div>

          <!-- Timeline Grid -->
          <div class="relative pl-4 sm:pl-10" v-if="experiences.length > 0">
            <div class="absolute left-4 sm:left-10 top-4 bottom-0 w-[2px] bg-gradient-to-b from-primary/50 via-zinc-200 to-transparent dark:from-primary/30 dark:via-white/5 dark:to-transparent"></div>

            <div class="space-y-8">
              <div v-for="(exp, index) in experiences" :key="exp.id" class="relative pl-6 sm:pl-10 group">
                <div class="absolute -left-[5px] sm:-left-[5px] top-6 w-3 h-3 rounded-full bg-white dark:bg-[hsl(228,16%,7%)] border-[2.5px] border-primary z-10 shadow-[0_0_0_4px_rgba(255,255,255,1)] dark:shadow-[0_0_0_4px_hsl(228,16%,7%)] group-hover:scale-125 transition-transform duration-300">
                  <div v-if="index === 0" class="absolute inset-0 m-auto w-1 h-1 bg-primary rounded-full animate-ping opacity-75"></div>
                </div>

                <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/[0.05] rounded-2xl p-6 sm:p-7 shadow-sm hover:shadow-md dark:shadow-none dark:hover:bg-white/[0.04] transition-all duration-300 relative overflow-hidden">
                  <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary/0 via-primary/0 to-primary/0 dark:group-hover:via-primary/30 transition-all duration-500"></div>

                  <div class="flex justify-between items-start gap-4">
                    <div class="flex-1">
                      <div class="flex flex-col sm:flex-row sm:items-baseline gap-1 sm:gap-3 mb-1">
                        <h3 class="text-lg font-bold text-zinc-900 dark:text-white tracking-tight">
                          {{ exp.companyName }}
                        </h3>
                        <span class="text-sm font-medium text-zinc-500 dark:text-zinc-400/80">
                          {{ formatDate(exp.startDate) }} - {{ formatDate(exp.finishDate) }}
                        </span>
                      </div>
                      
                      <p class="text-primary dark:text-primary/80 font-semibold text-[15px] mb-3">
                        {{ exp.position }}
                      </p>

                      <p v-if="exp.functions" class="text-zinc-600 dark:text-zinc-400/90 text-[14.5px] leading-relaxed">
                        {{ exp.functions }}
                      </p>

                      <div class="mt-4 flex justify-start">
                        <button 
                          @click="router.push(`/feedback/new?experienceId=${exp.id}`)" 
                          class="text-xs font-semibold h-8 bg-primary/10 hover:bg-primary/20 dark:bg-primary/20 dark:hover:bg-primary/30 text-primary dark:text-primary/90 rounded-lg flex items-center px-3 transition-colors"
                        >
                          <Star class="w-3.5 h-3.5 mr-1.5 fill-current" />
                          Solicitar Feedback
                        </button>
                      </div>
                    </div>
                    
                    <button @click="router.push(`/experiences/${exp.id}/edit`)" class="p-2 -mr-2 -mt-2 text-zinc-400 hover:text-zinc-800 dark:hover:text-zinc-200 transition-colors flex-shrink-0 rounded-lg hover:bg-zinc-100 dark:hover:bg-white/5">
                      <Menu class="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Empty Experiences Fallback -->
          <div v-else class="text-center py-16 px-4 relative z-10 bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl mt-8">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mx-auto mb-4 text-primary">
              <Briefcase class="w-8 h-8" />
            </div>
            <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">Tu trayectoria está vacía</h3>
            <p class="text-zinc-500 dark:text-zinc-400 mb-6 max-w-sm mx-auto">Comienza a construir tu reputación profesional añadiendo tus experiencias pasadas.</p>
            <Button @click="router.push('/experiences/new')" class="bg-primary hover:bg-primary/90 text-white shadow-lg shadow-primary/20">
              Añadir mi primera experiencia
            </Button>
          </div>
        </div>
      </div>
    </template>

    <!-- Premium Fullscreen Loading Overlay -->
    <div v-if="isGeneratingPDF" class="fixed inset-0 z-[10000] bg-zinc-950/90 backdrop-blur-md flex flex-col items-center justify-center space-y-6 text-white select-none pointer-events-auto">
      <div class="relative w-20 h-20 flex items-center justify-center">
        <!-- Glowing outer ring -->
        <div class="absolute inset-0 rounded-full border-4 border-primary/10 border-t-primary animate-spin"></div>
        <!-- Inner orange pulse -->
        <div class="w-10 h-10 rounded-full bg-orange-500/20 animate-pulse flex items-center justify-center text-primary">
          <Award class="w-6 h-6" />
        </div>
      </div>
      <div class="text-center space-y-2">
        <h3 class="text-xl font-bold tracking-tight bg-gradient-to-r from-primary to-orange-500 bg-clip-text text-transparent">
          Generando Informe Personal...
        </h3>
        <p class="text-xs text-zinc-400 max-w-xs leading-relaxed">
          MiCaché B2B está compilando tu informe personal con habilidades y radar chart verificado.
        </p>
      </div>
    </div>

    <!-- Premium PDF Template (only rendered when generating PDF, positioned absolute top-left behind the overlay) -->
    <div v-if="isGeneratingPDF" class="absolute left-0 top-0 z-[9999] bg-white">
      <div id="pdf-template" class="p-10 bg-white text-zinc-900 font-sans" style="width: 794px; min-height: 1120px; box-sizing: border-box;">
        <!-- Header -->
        <div class="flex items-center gap-6 mb-8 border-b pb-6" style="border-color: #e4e4e7;">
          <div class="w-24 h-24 rounded-full overflow-hidden bg-zinc-100 flex items-center justify-center border-2 border-zinc-200">
            <img v-if="profile?.photoUrl" :src="profile.photoUrl" alt="Foto" class="w-full h-full object-cover" />
            <UserIcon v-else class="w-12 h-12 text-zinc-400" />
          </div>
          <div>
            <h1 class="text-2xl font-bold text-zinc-900">{{ profile?.name }} {{ profile?.surname }}</h1>
            <p class="text-lg text-primary font-semibold" style="color: #f29727;">{{ profile?.jobTitle }}</p>
            <p class="text-sm text-zinc-500">{{ profile?.education }}</p>
          </div>
        </div>

        <!-- Grid: Skills and Experience -->
        <div class="grid grid-cols-2 gap-8">
          <!-- Skills -->
          <div>
            <h2 class="text-lg font-bold text-zinc-800 mb-4 uppercase border-b pb-2" style="border-color: #e4e4e7;">Habilidades Blandas</h2>
            <div class="w-full h-64">
              <SkillsRadarChart v-if="metrics" :metrics="metrics" />
            </div>
          </div>

          <!-- Experience -->
          <div>
            <h2 class="text-lg font-bold text-zinc-800 mb-4 uppercase border-b pb-2" style="border-color: #e4e4e7;">Experiencia</h2>
            <div class="space-y-4">
              <div v-for="exp in experiences" :key="exp.id">
                <h3 class="text-md font-bold text-zinc-900">{{ exp.position }}</h3>
                <p class="text-sm font-semibold" style="color: #f29727;">{{ exp.companyName }}</p>
                <p class="text-xs text-zinc-500">{{ formatDate(exp.startDate) }} - {{ formatDate(exp.finishDate) }}</p>
                <p class="text-sm text-zinc-600 mt-1">{{ exp.functions }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

