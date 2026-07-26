<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useFeedbackStore } from '~/stores/feedback.store'
import { ArrowLeft, Plus, Mail, Clock, CheckCircle2, ShieldCheck, Send, Trash2, AlertTriangle } from 'lucide-vue-next'
import { Button } from '~/components/ui/button'

definePageMeta({
  layout: 'default'
})

const feedbackStore = useFeedbackStore()
const { t } = useI18n()

onMounted(async () => {
  if (import.meta.client) {
    await feedbackStore.fetchRequests()
  }
})

const requests = computed(() => feedbackStore.requests)
const loading = computed(() => feedbackStore.loading)

const activeTab = ref('all')

const completedCount = computed(() => requests.value.filter(r => r.finished).length)
const pendingCount = computed(() => requests.value.filter(r => !r.finished).length)

const filteredRequests = computed(() => {
  if (activeTab.value === 'completed') return requests.value.filter(r => r.finished)
  if (activeTab.value === 'pending') return requests.value.filter(r => !r.finished)
  return requests.value
})

const toastState = ref({
  show: false,
  message: ''
})

const triggerToast = (message: string) => {
  toastState.value.message = message
  toastState.value.show = true
  setTimeout(() => {
    toastState.value.show = false
  }, 4000)
}

const handleToggleVisibility = async (requestId: string | number, visible: boolean) => {
  try {
    await feedbackStore.toggleRequestVisibility(requestId, visible)
    triggerToast(visible ? t('feedback.toast.visible') : t('feedback.toast.hidden'))
  } catch (err) {
    triggerToast(t('errors.generic'))
  }
}

const handleRemind = async (requestId: string | number) => {
  try {
    await feedbackStore.remindRequest(requestId)
    triggerToast(t('feedback.toast.remind'))
  } catch (err) {
    triggerToast(t('errors.generic'))
  }
}

const showDeleteModal = ref(false)
const requestToDelete = ref<any>(null)

const confirmDelete = (id: any) => {
  requestToDelete.value = id
  showDeleteModal.value = true
}

const executeDelete = async () => {
  if (requestToDelete.value) {
    try {
      await feedbackStore.deleteRequest(requestToDelete.value)
      triggerToast(t('feedback.toast.deleted'))
    } catch (err) {
      triggerToast(t('errors.generic'))
    } finally {
      showDeleteModal.value = false
      requestToDelete.value = null
    }
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  requestToDelete.value = null
}

const goBack = () => {
  useRouter().back()
}

const goToCreate = () => {
  navigateTo('/feedback/new')
}

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const FREE_DOMAINS = [
  'gmail.com', 'hotmail.com', 'outlook.com', 'yahoo.com', 'icloud.com',
  'proton.me', 'protonmail.com', 'aol.com', 'live.com', 'hotmail.es',
  'yahoo.es', 'live.cl', 'yandex.com', 'mail.com', 'zoho.com', 'gmx.com', 'outlook.es'
]

const isCorporate = (email?: string) => {
  if (!email || !email.includes('@')) return false
  const domain = email.substring(email.indexOf('@') + 1).toLowerCase().trim()
  return !FREE_DOMAINS.includes(domain)
}

const hasCorporate = (req: any) => isCorporate(req.targetEmail)
const hasPhone = (req: any) => !!req.targetPhone

const hasRegistered = (req: any) => {
  const base = (isCorporate(req.targetEmail) ? 30 : 0) + (req.targetPhone ? 10 : 0)
  const diff = req.trustScore - base
  return diff === 20 || diff === 60
}

const hasCompanyMatch = (req: any) => {
  const base = (isCorporate(req.targetEmail) ? 30 : 0) + (req.targetPhone ? 10 : 0)
  const diff = req.trustScore - base
  return diff === 40 || diff === 60
}

const getTrustLabel = (score: number) => {
  if (score >= 80) return t('home.trustProtocol.level1')
  if (score >= 50) return t('home.trustProtocol.level2')
  if (score >= 30) return t('home.trustProtocol.level3')
  return t('home.trustProtocol.level4')
}

const selectedRequest = ref<any>(null)
const showAnswersModal = ref(false)

const openAnswersModal = (req: any) => {
  selectedRequest.value = req
  showAnswersModal.value = true
}

const closeAnswersModal = () => {
  showAnswersModal.value = false
  selectedRequest.value = null
}

const relationshipIdLabels = computed<Record<number, string>>(() => ({
  0: t('feedback.relationships.SUPERVISOR'),
  1: t('feedback.relationships.PEER'),
  2: t('feedback.relationships.SUBORDINATE'),
  3: t('feedback.relationships.CLIENT'),
  4: t('feedback.relationships.OTHER')
}))
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- HEADER -->
    <div class="h-40 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
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
            <h1 class="text-2xl sm:text-3xl font-bold text-white tracking-tight">{{ $t('feedback.listTitle') }}</h1>
            <p class="text-white/80 text-xs sm:text-sm mt-0.5">{{ $t('feedback.listSubtitle') }}</p>
          </div>
        </div>

        <Button 
          @click="goToCreate"
          class="bg-gradient-to-r from-primary to-orange-500 hover:scale-[1.02] active:scale-[0.98] transition-all text-white border-0 rounded-xl px-3 sm:px-5 h-10 flex items-center gap-1.5 shadow-lg shadow-primary/20 flex-shrink-0"
        >
          <Plus class="w-4.5 h-4.5" />
          <span class="text-xs sm:text-sm hidden xs:inline">{{ $t('experience.add') }}</span>
        </Button>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div class="max-w-5xl mx-auto px-3 sm:px-6 -mt-10 relative z-20">
      
      <div class="flex space-x-1 bg-zinc-200/50 dark:bg-zinc-800/40 p-1 rounded-xl mb-6 max-w-md border border-zinc-200/30 dark:border-zinc-800/20 backdrop-blur-md">
        <button
          @click="activeTab = 'all'"
          class="flex-1 py-2 text-xs sm:text-sm font-medium rounded-lg transition-all duration-300 flex items-center justify-center gap-1.5"
          :class="activeTab === 'all' 
            ? 'bg-white dark:bg-zinc-900 text-primary dark:text-white shadow-sm border border-zinc-100 dark:border-zinc-800/50' 
            : 'text-zinc-600 dark:text-zinc-400 hover:text-zinc-900 dark:hover:text-white'"
        >
          <span>{{ $t('feedback.tabs.all') }}</span>
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
          <span>{{ $t('feedback.status.COMPLETED') }}</span>
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
          <span>{{ $t('feedback.status.PENDING') }}</span>
          <span class="px-1.5 py-0.5 text-[10px] rounded-md bg-amber-500/10 text-amber-600 dark:text-amber-400 font-bold">
            {{ pendingCount }}
          </span>
        </button>
      </div>
      
      <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div 
          v-for="i in 4" 
          :key="i"
          class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800/80 rounded-2xl p-6 space-y-4 animate-pulse"
        >
          <div class="flex justify-between items-start">
            <div class="space-y-2 flex-1">
              <div class="h-5 bg-zinc-200 dark:bg-zinc-800 rounded w-2/3"></div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="filteredRequests.length === 0" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center shadow-[0_20px_50px_rgba(0,0,0,0.05)]">
        <div class="w-16 h-16 bg-primary/10 rounded-2xl flex items-center justify-center mx-auto mb-6">
          <Mail class="w-8 h-8 text-primary" />
        </div>
        <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">
          {{ activeTab === 'pending' ? 'No hay solicitudes pendientes' : activeTab === 'completed' ? 'No hay valoraciones completadas' : 'No hay solicitudes aún' }}
        </h3>
        <Button v-if="activeTab === 'all' || activeTab === 'pending'" @click="goToCreate" class="bg-primary hover:bg-primary-hover text-white rounded-xl mt-4">
          Crear una solicitud
        </Button>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div 
          v-for="req in filteredRequests" 
          :key="req.id"
          class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-4 sm:p-6 shadow-sm flex flex-col justify-between"
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
              
              <span 
                v-if="req.finished" 
                class="inline-flex items-center px-2 py-1 sm:px-3 sm:py-1 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
              >
                <CheckCircle2 class="w-4 h-4 flex-shrink-0" />
                <span class="ml-1 hidden sm:inline">{{ $t('feedback.status.COMPLETED') }}</span>
              </span>
              <span 
                v-else 
                class="inline-flex items-center px-2 py-1 sm:px-3 sm:py-1 rounded-full text-xs font-medium bg-amber-500/10 text-amber-600 dark:text-amber-400"
              >
                <Clock class="w-4 h-4 flex-shrink-0" />
                <span class="ml-1 hidden sm:inline">{{ $t('feedback.status.PENDING') }}</span>
              </span>
            </div>

            <div class="space-y-2 text-sm text-zinc-600 dark:text-zinc-300">
              <div class="flex justify-between">
                <span>{{ $t('feedback.date') }}:</span>
                <span class="font-medium">{{ formatDate(req.createdAt) }}</span>
              </div>
              <div v-if="req.finished" class="flex justify-between items-center pt-1.5 border-t border-zinc-100 dark:border-zinc-800/40 mt-1.5">
                <span class="text-xs text-zinc-500 dark:text-zinc-400">{{ $t('feedback.referralTrust') }}:</span>
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
              </div>
            </div>
          </div>

          <div class="mt-6 pt-4 border-t border-zinc-100 dark:border-zinc-800 flex items-center justify-between">
            <div v-if="req.finished" class="flex items-center space-x-2">
              <span class="text-xs text-zinc-500 dark:text-zinc-400 font-medium">{{ $t('feedback.publicProfile') }}:</span>
              <label :for="'toggle-' + req.id" class="inline-flex items-center cursor-pointer select-none">
                <input 
                  type="checkbox" 
                  :id="'toggle-' + req.id" 
                  class="sr-only peer"
                  :checked="req.visible"
                  @change="handleToggleVisibility(req.id, ($event.target as HTMLInputElement).checked)"
                >
                <div class="w-9 h-5 bg-zinc-200 dark:bg-zinc-800 rounded-full relative peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-zinc-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-primary"></div>
              </label>
            </div>
            <div v-else class="text-xs text-zinc-400 dark:text-zinc-500 italic">
              {{ $t('feedback.waitingResponse') }}
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
                <span>{{ $t('feedback.actions.resend') }}</span>
              </Button>
              <Button 
                v-if="!req.finished"
                variant="ghost"
                class="text-xs text-rose-500 hover:text-rose-600 hover:bg-rose-500/10 rounded-lg h-9 flex items-center gap-1.5"
                @click="confirmDelete(req.id)"
                :disabled="loading"
              >
                <Trash2 class="w-3.5 h-3.5" />
                <span>{{ $t('feedback.actions.cancel') }}</span>
              </Button>
              <Button 
                v-else
                variant="ghost"
                class="text-xs text-primary hover:text-primary-hover rounded-lg h-9"
                @click="openAnswersModal(req)"
              >
                {{ $t('feedback.viewAnswers') }}
              </Button>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- DETAIL ANSWERS MODAL -->
    <div v-if="showAnswersModal && selectedRequest" class="fixed inset-0 z-[1000] flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm">
      <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-white/5 rounded-3xl max-w-lg w-full p-6 shadow-2xl space-y-6 text-zinc-900 dark:text-zinc-100">
        <div class="flex items-center justify-between border-b border-zinc-100 dark:border-zinc-800 pb-3">
          <div>
            <h3 class="text-lg font-bold text-zinc-900 dark:text-white">Detalle de Respuestas</h3>
            <p class="text-xs text-zinc-500 dark:text-zinc-400">Feedback de {{ selectedRequest.targetName }} {{ selectedRequest.targetSurname }}</p>
          </div>
          <button @click="closeAnswersModal" class="text-zinc-400 p-1.5 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg">
            ✕
          </button>
        </div>

        <div class="space-y-4 max-h-[60vh] overflow-y-auto">
          <div class="grid grid-cols-2 gap-4 bg-zinc-50 dark:bg-zinc-800/40 p-4 rounded-2xl">
            <div>
              <span class="text-[10px] text-zinc-400 uppercase tracking-wider block">Relación</span>
              <span class="text-sm font-semibold text-zinc-800 dark:text-zinc-200">
                {{ relationshipIdLabels[selectedRequest.relationshipId] || 'Desconocido' }}
              </span>
            </div>
            <div>
              <span class="text-[10px] text-zinc-400 uppercase tracking-wider block">Fecha</span>
              <span class="text-sm font-semibold text-zinc-800 dark:text-zinc-200">
                {{ formatDate(selectedRequest.createdAt) }}
              </span>
            </div>
          </div>

          <div class="space-y-2">
            <h4 class="text-xs font-bold text-zinc-400 uppercase tracking-wider">Comentarios adicionales</h4>
            <div v-if="selectedRequest.extraAnswers?.comments" class="p-4 bg-primary/5 border border-primary/10 rounded-2xl">
              <p class="text-sm text-zinc-700 dark:text-zinc-300 italic">"{{ selectedRequest.extraAnswers.comments }}"</p>
            </div>
            <div v-else class="p-4 bg-zinc-50 dark:bg-zinc-800/30 rounded-2xl text-center text-zinc-500 text-xs italic">
              Sin comentarios cualitativos adicionales.
            </div>
          </div>
        </div>

        <div class="flex justify-end pt-3 border-t border-zinc-100 dark:border-zinc-800">
          <Button @click="closeAnswersModal" class="bg-zinc-100 dark:bg-zinc-800 text-zinc-800 dark:text-zinc-200 rounded-xl px-5 h-10">
            Cerrar
          </Button>
        </div>
      </div>
    </div>

    <!-- Confirm Delete Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 z-[1000] flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm">
      <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-white/5 rounded-3xl max-w-sm w-full p-6 shadow-2xl space-y-4 text-zinc-900 dark:text-zinc-100">
        <div class="flex items-center gap-3 text-red-500">
          <AlertTriangle class="w-6 h-6" />
          <h3 class="text-lg font-bold text-zinc-900 dark:text-white">¿Cancelar solicitud?</h3>
        </div>
        <p class="text-sm text-zinc-500 dark:text-zinc-400">
          ¿Estás seguro de que quieres cancelar esta solicitud de feedback?
        </p>
        <div class="flex justify-end gap-3 pt-2">
          <button class="px-4 py-2 text-sm font-semibold rounded-xl bg-zinc-100 dark:bg-zinc-800 text-zinc-700 dark:text-zinc-300" @click="cancelDelete">
            Cancelar
          </button>
          <Button variant="destructive" class="px-4 py-2 text-sm font-semibold rounded-xl bg-red-600 text-white" @click="executeDelete">
            Eliminar
          </Button>
        </div>
      </div>
    </div>

    <!-- TOAST -->
    <div v-if="toastState.show" class="fixed bottom-6 right-6 z-50 flex items-center p-4 space-x-3 text-white bg-zinc-900 border border-white/10 rounded-2xl shadow-2xl">
      <div class="w-5 h-5 rounded-full bg-emerald-500 flex items-center justify-center text-xs font-bold text-white">✓</div>
      <p class="text-sm font-medium">{{ toastState.message }}</p>
    </div>
  </div>
</template>
