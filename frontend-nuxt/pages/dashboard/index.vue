<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import SkillsRadarChart360 from '~/components/dashboard/SkillsRadarChart360.vue'
import FitCulturalCard from '~/components/profile/FitCulturalCard.vue'
import type { UserProfile } from '~/types'
import { useProfileStore } from '~/stores/profile.store'
import { useExperienceStore } from '~/stores/experience.store'
import { useAnalyticsStore } from '~/stores/analytics.store'
import { useFeedbackStore } from '~/stores/feedback.store'
import { useAuthStore } from '~/stores/auth.store'
import { 
  User as UserIcon, 
  Star, 
  Sparkles, 
  ArrowRight, 
  CheckCircle2, 
  Circle, 
  ShieldCheck, 
  UserPlus, 
  Plus, 
  Award,
  Dna,
  HelpCircle,
  Clock,
  Copy,
  ExternalLink, 
  ArrowUpRight,
  Check,
  Send
} from 'lucide-vue-next'
import { Button } from '~/components/ui/button'
import { toast } from 'vue-sonner'

definePageMeta({
  layout: 'default'
})

const profileStore = useProfileStore()
const experienceStore = useExperienceStore()
const analyticsStore = useAnalyticsStore()
const feedbackStore = useFeedbackStore()
const authStore = useAuthStore()
const { t } = useI18n()

const isCopied = ref(false)
const publicProfileData = ref<UserProfile | null>(null)

onMounted(async () => {
  if (import.meta.client) {
    await Promise.all([
      profileStore.fetchProfile(),
      experienceStore.fetchExperiences(),
      analyticsStore.fetchMetrics(),
      feedbackStore.fetchRequests()
    ])
    const userId = authStore.user?.id
    if (userId) {
      try {
        publicProfileData.value = await $api<UserProfile>(`/public/profile/${userId}`)
      } catch (e) {
        console.error('Error fetching dashboard public profile:', e)
      }
    }
  }
})

const metrics = computed(() => analyticsStore.metrics)
const averageScore = computed(() => metrics.value?.averageScore || 0)

const trustScorePercent = computed(() => {
  const score = metrics.value?.averageScore || 0
  if (score === 0) return 0
  return score > 5 ? Math.round(score) : Math.round(score * 20)
})

const trustLevelBadge = computed(() => {
  const val = trustScorePercent.value
  if (val >= 80) return { label: 'Nivel Oro', color: 'text-amber-300 bg-amber-500/20 border-amber-500/40' }
  if (val >= 50) return { label: 'Nivel Plata', color: 'text-zinc-200 bg-zinc-500/20 border-zinc-400/30' }
  if (val >= 30) return { label: 'Nivel Bronce', color: 'text-orange-300 bg-orange-500/20 border-orange-500/40' }
  return { label: 'Inicial', color: 'text-zinc-400 bg-zinc-800 border-zinc-700' }
})

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { year: 'numeric', month: 'short', day: 'numeric' })
}

const profile = computed(() => profileStore.profile)
const experiences = computed(() => experienceStore.sortedByDate)
const requests = computed(() => feedbackStore.requests)
const loading = computed(() => profileStore.loading || experienceStore.loading || analyticsStore.loading || feedbackStore.loading)

const isNewUser = computed(() => !profile.value)
const hasExperiences = computed(() => experiences.value.length > 0)
const hasFeedback = computed(() => !!metrics.value)
const showOnboarding = computed(() => !profile.value || experiences.value.length === 0)

const isExporting = ref(false)
const isGeneratingPDF = ref(false)

const certifiedRefs = computed(() => (requests.value || []).filter(r => r.finished && r.visible).length)
const pendingRequests = computed(() => (requests.value || []).filter(r => !r.finished).length)

const recentRequests = computed(() => {
  return [...(requests.value || [])]
    .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
    .slice(0, 4)
})

const publicSlug = computed(() => profile.value?.username || profile.value?.userId || authStore.user?.id || profile.value?.id || '')

const copyProfileLink = () => {
  if (import.meta.client) {
    const url = `${window.location.origin}/u/${publicSlug.value}`
    navigator.clipboard.writeText(url).then(() => {
      isCopied.value = true
      toast.success(t('dashboard.linkCopied', 'Enlace copiado al portapapeles'))
      setTimeout(() => {
        isCopied.value = false
      }, 2500)
    }).catch(err => {
      console.error('Error copying link:', err)
      toast.error('No se pudo copiar el enlace')
    })
  }
}

const remindingId = ref<string | number | null>(null)

const handleSendEmailReminder = async (req: any) => {
  try {
    remindingId.value = req.id
    await feedbackStore.remindRequest(req.id)
    toast.success(t('feedback.toast.remind', 'Recordatorio oficial enviado por email con éxito.'))
  } catch (err: any) {
    toast.error(err?.message || t('errors.generic', 'No se pudo enviar el recordatorio. Por favor, reintenta.'))
  } finally {
    remindingId.value = null
  }
}

const getTrustLabel = (score: number) => {
  if (score >= 80) return t('home.trustProtocol.level1')
  if (score >= 50) return t('home.trustProtocol.level2')
  if (score >= 30) return t('home.trustProtocol.level3')
  return t('home.trustProtocol.level4')
}
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">
    
    <!-- Loading State -->
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
      <!-- ONBOARDING DASHBOARD -->
      <div v-if="showOnboarding" class="max-w-5xl mx-auto px-4 py-8 space-y-12 animate-in fade-in slide-in-from-bottom-4 duration-500">
        
        <div class="relative overflow-hidden bg-gradient-to-tr from-primary/10 via-primary/[0.05] to-transparent dark:from-primary/20 dark:via-primary/[0.05] border border-primary/20 dark:border-primary/10 rounded-3xl p-8 md:p-10 shadow-sm backdrop-blur-md">
          <div class="absolute top-0 right-0 w-80 h-80 bg-primary/10 dark:bg-primary/5 rounded-full blur-3xl -translate-y-1/3 translate-x-1/3 pointer-events-none"></div>

          <div class="relative z-10 max-w-3xl space-y-4 text-center md:text-left">
            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-primary/20 text-primary uppercase tracking-wider">
              <Sparkles class="w-3.5 h-3.5" />
              {{ $t('dashboard.onboarding.badge') }}
            </span>
            <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight text-zinc-900 dark:text-white font-heading">
              {{ $t('dashboard.onboarding.titlePrefix') }} <span class="text-primary">{{ $t('dashboard.onboarding.titleHighlight') }}</span>
            </h1>
            <p class="text-zinc-600 dark:text-zinc-300 text-base md:text-lg leading-relaxed max-w-2xl" v-html="$t('dashboard.onboarding.welcomeText')">
            </p>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          
          <div class="lg:col-span-7 space-y-6">
            <h2 class="text-xl font-bold tracking-wide text-zinc-900 dark:text-white uppercase font-heading flex items-center gap-2">
              <Award class="w-5 h-5 text-primary" />
              {{ $t('dashboard.onboarding.planTitle') }}
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
                        <span class="text-xs font-bold text-primary uppercase tracking-wider">{{ $t('dashboard.onboarding.step', { num: 1 }) }}</span>
                        <span v-if="!isNewUser" class="text-xs font-medium text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">{{ $t('dashboard.onboarding.statusCompleted') }}</span>
                        <span v-else class="text-xs font-medium text-amber-500 bg-amber-500/10 px-2 py-0.5 rounded-full">{{ $t('dashboard.onboarding.statusRequired') }}</span>
                      </div>
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white">{{ $t('dashboard.onboarding.step1Title') }}</h3>
                      <p class="text-sm text-zinc-500 dark:text-zinc-400 mt-1 max-w-md">
                        {{ $t('dashboard.onboarding.step1Desc') }}
                      </p>
                    </div>
                    <Button 
                      @click="navigateTo('/profile/edit')"
                      :variant="!isNewUser ? 'outline' : 'default'"
                      size="sm"
                      class="flex-shrink-0"
                    >
                      <UserPlus class="w-4 h-4 mr-2" />
                      {{ !isNewUser ? $t('dashboard.onboarding.editProfile') : $t('dashboard.onboarding.createProfile') }}
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
                        <span class="text-xs font-bold text-primary uppercase tracking-wider">{{ $t('dashboard.onboarding.step', { num: 2 }) }}</span>
                        <span v-if="hasExperiences" class="text-xs font-medium text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">{{ $t('dashboard.onboarding.statusCompleted') }}</span>
                        <span v-else class="text-xs font-medium text-zinc-400 bg-zinc-100 dark:bg-white/5 px-2 py-0.5 rounded-full">{{ $t('dashboard.onboarding.statusPending') }}</span>
                      </div>
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white">{{ $t('dashboard.onboarding.step2Title') }}</h3>
                      <p class="text-sm text-zinc-500 dark:text-zinc-400 mt-1 max-w-md">
                        {{ $t('dashboard.onboarding.step2Desc') }}
                      </p>
                    </div>
                    <Button 
                      @click="navigateTo('/experiences/new')"
                      :disabled="isNewUser"
                      :variant="hasExperiences ? 'outline' : 'default'"
                      size="sm"
                      class="flex-shrink-0"
                    >
                      <Plus class="w-4 h-4 mr-2" />
                      {{ $t('dashboard.onboarding.addExperience') }}
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
                        <span class="text-xs font-bold text-primary uppercase tracking-wider">{{ $t('dashboard.onboarding.step', { num: 3 }) }}</span>
                        <span v-if="hasFeedback" class="text-xs font-medium text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">{{ $t('dashboard.onboarding.statusCompleted') }}</span>
                        <span v-else class="text-xs font-medium text-zinc-400 bg-zinc-100 dark:bg-white/5 px-2 py-0.5 rounded-full">{{ $t('dashboard.onboarding.statusPending') }}</span>
                      </div>
                      <h3 class="text-lg font-bold text-zinc-900 dark:text-white">{{ $t('dashboard.onboarding.step3Title') }}</h3>
                      <p class="text-sm text-zinc-500 dark:text-zinc-400 mt-1 max-w-md">
                        {{ $t('dashboard.onboarding.step3Desc') }}
                      </p>
                    </div>
                    <Button 
                      @click="navigateTo('/feedback/new')"
                      :disabled="!hasExperiences"
                      variant="default"
                      size="sm"
                      class="flex-shrink-0 bg-primary hover:bg-primary/90 text-white"
                    >
                      <ArrowRight class="w-4 h-4 mr-2" />
                      {{ $t('dashboard.onboarding.requestFeedback') }}
                    </Button>
                  </div>
                </div>
              </div>

            </div>
          </div>

          <div class="lg:col-span-5 bg-white/40 dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 sm:p-8 backdrop-blur-md relative overflow-hidden shadow-sm">
            <div class="absolute -top-12 -left-12 w-40 h-40 bg-primary/10 dark:bg-primary/5 rounded-full blur-2xl pointer-events-none"></div>
            
            <div class="space-y-4 mb-6">
              <div class="flex items-center gap-2">
                <span class="p-1.5 rounded-lg bg-primary/10 text-primary">
                  <Dna class="w-5 h-5" />
                </span>
                <span class="text-sm font-bold tracking-wide uppercase text-zinc-500 dark:text-zinc-400">{{ $t('dashboard.onboarding.liveDemo') }}</span>
              </div>
              <h3 class="text-xl font-bold text-zinc-900 dark:text-white font-heading">
                {{ $t('dashboard.onboarding.futureTitle') }}
              </h3>
              <p class="text-xs text-zinc-500 dark:text-zinc-400 leading-relaxed">
                {{ $t('dashboard.onboarding.futureDesc') }}
              </p>
            </div>

            <div class="w-full relative flex items-center justify-center p-2 rounded-2xl bg-zinc-50/50 dark:bg-zinc-900/30 border border-zinc-100 dark:border-white/[0.02]">
              <ClientOnly>
                <SkillsRadarChart360 />
              </ClientOnly>
            </div>

            <div class="mt-6 flex items-start gap-2.5 bg-primary/5 dark:bg-primary/[0.03] border border-primary/10 p-3 rounded-xl">
              <HelpCircle class="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
              <p class="text-xs text-zinc-600 dark:text-zinc-400 leading-relaxed" v-html="$t('dashboard.onboarding.didYouKnow')">
              </p>
            </div>
          </div>

        </div>

      </div>

      <!-- STANDARD DASHBOARD -->
      <div v-else class="max-w-6xl mx-auto px-4 py-8 space-y-8 animate-in fade-in duration-500">
        
        <!-- Header Banner Integrado con Quick Actions -->
        <div class="relative overflow-hidden bg-gradient-to-r from-zinc-900/90 via-[hsl(228,15%,10%)] to-amber-950/25 border border-zinc-200/20 dark:border-white/10 rounded-3xl p-6 sm:p-8 shadow-xl backdrop-blur-xl flex flex-col lg:flex-row items-start lg:items-center justify-between gap-6">
          <div class="absolute top-0 right-0 w-80 h-80 bg-primary/10 dark:bg-primary/5 rounded-full blur-3xl pointer-events-none"></div>
          
          <div class="flex items-center gap-4 sm:gap-5">
            <div class="relative w-16 h-16 sm:w-20 sm:h-20 rounded-2xl overflow-hidden bg-zinc-100 dark:bg-zinc-800 border-2 border-primary/40 flex items-center justify-center flex-shrink-0 shadow-lg shadow-primary/10">
              <img v-if="profile?.photoUrl" :src="profile.photoUrl" alt="Avatar" loading="lazy" class="w-full h-full object-cover" />
              <UserIcon v-else class="w-8 h-8 sm:w-10 sm:h-10 text-zinc-400" />
              <span class="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-emerald-500 border-2 border-zinc-900 flex items-center justify-center">
                <ShieldCheck class="w-3 h-3 text-zinc-950" />
              </span>
            </div>
            <div class="space-y-1">
              <div class="flex flex-wrap items-center gap-2">
                <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white font-heading">
                  {{ $t('dashboard.welcome', { name: profile?.name || '' }) }}
                </h1>
                <span class="inline-flex items-center gap-1 text-[11px] font-extrabold px-2.5 py-0.5 rounded-full border shadow-sm" :class="trustLevelBadge.color">
                  <Sparkles class="w-3 h-3" />
                  {{ trustLevelBadge.label }} ({{ trustScorePercent }}%)
                </span>
              </div>
              <p class="text-zinc-500 dark:text-zinc-400 text-xs sm:text-sm">
                {{ $t('dashboard.subtitle') }}
              </p>
            </div>
          </div>

          <!-- Action Buttons Group -->
          <div class="flex flex-wrap items-center gap-2.5 w-full lg:w-auto">
            <button 
              @click="copyProfileLink"
              class="flex-1 sm:flex-initial inline-flex items-center justify-center gap-1.5 px-3.5 py-2.5 rounded-xl bg-white dark:bg-white/5 border border-zinc-200 dark:border-white/10 text-zinc-700 dark:text-zinc-200 hover:bg-zinc-100 dark:hover:bg-white/10 transition-all text-xs font-semibold"
            >
              <Check v-if="isCopied" class="w-3.5 h-3.5 text-emerald-400" />
              <Copy v-else class="w-3.5 h-3.5 text-zinc-400" />
              <span :class="{ 'text-emerald-400 font-bold': isCopied }">{{ isCopied ? $t('common.copied') : $t('dashboard.copyLink') }}</span>
            </button>

            <button 
              @click="navigateTo(`/u/${publicSlug}`)"
              class="flex-1 sm:flex-initial inline-flex items-center justify-center gap-1.5 px-3.5 py-2.5 rounded-xl bg-white dark:bg-white/5 border border-zinc-200 dark:border-white/10 text-zinc-700 dark:text-zinc-200 hover:bg-zinc-100 dark:hover:bg-white/10 transition-all text-xs font-semibold"
            >
              <ExternalLink class="w-3.5 h-3.5 text-zinc-400" />
              <span>{{ $t('dashboard.openPublic') }}</span>
            </button>

            <Button 
              @click="navigateTo('/feedback/new')"
              class="w-full sm:w-auto bg-gradient-to-r from-amber-400 via-primary to-orange-500 hover:brightness-110 text-zinc-950 flex items-center justify-center gap-1.5 px-5 py-2.5 rounded-xl font-black shadow-lg shadow-primary/20 text-xs transition-all"
            >
              <Plus class="w-4 h-4" />
              {{ $t('dashboard.quickActions.requestFeedback') }}
            </Button>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          
          <!-- Left Main Area: KPIs & Recent Certifications -->
          <div class="lg:col-span-7 space-y-6 order-2 lg:order-1">
            
            <!-- 3 KPI Cards -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              
              <!-- KPI 1: Trust Score Corrected -->
              <div class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/60 dark:border-white/10 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-34 hover:border-primary/40 transition-all">
                <div class="flex justify-between items-start">
                  <span class="text-[11px] font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">{{ $t('dashboard.metrics.trustScore', 'Índice de Confianza') }}</span>
                  <div class="p-1.5 bg-amber-500/10 rounded-lg text-amber-400 border border-amber-500/20">
                    <ShieldCheck class="w-4 h-4" />
                  </div>
                </div>
                <div class="mt-2 space-y-1">
                  <div class="flex items-baseline gap-1.5">
                    <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ trustScorePercent }}%</span>
                    <span class="text-[11px] font-bold text-emerald-400">Certificado</span>
                  </div>
                  <span class="inline-block text-[10px] font-semibold text-zinc-400">Protocolo Let's Trust 360°</span>
                </div>
              </div>

              <!-- KPI 2: Certificaciones 360 -->
              <div class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/60 dark:border-white/10 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-34 hover:border-emerald-500/40 transition-all">
                <div class="flex justify-between items-start">
                  <span class="text-[11px] font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">{{ $t('dashboard.metrics.completedFeedback', 'Certificaciones 360°') }}</span>
                  <div class="p-1.5 bg-emerald-500/10 rounded-lg text-emerald-400 border border-emerald-500/20">
                    <Award class="w-4 h-4" />
                  </div>
                </div>
                <div class="mt-2 space-y-1">
                  <div class="flex items-baseline gap-1.5">
                    <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ certifiedRefs }}</span>
                    <span class="text-[11px] font-semibold text-zinc-400">{{ $t('profile.verificationBadge') }}</span>
                  </div>
                  <span class="inline-block text-[10px] font-semibold text-emerald-400">Ponderadas en perfil</span>
                </div>
              </div>

              <!-- KPI 3: Solicitudes Pendientes -->
              <div class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/60 dark:border-white/10 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-34 hover:border-amber-500/40 transition-all">
                <div class="flex justify-between items-start">
                  <span class="text-[11px] font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">{{ $t('dashboard.metrics.pendingRequests', 'En Espera') }}</span>
                  <div class="p-1.5 bg-amber-500/10 rounded-lg text-amber-400 border border-amber-500/20">
                    <Clock class="w-4 h-4" />
                  </div>
                </div>
                <div class="mt-2 space-y-1">
                  <div class="flex items-baseline gap-1.5">
                    <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ pendingRequests }}</span>
                    <span class="text-[11px] font-semibold text-amber-400">{{ $t('feedback.status.PENDING') }}</span>
                  </div>
                  <span class="inline-block text-[10px] font-semibold text-zinc-400">Listas para recordar</span>
                </div>
              </div>

            </div>

            <!-- Certificaciones Recientes con Acción Directa de WhatsApp -->
            <div class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/60 dark:border-white/10 rounded-3xl p-6 shadow-sm space-y-4">
              <div class="flex items-center justify-between border-b border-zinc-100 dark:border-white/5 pb-3">
                <div class="flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-primary animate-pulse"></span>
                  <h2 class="text-base font-bold text-zinc-900 dark:text-white uppercase tracking-wider font-heading">
                    {{ $t('dashboard.recentFeedback', 'Certificaciones Recientes') }}
                  </h2>
                </div>
                <button 
                  @click="navigateTo('/feedback')"
                  class="text-xs text-primary font-bold hover:underline flex items-center gap-1.5"
                >
                  {{ $t('feedback.listTitle', 'Ver todas') }}
                  <ArrowUpRight class="w-3.5 h-3.5" />
                </button>
              </div>

              <div v-if="recentRequests.length > 0" class="divide-y divide-zinc-200/50 dark:divide-white/5">
                <div v-for="req in recentRequests" :key="req.id" class="py-3.5 flex flex-col sm:flex-row sm:items-center justify-between gap-3 first:pt-1 last:pb-1">
                  <div class="space-y-1">
                    <div class="flex items-center gap-2">
                      <span class="text-sm font-bold text-zinc-900 dark:text-white block">
                        {{ req.targetName }} {{ req.targetSurname || '' }}
                      </span>
                    </div>
                    <span class="text-xs text-zinc-500 dark:text-zinc-400 block break-all">
                      {{ req.targetEmail }} · {{ formatDate(req.createdAt || '') }}
                    </span>
                  </div>

                  <div class="flex items-center gap-2 self-start sm:self-auto">
                    <span 
                      v-if="req.finished" 
                      class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold border"
                      :class="{
                        'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20': (req.trustScore ?? 0) >= 80,
                        'bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/20': (req.trustScore ?? 0) >= 50 && (req.trustScore ?? 0) < 80,
                        'bg-orange-500/10 text-orange-600 dark:text-orange-400 border-orange-500/20': (req.trustScore ?? 0) >= 30 && (req.trustScore ?? 0) < 50,
                        'bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/20': (req.trustScore ?? 0) < 30
                      }"
                    >
                      <ShieldCheck class="w-3 h-3 mr-1" />
                      {{ $t('feedback.status.COMPLETED') }} ({{ getTrustLabel(req.trustScore ?? 0) }})
                    </span>

                    <template v-else>
                      <button 
                        @click="handleSendEmailReminder(req)"
                        :disabled="remindingId === req.id"
                        class="inline-flex items-center gap-1.5 px-3 py-1 rounded-xl bg-amber-500/10 hover:bg-amber-500/20 border border-amber-500/30 text-amber-300 text-xs font-bold transition-all disabled:opacity-50"
                        :title="$t('feedback.actions.resend', 'Reenviar recordatorio oficial por email')"
                      >
                        <Send class="w-3 h-3 text-amber-400" />
                        <span>{{ remindingId === req.id ? 'Enviando...' : $t('feedback.actions.resend', 'Reenviar Email') }}</span>
                      </button>
                      <span class="inline-flex items-center px-2 py-1 rounded-lg text-[11px] font-semibold bg-amber-500/10 text-amber-500 border border-amber-500/20">
                        {{ $t('feedback.status.PENDING') }}
                      </span>
                    </template>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-zinc-500 dark:text-zinc-400 text-sm italic">
                {{ $t('dashboard.noFeedbackYet', 'Aún no has recibido certificaciones completadas.') }}
              </div>
            </div>

          </div>

          <!-- Right Column: Sticky Talent DNA & Cultural Fit -->
          <div class="lg:col-span-5 space-y-6 order-1 lg:order-2 lg:sticky lg:top-6 self-start">
            <div class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/60 dark:border-white/10 rounded-3xl p-5 sm:p-6 backdrop-blur-md relative overflow-hidden shadow-xl">
              <div class="absolute -top-12 -left-12 w-40 h-40 bg-primary/10 dark:bg-primary/5 rounded-full blur-2xl pointer-events-none"></div>
              
              <div class="space-y-1 mb-4 border-b border-zinc-100 dark:border-white/5 pb-3 flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="p-1.5 rounded-lg bg-primary/10 text-primary">
                    <Dna class="w-4 h-4" />
                  </span>
                  <span class="text-xs font-extrabold tracking-wide uppercase text-zinc-900 dark:text-white">{{ $t('dashboard.radarTitle') }}</span>
                </div>
                <span class="text-[10px] font-bold text-amber-400 bg-amber-500/10 px-2 py-0.5 rounded-md border border-amber-500/20">360° Live</span>
              </div>

              <div class="w-full relative flex items-center justify-center p-1 rounded-2xl bg-zinc-50/50 dark:bg-zinc-900/30 border border-zinc-100 dark:border-white/[0.02]">
                <ClientOnly>
                  <SkillsRadarChart360 :metrics="publicProfileData?.skillsMultiLayer" :archetype="publicProfileData?.archetype" />
                </ClientOnly>
              </div>

              <div class="mt-4 flex items-start gap-2.5 bg-primary/5 dark:bg-primary/[0.03] border border-primary/10 p-3 rounded-xl" v-if="metrics">
                <HelpCircle class="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                <p class="text-xs text-zinc-600 dark:text-zinc-400 leading-relaxed">
                  <strong>{{ $t('extraFeedback.didYouKnow') }}</strong> {{ $t('extraFeedback.didYouKnowPdf') }}
                </p>
              </div>
            </div>

            <!-- Fit Cultural Card en Dashboard -->
            <FitCulturalCard v-if="publicProfileData?.archetype" :data="publicProfileData.archetype" />
          </div>

        </div>

      </div>
    </template>
  </div>
</template>
