<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import client from '@/api/client';
import SkillsRadarChart from '@/components/dashboard/SkillsRadarChart.vue';
import { Briefcase, Calendar, Award } from 'lucide-vue-next';

const route = useRoute();
const userId = route.params.userId;

const profile = ref(null);
const loading = ref(true);
const error = ref(null);

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
</script>

<template>
  <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-24 min-h-screen">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary mx-auto"></div>
      <p class="mt-4 text-[hsl(220,10%,60%)]">Cargando perfil...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-20">
      <p class="text-red-500 font-medium">{{ error }}</p>
    </div>

    <!-- Profile Content -->
    <div v-else-if="profile" class="space-y-8 animate-in fade-in-50 duration-500">
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
  </div>
</template>
