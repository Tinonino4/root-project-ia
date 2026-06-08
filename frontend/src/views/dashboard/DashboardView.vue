<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile.store';
import { useExperienceStore } from '@/stores/experience.store';
import { useAnalyticsStore } from '@/stores/analytics.store';
import { useFeedbackStore } from '@/stores/feedback.store';
import { useAuthStore } from '@/stores/auth.store';
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
  HelpCircle,
  Clock,
  Copy,
  ExternalLink,
  Share2,
  ArrowUpRight
} from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import SkillsRadarChart from '@/components/dashboard/SkillsRadarChart.vue';
import html2pdf from 'html2pdf.js';
import { toast } from 'vue-sonner';

const router = useRouter();
const profileStore = useProfileStore();
const experienceStore = useExperienceStore();
const analyticsStore = useAnalyticsStore();
const feedbackStore = useFeedbackStore();
const authStore = useAuthStore();

onMounted(async () => {
  await Promise.all([
    profileStore.fetchProfile(),
    experienceStore.fetchExperiences(),
    analyticsStore.fetchMetrics(),
    feedbackStore.fetchRequests()
  ]);
});

const metrics = computed(() => analyticsStore.metrics);
const averageScore = computed(() => metrics.value?.averageScore || 0);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', { year: 'numeric', month: 'short', day: 'numeric' });
};

const profile = computed(() => profileStore.profile);
const experiences = computed(() => experienceStore.sortedByDate);
const requests = computed(() => feedbackStore.requests);
const loading = computed(() => profileStore.loading || experienceStore.loading || analyticsStore.loading || feedbackStore.loading);

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

const certifiedRefs = computed(() => requests.value.filter(r => r.finished && r.visible).length);
const finishedRequests = computed(() => requests.value.filter(r => r.finished).length);
const pendingRequests = computed(() => requests.value.filter(r => !r.finished).length);

const recentRequests = computed(() => {
  return [...requests.value]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 4);
});

const copyProfileLink = () => {
  const url = `${window.location.origin}/u/${authStore.user?.id}`;
  navigator.clipboard.writeText(url).then(() => {
    toast.success('¡Enlace copiado!', {
      description: 'El enlace a tu perfil público se ha copiado al portapapeles.',
    });
  }).catch(err => {
    console.error('Error copying link:', err);
    toast.error('No se pudo copiar el enlace.');
  });
};

const getTrustLabel = (score) => {
  if (score >= 80) return 'Excelente';
  if (score >= 50) return 'Alta';
  if (score >= 30) return 'Media';
  return 'Básica';
};

const exportToPDF = () => {
  if (isExporting.value || !profile.value) return;

  isExporting.value = true;
  isGeneratingPDF.value = true;

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
    
    <!-- Loading State with Shimmer Skeletons -->
    <div v-if="loading && (!profile || experiences.length === 0)" class="max-w-5xl mx-auto px-4 py-8 space-y-12 animate-pulse">
      <div class="h-44 bg-zinc-200/50 dark:bg-white/[0.02] border border-zinc-200/50 dark:border-white/5 rounded-3xl p-8 shadow-sm">
        <div class="space-y-4 max-w-3xl">
          <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/6"></div>
          <div class="h-8 bg-zinc-200 dark:bg-zinc-800 rounded w-1/2"></div>
          <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-3/4"></div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        <div class="lg:col-span-7 space-y-6">
          <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3 mb-6"></div>
          <div class="space-y-6">
            <div v-for="i in 3" :key="i" class="bg-zinc-200/30 dark:bg-white/[0.01] border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 space-y-3">
              <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3"></div>
              <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-full"></div>
            </div>
          </div>
        </div>
        <div class="lg:col-span-5 bg-zinc-200/40 dark:bg-white/[0.02] border border-zinc-200/50 dark:border-white/5 rounded-3xl p-8 space-y-6">
          <div class="aspect-square bg-zinc-200 dark:bg-zinc-800/50 rounded-2xl"></div>
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
      <div v-else class="max-w-6xl mx-auto px-4 py-8 space-y-8 animate-in fade-in duration-500">
        
        <!-- Welcome Hero Banner -->
        <div class="relative overflow-hidden bg-gradient-to-tr from-primary/10 via-primary/[0.03] to-transparent dark:from-primary/20 dark:via-primary/[0.05] border border-zinc-200 dark:border-white/5 rounded-3xl p-8 shadow-sm backdrop-blur-md flex flex-col md:flex-row items-center justify-between gap-6">
          <div class="absolute top-0 right-0 w-72 h-72 bg-primary/10 dark:bg-primary/5 rounded-full blur-3xl pointer-events-none"></div>
          
          <div class="flex items-center gap-5 text-center md:text-left flex-col md:flex-row">
            <div class="w-20 h-20 rounded-full overflow-hidden bg-zinc-100 dark:bg-zinc-800 border-2 border-primary/30 flex items-center justify-center flex-shrink-0">
              <img v-if="profile?.photoUrl" :src="profile.photoUrl" alt="Avatar" class="w-full h-full object-cover" />
              <UserIcon v-else class="w-10 h-10 text-zinc-400" />
            </div>
            <div class="space-y-1">
              <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white font-heading">
                ¡Hola de nuevo, <span class="text-primary">{{ profile?.name }}</span>!
              </h1>
              <p class="text-zinc-500 dark:text-zinc-400 text-sm">
                Tu perfil profesional está activo. Gestionalo y solicita feedback para seguir acumulando referencias.
              </p>
            </div>
          </div>

          <div class="flex items-center gap-3 w-full md:w-auto justify-center">
            <button 
              @click="router.push('/profile')"
              class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-white dark:bg-white/5 border border-zinc-200 dark:border-white/5 text-zinc-700 dark:text-zinc-300 hover:bg-zinc-50 dark:hover:bg-white/10 transition-all duration-200 text-sm font-semibold"
            >
              Ver Mi Perfil
            </button>
            <Button 
              @click="router.push('/feedback/new')"
              class="bg-primary hover:bg-primary/95 text-white flex items-center gap-1.5 px-4 rounded-xl font-semibold shadow-lg shadow-primary/20"
            >
              <Plus class="w-4 h-4" />
              Pedir Feedback
            </Button>
          </div>
        </div>

        <!-- 2 Column Workspace Grid -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          
          <!-- Left Column: Metrics and Actions -->
          <div class="lg:col-span-7 space-y-8">
            
            <!-- Statistics Cards Grid -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              
              <!-- Stat 1: Soft Skill Score -->
              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-32">
                <div class="flex justify-between items-start">
                  <span class="text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">Promedio Habilidades</span>
                  <div class="p-1.5 bg-primary/10 rounded-lg text-primary">
                    <Award class="w-4.5 h-4.5" />
                  </div>
                </div>
                <div class="mt-2">
                  <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ averageScore.toFixed(1) }}</span>
                  <span class="text-xs text-zinc-500 dark:text-zinc-400 font-semibold ml-1">/ 5.0</span>
                </div>
              </div>

              <!-- Stat 2: Certified Refs -->
              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-32">
                <div class="flex justify-between items-start">
                  <span class="text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">Referencias Certificadas</span>
                  <div class="p-1.5 bg-emerald-500/10 rounded-lg text-emerald-500">
                    <ShieldCheck class="w-4.5 h-4.5" />
                  </div>
                </div>
                <div class="mt-2">
                  <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ certifiedRefs }}</span>
                  <span class="text-xs text-zinc-500 dark:text-zinc-400 font-semibold ml-1.5">en tu perfil</span>
                </div>
              </div>

              <!-- Stat 3: Pending Requests -->
              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-32">
                <div class="flex justify-between items-start">
                  <span class="text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">Solicitudes Pendientes</span>
                  <div class="p-1.5 bg-amber-500/10 rounded-lg text-amber-500">
                    <Clock class="w-4.5 h-4.5" />
                  </div>
                </div>
                <div class="mt-2">
                  <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ pendingRequests }}</span>
                  <span class="text-xs text-zinc-500 dark:text-zinc-400 font-semibold ml-1.5">esperando</span>
                </div>
              </div>

            </div>

            <!-- Quick Actions Panel -->
            <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 shadow-sm">
              <h2 class="text-lg font-bold text-zinc-900 dark:text-white uppercase tracking-wider mb-4 font-heading">
                Acciones Rápidas
              </h2>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <button 
                  @click="copyProfileLink"
                  class="flex items-center justify-between p-4 rounded-xl bg-zinc-50 dark:bg-white/[0.02] hover:bg-zinc-100 dark:hover:bg-white/5 border border-zinc-200/50 dark:border-white/5 text-left group transition-all duration-300"
                >
                  <div class="space-y-0.5">
                    <span class="text-sm font-bold text-zinc-900 dark:text-white block">Compartir mi Perfil</span>
                    <span class="text-xs text-zinc-500 dark:text-zinc-400">Copia tu enlace público al portapapeles</span>
                  </div>
                  <Copy class="w-5 h-5 text-zinc-400 group-hover:text-primary transition-colors" />
                </button>

                <button 
                  @click="router.push(`/u/${authStore.user?.id}`)"
                  class="flex items-center justify-between p-4 rounded-xl bg-zinc-50 dark:bg-white/[0.02] hover:bg-zinc-100 dark:hover:bg-white/5 border border-zinc-200/50 dark:border-white/5 text-left group transition-all duration-300"
                >
                  <div class="space-y-0.5">
                    <span class="text-sm font-bold text-zinc-900 dark:text-white block">Ver Perfil Público</span>
                    <span class="text-xs text-zinc-500 dark:text-zinc-400">Visualiza tu portafolio como un reclutador</span>
                  </div>
                  <ExternalLink class="w-5 h-5 text-zinc-400 group-hover:text-primary transition-colors" />
                </button>
              </div>
            </div>

            <!-- Recent Requests Checklist -->
            <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 shadow-sm space-y-4">
              <div class="flex items-center justify-between">
                <h2 class="text-lg font-bold text-zinc-900 dark:text-white uppercase tracking-wider font-heading">
                  Solicitudes Recientes
                </h2>
                <button 
                  @click="router.push('/feedback')"
                  class="text-xs text-primary font-bold hover:underline flex items-center gap-1.5"
                >
                  Ver Todas
                  <ArrowUpRight class="w-3.5 h-3.5" />
                </button>
              </div>

              <div v-if="recentRequests.length > 0" class="divide-y divide-zinc-200/50 dark:divide-white/5">
                <div v-for="req in recentRequests" :key="req.id" class="py-3 flex items-center justify-between first:pt-0 last:pb-0">
                  <div class="space-y-1">
                    <span class="text-sm font-bold text-zinc-900 dark:text-white block">
                      {{ req.targetName }} {{ req.targetSurname || '' }}
                    </span>
                    <span class="text-xs text-zinc-500 dark:text-zinc-400 block break-all">
                      {{ req.targetEmail }} · {{ formatDate(req.createdAt) }}
                    </span>
                  </div>
                  <div>
                    <span 
                      v-if="req.finished" 
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
                    >
                      Completada ({{ getTrustLabel(req.trustScore) }})
                    </span>
                    <span 
                      v-else 
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-amber-500/10 text-amber-600 dark:text-amber-400"
                    >
                      Pendiente
                    </span>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-zinc-500 dark:text-zinc-400 text-sm italic">
                No has enviado ninguna solicitud de feedback todavía.
              </div>
            </div>

          </div>

          <!-- Right Column: Skills Radar Chart -->
          <div class="lg:col-span-5 bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 sm:p-8 backdrop-blur-md relative overflow-hidden shadow-sm">
            <div class="absolute -top-12 -left-12 w-40 h-40 bg-primary/10 dark:bg-primary/5 rounded-full blur-2xl pointer-events-none"></div>
            
            <div class="space-y-4 mb-6">
              <div class="flex items-center gap-2">
                <span class="p-1.5 rounded-lg bg-primary/10 text-primary">
                  <ShieldCheck class="w-5 h-5" />
                </span>
                <span class="text-sm font-bold tracking-wide uppercase text-zinc-500 dark:text-zinc-400">Mi Caché Profesional</span>
              </div>
              <p class="text-xs text-zinc-500 dark:text-zinc-400 leading-relaxed">
                Este radar interactivo resume tus habilidades blandas. Los datos proceden del feedback certificado de tus compañeros.
              </p>
            </div>

            <!-- Radar chart of active user -->
            <div class="w-full relative aspect-square flex items-center justify-center p-2 rounded-2xl bg-zinc-50/50 dark:bg-zinc-900/30 border border-zinc-100 dark:border-white/[0.02]">
              <SkillsRadarChart v-if="metrics" :metrics="metrics" />
              <div v-else class="text-center py-20 text-zinc-500 text-sm italic">
                Aún no tienes valoraciones. Envía solicitudes para revelar tu radar de habilidades.
              </div>
            </div>

            <div class="mt-6 flex items-start gap-2.5 bg-primary/5 dark:bg-primary/[0.03] border border-primary/10 p-3 rounded-xl" v-if="metrics">
              <HelpCircle class="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
              <p class="text-xs text-zinc-600 dark:text-zinc-400 leading-relaxed">
                <strong>¿Sabías qué?</strong> Puedes descargar un informe certificado completo en PDF con tu historial completo desde el apartado **Mi Perfil**.
              </p>
            </div>
          </div>

        </div>

      </div>
    </template>

    <!-- Premium Fullscreen Loading Overlay (PDF Export template hidden, kept for backward compat if trigger is called) -->
    <div v-if="isGeneratingPDF" class="fixed inset-0 z-[10000] bg-zinc-950/90 backdrop-blur-md flex flex-col items-center justify-center space-y-6 text-white select-none pointer-events-auto">
      <div class="relative w-20 h-20 flex items-center justify-center">
        <div class="absolute inset-0 rounded-full border-4 border-primary/10 border-t-primary animate-spin"></div>
        <div class="w-10 h-10 rounded-full bg-orange-500/20 animate-pulse flex items-center justify-center text-primary">
          <Award class="w-6 h-6" />
        </div>
      </div>
      <div class="text-center space-y-2">
        <h3 class="text-xl font-bold tracking-tight bg-gradient-to-r from-primary to-orange-500 bg-clip-text text-transparent">
          Generando Informe Personal...
        </h3>
        <p class="text-xs text-zinc-400 max-w-xs leading-relaxed">
          MiCaché está compilando tu informe personal con habilidades y radar chart verificado.
        </p>
      </div>
    </div>

    <!-- Hidden PDF template container for backward compatibility -->
    <div v-if="isGeneratingPDF" class="absolute left-0 top-0 z-[9999] bg-white">
      <div id="pdf-template" class="p-10 bg-white text-zinc-900 font-sans" style="width: 794px; min-height: 1120px; box-sizing: border-box;">
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
        <div class="grid grid-cols-2 gap-8">
          <div>
            <h2 class="text-lg font-bold text-zinc-800 mb-4 uppercase border-b pb-2" style="border-color: #e4e4e7;">Habilidades Blandas</h2>
            <div class="w-full h-64">
              <SkillsRadarChart v-if="metrics" :metrics="metrics" />
            </div>
          </div>
          <div>
            <h2 class="text-lg font-bold text-zinc-800 mb-4 uppercase border-b pb-2" style="border-color: #e4e4e7;">Experiencia</h2>
            <div class="space-y-4">
              <div v-for="exp in experiences" :key="exp.id">
                <h3 class="text-md font-bold text-zinc-900">{{ exp.position }}</h3>
                <p class="text-sm font-semibold" style="color: #f29727;">{{ exp.companyName }}</p>
                <p class="text-xs text-zinc-500">{{ formatDate(exp.startDate) }} - {{ formatDate(exp.finishDate) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
