<script setup>
import { onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile.store';
import { useExperienceStore } from '@/stores/experience.store';
import { ChevronDown, Menu, User as UserIcon, Briefcase } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const router = useRouter();
const profileStore = useProfileStore();
const experienceStore = useExperienceStore();

onMounted(async () => {
  await Promise.all([
    profileStore.fetchProfile(),
    experienceStore.fetchExperiences()
  ]);
});

// Mock rating (4 out of 5)
const totalBlocks = 5;
const filledBlocks = 4;

const formatDate = (dateString) => {
  if (!dateString) return 'Actualidad';
  const options = { year: 'numeric', month: 'long' };
  const date = new Date(dateString);
  const formatted = date.toLocaleDateString('es-ES', options);
  return formatted.charAt(0).toUpperCase() + formatted.slice(1);
};

const profile = computed(() => profileStore.profile);
const experiences = computed(() => experienceStore.sortedByDate);
const loading = computed(() => profileStore.loading || experienceStore.loading);
</script>

<template>
  <!-- Main Container: Adaptable Light/Premium Dark -->
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- Loading State -->
    <div v-if="loading && (!profile || experiences.length === 0)" class="flex flex-col items-center justify-center min-h-[50vh] space-y-4">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-primary"></div>
      <p class="text-zinc-500 dark:text-zinc-400 font-medium tracking-wide animate-pulse">Cargando tu perfil...</p>
    </div>

    <template v-else>
      <!-- ─── HERO HEADER ─────────────────────────────────────────────────── -->
      <!-- Gradient header with subtle mesh/texture illusion via gradient stops -->
      <div class="h-56 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
        <!-- Decoraciones sutiles (blobs de luz) -->
        <div class="absolute top-0 right-0 w-96 h-96 bg-white/10 dark:bg-white/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3"></div>
        <div class="absolute bottom-0 left-0 w-64 h-64 bg-primary/20 rounded-full blur-2xl translate-y-1/2 -translate-x-1/4"></div>
      </div>

      <!-- ─── PROFILE SECTION ─────────────────────────────────────────────── -->
      <div class="max-w-3xl mx-auto px-4 -mt-24 relative z-10 flex flex-col items-center text-center">
        
        <!-- Avatar (Glowing) -->
        <div class="w-36 h-36 rounded-full overflow-hidden bg-white dark:bg-zinc-900 border-4 border-white dark:border-[hsl(228,16%,7%)] shadow-xl dark:shadow-[0_0_40px_rgba(242,151,39,0.15)] mb-6 flex items-center justify-center relative group transition-transform duration-300 hover:scale-105">
          <img v-if="profile?.photoUrl" :src="profile.photoUrl" alt="Foto de Perfil" class="w-full h-full object-cover" />
          <UserIcon v-else class="w-16 h-16 text-zinc-300 dark:text-zinc-600" />
          <div class="absolute inset-0 rounded-full ring-1 ring-inset ring-black/5 dark:ring-white/10 pointer-events-none"></div>
        </div>

        <!-- Info -->
        <h1 class="text-3xl font-bold uppercase tracking-tight text-zinc-900 dark:text-white mb-2 font-heading">
          {{ profile?.name }} {{ profile?.surname || '' }}
        </h1>
        <div class="text-zinc-600 dark:text-zinc-400 font-medium text-[15px] space-y-1.5">
          <p class="text-primary dark:text-primary/90 font-semibold tracking-wide">{{ profile?.jobTitle || 'Profesional en Mi Caché' }}</p>
          <p v-if="profile?.education" class="text-sm opacity-90">{{ profile?.education }}</p>
        </div>

        <!-- Rating Blocks (Refined) -->
        <div class="mt-8 flex items-center justify-center gap-3 bg-white/50 dark:bg-white/[0.02] py-2.5 px-5 rounded-2xl backdrop-blur-sm border border-zinc-200 dark:border-white/5 shadow-sm dark:shadow-none">
          <div class="flex gap-2">
            <div 
              v-for="i in totalBlocks" 
              :key="i"
              class="w-6 h-10 rounded-[3px] transition-colors"
              :class="i <= filledBlocks ? 'bg-primary dark:bg-primary/90 shadow-[0_0_10px_rgba(242,151,39,0.3)]' : 'bg-zinc-200/50 dark:bg-white/5 border border-zinc-300 dark:border-white/10'"
            ></div>
          </div>
          <ChevronDown class="w-5 h-5 text-zinc-400 ml-2" />
        </div>
      </div>

      <!-- ─── EXPERIENCE SECTION (CARDS & TIMELINE) ───────────────────────── -->
      <div class="max-w-3xl mx-auto mt-20 relative px-4 sm:px-6">
        
        <!-- Elegant Tab Title -->
        <div class="flex items-center mb-10 relative z-10 justify-center sm:justify-start">
          <div class="inline-flex items-center gap-2.5 px-5 py-2 rounded-full bg-primary/10 dark:bg-primary/[0.08] border border-primary/20 dark:border-primary/10 shadow-sm backdrop-blur-md">
            <div class="w-6 h-6 rounded-full bg-primary flex items-center justify-center text-white shadow-[0_0_10px_rgba(242,151,39,0.3)]">
              <Briefcase class="w-3.5 h-3.5" />
            </div>
            <span class="text-lg font-bold tracking-wide text-primary dark:text-primary/90 uppercase text-sm">Experiencia</span>
          </div>
        </div>

        <!-- Timeline Container -->
        <div class="relative pl-4 sm:pl-10" v-if="experiences.length > 0">
          
          <!-- Timeline Vertical Line (Subtle) -->
          <div class="absolute left-4 sm:left-10 top-4 bottom-0 w-[2px] bg-gradient-to-b from-primary/50 via-zinc-200 to-transparent dark:from-primary/30 dark:via-white/5 dark:to-transparent"></div>

          <!-- Timeline Items -->
          <div class="space-y-8">
            <div v-for="(exp, index) in experiences" :key="exp.id" class="relative pl-6 sm:pl-10 group">
              
              <!-- Timeline Node (Radar effect) -->
              <div class="absolute -left-[5px] sm:-left-[5px] top-6 w-3 h-3 rounded-full bg-white dark:bg-[hsl(228,16%,7%)] border-[2.5px] border-primary z-10 shadow-[0_0_0_4px_rgba(255,255,255,1)] dark:shadow-[0_0_0_4px_hsl(228,16%,7%)] group-hover:scale-125 transition-transform duration-300">
                <div v-if="index === 0" class="absolute inset-0 m-auto w-1 h-1 bg-primary rounded-full animate-ping opacity-75"></div>
              </div>

              <!-- Content Card (Glassmorphism / Neumorphism) -->
              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/[0.05] rounded-2xl p-6 sm:p-7 shadow-sm hover:shadow-md dark:shadow-none dark:hover:bg-white/[0.04] transition-all duration-300 relative overflow-hidden">
                
                <!-- Subtle gradient glow in dark mode on hover -->
                <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary/0 via-primary/0 to-primary/0 dark:group-hover:via-primary/30 transition-all duration-500"></div>

                <div class="flex justify-between items-start gap-4">
                  <div class="flex-1">
                    <!-- Header: Company & Dates -->
                    <div class="flex flex-col sm:flex-row sm:items-baseline gap-1 sm:gap-3 mb-1">
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white tracking-tight">
                        {{ exp.companyName }}
                      </h3>
                      <span class="text-sm font-medium text-zinc-500 dark:text-zinc-400/80">
                        {{ formatDate(exp.startDate) }} - {{ formatDate(exp.finishDate) }}
                      </span>
                    </div>
                    
                    <!-- Role -->
                    <p class="text-primary dark:text-primary/80 font-semibold text-[15px] mb-3">
                      {{ exp.position }}
                    </p>

                    <!-- Description -->
                    <p v-if="exp.functions" class="text-zinc-600 dark:text-zinc-400/90 text-[14.5px] leading-relaxed">
                      {{ exp.functions }}
                    </p>
                  </div>
                  
                  <!-- Action Icon -->
                  <button @click="router.push(`/experiences/${exp.id}/edit`)" class="p-2 -mr-2 -mt-2 text-zinc-400 hover:text-zinc-800 dark:hover:text-zinc-200 transition-colors flex-shrink-0 rounded-lg hover:bg-zinc-100 dark:hover:bg-white/5">
                    <Menu class="w-5 h-5" />
                  </button>
                </div>

              </div>
            </div>
          </div>
        </div>
        
        <!-- Empty State -->
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
    </template>
  </div>
</template>
