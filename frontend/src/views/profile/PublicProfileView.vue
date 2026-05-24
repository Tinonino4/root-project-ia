<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import client from '@/api/client';
import SkillsRadarChart from '@/components/dashboard/SkillsRadarChart.vue';
import { Briefcase, Calendar, Award, ArrowLeft, Download } from 'lucide-vue-next';
import html2pdf from 'html2pdf.js';

const route = useRoute();
const router = useRouter();
const userId = route.params.userId;

const profile = ref(null);
const loading = ref(true);
const error = ref(null);
const isExporting = ref(false);
const isGeneratingPDF = ref(false);

onMounted(async () => {
  try {
    const response = await client.get(`/public/profile/${userId}`);
    profile.value = response.data;
  } catch (err) {
    console.error('Error fetching public profile:', err);
    error.value = 'No se pudo cargar el perfil o no existe.';
  } finally {
    loading.value = false;
  }
});

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', { month: 'short', year: 'numeric' });
};

const exportPDF = () => {
  if (isExporting.value || !profile.value) return;
  
  isExporting.value = true;
  isGeneratingPDF.value = true;

  // Esperar a que Vue monte el elemento en el DOM usando la clase de posicionamiento absoluto
  setTimeout(() => {
    const element = document.getElementById('pdf-profile-template');
    if (!element) {
      console.error('El elemento PDF no se encontró en el DOM.');
      isExporting.value = false;
      isGeneratingPDF.value = false;
      return;
    }
    
    const opt = {
      margin:       [12, 12, 18, 12],
      filename:     `informe_micache_${profile.value.name}_${profile.value.surname}.pdf`,
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
  <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-20 min-h-screen">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary mx-auto"></div>
      <p class="mt-4 text-[hsl(220,10%,60%)]">Cargando perfil...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-20">
      <p class="text-red-500 font-medium">{{ error }}</p>
      <button 
        @click="router.back()"
        class="mt-6 inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-zinc-100 dark:bg-zinc-800 text-zinc-700 dark:text-zinc-300 hover:bg-zinc-200 transition-colors"
      >
        <ArrowLeft class="w-4 h-4" /> Volver
      </button>
    </div>

    <!-- Profile Content -->
    <div v-else-if="profile" class="space-y-8 animate-in fade-in-50 duration-500">
      <!-- Header Actions -->
      <div class="flex items-center justify-between gap-4">
        <button 
          @click="router.back()"
          class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-[hsl(228,15%,9%)] border border-white/5 text-zinc-400 hover:text-white hover:bg-[hsl(228,15%,12%)] transition-all duration-200 text-sm font-semibold"
        >
          <ArrowLeft class="w-4 h-4" />
          Volver
        </button>
        
        <button 
          @click="exportPDF"
          :disabled="isExporting"
          class="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-primary text-white hover:bg-primary/90 shadow-lg shadow-primary/20 transition-all duration-200 text-sm font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:-translate-y-0.5"
        >
          <Download class="w-4 h-4" :class="{'animate-bounce': isExporting}" />
          {{ isExporting ? 'Generando PDF...' : 'Exportar Informe PDF' }}
        </button>
      </div>

      <!-- Hero Section -->
      <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-8 backdrop-blur-xl shadow-2xl">
        <div class="flex flex-col md:flex-row gap-6 items-center md:items-start">
          <!-- Avatar -->
          <div class="w-32 h-32 rounded-full bg-[hsl(228,15%,15%)] flex items-center justify-center text-4xl font-bold text-primary border-2 border-white/10 shadow-inner">
            <img v-if="profile.photoUrl" :src="profile.photoUrl" alt="Avatar" class="w-full h-full rounded-full object-cover" />
            <span v-else>{{ profile.name?.charAt(0) }}{{ profile.surname?.charAt(0) }}</span>
          </div>

          <!-- Info -->
          <div class="flex-1 text-center md:text-left space-y-2">
            <h1 class="text-3xl font-bold text-white tracking-tight">{{ profile.name }} {{ profile.surname }}</h1>
            <p class="text-lg text-primary font-semibold">{{ profile.jobTitle }}</p>
            <p class="text-[hsl(220,10%,75%)] mt-4 max-w-2xl text-balance">{{ profile.aboutMe }}</p>
          </div>
        </div>
      </div>

      <!-- Grid: Skills and Experience -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <!-- Skills (Radar Chart) -->
        <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-6 backdrop-blur-xl shadow-xl">
          <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <Award class="w-5 h-5 text-primary" />
            Habilidades Blandas
          </h2>
          <div v-if="profile.skills" class="h-80 flex items-center justify-center">
            <SkillsRadarChart :metrics="profile.skills" />
          </div>
          <div v-else class="text-center py-20 text-[hsl(220,10%,40%)]">
            No hay métricas de habilidades disponibles.
          </div>
        </div>

        <!-- Experience Timeline -->
        <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-6 backdrop-blur-xl shadow-xl">
          <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <Briefcase class="w-5 h-5 text-primary" />
            Experiencia Profesional
          </h2>
          <div v-if="profile.experiences && profile.experiences.length > 0" class="space-y-6 relative before:absolute before:inset-y-0 before:left-3 before:w-px before:bg-white/5">
            <div v-for="exp in profile.experiences" :key="exp.id" class="relative pl-8 group">
              <!-- Timeline dot -->
              <div class="absolute left-1.5 top-2 w-3 h-3 rounded-full bg-primary -translate-x-1/2 group-hover:scale-125 transition-transform duration-200"></div>
              
              <div class="space-y-1">
                <h3 class="text-lg font-semibold text-white group-hover:text-primary transition-colors duration-200">{{ exp.position }}</h3>
                <p class="text-[hsl(220,10%,70%)] text-sm font-medium">{{ exp.companyName }} <span v-if="exp.department">· {{ exp.department }}</span></p>
                <div class="flex items-center gap-2 text-xs text-[hsl(220,10%,50%)]">
                  <Calendar class="w-3.5 h-3.5" />
                  <span>{{ formatDate(exp.startDate) }} - {{ exp.finishDate ? formatDate(exp.finishDate) : 'Presente' }}</span>
                </div>
                <p class="text-sm text-[hsl(220,10%,60%)] mt-2 leading-relaxed">{{ exp.functions }}</p>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-20 text-[hsl(220,10%,40%)]">
            No se ha registrado experiencia profesional.
          </div>
        </div>
      </div>
    </div>

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
          Generando Informe Certificado...
        </h3>
        <p class="text-xs text-zinc-400 max-w-xs leading-relaxed">
          MiCaché B2B está compilando las referencias y habilidades blandas del candidato.
        </p>
      </div>
    </div>

    <!-- Premium PDF Template (only rendered when generating PDF, positioned absolute top-left behind the overlay) -->
    <div v-if="isGeneratingPDF" class="absolute left-0 top-0 z-[9999] bg-white">
      <div 
        id="pdf-profile-template" 
        class="bg-white text-zinc-900 p-12 font-sans relative overflow-hidden flex flex-col justify-between" 
        style="width: 794px; min-height: 1120px;"
      >
        <!-- Tilted Diagonal Corporate Watermark -->
        <div class="absolute inset-0 pointer-events-none select-none z-0 flex flex-col justify-around items-center overflow-hidden">
          <div 
            class="text-6xl font-black uppercase tracking-[0.25em] transform -rotate-45 select-none"
            style="color: rgba(24, 24, 27, 0.035);"
          >
            Verificado por MiCaché
          </div>
          <div 
            class="text-6xl font-black uppercase tracking-[0.25em] transform -rotate-45 select-none"
            style="color: rgba(24, 24, 27, 0.035);"
          >
            Verificado por MiCaché
          </div>
        </div>

        <div class="relative z-10 flex-1 flex flex-col justify-between">
          <!-- Content Wrap -->
          <div class="space-y-8">
            
            <!-- PDF Header Banner -->
            <div class="flex items-center justify-between border-b pb-6" style="border-color: #e4e4e7;">
              <div>
                <h2 class="text-2xl font-black tracking-tight" style="color: #f29727;">MiCaché</h2>
                <p class="text-xs font-semibold text-zinc-500 uppercase tracking-widest mt-0.5">Informe Profesional Certificado</p>
              </div>
              <div class="text-right">
                <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-green-50 border border-green-200 text-[10px] font-bold text-green-700 uppercase tracking-wider">
                  ✓ Verificado
                </div>
                <p class="text-[10px] text-zinc-400 mt-1.5">Emitido: {{ new Date().toLocaleDateString('es-ES') }}</p>
              </div>
            </div>

            <!-- Candidate Info block -->
            <div class="flex gap-6 items-start bg-zinc-50 border rounded-2xl p-6" style="border-color: #e4e4e7;">
              <!-- Initials Avatar -->
              <div 
                class="w-20 h-20 rounded-full flex items-center justify-center font-bold text-2xl flex-shrink-0 shadow-inner"
                style="background-color: rgba(242, 151, 39, 0.1); color: #f29727; border: 1px solid rgba(242, 151, 39, 0.2);"
              >
                <span>{{ profile.name?.charAt(0) }}{{ profile.surname?.charAt(0) }}</span>
              </div>
              <div class="space-y-1.5 flex-1 min-w-0">
                <h3 class="text-xl font-extrabold text-zinc-950 truncate">{{ profile.name }} {{ profile.surname }}</h3>
                <p class="text-sm font-bold" style="color: #f29727;">{{ profile.jobTitle }}</p>
                <p class="text-xs text-zinc-600 leading-relaxed pt-1">{{ profile.aboutMe }}</p>
              </div>
            </div>

            <!-- Soft Skills Metrics Section -->
            <div class="space-y-4">
              <h3 class="text-sm font-black uppercase tracking-wider text-zinc-400 border-b pb-2 flex items-center gap-2" style="border-color: #e4e4e7;">
                <Award class="w-4 h-4" style="color: #f29727;" />
                Soft-Skills y Habilidades Blandas
              </h3>
              
              <div v-if="profile.skills" class="grid grid-cols-2 gap-x-8 gap-y-4">
                <!-- Teamwork -->
                <div class="space-y-1.5">
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800">
                    <span>Trabajo en equipo</span>
                    <span>{{ (profile.skills.teamwork || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2" style="background-color: #f4f4f5;">
                    <div 
                      class="h-2 rounded-full" 
                      :style="{ width: `${((profile.skills.teamwork || 0) / 5) * 100}%` }"
                      style="background-color: #f29727;"
                    ></div>
                  </div>
                </div>

                <!-- Proactivity -->
                <div class="space-y-1.5">
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800">
                    <span>Proactividad</span>
                    <span>{{ (profile.skills.proactivity || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2" style="background-color: #f4f4f5;">
                    <div 
                      class="h-2 rounded-full" 
                      :style="{ width: `${((profile.skills.proactivity || 0) / 5) * 100}%` }"
                      style="background-color: #f29727;"
                    ></div>
                  </div>
                </div>

                <!-- Integrity -->
                <div class="space-y-1.5">
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800">
                    <span>Integridad</span>
                    <span>{{ (profile.skills.integrity || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2" style="background-color: #f4f4f5;">
                    <div 
                      class="h-2 rounded-full" 
                      :style="{ width: `${((profile.skills.integrity || 0) / 5) * 100}%` }"
                      style="background-color: #f29727;"
                    ></div>
                  </div>
                </div>

                <!-- Confidence -->
                <div class="space-y-1.5">
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800">
                    <span>Confianza en sí mismo</span>
                    <span>{{ (profile.skills.selfConfidence || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2" style="background-color: #f4f4f5;">
                    <div 
                      class="h-2 rounded-full" 
                      :style="{ width: `${((profile.skills.selfConfidence || 0) / 5) * 100}%` }"
                      style="background-color: #f29727;"
                    ></div>
                  </div>
                </div>

                <!-- Flexibility -->
                <div class="space-y-1.5">
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800">
                    <span>Flexibilidad</span>
                    <span>{{ (profile.skills.flexibility || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2" style="background-color: #f4f4f5;">
                    <div 
                      class="h-2 rounded-full" 
                      :style="{ width: `${((profile.skills.flexibility || 0) / 5) * 100}%` }"
                      style="background-color: #f29727;"
                    ></div>
                  </div>
                </div>
              </div>

              <div v-else class="text-center py-8 text-zinc-400 text-xs border border-dashed rounded-xl" style="border-color: #e4e4e7;">
                No hay puntuaciones de habilidades blandas certificadas públicamente.
              </div>
            </div>

            <!-- Professional Experience Section -->
            <div class="space-y-4">
              <h3 class="text-sm font-black uppercase tracking-wider text-zinc-400 border-b pb-2 flex items-center gap-2" style="border-color: #e4e4e7;">
                <Briefcase class="w-4 h-4" style="color: #f29727;" />
                Trayectoria Profesional Certificada
              </h3>
              
              <div v-if="profile.experiences && profile.experiences.length > 0" class="space-y-4">
                <div 
                  v-for="exp in profile.experiences" 
                  :key="exp.id" 
                  class="border-l-2 pl-4 py-1 space-y-1 relative" 
                  style="border-color: #f29727;"
                >
                  <h4 class="text-sm font-bold text-zinc-900">{{ exp.position }}</h4>
                  <p class="text-xs font-semibold text-zinc-600">{{ exp.companyName }} <span v-if="exp.department">· {{ exp.department }}</span></p>
                  <p class="text-[10px] text-zinc-400 font-medium">
                    {{ formatDate(exp.startDate) }} - {{ exp.finishDate ? formatDate(exp.finishDate) : 'Presente' }}
                  </p>
                  <p class="text-xs text-zinc-500 mt-1 leading-relaxed">{{ exp.functions }}</p>
                </div>
              </div>
              <div v-else class="text-center py-8 text-zinc-400 text-xs border border-dashed rounded-xl" style="border-color: #e4e4e7;">
                No se ha registrado experiencia profesional.
              </div>
            </div>
            
          </div>

          <!-- PDF Footer Certification details -->
          <div class="pt-8 border-t mt-12 flex justify-between items-end text-[9px] text-zinc-400" style="border-color: #e4e4e7;">
            <div class="space-y-1 max-w-[70%]">
              <p class="font-bold text-zinc-500 uppercase tracking-wide">Garantía de Autenticidad MiCaché B2B</p>
              <p class="leading-relaxed">
                Este reporte ha sido certificado mediante el protocolo de feedback seguro de MiCaché. La valoración de habilidades blandas es el resultado de opiniones anonimizadas de compañeros y superiores validados.
              </p>
            </div>
            <div class="text-right">
              <p class="font-semibold text-zinc-500">ID Candidato:</p>
              <p class="font-mono">{{ userId.substring(0, 8) }}...{{ userId.substring(userId.length - 8) }}</p>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>
