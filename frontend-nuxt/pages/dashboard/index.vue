<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
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
  HelpCircle,
  Clock,
  Copy,
  ExternalLink,
  ArrowUpRight,
  Check
} from 'lucide-vue-next'
import { Button } from '~/components/ui/button'

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

onMounted(async () => {
  if (import.meta.client) {
    await Promise.all([
      profileStore.fetchProfile(),
      experienceStore.fetchExperiences(),
      analyticsStore.fetchMetrics(),
      feedbackStore.fetchRequests()
    ])
  }
})

const metrics = computed(() => analyticsStore.metrics)
const averageScore = computed(() => metrics.value?.averageScore || 0)

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

const mockMetrics = {
  teamwork: 4.8,
  proactivity: 4.4,
  integrity: 4.7,
  selfConfidence: 4.2,
  flexibility: 4.5
}

const isExporting = ref(false)
const isGeneratingPDF = ref(false)

const certifiedRefs = computed(() => requests.value.filter(r => r.finished && r.visible).length)
const pendingRequests = computed(() => requests.value.filter(r => !r.finished).length)

const recentRequests = computed(() => {
  return [...requests.value]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 4)
})

const publicSlug = computed(() => profile.value?.username || profile.value?.userId || authStore.user?.id || profile.value?.id || '')

const copyProfileLink = () => {
  if (import.meta.client) {
    const url = `${window.location.origin}/u/${publicSlug.value}`
    navigator.clipboard.writeText(url).then(() => {
      isCopied.value = true
      setTimeout(() => {
        isCopied.value = false
      }, 2500)
    }).catch(err => {
      console.error('Error copying link:', err)
    })
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
                  <ShieldCheck class="w-5 h-5" />
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

            <div class="w-full relative aspect-square flex items-center justify-center p-2 rounded-2xl bg-zinc-50/50 dark:bg-zinc-900/30 border border-zinc-100 dark:border-white/[0.02]">
              <ClientOnly>
                <LazySkillsRadarChart :metrics="mockMetrics" />
              </ClientOnly>
              <div class="absolute top-3 right-3 bg-zinc-900/80 dark:bg-black/80 backdrop-blur-md px-2.5 py-1 rounded-full border border-white/10 flex items-center gap-1 shadow-lg">
                <Star class="w-3 h-3 text-amber-400 fill-current" />
                <span class="text-xs font-bold text-white">4.6 Avg</span>
              </div>
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
        
        <div class="relative overflow-hidden bg-gradient-to-tr from-primary/10 via-primary/[0.03] to-transparent dark:from-primary/20 dark:via-primary/[0.05] border border-zinc-200 dark:border-white/5 rounded-3xl p-8 shadow-sm backdrop-blur-md flex flex-col md:flex-row items-center justify-between gap-6">
          <div class="absolute top-0 right-0 w-72 h-72 bg-primary/10 dark:bg-primary/5 rounded-full blur-3xl pointer-events-none"></div>
          
          <div class="flex items-center gap-5 text-center md:text-left flex-col md:flex-row">
            <div class="w-20 h-20 rounded-full overflow-hidden bg-zinc-100 dark:bg-zinc-800 border-2 border-primary/30 flex items-center justify-center flex-shrink-0">
              <NuxtImg v-if="profile?.photoUrl" :src="profile.photoUrl" alt="Avatar" format="webp" loading="lazy" class="w-full h-full object-cover" />
              <UserIcon v-else class="w-10 h-10 text-zinc-400" />
            </div>
            <div class="space-y-1">
              <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white font-heading">
                {{ $t('dashboard.welcome', { name: profile?.name || '' }) }}
              </h1>
              <p class="text-zinc-500 dark:text-zinc-400 text-sm">
                {{ $t('dashboard.subtitle') }}
              </p>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row items-center gap-3 w-full sm:w-auto justify-center">
            <button 
              @click="navigateTo('/profile')"
              class="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-white dark:bg-white/5 border border-zinc-200 dark:border-white/5 text-zinc-700 dark:text-zinc-300 hover:bg-zinc-50 dark:hover:bg-white/10 transition-all duration-200 text-sm font-semibold whitespace-nowrap"
            >
              {{ $t('profile.title') }}
            </button>
            <Button 
              @click="navigateTo('/feedback/new')"
              class="w-full sm:w-auto bg-primary hover:bg-primary/95 text-white flex items-center justify-center gap-1.5 px-4 py-2.5 rounded-xl font-semibold shadow-lg shadow-primary/20 whitespace-nowrap"
            >
              <Plus class="w-4 h-4" />
              {{ $t('dashboard.quickActions.requestFeedback') }}
            </Button>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          
          <div class="lg:col-span-7 space-y-8 order-2 lg:order-1">
            
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              
              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-32">
                <div class="flex justify-between items-start">
                  <span class="text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">{{ $t('dashboard.metrics.verifiedSkills') }}</span>
                  <div class="p-1.5 bg-primary/10 rounded-lg text-primary">
                    <Award class="w-4.5 h-4.5" />
                  </div>
                </div>
                <div class="mt-2">
                  <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ averageScore.toFixed(1) }}</span>
                  <span class="text-xs text-zinc-500 dark:text-zinc-400 font-semibold ml-1">/ 5.0</span>
                </div>
              </div>

              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-32">
                <div class="flex justify-between items-start">
                  <span class="text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">{{ $t('dashboard.metrics.completedFeedback') }}</span>
                  <div class="p-1.5 bg-emerald-500/10 rounded-lg text-emerald-500">
                    <ShieldCheck class="w-4.5 h-4.5" />
                  </div>
                </div>
                <div class="mt-2">
                  <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ certifiedRefs }}</span>
                  <span class="text-xs text-zinc-500 dark:text-zinc-400 font-semibold ml-1.5">{{ $t('profile.verificationBadge') }}</span>
                </div>
              </div>

              <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-2xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between h-32">
                <div class="flex justify-between items-start">
                  <span class="text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">{{ $t('dashboard.metrics.pendingRequests') }}</span>
                  <div class="p-1.5 bg-amber-500/10 rounded-lg text-amber-500">
                    <Clock class="w-4.5 h-4.5" />
                  </div>
                </div>
                <div class="mt-2">
                  <span class="text-3xl font-black text-zinc-900 dark:text-white">{{ pendingRequests }}</span>
                  <span class="text-xs text-zinc-500 dark:text-zinc-400 font-semibold ml-1.5">{{ $t('feedback.status.PENDING') }}</span>
                </div>
              </div>

            </div>

            <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 shadow-sm">
              <h2 class="text-lg font-bold text-zinc-900 dark:text-white uppercase tracking-wider mb-4 font-heading">
                {{ $t('dashboard.quickActions.title') }}
              </h2>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <button 
                  @click="copyProfileLink"
                  class="flex items-center justify-between p-4 rounded-xl bg-zinc-50 dark:bg-white/[0.02] hover:bg-zinc-100 dark:hover:bg-white/5 border border-zinc-200/50 dark:border-white/5 text-left group transition-all duration-300"
                >
                  <div class="space-y-0.5">
                    <span class="text-sm font-bold block" :class="isCopied ? 'text-emerald-400' : 'text-zinc-900 dark:text-white'">
                      {{ isCopied ? $t('common.copied') : $t('profile.copyPublicUrl') }}
                    </span>
                    <span class="text-xs text-zinc-500 dark:text-zinc-400">{{ $t('dashboard.copyLink') }}</span>
                  </div>
                  <Check v-if="isCopied" class="w-5 h-5 text-emerald-400" />
                  <Copy v-else class="w-5 h-5 text-zinc-400 group-hover:text-primary transition-colors" />
                </button>

                <button 
                  @click="navigateTo(`/u/${publicSlug}`)"
                  class="flex items-center justify-between p-4 rounded-xl bg-zinc-50 dark:bg-white/[0.02] hover:bg-zinc-100 dark:hover:bg-white/5 border border-zinc-200/50 dark:border-white/5 text-left group transition-all duration-300"
                >
                  <div class="space-y-0.5">
                    <span class="text-sm font-bold text-zinc-900 dark:text-white block">{{ $t('dashboard.openPublic') }}</span>
                    <span class="text-xs text-zinc-500 dark:text-zinc-400">{{ $t('dashboard.publicProfileLink') }}</span>
                  </div>
                  <ExternalLink class="w-5 h-5 text-zinc-400 group-hover:text-primary transition-colors" />
                </button>
              </div>
            </div>

            <div class="bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 shadow-sm space-y-4">
              <div class="flex items-center justify-between">
                <h2 class="text-lg font-bold text-zinc-900 dark:text-white uppercase tracking-wider font-heading">
                  {{ $t('dashboard.recentFeedback') }}
                </h2>
                <button 
                  @click="navigateTo('/feedback')"
                  class="text-xs text-primary font-bold hover:underline flex items-center gap-1.5"
                >
                  {{ $t('feedback.listTitle') }}
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
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold border"
                      :class="{
                        'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20': req.trustScore >= 80,
                        'bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/20': req.trustScore >= 50 && req.trustScore < 80,
                        'bg-orange-500/10 text-orange-600 dark:text-orange-400 border-orange-500/20': req.trustScore >= 30 && req.trustScore < 50,
                        'bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/20': req.trustScore < 30
                      }"
                    >
                      {{ $t('feedback.status.COMPLETED') }} ({{ getTrustLabel(req.trustScore) }})
                    </span>
                    <span 
                      v-else 
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-amber-500/10 text-amber-600 dark:text-amber-400"
                    >
                      {{ $t('feedback.status.PENDING') }}
                    </span>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-zinc-500 dark:text-zinc-400 text-sm italic">
                {{ $t('dashboard.noFeedbackYet') }}
              </div>
            </div>

          </div>

          <div class="lg:col-span-5 bg-white dark:bg-white/[0.02] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 sm:p-8 backdrop-blur-md relative overflow-hidden shadow-sm order-1 lg:order-2">
            <div class="absolute -top-12 -left-12 w-40 h-40 bg-primary/10 dark:bg-primary/5 rounded-full blur-2xl pointer-events-none"></div>
            
            <div class="space-y-4 mb-6">
              <div class="flex items-center gap-2">
                <span class="p-1.5 rounded-lg bg-primary/10 text-primary">
                  <ShieldCheck class="w-5 h-5" />
                </span>
                <span class="text-sm font-bold tracking-wide uppercase text-zinc-500 dark:text-zinc-400">{{ $t('dashboard.radarTitle') }}</span>
              </div>
              <p class="text-xs text-zinc-500 dark:text-zinc-400 leading-relaxed">
                {{ $t('dashboard.radarSubtitle') }}
              </p>
            </div>

            <div class="w-full relative aspect-square flex items-center justify-center p-2 rounded-2xl bg-zinc-50/50 dark:bg-zinc-900/30 border border-zinc-100 dark:border-white/[0.02]">
              <ClientOnly>
                <LazySkillsRadarChart v-if="metrics" :metrics="metrics" />
                <div v-else class="text-center py-20 text-zinc-500 text-sm italic">
                  {{ $t('dashboard.noFeedbackYet') }}
                </div>
              </ClientOnly>
            </div>

            <div class="mt-6 flex items-start gap-2.5 bg-primary/5 dark:bg-primary/[0.03] border border-primary/10 p-3 rounded-xl" v-if="metrics">
              <HelpCircle class="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
              <p class="text-xs text-zinc-600 dark:text-zinc-400 leading-relaxed">
                <strong>{{ $t('extraFeedback.didYouKnow') }}</strong> {{ $t('extraFeedback.didYouKnowPdf') }}
              </p>
            </div>
          </div>

        </div>

      </div>
    </template>
  </div>
</template>
