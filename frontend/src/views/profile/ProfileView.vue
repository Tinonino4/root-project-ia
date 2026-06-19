<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile.store';
import { useAuthStore } from '@/stores/auth.store';
import client from '@/api/client';
import SkillsRadarChart from '@/components/dashboard/SkillsRadarChart.vue';
import { Button } from '@/components/ui/button';
import { 
  Mail, 
  MapPin, 
  Phone, 
  Briefcase, 
  GraduationCap, 
  Edit, 
  User as UserIcon, 
  Calendar, 
  Award, 
  Download, 
  ShieldCheck, 
  Plus, 
  Trash2, 
  Eye,
  ExternalLink,
  Copy,
  Star,
  AlertTriangle
} from 'lucide-vue-next';
import html2pdf from 'html2pdf.js';
import { toast } from 'vue-sonner';


const router = useRouter();
const profileStore = useProfileStore();
const authStore = useAuthStore();

const publicProfileData = ref(null);
const loading = ref(true);
const error = ref(null);
const isExporting = ref(false);
const isGeneratingPDF = ref(false);

const expandedExperiences = ref({});

const toggleExperienceBreakdown = (expId) => {
  expandedExperiences.value[expId] = !expandedExperiences.value[expId];
};

const getMetricsForExperience = (expId) => {
  if (!publicProfileData.value || !publicProfileData.value.experienceMetrics) return null;
  return publicProfileData.value.experienceMetrics.find(m => m.experienceId === expId) || null;
};

const getTrustLevelLabel = (score) => {
  if (score >= 80) return 'Excelente';
  if (score >= 50) return 'Alta';
  if (score >= 30) return 'Media';
  return 'Básica';
};

const relationshipLabels = {
  DIRECT_MANAGER: 'Jefe directo',
  COLLEAGUE: 'Compañero/a',
  SUBORDINATE: 'Subordinado/a',
  CLIENT: 'Cliente',
  OTHER: 'Otro/a'
};

const categoryLabels = {
  TEAMWORK: 'Trabajo en equipo',
  SELF_CONFIDENCE: 'Autoconfianza',
  PROACTIVITY: 'Proactividad',
  INTEGRITY: 'Integridad',
  FLEXIBILITY: 'Flexibilidad'
};

const privateProfile = computed(() => profileStore.profile);

onMounted(async () => {
  await loadAllData();
});

const loadAllData = async () => {
  try {
    loading.value = true;
    error.value = null;
    
    // 1. Cargar el perfil privado (con email, teléfono, etc.)
    await profileStore.fetchProfile();
    
    // 2. Cargar datos del perfil público (radar, métricas de experiencia, etc.)
    const userId = authStore.user?.id;
    if (userId) {
      const response = await client.get(`/public/profile/${userId}`);
      publicProfileData.value = response.data;
    }
  } catch (err) {
    console.error('Error loading profile view data:', err);
    error.value = 'Error al cargar tu información de perfil. Por favor, reintenta.';
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', { month: 'short', year: 'numeric' });
};

const copyProfileLink = () => {
  const slugOrId = privateProfile.value?.username || authStore.user?.id;
  const url = `${window.location.origin}/u/${slugOrId}`;
  navigator.clipboard.writeText(url).then(() => {
    toast.success('¡Enlace copiado!', {
      description: 'El enlace a tu perfil público se ha copiado al portapapeles.',
    });
  }).catch(err => {
    console.error('Error copying link:', err);
    toast.error('No se pudo copiar el enlace.');
  });
};

const showDeleteModal = ref(false);
const experienceToDelete = ref(null);

const confirmDelete = (expId) => {
  experienceToDelete.value = expId;
  showDeleteModal.value = true;
};

const executeDelete = async () => {
  if (experienceToDelete.value) {
    try {
      await client.delete(`/professional/experiences/${experienceToDelete.value}`);
      toast.success('Experiencia eliminada con éxito.');
      await loadAllData();
    } catch (err) {
      console.error('Error deleting experience:', err);
      toast.error('Error al eliminar la experiencia.');
    } finally {
      showDeleteModal.value = false;
      experienceToDelete.value = null;
    }
  }
};

const cancelDelete = () => {
  showDeleteModal.value = false;
  experienceToDelete.value = null;
};

const exportPDF = () => {
  if (isExporting.value || !publicProfileData.value) return;
  
  isExporting.value = true;
  isGeneratingPDF.value = true;

  setTimeout(() => {
    const element = document.getElementById('pdf-profile-template');
    if (!element) {
      console.error('El elemento PDF no se encontró en el DOM.');
      isExporting.value = false;
      isGeneratingPDF.value = false;
      return;
    }
    
    const opt = {
      margin:       [10, 10, 15, 10],
      filename:     `informe_micache_${publicProfileData.value.name}_${publicProfileData.value.surname}.pdf`,
      image:        { type: 'jpeg', quality: 0.98 },
      html2canvas:  { 
        scale: 2.0, 
        useCORS: true, 
        logging: false,
        scrollY: 0,
        scrollX: 0,
        windowWidth: 700
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

const topSkill = computed(() => {
  if (!publicProfileData.value || !publicProfileData.value.skills) return null;
  const skills = publicProfileData.value.skills;
  const candidates = [
    { key: 'teamwork', label: 'Trabajo en equipo' },
    { key: 'proactivity', label: 'Proactividad' },
    { key: 'integrity', label: 'Integridad' },
    { key: 'selfConfidence', label: 'Autoconfianza' },
    { key: 'flexibility', label: 'Flexibilidad' }
  ];
  
  let best = candidates[0];
  let maxVal = skills[best.key] || 0;
  
  for (let i = 1; i < candidates.length; i++) {
    const val = skills[candidates[i].key] || 0;
    if (val > maxVal) {
      maxVal = val;
      best = candidates[i];
    }
  }
  
  return maxVal > 0 ? best : null;
});
</script>

<template>
  <div class="max-w-6xl mx-auto px-0 sm:px-6 lg:px-8 py-6 space-y-8 min-h-screen">
    
    <!-- Loading State with Shimmer Skeletons -->
    <div v-if="loading" class="space-y-8 animate-pulse">
      <div class="flex items-center justify-between gap-4">
        <div class="w-48 h-10 bg-white/5 border border-white/5 rounded-xl"></div>
        <div class="flex gap-3">
          <div class="w-32 h-10 bg-white/5 border border-white/5 rounded-xl"></div>
          <div class="w-32 h-10 bg-white/5 border border-white/5 rounded-xl"></div>
        </div>
      </div>

      <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-8 shadow-2xl">
        <div class="flex flex-col md:flex-row gap-6 items-center md:items-start">
          <div class="w-32 h-32 rounded-full bg-white/5 flex-shrink-0"></div>
          <div class="flex-1 text-center md:text-left space-y-4 w-full">
            <div class="h-8 bg-white/5 rounded w-1/3 mx-auto md:mx-0"></div>
            <div class="h-5 bg-white/5 rounded w-1/4 mx-auto md:mx-0"></div>
            <div class="space-y-2 pt-2">
              <div class="h-4 bg-white/5 rounded w-full"></div>
              <div class="h-4 bg-white/5 rounded w-5/6 mx-auto md:mx-0"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-6 shadow-xl space-y-6">
          <div class="h-6 bg-white/5 rounded w-1/3"></div>
          <div class="h-80 flex items-center justify-center">
            <div class="w-56 h-56 rounded-full border-4 border-white/5 flex items-center justify-center">
              <div class="w-36 h-36 rounded-full border-4 border-white/5"></div>
            </div>
          </div>
        </div>
        <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-6 shadow-xl space-y-6">
          <div class="h-6 bg-white/5 rounded w-1/3"></div>
          <div class="space-y-6">
            <div v-for="i in 3" :key="i" class="space-y-2">
              <div class="h-5 bg-white/5 rounded w-1/2"></div>
              <div class="h-4 bg-white/5 rounded w-1/3"></div>
              <div class="h-4 bg-white/5 rounded w-full pt-2"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-500/10 border border-red-500/20 text-red-500 p-6 rounded-2xl text-center space-y-4">
      <p class="font-medium">{{ error }}</p>
      <Button variant="outline" @click="loadAllData" class="border-red-500/20 text-red-400 hover:bg-red-500/10">Reintentar</Button>
    </div>

    <!-- Profile Content -->
    <div v-else-if="publicProfileData" class="space-y-8 animate-in fade-in-50 duration-500">
      


      <!-- Hero Section -->
      <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-8 pt-10 sm:pt-8 backdrop-blur-xl shadow-2xl relative group">
        <!-- Edit Profile Button -->
        <button 
          @click="router.push('/profile/edit')"
          class="absolute top-4 right-4 p-2.5 rounded-xl bg-white/5 border border-white/5 text-zinc-400 hover:text-white hover:bg-white/10 transition-all duration-200"
          title="Editar información básica"
        >
          <Edit class="w-4 h-4" />
        </button>

        <div class="flex flex-col md:flex-row gap-6 items-center md:items-start">
          <!-- Avatar -->
          <div class="w-32 h-32 rounded-full bg-[hsl(228,15%,15%)] flex items-center justify-center text-4xl font-bold text-primary border-2 border-white/10 shadow-inner flex-shrink-0">
            <img v-if="publicProfileData.photoUrl" :src="publicProfileData.photoUrl" alt="Avatar" class="w-full h-full rounded-full object-cover" />
            <span v-else>{{ publicProfileData.name?.charAt(0) }}{{ publicProfileData.surname?.charAt(0) }}</span>
          </div>

          <!-- Info -->
          <div class="flex-1 text-center md:text-left space-y-2">
            <h1 class="text-3xl font-bold text-white tracking-tight">{{ publicProfileData.name }} {{ publicProfileData.surname }}</h1>
            <p class="text-lg text-primary font-semibold flex flex-wrap items-center justify-center md:justify-start gap-3">
              <span>{{ publicProfileData.jobTitle || 'Profesional' }}</span>
              <span v-if="publicProfileData.totalReferencesCount > 0" class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 shadow-lg select-none">
                <ShieldCheck class="w-3.5 h-3.5 mr-1 text-emerald-400" />
                {{ publicProfileData.totalReferencesCount }} {{ publicProfileData.totalReferencesCount === 1 ? 'Referencia Certificada' : 'Referencias Certificadas' }}
              </span>
            </p>
            <p class="text-[hsl(220,10%,75%)] mt-4 max-w-2xl text-balance">
              {{ publicProfileData.aboutMe || 'Sin descripción personal aún. Edita tu perfil para añadir una biografía profesional.' }}
            </p>

            <!-- Actions Dock inside the Hero Card -->
            <div class="pt-4 flex flex-wrap gap-2.5 justify-center md:justify-start">
              <button 
                @click="copyProfileLink"
                class="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 text-zinc-300 hover:text-white transition-all text-xs font-semibold whitespace-nowrap"
              >
                <Copy class="w-3.5 h-3.5" />
                Compartir
              </button>
              
              <button 
                @click="router.push(`/u/${privateProfile?.username || authStore.user?.id}`)"
                class="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 text-zinc-300 hover:text-white transition-all text-xs font-semibold whitespace-nowrap"
              >
                <ExternalLink class="w-3.5 h-3.5" />
                Vista Pública
              </button>

              <button 
                @click="exportPDF"
                :disabled="isExporting"
                class="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-primary hover:bg-primary/90 text-white shadow-lg shadow-primary/25 transition-all text-xs font-semibold disabled:opacity-50 whitespace-nowrap"
              >
                <Download class="w-3.5 h-3.5" :class="{'animate-bounce': isExporting}" />
                {{ isExporting ? 'PDF...' : 'Descargar PDF' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Grid: Skills/Contact and Experience -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        <!-- Left Column: Skills & Contact Details -->
        <div class="lg:col-span-4 space-y-6 lg:sticky lg:top-6 self-start">
          
          <!-- Habilidades Blandas (Radar Chart) -->
          <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl">
            <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
              <Award class="w-5 h-5 text-primary" />
              Habilidades Blandas
            </h2>
            <div v-if="publicProfileData.skills" class="h-80 flex items-center justify-center">
              <SkillsRadarChart :metrics="publicProfileData.skills" />
            </div>
            <div v-else class="text-center py-20 text-zinc-500 text-sm italic">
              No hay métricas de habilidades disponibles. Solicita feedback para generar tu gráfico.
            </div>
          </div>

          <!-- Certification Summary Card -->
          <div v-if="publicProfileData.skills" class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl space-y-5">
            <h3 class="text-lg font-bold text-white border-b border-white/5 pb-3 flex items-center gap-2">
              <ShieldCheck class="w-5 h-5 text-emerald-400" />
              Resumen de Certificación
            </h3>
            <div class="space-y-4">
              <!-- Global Average -->
              <div class="flex items-center justify-between">
                <span class="text-zinc-400 text-sm">Media Global</span>
                <div class="flex items-center text-amber-400 gap-1.5">
                  <span class="text-sm font-bold text-white">{{ publicProfileData.skills.averageScore.toFixed(1) }}</span>
                  <div class="flex items-center">
                    <svg v-for="star in 5" :key="star" class="w-3.5 h-3.5" :class="star <= Math.round(publicProfileData.skills.averageScore) ? 'fill-current' : 'text-zinc-700'" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                </div>
              </div>

              <!-- References Count -->
              <div class="flex items-center justify-between">
                <span class="text-zinc-400 text-sm">Referencias</span>
                <span class="text-sm font-bold text-white flex items-center gap-1">
                  <ShieldCheck class="w-4 h-4 text-emerald-400" />
                  {{ publicProfileData.totalReferencesCount }} validadas
                </span>
              </div>

              <!-- Top Skill -->
              <div class="flex items-center justify-between" v-if="topSkill">
                <span class="text-zinc-400 text-sm">Habilidad Destacada</span>
                <span class="text-xs font-bold px-2.5 py-1 rounded-lg bg-primary/10 text-primary border border-primary/20">
                  {{ topSkill.label }}
                </span>
              </div>
            </div>
          </div>

          <!-- Información de Contacto -->
          <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl relative group">
            <button 
              @click="router.push('/profile/edit')"
              class="absolute top-4 right-4 p-2 rounded-lg bg-white/5 border border-white/5 text-zinc-400 hover:text-white hover:bg-white/10 transition-all duration-200"
              title="Editar datos de contacto"
            >
              <Edit class="w-4.5 h-4.5" />
            </button>

            <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
              <UserIcon class="w-5 h-5 text-primary" />
              Información Personal
            </h2>
            
            <div class="space-y-4">
              <div class="flex items-start gap-3 text-zinc-300">
                <Mail class="w-5 h-5 text-zinc-500 mt-0.5 flex-shrink-0" />
                <div class="space-y-0.5">
                  <span class="text-xs text-zinc-500 font-bold uppercase tracking-wider block">Email de Contacto</span>
                  <span class="text-sm break-all font-medium">{{ privateProfile?.contactEmail || 'No especificado' }}</span>
                </div>
              </div>

              <div class="flex items-start gap-3 text-zinc-300">
                <Phone class="w-5 h-5 text-zinc-500 mt-0.5 flex-shrink-0" />
                <div class="space-y-0.5">
                  <span class="text-xs text-zinc-500 font-bold uppercase tracking-wider block">Teléfono</span>
                  <span class="text-sm font-medium">{{ privateProfile?.phoneNumber || 'No especificado' }}</span>
                </div>
              </div>

              <div class="flex items-start gap-3 text-zinc-300">
                <MapPin class="w-5 h-5 text-zinc-500 mt-0.5 flex-shrink-0" />
                <div class="space-y-0.5">
                  <span class="text-xs text-zinc-500 font-bold uppercase tracking-wider block">Ubicación</span>
                  <span class="text-sm font-medium">
                    {{ privateProfile?.city || 'No especificada' }} 
                    <span class="text-zinc-500" v-if="privateProfile?.zipcode">({{ privateProfile.zipcode }})</span>
                  </span>
                </div>
              </div>

              <div class="flex items-start gap-3 text-zinc-300" v-if="privateProfile?.education">
                <GraduationCap class="w-5 h-5 text-zinc-500 mt-0.5 flex-shrink-0" />
                <div class="space-y-0.5">
                  <span class="text-xs text-zinc-500 font-bold uppercase tracking-wider block">Formación</span>
                  <span class="text-sm font-medium">{{ privateProfile.education }}</span>
                </div>
              </div>

              <div class="flex items-start gap-3 text-zinc-300" v-if="privateProfile?.birthday">
                <Calendar class="w-5 h-5 text-zinc-500 mt-0.5 flex-shrink-0" />
                <div class="space-y-0.5">
                  <span class="text-xs text-zinc-500 font-bold uppercase tracking-wider block">Fecha de Nacimiento</span>
                  <span class="text-sm font-medium">{{ privateProfile.birthday }}</span>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- Right Column: Experience Timeline -->
        <div class="lg:col-span-8">
          <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-bold text-white flex items-center gap-2">
                <Briefcase class="w-5 h-5 text-primary" />
                Experiencia Profesional
              </h2>
              <Button 
                @click="router.push('/experiences/new')"
                size="sm" 
                class="bg-primary hover:bg-primary/95 text-white flex items-center gap-1 px-3"
              >
                <Plus class="w-4 h-4" />
                Añadir
              </Button>
            </div>

            <!-- Experience Timeline -->
            <div v-if="publicProfileData.experiences && publicProfileData.experiences.length > 0" class="space-y-6 relative before:absolute before:inset-y-0 before:left-2.5 before:w-px before:bg-white/5">
              <div v-for="exp in publicProfileData.experiences" :key="exp.id" class="relative pl-6 sm:pl-8 group">
                <!-- Timeline dot -->
                <div class="absolute left-2.5 top-2.5 w-3 h-3 rounded-full bg-primary -translate-x-1/2 group-hover:scale-125 transition-transform duration-200"></div>
                
                <!-- Action Controls (Floating top right) -->
                <div class="absolute right-0 top-0 flex items-center gap-1 opacity-80 group-hover:opacity-100 transition-opacity">
                  <button 
                    @click="router.push(`/feedback/new?experienceId=${exp.id}`)"
                    class="p-1.5 text-zinc-400 hover:text-primary hover:bg-white/5 rounded-lg transition-colors"
                    title="Solicitar Feedback"
                  >
                    <Star class="w-4 h-4 text-amber-500 fill-current" />
                  </button>
                  <button 
                    @click="router.push(`/experiences/${exp.id}/edit`)"
                    class="p-1.5 text-zinc-400 hover:text-white hover:bg-white/5 rounded-lg transition-colors"
                    title="Editar Experiencia"
                  >
                    <Edit class="w-4 h-4" />
                  </button>
                  <button 
                    @click="confirmDelete(exp.id)"
                    class="p-1.5 text-zinc-400 hover:text-red-500 hover:bg-white/5 rounded-lg transition-colors"
                    title="Eliminar Experiencia"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>

                <div class="space-y-1">
                  <h3 class="text-lg font-semibold text-white group-hover:text-primary transition-colors duration-200 pr-20 sm:pr-24">{{ exp.position }}</h3>
                  <p class="text-[hsl(220,10%,70%)] text-sm font-medium pr-20 sm:pr-24">{{ exp.companyName }} <span v-if="exp.department">· {{ exp.department }}</span></p>
                  <div class="flex items-center gap-2 text-xs text-[hsl(220,10%,50%)]">
                    <Calendar class="w-3.5 h-3.5" />
                    <span>{{ formatDate(exp.startDate) }} - {{ exp.finishDate ? formatDate(exp.finishDate) : 'Presente' }}</span>
                  </div>
                  <p class="text-sm text-[hsl(220,10%,60%)] mt-2 leading-relaxed">{{ exp.functions }}</p>

                  <!-- Experience Rating Breakdown (Showable/Hideable) -->
                  <div v-if="getMetricsForExperience(exp.id)" class="mt-4 p-4 rounded-xl bg-white/[0.01] border border-white/5 space-y-4">
                    <div class="flex flex-wrap items-center justify-between gap-3">
                      <!-- Rating and Reference Count -->
                      <div class="flex items-center gap-2">
                        <div class="flex items-center text-amber-400">
                          <span class="text-sm font-bold text-white mr-1.5">
                            {{ getMetricsForExperience(exp.id).averageScore.toFixed(1) }}
                          </span>
                          <svg v-for="star in 5" :key="star" class="w-3.5 h-3.5" :class="star <= Math.round(getMetricsForExperience(exp.id).averageScore) ? 'fill-current' : 'text-zinc-700'" viewBox="0 0 20 20" fill="currentColor">
                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                          </svg>
                        </div>
                        <span class="text-xs text-[hsl(220,10%,55%)]">
                          ({{ getMetricsForExperience(exp.id).referencesCount }} {{ getMetricsForExperience(exp.id).referencesCount === 1 ? 'referencia' : 'referencias' }})
                        </span>
                      </div>

                      <!-- Trust Level Badge -->
                      <div class="flex items-center gap-1.5">
                        <span class="inline-flex items-center px-2 py-0.5 rounded-md text-[10px] font-bold"
                          :class="{
                            'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20': getMetricsForExperience(exp.id).averageTrustScore >= 80,
                            'bg-amber-500/10 text-amber-400 border border-amber-500/20': getMetricsForExperience(exp.id).averageTrustScore >= 50 && getMetricsForExperience(exp.id).averageTrustScore < 80,
                            'bg-orange-500/10 text-orange-400 border border-orange-500/20': getMetricsForExperience(exp.id).averageTrustScore >= 30 && getMetricsForExperience(exp.id).averageTrustScore < 50,
                            'bg-rose-500/10 text-rose-400 border border-rose-500/20': getMetricsForExperience(exp.id).averageTrustScore < 30
                          }"
                        >
                          <ShieldCheck class="w-3 h-3 mr-1" />
                          Confianza: {{ getTrustLevelLabel(getMetricsForExperience(exp.id).averageTrustScore) }}
                        </span>
                      </div>

                      <!-- Action Toggle -->
                      <button 
                        @click="toggleExperienceBreakdown(exp.id)"
                        class="text-xs text-primary hover:text-primary-hover font-semibold flex items-center gap-1 transition-all duration-200"
                      >
                        <span>{{ expandedExperiences[exp.id] ? 'Ocultar detalles' : 'Ver detalles' }}</span>
                        <Eye class="w-3.5 h-3.5" />
                      </button>
                    </div>

                    <!-- Details Section (Accordion) -->
                    <div v-if="expandedExperiences[exp.id]" class="mt-4 pt-4 border-t border-white/5 space-y-4 animate-in fade-in slide-in-from-top-2 duration-300">
                      <!-- Soft Skills breakdown bars -->
                      <div class="space-y-3">
                        <h4 class="text-[10px] font-bold text-zinc-400 uppercase tracking-widest">Habilidades Blandas en este Rol</h4>
                        <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-3">
                          <div v-for="(val, skill) in getMetricsForExperience(exp.id).categoryAverages" :key="skill" class="space-y-1">
                            <div class="flex justify-between text-xs font-medium">
                              <span class="text-zinc-400">{{ categoryLabels[skill] || skill }}</span>
                              <span class="text-white font-bold">{{ val.toFixed(1) }}/5.0</span>
                            </div>
                            <div class="h-1.5 w-full bg-zinc-800 rounded-full overflow-hidden">
                              <div 
                                class="h-full bg-gradient-to-r from-primary to-orange-500 rounded-full transition-all duration-500" 
                                :style="{ width: (val * 20) + '%' }"
                              ></div>
                            </div>
                          </div>
                        </div>
                      </div>

                      <!-- Evaluators roles distribution -->
                      <div v-if="Object.keys(getMetricsForExperience(exp.id).relationshipCounts).length > 0" class="pt-3 border-t border-white/5">
                        <h4 class="text-[10px] font-bold text-zinc-400 uppercase tracking-widest mb-2.5">Distribución de Evaluadores</h4>
                        <div class="flex flex-wrap gap-2">
                          <span 
                            v-for="(count, role) in getMetricsForExperience(exp.id).relationshipCounts" 
                            :key="role" 
                            class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs bg-white/[0.02] border border-white/5 text-zinc-300 font-medium select-none"
                          >
                            <span class="w-1.5 h-1.5 rounded-full bg-primary mr-2 shadow-sm"></span>
                            {{ relationshipLabels[role] || role }}: <strong class="text-white ml-1 font-bold">{{ count }}</strong>
                          </span>
                        </div>
                      </div>

                      <!-- Testimonios / Comentarios (Testimonials) -->
                      <div v-if="getMetricsForExperience(exp.id).testimonials && getMetricsForExperience(exp.id).testimonials.length > 0" class="pt-4 border-t border-white/5 space-y-3">
                        <h4 class="text-[10px] font-bold text-zinc-400 uppercase tracking-widest">Opiniones y Comentarios Recibidos (Privado)</h4>
                        <div class="space-y-3">
                          <div 
                            v-for="t in getMetricsForExperience(exp.id).testimonials" 
                            :key="t.createdAt"
                            class="p-4 rounded-xl bg-white/[0.02] border border-white/5 space-y-2"
                          >
                            <div class="flex items-center justify-between text-xs">
                              <span class="font-bold text-zinc-300">{{ t.evaluatorName }} {{ t.evaluatorSurname }} <span class="text-zinc-500 font-medium">({{ relationshipLabels[t.relationshipCode] || t.relationshipCode }})</span></span>
                              <span class="inline-flex items-center text-[10px] font-semibold text-emerald-400">
                                <ShieldCheck class="w-3.5 h-3.5 mr-1" />
                                Confianza: {{ getTrustLevelLabel(t.trustScore) }} ({{ t.trustScore }}%)
                              </span>
                            </div>
                            <p class="text-xs text-zinc-400 italic leading-relaxed">
                              "{{ t.comment }}"
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-20 text-zinc-500 text-sm italic">
              No has registrado experiencia profesional. Haz clic en "Añadir" para registrar tu primera experiencia.
            </div>
          </div>
        </div>

      </div>

    </div>

    <!-- Premium Fullscreen Loading Overlay for PDF export -->
    <div v-if="isGeneratingPDF" class="fixed inset-0 z-[10000] bg-zinc-950/90 backdrop-blur-md flex flex-col items-center justify-center space-y-6 text-white select-none pointer-events-auto">
      <div class="relative w-20 h-20 flex items-center justify-center">
        <div class="absolute inset-0 rounded-full border-4 border-primary/10 border-t-primary animate-spin"></div>
        <div class="w-10 h-10 rounded-full bg-orange-500/20 animate-pulse flex items-center justify-center text-primary">
          <Award class="w-6 h-6" />
        </div>
      </div>
      <div class="text-center space-y-2">
        <h3 class="text-xl font-bold tracking-tight bg-gradient-to-r from-primary to-orange-500 bg-clip-text text-transparent">
          Generando tu Informe Certificado...
        </h3>
        <p class="text-xs text-zinc-400 max-w-xs leading-relaxed">
          MiCaché está compilando tu portafolio y habilidades blandas en un documento PDF.
        </p>
      </div>
    </div>

    <!-- Premium PDF Template (only rendered when generating PDF, positioned absolute top-left behind the overlay) -->
    <div v-if="isGeneratingPDF" class="absolute left-0 top-0 z-[9999] bg-white" style="width: 700px; min-width: 700px; overflow: visible;">
      <div 
        id="pdf-profile-template" 
        class="bg-white text-zinc-900 font-sans relative overflow-hidden flex flex-col justify-between" 
        style="width: 700px; min-height: 1000px; box-sizing: border-box; padding: 24px;"
      >
        <!-- Tilted Diagonal Corporate Watermark -->
        <div class="absolute inset-0 pointer-events-none select-none z-0 flex flex-col justify-around items-center overflow-hidden">
          <div class="text-6xl font-black uppercase tracking-[0.25em] transform -rotate-45 select-none" style="color: rgba(24, 24, 27, 0.035);">
            Verificado por MiCaché
          </div>
          <div class="text-6xl font-black uppercase tracking-[0.25em] transform -rotate-45 select-none" style="color: rgba(24, 24, 27, 0.035);">
            Verificado por MiCaché
          </div>
        </div>

        <div class="relative z-10 flex-1 flex flex-col justify-between" style="height: 100%;">
          <div>
            <!-- Header Banner -->
            <div class="flex items-center justify-between border-b pb-6" style="border-color: #e4e4e7; margin-bottom: 30px;">
              <div>
                <h2 class="text-2xl font-black tracking-tight" style="color: #f29727; margin: 0; line-height: 1.1;">MiCaché</h2>
                <p class="text-xs font-semibold text-zinc-500 uppercase tracking-widest" style="margin: 4px 0 0 0;">Informe Profesional Certificado</p>
              </div>
              <div class="text-right">
                <div class="inline-flex items-center px-3 py-1 rounded-full bg-green-50 border border-green-200 text-[10px] font-bold text-green-700 uppercase tracking-wider">
                  ✓ Verificado
                </div>
                <p class="text-[10px] text-zinc-400" style="margin: 6px 0 0 0;">Emitido: {{ new Date().toLocaleDateString('es-ES') }}</p>
              </div>
            </div>

            <!-- Candidate Info block -->
            <div class="flex items-start bg-zinc-50 border rounded-2xl p-6" style="border-color: #e4e4e7; margin-bottom: 30px;">
              <!-- Avatar (Image or Initials) -->
              <div 
                class="w-20 h-20 rounded-full flex items-center justify-center font-bold text-2xl flex-shrink-0 shadow-inner overflow-hidden"
                style="background-color: rgba(242, 151, 39, 0.1); color: #f29727; border: 1px solid rgba(242, 151, 39, 0.2); margin-right: 24px;"
              >
                <img v-if="publicProfileData.photoUrl" :src="publicProfileData.photoUrl" alt="Avatar" class="w-full h-full object-cover" crossorigin="anonymous" />
                <span v-else>{{ publicProfileData.name?.charAt(0) }}{{ publicProfileData.surname?.charAt(0) }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <h3 class="text-xl font-extrabold text-zinc-950" style="margin: 0 0 4px 0; line-height: 1.2;">{{ publicProfileData.name }} {{ publicProfileData.surname }}</h3>
                <p class="text-sm font-bold" style="color: #f29727; margin: 0 0 8px 0; line-height: 1.2;">{{ publicProfileData.jobTitle }}</p>
                <p class="text-xs text-zinc-600 leading-relaxed" style="margin: 0; line-height: 1.5; word-wrap: break-word;">{{ publicProfileData.aboutMe || 'Sin descripción personal.' }}</p>
              </div>
            </div>

            <!-- Soft Skills Metrics Section -->
            <div style="margin-bottom: 30px;">
              <h3 class="text-sm font-black uppercase tracking-wider text-zinc-400 border-b pb-2" style="border-color: #e4e4e7; display: flex; align-items: center; margin: 0 0 16px 0;">
                <Award class="w-4 h-4" style="color: #f29727; margin-right: 8px; display: inline-block; vertical-align: middle;" />
                <span style="display: inline-block; vertical-align: middle;">Soft-Skills y Habilidades Blandas</span>
              </h3>
              
              <div v-if="publicProfileData.skills" class="grid grid-cols-2 gap-x-8 gap-y-4">
                <!-- Teamwork -->
                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Trabajo en equipo</span>
                    <span style="color: #f29727;">{{ (publicProfileData.skills.teamwork || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((publicProfileData.skills.teamwork || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <!-- Proactivity -->
                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Proactividad</span>
                    <span style="color: #f29727;">{{ (publicProfileData.skills.proactivity || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((publicProfileData.skills.proactivity || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <!-- Integrity -->
                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Integridad</span>
                    <span style="color: #f29727;">{{ (publicProfileData.skills.integrity || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((publicProfileData.skills.integrity || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <!-- Confidence -->
                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Confianza en sí mismo</span>
                    <span style="color: #f29727;">{{ (publicProfileData.skills.selfConfidence || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((publicProfileData.skills.selfConfidence || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <!-- Flexibility -->
                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Flexibilidad</span>
                    <span style="color: #f29727;">{{ (publicProfileData.skills.flexibility || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((publicProfileData.skills.flexibility || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Professional Experience Section -->
            <div style="margin-bottom: 30px;">
              <h3 class="text-sm font-black uppercase tracking-wider text-zinc-400 border-b pb-2" style="border-color: #e4e4e7; display: flex; align-items: center; margin: 0 0 16px 0;">
                <Briefcase class="w-4 h-4" style="color: #f29727; margin-right: 8px; display: inline-block; vertical-align: middle;" />
                <span style="display: inline-block; vertical-align: middle;">Trayectoria Profesional Certificada</span>
              </h3>
              
              <div v-if="publicProfileData.experiences && publicProfileData.experiences.length > 0">
                <div 
                  v-for="exp in publicProfileData.experiences" 
                  :key="exp.id" 
                  class="border-l-2 pl-4 py-1 relative" 
                  style="border-color: #f29727; margin-bottom: 20px; page-break-inside: avoid;"
                >
                  <h4 class="text-sm font-bold text-zinc-900" style="margin: 0 0 2px 0;">{{ exp.position }}</h4>
                  <p class="text-xs font-semibold text-zinc-600" style="margin: 0 0 2px 0;">{{ exp.companyName }} <span v-if="exp.department">· {{ exp.department }}</span></p>
                  <p class="text-[10px] text-zinc-400 font-medium" style="margin: 0 0 6px 0;">
                    {{ formatDate(exp.startDate) }} - {{ exp.finishDate ? formatDate(exp.finishDate) : 'Presente' }}
                  </p>
                  <p class="text-xs text-zinc-500 leading-relaxed" style="margin: 0 0 8px 0; word-wrap: break-word;">{{ exp.functions }}</p>

                  <!-- Métricas Certificadas en el PDF (Nuevo feature) -->
                  <div v-if="getMetricsForExperience(exp.id)" class="p-3 bg-zinc-50 rounded-xl border border-zinc-200/60" style="margin-top: 8px;">
                    <div class="flex items-center justify-between flex-wrap" style="margin-bottom: 6px;">
                      <div class="flex items-center" style="gap: 8px;">
                        <div class="flex items-center text-amber-500">
                          <strong class="text-xs text-zinc-800" style="margin-right: 6px;">{{ getMetricsForExperience(exp.id).averageScore.toFixed(1) }} / 5.0</strong>
                          <div class="flex items-center" style="gap: 2px;">
                            <svg v-for="star in 5" :key="star" class="w-3 h-3" :style="{ color: star <= Math.round(getMetricsForExperience(exp.id).averageScore) ? '#f59e0b' : '#e4e4e7' }" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                            </svg>
                          </div>
                        </div>
                        <span class="text-[10px] text-zinc-500 font-semibold" style="margin-left: 6px;">
                          ({{ getMetricsForExperience(exp.id).referencesCount }} {{ getMetricsForExperience(exp.id).referencesCount === 1 ? 'referencia' : 'referencias' }})
                        </span>
                      </div>
                      <div class="inline-flex items-center px-2 py-0.5 rounded text-[9px] font-bold uppercase tracking-wider bg-emerald-50 border text-emerald-700 border-emerald-200">
                        ✓ Confianza: {{ getTrustLevelLabel(getMetricsForExperience(exp.id).averageTrustScore) }}
                      </div>
                    </div>

                    <!-- Breakdown de Soft Skills en este rol (Compacto) -->
                    <table style="width: 100%; border-collapse: collapse; margin-top: 10px; border-top: 1px solid #e4e4e7; table-layout: fixed;">
                      <thead>
                        <tr>
                          <th v-for="(val, skill) in getMetricsForExperience(exp.id).categoryAverages" :key="skill" style="font-size: 9px; font-weight: 500; color: #71717a; text-align: left; padding: 8px 4px 2px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-family: inherit; line-height: 1.2;">
                            {{ categoryLabels[skill] || skill }}
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td v-for="(val, skill) in getMetricsForExperience(exp.id).categoryAverages" :key="skill" style="font-size: 10px; font-weight: 700; color: #18181b; text-align: left; padding: 2px 4px 4px 0; font-family: inherit; line-height: 1.2;">
                            {{ val.toFixed(1) }}/5.0
                          </td>
                        </tr>
                      </tbody>
                    </table>

                    <!-- Testimonios Cualitativos en el PDF -->
                    <div v-if="getMetricsForExperience(exp.id).testimonials && getMetricsForExperience(exp.id).testimonials.length > 0" style="margin-top: 12px; border-top: 1px solid #e4e4e7; padding-top: 8px;">
                      <p style="font-size: 8px; font-weight: 800; color: #a1a1aa; text-transform: uppercase; tracking-wider; margin: 0 0 6px 0;">Comentarios Certificados de Referentes</p>
                      <div style="display: flex; flex-direction: column; gap: 8px;">
                        <div 
                          v-for="t in getMetricsForExperience(exp.id).testimonials" 
                          :key="t.createdAt"
                          style="font-size: 10px; background-color: #fafafa; border: 1px solid #f4f4f5; padding: 8px; border-radius: 8px; font-family: inherit;"
                        >
                          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; font-size: 8px; color: #71717a; font-weight: 600;">
                            <span>Referente: {{ relationshipLabels[t.relationshipCode] || t.relationshipCode }} (Verificado)</span>
                            <span style="color: #10b981;">Confianza: {{ getTrustLevelLabel(t.trustScore) }}</span>
                          </div>
                          <p style="margin: 0; color: #52525b; font-style: italic; line-height: 1.4;">
                            "{{ t.comment }}"
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Bottom Footer Certificate -->
          <div class="pt-8 border-t flex justify-between items-end text-[9px] text-zinc-400" style="border-color: #e4e4e7; margin-top: 30px;">
            <div class="space-y-1 max-w-[70%]">
              <p class="font-bold text-zinc-500 uppercase tracking-wide" style="margin: 0 0 2px 0;">Garantía de Autenticidad MiCaché B2B</p>
              <p class="leading-relaxed" style="margin: 0;">
                Este reporte ha sido certificado mediante el protocolo de feedback seguro de MiCaché. La valoración de habilidades blandas es el resultado de opiniones anonimizadas de compañeros y superiores validados.
              </p>
            </div>
            <div class="text-right">
              <p class="font-semibold text-zinc-500" style="margin: 0 0 2px 0;">ID Candidato:</p>
              <p class="font-mono" style="margin: 0;">{{ authStore.user?.id.substring(0, 8) }}...{{ authStore.user?.id.substring(authStore.user?.id.length - 8) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Custom Styled Confirm Delete Modal (Nested to maintain single root) -->
    <div v-if="showDeleteModal" class="fixed inset-0 z-[1000] flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm">
      <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-white/5 rounded-3xl max-w-sm w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-200 text-zinc-900 dark:text-zinc-100">
        <div class="flex items-center gap-3 text-red-500">
          <div class="p-2 bg-red-500/10 dark:bg-red-500/20 rounded-xl">
            <AlertTriangle class="w-6 h-6 text-red-500" />
          </div>
          <h3 class="text-lg font-bold text-zinc-900 dark:text-white">¿Eliminar experiencia?</h3>
        </div>
        <p class="text-sm text-zinc-500 dark:text-zinc-400 leading-relaxed">
          ¿Estás seguro de que quieres eliminar esta experiencia laboral? Esto eliminará también cualquier feedback asociado de forma permanente y no se podrá deshacer.
        </p>
        <div class="flex justify-end gap-3 pt-2">
          <button 
            class="px-4 py-2 text-sm font-semibold rounded-xl bg-zinc-100 dark:bg-zinc-800 hover:bg-zinc-200 dark:hover:bg-zinc-700 transition-colors text-zinc-700 dark:text-zinc-300"
            @click="cancelDelete"
          >
            Cancelar
          </button>
          <Button 
            variant="destructive"
            class="px-4 py-2 text-sm font-semibold rounded-xl bg-red-600 hover:bg-red-700 text-white shadow-lg shadow-red-500/20 border-0"
            @click="executeDelete"
          >
            Eliminar
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>
