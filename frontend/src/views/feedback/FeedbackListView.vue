<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useFeedbackStore } from '@/stores/feedback.store';
import { ArrowLeft, Plus, Mail, Clock, CheckCircle2, XCircle, ShieldCheck, Send, Trash2, AlertTriangle } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const router = useRouter();
const feedbackStore = useFeedbackStore();

onMounted(async () => {
  await feedbackStore.fetchRequests();
});

const requests = computed(() => feedbackStore.requests);
const loading = computed(() => feedbackStore.loading);

const activeTab = ref('all'); // 'all', 'completed', 'pending'

const completedCount = computed(() => requests.value.filter(r => r.finished).length);
const pendingCount = computed(() => requests.value.filter(r => !r.finished).length);

const filteredRequests = computed(() => {
  if (activeTab.value === 'completed') return requests.value.filter(r => r.finished);
  if (activeTab.value === 'pending') return requests.value.filter(r => !r.finished);
  return requests.value;
});

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

const handleRemind = async (requestId) => {
  try {
    await feedbackStore.remindRequest(requestId);
    triggerToast('Recordatorio enviado con éxito.');
  } catch (err) {
    triggerToast('Error al enviar el recordatorio');
  }
};

const showDeleteModal = ref(false);
const requestToDelete = ref(null);

const confirmDelete = (id) => {
  requestToDelete.value = id;
  showDeleteModal.value = true;
};

const executeDelete = async () => {
  if (requestToDelete.value) {
    try {
      await feedbackStore.deleteRequest(requestToDelete.value);
      triggerToast('Solicitud cancelada correctamente.');
    } catch (err) {
      triggerToast('Error al cancelar la solicitud');
    } finally {
      showDeleteModal.value = false;
      requestToDelete.value = null;
    }
  }
};

const cancelDelete = () => {
  showDeleteModal.value = false;
  requestToDelete.value = null;
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

const FREE_DOMAINS = [
  'gmail.com', 'hotmail.com', 'outlook.com', 'yahoo.com', 'icloud.com',
  'proton.me', 'protonmail.com', 'aol.com', 'live.com', 'hotmail.es',
  'yahoo.es', 'live.cl', 'yandex.com', 'mail.com', 'zoho.com', 'gmx.com', 'outlook.es'
];

const isCorporate = (email) => {
  if (!email || !email.includes('@')) return false;
  const domain = email.substring(email.indexOf('@') + 1).toLowerCase().trim();
  return !FREE_DOMAINS.includes(domain);
};

const hasCorporate = (req) => isCorporate(req.targetEmail);
const hasPhone = (req) => !!req.targetPhone;

const hasRegistered = (req) => {
  const base = (isCorporate(req.targetEmail) ? 30 : 0) + (req.targetPhone ? 10 : 0);
  const diff = req.trustScore - base;
  return diff === 20 || diff === 60;
};

const hasCompanyMatch = (req) => {
  const base = (isCorporate(req.targetEmail) ? 30 : 0) + (req.targetPhone ? 10 : 0);
  const diff = req.trustScore - base;
  return diff === 40 || diff === 60;
};

const getTrustLabel = (score) => {
  if (score >= 80) return 'Excelente';
  if (score >= 50) return 'Alta';
  if (score >= 30) return 'Media';
  return 'Básica';
};

const selectedRequest = ref(null);
const showAnswersModal = ref(false);

const openAnswersModal = (req) => {
  selectedRequest.value = req;
  showAnswersModal.value = true;
};

const closeAnswersModal = () => {
  showAnswersModal.value = false;
  selectedRequest.value = null;
};

const relationshipIdLabels = {
  0: 'Jefe directo',
  1: 'Compañero/a',
  2: 'Subordinado/a',
  3: 'Cliente',
  4: 'Otro'
};
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- HEADER -->
    <div class="h-40 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdG09IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSJub25lIi8+PGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjA1KSIvPjwvc3ZnPg==')] opacity-30"></div>
      
      <div class="max-w-5xl mx-auto px-4 sm:px-6 h-full flex items-center justify-between relative z-10">
        <div class="flex items-center space-x-3 sm:space-x-4">
          <button 
            @click="goBack" 
            class="p-2 sm:p-2.5 rounded-xl bg-white/10 backdrop-blur-md border border-white/20 text-white hover:bg-white/20 transition-all duration-300 shadow-lg group"
            aria-label="Volver"
          >
            <ArrowLeft class="w-4.5 h-4.5 sm:w-5 sm:h-5 group-hover:-translate-x-1 transition-transform" />
          </button>
          <div>
            <h1 class="text-2xl sm:text-3xl font-bold text-white tracking-tight">Solicitudes</h1>
            <p class="text-white/80 text-xs sm:text-sm mt-0.5">Gestiona tus validaciones</p>
          </div>
        </div>

        <Button 
          @click="goToCreate"
          class="bg-gradient-to-r from-primary to-orange-500 hover:scale-[1.02] active:scale-[0.98] transition-all text-white border-0 rounded-xl px-3 sm:px-5 h-10 flex items-center gap-1.5 shadow-lg shadow-primary/20 flex-shrink-0"
        >
          <Plus class="w-4.5 h-4.5" />
          <span class="text-xs sm:text-sm hidden xs:inline">Nueva</span>
        </Button>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-5xl mx-auto px-3 sm:px-6 -mt-10 relative z-20">
      
      <!-- Tabs Navigation -->
      <div class="flex space-x-1 bg-zinc-200/50 dark:bg-zinc-800/40 p-1 rounded-xl mb-6 max-w-md border border-zinc-200/30 dark:border-zinc-800/20 backdrop-blur-md">
        <button
          @click="activeTab = 'all'"
          class="flex-1 py-2 text-xs sm:text-sm font-medium rounded-lg transition-all duration-300 flex items-center justify-center gap-1.5"
          :class="activeTab === 'all' 
            ? 'bg-white dark:bg-zinc-900 text-primary dark:text-white shadow-sm border border-zinc-100 dark:border-zinc-800/50' 
            : 'text-zinc-600 dark:text-zinc-400 hover:text-zinc-900 dark:hover:text-white'"
        >
          <span>Todas</span>
          <span class="px-1.5 py-0.5 text-[10px] rounded-md bg-zinc-150 dark:bg-zinc-800 text-zinc-600 dark:text-zinc-400 font-bold">
            {{ requests.length }}
          </span>
        </button>
        <button
          @click="activeTab = 'completed'"
          class="flex-1 py-2 text-xs sm:text-sm font-medium rounded-lg transition-all duration-300 flex items-center justify-center gap-1.5"
          :class="activeTab === 'completed' 
            ? 'bg-white dark:bg-zinc-900 text-primary dark:text-white shadow-sm border border-zinc-100 dark:border-zinc-800/50' 
            : 'text-zinc-600 dark:text-zinc-400 hover:text-zinc-900 dark:hover:text-white'"
        >
          <span>Completadas</span>
          <span class="px-1.5 py-0.5 text-[10px] rounded-md bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 font-bold">
            {{ completedCount }}
          </span>
        </button>
        <button
          @click="activeTab = 'pending'"
          class="flex-1 py-2 text-xs sm:text-sm font-medium rounded-lg transition-all duration-300 flex items-center justify-center gap-1.5"
          :class="activeTab === 'pending' 
            ? 'bg-white dark:bg-zinc-900 text-primary dark:text-white shadow-sm border border-zinc-100 dark:border-zinc-800/50' 
            : 'text-zinc-600 dark:text-zinc-400 hover:text-zinc-900 dark:hover:text-white'"
        >
          <span>Pendientes</span>
          <span class="px-1.5 py-0.5 text-[10px] rounded-md bg-amber-500/10 text-amber-600 dark:text-amber-400 font-bold">
            {{ pendingCount }}
          </span>
        </button>
      </div>
      
      <!-- Loading State with Shimmer Skeletons -->
      <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div 
          v-for="i in 4" 
          :key="i"
          class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800/80 rounded-2xl p-6 space-y-4 animate-pulse"
        >
          <div class="flex justify-between items-start">
            <div class="space-y-2 flex-1">
              <div class="h-5 bg-zinc-200 dark:bg-zinc-800 rounded w-2/3"></div>
              <div class="h-3.5 bg-zinc-200 dark:bg-zinc-800 rounded w-1/2"></div>
            </div>
            <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-20"></div>
          </div>
          <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3"></div>
          <div class="h-px bg-zinc-100 dark:bg-zinc-800/60 my-4"></div>
          <div class="flex justify-between items-center">
            <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-24"></div>
            <div class="h-6 bg-zinc-200 dark:bg-zinc-800 rounded w-12"></div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredRequests.length === 0" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center shadow-[0_20px_50px_rgba(0,0,0,0.05)] animate-in fade-in duration-300">
        <div class="w-16 h-16 bg-primary/10 rounded-2xl flex items-center justify-center mx-auto mb-6">
          <Mail class="w-8 h-8 text-primary" />
        </div>
        <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">
          {{ activeTab === 'pending' ? 'No hay solicitudes pendientes' : activeTab === 'completed' ? 'No hay valoraciones completadas' : 'No hay solicitudes aún' }}
        </h3>
        <p class="text-zinc-500 dark:text-zinc-400 mb-6 max-w-md mx-auto">
          {{ activeTab === 'pending' 
               ? 'Todas tus solicitudes de feedback han sido respondidas.' 
               : activeTab === 'completed' 
                 ? 'Aún estás esperando que tus referentes completen sus valoraciones.' 
                 : 'Solicita feedback a tus compañeros o jefes para empezar a medir tus habilidades profesionales.' }}
        </p>
        <Button v-if="activeTab === 'all' || activeTab === 'pending'" @click="goToCreate" class="bg-primary hover:bg-primary-hover text-white rounded-xl">
          Crear una solicitud
        </Button>
      </div>

      <!-- Requests Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div 
          v-for="req in filteredRequests" 
          :key="req.id"
          class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-4 sm:p-6 shadow-[0_10px_30px_rgba(0,0,0,0.03)] hover:shadow-[0_15px_40px_rgba(0,0,0,0.08)] transition-all duration-500 group flex flex-col justify-between"
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
                class="inline-flex items-center px-2 py-1 sm:px-3 sm:py-1 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
                title="Completado"
              >
                <CheckCircle2 class="w-4 h-4 flex-shrink-0" />
                <span class="ml-1 hidden sm:inline">Completado</span>
              </span>
              <span 
                v-else 
                class="inline-flex items-center px-2 py-1 sm:px-3 sm:py-1 rounded-full text-xs font-medium bg-amber-500/10 text-amber-600 dark:text-amber-400"
                title="Pendiente"
              >
                <Clock class="w-4 h-4 flex-shrink-0" />
                <span class="ml-1 hidden sm:inline">Pendiente</span>
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
              <div v-if="req.finished" class="flex justify-between items-center pt-1.5 border-t border-zinc-100 dark:border-zinc-800/40 mt-1.5">
                <span class="text-xs text-zinc-500 dark:text-zinc-400">Confianza del Referente:</span>
                <div class="relative group cursor-help select-none">
                  <span 
                    class="inline-flex items-center px-2 py-0.5 rounded text-xs font-bold border"
                    :class="{
                      'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20': req.trustScore >= 80,
                      'bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/20': req.trustScore >= 50 && req.trustScore < 80,
                      'bg-orange-500/10 text-orange-600 dark:text-orange-400 border-orange-500/20': req.trustScore >= 30 && req.trustScore < 50,
                      'bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/20': req.trustScore < 30
                    }"
                  >
                    <ShieldCheck class="w-3.5 h-3.5 mr-1" />
                    {{ getTrustLabel(req.trustScore) }} ({{ req.trustScore }}%)
                  </span>
                  
                  <!-- Hover tooltip desglosando los puntos de confianza -->
                  <div class="absolute bottom-full right-0 mb-2 w-64 p-3 rounded-xl bg-zinc-900 border border-white/10 text-white text-xs space-y-1.5 opacity-0 pointer-events-none group-hover:opacity-100 transition-opacity duration-200 shadow-2xl z-50">
                    <p class="font-bold text-zinc-300 mb-1 border-b border-white/10 pb-1 flex items-center gap-1.5">
                      <ShieldCheck class="w-3.5 h-3.5 text-primary" />
                      Desglose de Verificación
                    </p>
                    <div class="flex justify-between">
                      <span class="text-zinc-400">Email Corporativo (+30%):</span>
                      <span class="font-bold" :class="hasCorporate(req) ? 'text-emerald-400' : 'text-zinc-600'">{{ hasCorporate(req) ? '✓ Sí' : '✗ No' }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-zinc-400">Coincide con Empresa (+40%):</span>
                      <span class="font-bold" :class="hasCompanyMatch(req) ? 'text-emerald-400' : 'text-zinc-600'">{{ hasCompanyMatch(req) ? '✓ Sí' : '✗ No' }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-zinc-400">Referente Registrado (+20%):</span>
                      <span class="font-bold" :class="hasRegistered(req) ? 'text-emerald-400' : 'text-zinc-600'">{{ hasRegistered(req) ? '✓ Sí' : '✗ No' }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-zinc-400">Teléfono Provisto (+10%):</span>
                      <span class="font-bold" :class="hasPhone(req) ? 'text-emerald-400' : 'text-zinc-600'">{{ hasPhone(req) ? '✓ Sí' : '✗ No' }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-6 pt-4 border-t border-zinc-100 dark:border-zinc-800 flex items-center justify-between">
            <!-- Toggle switch for visibility (only shown if finished) -->
            <div v-if="req.finished" class="flex items-center space-x-2">
              <span class="text-xs text-zinc-500 dark:text-zinc-400 font-medium">Perfil Público:</span>
              <label :for="'toggle-' + req.id" class="inline-flex items-center cursor-pointer select-none">
                <input 
                  type="checkbox" 
                  :id="'toggle-' + req.id" 
                  class="sr-only peer"
                  :checked="req.visible"
                  @change="handleToggleVisibility(req.id, $event.target.checked)"
                >
                <div class="w-9 h-5 bg-zinc-200 dark:bg-zinc-800 rounded-full relative peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-zinc-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-primary"></div>
              </label>
            </div>
            <div v-else class="text-xs text-zinc-400 dark:text-zinc-500 italic">
              Esperando respuesta...
            </div>

            <div class="flex items-center gap-2">
              <Button 
                v-if="!req.finished"
                variant="outline"
                class="text-xs border-zinc-200 dark:border-zinc-700 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg h-9 text-zinc-700 dark:text-zinc-300 flex items-center gap-1.5"
                @click="handleRemind(req.id)"
                :disabled="loading"
              >
                <Send class="w-3.5 h-3.5" />
                <span>Recordar</span>
              </Button>
              <Button 
                v-if="!req.finished"
                variant="ghost"
                class="text-xs text-rose-500 hover:text-rose-600 hover:bg-rose-500/10 rounded-lg h-9 flex items-center gap-1.5"
                @click="confirmDelete(req.id)"
                :disabled="loading"
              >
                <Trash2 class="w-3.5 h-3.5" />
                <span>Cancelar</span>
              </Button>
              <Button 
                v-else
                variant="ghost"
                class="text-xs text-primary hover:text-primary-hover rounded-lg h-9"
                @click="openAnswersModal(req)"
              >
                Ver Respuestas
              </Button>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- DETAIL ANSWERS MODAL -->
    <div v-if="showAnswersModal && selectedRequest" class="fixed inset-0 z-[1000] flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm">
      <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-white/5 rounded-3xl max-w-lg w-full p-6 shadow-2xl space-y-6 animate-in fade-in zoom-in-95 duration-200 text-zinc-900 dark:text-zinc-100">
        
        <!-- Modal Header -->
        <div class="flex items-center justify-between border-b border-zinc-100 dark:border-zinc-800 pb-3">
          <div class="space-y-0.5">
            <h3 class="text-lg font-bold text-zinc-900 dark:text-white">Detalle de Respuestas</h3>
            <p class="text-xs text-zinc-500 dark:text-zinc-400">Feedback recibido de {{ selectedRequest.targetName }} {{ selectedRequest.targetSurname }}</p>
          </div>
          <button 
            @click="closeAnswersModal"
            class="text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 p-1.5 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg transition-colors"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Modal Content -->
        <div class="space-y-4 max-h-[60vh] overflow-y-auto pr-1">
          <!-- Referent Details -->
          <div class="grid grid-cols-2 gap-4 bg-zinc-50 dark:bg-zinc-800/40 p-4 rounded-2xl border border-zinc-100 dark:border-zinc-800/50">
            <div>
              <span class="text-[10px] text-zinc-400 dark:text-zinc-500 font-bold uppercase tracking-wider block">Relación</span>
              <span class="text-sm font-semibold text-zinc-800 dark:text-zinc-200">
                {{ relationshipIdLabels[selectedRequest.relationshipId] || 'Desconocido' }}
              </span>
            </div>
            <div>
              <span class="text-[10px] text-zinc-400 dark:text-zinc-500 font-bold uppercase tracking-wider block">Fecha de Envío</span>
              <span class="text-sm font-semibold text-zinc-800 dark:text-zinc-200">
                {{ formatDate(selectedRequest.createdAt) }}
              </span>
            </div>
            <div>
              <span class="text-[10px] text-zinc-400 dark:text-zinc-500 font-bold uppercase tracking-wider block">Confianza Verificada</span>
              <span class="text-sm font-semibold flex items-center gap-1.5 text-zinc-800 dark:text-zinc-200">
                <ShieldCheck class="w-4 h-4 text-primary" />
                {{ getTrustLabel(selectedRequest.trustScore) }} ({{ selectedRequest.trustScore }}%)
              </span>
            </div>
            <div>
              <span class="text-[10px] text-zinc-400 dark:text-zinc-500 font-bold uppercase tracking-wider block">Email</span>
              <span class="text-xs font-semibold text-zinc-800 dark:text-zinc-200 break-all">
                {{ selectedRequest.targetEmail }}
              </span>
            </div>
          </div>

          <!-- Qualitative Comments (Free Text Question) -->
          <div class="space-y-2">
            <h4 class="text-xs font-bold text-zinc-400 dark:text-zinc-500 uppercase tracking-wider">Comentarios o Opinión Adicional</h4>
            <div 
              v-if="selectedRequest.extraAnswers?.comments" 
              class="p-4 bg-primary/5 border border-primary/10 dark:border-primary/20 rounded-2xl relative"
            >
              <p class="text-sm text-zinc-700 dark:text-zinc-300 italic leading-relaxed">
                "{{ selectedRequest.extraAnswers.comments }}"
              </p>
            </div>
            <div 
              v-else 
              class="p-4 bg-zinc-50 dark:bg-zinc-800/30 border border-dashed border-zinc-200 dark:border-zinc-800 rounded-2xl text-center text-zinc-500 dark:text-zinc-400 text-xs italic"
            >
              El referente no proporcionó comentarios cualitativos adicionales.
            </div>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="flex justify-end pt-3 border-t border-zinc-100 dark:border-zinc-800">
          <Button 
            @click="closeAnswersModal" 
            class="bg-zinc-100 dark:bg-zinc-800 hover:bg-zinc-200 dark:hover:bg-zinc-700 text-zinc-800 dark:text-zinc-200 rounded-xl px-5 h-10"
          >
            Cerrar
          </Button>
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
          <h3 class="text-lg font-bold text-zinc-900 dark:text-white">¿Cancelar solicitud?</h3>
        </div>
        <p class="text-sm text-zinc-500 dark:text-zinc-400 leading-relaxed">
          ¿Estás seguro de que quieres cancelar esta solicitud de feedback? Esta acción no se puede deshacer.
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
