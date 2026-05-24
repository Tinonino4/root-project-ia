<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useFeedbackStore } from '@/stores/feedback.store';
import { ArrowLeft, Plus, Mail, Clock, CheckCircle2, XCircle } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const router = useRouter();
const feedbackStore = useFeedbackStore();

onMounted(async () => {
  await feedbackStore.fetchRequests();
});

const requests = computed(() => feedbackStore.requests);
const loading = computed(() => feedbackStore.loading);

const toast = ref({
  show: false,
  message: ''
});

const triggerToast = (message) => {
  toast.value.message = message;
  toast.value.show = true;
  setTimeout(() => {
    toast.value.show = false;
  }, 4000);
};

const handleToggleVisibility = async (requestId, visible) => {
  try {
    await feedbackStore.toggleRequestVisibility(requestId, visible);
    triggerToast(visible 
      ? 'Referencia visible. Tus soft-skills se han recalculado.' 
      : 'Referencia oculta. Tus soft-skills se han recalculado.'
    );
  } catch (err) {
    triggerToast('Error al cambiar la visibilidad');
  }
};

const goBack = () => {
  router.back();
};

const goToCreate = () => {
  router.push('/feedback/new');
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- HEADER -->
    <div class="h-40 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdG09IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSJub25lIi8+PGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjA1KSIvPjwvc3ZnPg==')] opacity-30"></div>
      
      <div class="max-w-5xl mx-auto px-6 h-full flex items-center justify-between relative z-10">
        <div class="flex items-center space-x-4">
          <button 
            @click="goBack" 
            class="p-2.5 rounded-xl bg-white/10 backdrop-blur-md border border-white/20 text-white hover:bg-white/20 transition-all duration-300 shadow-lg group"
            aria-label="Volver"
          >
            <ArrowLeft class="w-5 h-5 group-hover:-translate-x-1 transition-transform" />
          </button>
          <div>
            <h1 class="text-3xl font-bold text-white tracking-tight">Solicitudes de Feedback</h1>
            <p class="text-white/80 text-sm mt-0.5">Gestiona las validaciones de tus habilidades</p>
          </div>
        </div>

        <Button 
          @click="goToCreate"
          class="bg-white/10 hover:bg-white/20 text-white border border-white/20 backdrop-blur-md rounded-xl px-5 h-11 flex items-center space-x-2 transition-all duration-300 shadow-lg"
        >
          <Plus class="w-5 h-5" />
          <span>Nueva Solicitud</span>
        </Button>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-5xl mx-auto px-6 -mt-10 relative z-20">
      
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>

      <!-- Empty State -->
      <div v-else-if="requests.length === 0" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center shadow-[0_20px_50px_rgba(0,0,0,0.05)]">
        <div class="w-16 h-16 bg-primary/10 rounded-2xl flex items-center justify-center mx-auto mb-6">
          <Mail class="w-8 h-8 text-primary" />
        </div>
        <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">No hay solicitudes aún</h3>
        <p class="text-zinc-500 dark:text-zinc-400 mb-6 max-w-md mx-auto">Solicita feedback a tus compañeros o jefes para empezar a medir tus habilidades profesionales.</p>
        <Button @click="goToCreate" class="bg-primary hover:bg-primary-hover text-white rounded-xl">
          Crear mi primera solicitud
        </Button>
      </div>

      <!-- Requests Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div 
          v-for="req in requests" 
          :key="req.id"
          class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 shadow-[0_10px_30px_rgba(0,0,0,0.03)] hover:shadow-[0_15px_40px_rgba(0,0,0,0.08)] transition-all duration-500 group flex flex-col justify-between"
        >
          <div>
            <div class="flex justify-between items-start mb-4">
              <div>
                <h3 class="text-lg font-bold text-zinc-900 dark:text-white group-hover:text-primary transition-colors">
                  {{ req.targetName }} {{ req.targetSurname }}
                </h3>
                <p class="text-sm text-zinc-500 dark:text-zinc-400 flex items-center mt-0.5">
                  <Mail class="w-3.5 h-3.5 mr-1" />
                  {{ req.targetEmail }}
                </p>
              </div>
              
              <!-- Status Badge -->
              <span 
                v-if="req.finished" 
                class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
              >
                <CheckCircle2 class="w-3.5 h-3.5 mr-1" />
                Completado
              </span>
              <span 
                v-else 
                class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-amber-500/10 text-amber-600 dark:text-amber-400"
              >
                <Clock class="w-3.5 h-3.5 mr-1" />
                Pendiente
              </span>
            </div>

            <div class="space-y-2 text-sm text-zinc-600 dark:text-zinc-300">
              <div class="flex justify-between">
                <span>Fecha:</span>
                <span class="font-medium">{{ formatDate(req.createdAt) }}</span>
              </div>
              <div class="flex justify-between">
                <span>Token:</span>
                <span class="font-mono text-xs bg-zinc-100 dark:bg-zinc-800 px-1.5 py-0.5 rounded">{{ req.id.substring(0, 8) }}...</span>
              </div>
            </div>
          </div>

          <div class="mt-6 pt-4 border-t border-zinc-100 dark:border-zinc-800 flex items-center justify-between">
            <!-- Toggle switch for visibility (only shown if finished) -->
            <div v-if="req.finished" class="flex items-center space-x-2">
              <span class="text-xs text-zinc-500 dark:text-zinc-400 font-medium">Perfil Público:</span>
              <label :for="'toggle-' + req.id" class="inline-flex relative items-center cursor-pointer select-none">
                <input 
                  type="checkbox" 
                  :id="'toggle-' + req.id" 
                  class="sr-only peer"
                  :checked="req.visible"
                  @change="handleToggleVisibility(req.id, $event.target.checked)"
                >
                <div class="w-9 h-5 bg-zinc-200 dark:bg-zinc-800 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-zinc-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-primary"></div>
              </label>
            </div>
            <div v-else class="text-xs text-zinc-400 dark:text-zinc-500 italic">
              Esperando respuesta...
            </div>

            <div>
              <Button 
                v-if="!req.finished"
                variant="outline"
                class="text-xs border-zinc-200 dark:border-zinc-700 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg h-9"
                @click="() => {}"
              >
                Recordar
              </Button>
              <Button 
                v-else
                variant="ghost"
                class="text-xs text-primary hover:text-primary-hover rounded-lg h-9"
                @click="() => {}"
              >
                Ver Respuestas
              </Button>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- TOAST NOTIFICATION -->
    <div 
      v-if="toast.show" 
      class="fixed bottom-6 right-6 z-50 flex items-center p-4 space-x-3 text-white bg-zinc-900 dark:bg-zinc-950 border border-white/10 rounded-2xl shadow-2xl transition-all duration-300 animate-in fade-in slide-in-from-bottom-4"
    >
      <div class="w-5 h-5 rounded-full bg-emerald-500 flex items-center justify-center text-xs font-bold text-white">✓</div>
      <p class="text-sm font-medium">{{ toast.message }}</p>
    </div>
  </div>
</template>
