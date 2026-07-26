<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Briefcase, Calendar, Award, ArrowLeft, Download, ShieldCheck, Eye } from 'lucide-vue-next'
import { useAuthStore } from '~/stores/auth.store'
import type { UserProfile, ExperienceMetric, TopSkill } from '~/types'

definePageMeta({
  layout: 'public'
})

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()
const idOrSlug = computed(() => route.params.id as string)

const { data: profileData, error: fetchError, status } = await useAsyncData(
  `public-profile-${idOrSlug.value}`,
  () => $api<UserProfile>(`/public/profile/${idOrSlug.value}`),
  { watch: [idOrSlug] }
)

const profile = computed(() => profileData.value)
const loading = computed(() => status.value === 'pending')
const error = computed(() => fetchError.value ? 'El perfil solicitado no existe o no está disponible públicamente.' : null)
const userId = computed(() => profileData.value?.userId || null)

const isExporting = ref(false)
const isGeneratingPDF = ref(false)

const expandedExperiences = ref<Record<string | number, boolean>>({})

const toggleExperienceBreakdown = (expId: string | number) => {
  expandedExperiences.value[expId] = !expandedExperiences.value[expId]
}

const getMetricsForExperience = (expId: string | number): ExperienceMetric | null => {
  if (!profile.value || !profile.value.experienceMetrics) return null
  return profile.value.experienceMetrics.find((m: ExperienceMetric) => m.experienceId === expId) || null
}

const getTrustLevelLabel = (score: number) => {
  if (score >= 80) return t('home.trustProtocol.level1')
  if (score >= 50) return t('home.trustProtocol.level2')
  if (score >= 30) return t('home.trustProtocol.level3')
  return t('home.trustProtocol.level4')
}

const relationshipLabels = computed<Record<string, string>>(() => ({
  DIRECT_MANAGER: t('feedback.relationships.SUPERVISOR'),
  COLLEAGUE: t('feedback.relationships.PEER'),
  SUBORDINATE: t('feedback.relationships.SUBORDINATE'),
  CLIENT: t('feedback.relationships.CLIENT'),
  OTHER: t('feedback.relationships.OTHER')
}))

const categoryLabels = computed<Record<string, string>>(() => ({
  TEAMWORK: t('questionnaire.categories.TEAMWORK.name'),
  SELF_CONFIDENCE: t('questionnaire.categories.SELF_CONFIDENCE.name'),
  PROACTIVITY: t('questionnaire.categories.PROACTIVITY.name'),
  INTEGRITY: t('questionnaire.categories.INTEGRITY.name'),
  FLEXIBILITY: t('questionnaire.categories.FLEXIBILITY.name')
}))

const profileFullName = computed(() => {
  if (!profile.value) return ''
  return `${profile.value.name || ''} ${profile.value.surname || ''}`.trim()
})

const requestUrl = useRequestURL()

const getAbsoluteImageUrl = (url?: string) => {
  if (!url) return `${requestUrl.origin}/logo-cache.png`
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return `${requestUrl.origin}${url.startsWith('/') ? '' : '/'}${url}`
}

const pageTitle = computed(() => {
  if (profile.value) {
    const fullName = profileFullName.value
    return t('seo.profileTitle', { name: fullName })
  }
  return t('seo.defaultTitle')
})

const pageDesc = computed(() => {
  if (profile.value?.headline || profile.value?.bio) {
    return profile.value.headline || profile.value.bio
  }
  return profile.value ? t('seo.profileDesc', { name: profileFullName.value }) : t('seo.defaultDescription')
})

const pageImage = computed(() => getAbsoluteImageUrl(profile.value?.photoUrl || profile.value?.avatarUrl))

useSeoMeta({
  title: () => pageTitle.value,
  ogTitle: () => pageTitle.value,
  twitterTitle: () => pageTitle.value,
  description: () => pageDesc.value,
  ogDescription: () => pageDesc.value,
  twitterDescription: () => pageDesc.value,
  ogImage: () => pageImage.value,
  twitterImage: () => pageImage.value,
  ogUrl: () => requestUrl.href,
  ogType: 'profile',
  ogSiteName: 'Caché',
  twitterCard: 'summary_large_image',
})

useHead({
  link: [
    {
      rel: 'canonical',
      href: () => requestUrl.href
    }
  ]
})

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { month: 'short', year: 'numeric' })
}

const exportPDF = async () => {
  if (isExporting.value || !profile.value) return
  
  isExporting.value = true
  isGeneratingPDF.value = true

  if (import.meta.client) {
    const html2pdfModule = await import('html2pdf.js')
    const html2pdf = html2pdfModule.default || html2pdfModule
    setTimeout(() => {
      const element = document.getElementById('pdf-profile-template')
      if (!element) {
        console.error('El elemento PDF no se encontró en el DOM.')
        isExporting.value = false
        isGeneratingPDF.value = false
        return
      }
      
      const opt = {
        margin:       [10, 10, 15, 10],
        filename:     `informe_micache_${profile.value.name}_${profile.value.surname}.pdf`,
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
      }

      html2pdf()
        .from(element)
        .set(opt)
        .save()
        .then(() => {
          isExporting.value = false
          isGeneratingPDF.value = false
        })
        .catch((err: unknown) => {
          console.error('Error al exportar PDF:', err)
          isExporting.value = false
          isGeneratingPDF.value = false
        })
    }, 200)
  }
}

const topSkill = computed(() => {
  if (!profile.value || !profile.value.skills) return null
  const skills = profile.value.skills
  const candidates = [
    { key: 'teamwork', categoryKey: 'TEAMWORK' },
    { key: 'proactivity', categoryKey: 'PROACTIVITY' },
    { key: 'integrity', categoryKey: 'INTEGRITY' },
    { key: 'selfConfidence', categoryKey: 'SELF_CONFIDENCE' },
    { key: 'flexibility', categoryKey: 'FLEXIBILITY' }
  ]
  
  let best = candidates[0]
  let maxVal = skills[best.key] || 0
  
  for (let i = 1; i < candidates.length; i++) {
    const val = skills[candidates[i].key] || 0
    if (val > maxVal) {
      maxVal = val
      best = candidates[i]
    }
  }
  
  return maxVal > 0 ? best : null
})

const isOwnProfile = computed(() => {
  if (!authStore.isAuthenticated) return false
  const currentUserId = authStore.user?.id
  if (!currentUserId) return false
  return (
    userId.value === currentUserId ||
    profile.value?.userId === currentUserId ||
    profile.value?.id === currentUserId ||
    profile.value?.username === currentUserId
  )
})

const goBack = () => {
  if (import.meta.client) {
    if (window.history.length > 1) {
      router.back()
    } else {
      navigateTo('/')
    }
  }
}
</script>

<template>
  <div :class="['max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 min-h-screen', authStore.isAuthenticated ? 'py-6' : 'py-12']">
    
    <!-- Loading State -->
    <div v-if="loading" class="space-y-8 animate-pulse">
      <div class="flex items-center justify-between gap-4">
        <div class="w-24 h-10 bg-white/5 border border-white/5 rounded-xl"></div>
        <div class="w-44 h-10 bg-white/5 border border-white/5 rounded-xl"></div>
      </div>
      <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-8 shadow-2xl">
        <div class="flex flex-col md:flex-row gap-6 items-center md:items-start">
          <div class="w-32 h-32 rounded-full bg-white/5 flex-shrink-0"></div>
          <div class="flex-1 text-center md:text-left space-y-4 w-full">
            <div class="h-8 bg-white/5 rounded w-1/3 mx-auto md:mx-0"></div>
            <div class="h-5 bg-white/5 rounded w-1/4 mx-auto md:mx-0"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-500/10 border border-red-500/20 text-red-500 p-8 rounded-2xl text-center space-y-4 max-w-lg mx-auto mt-12">
      <h2 class="text-xl font-bold">Perfil No Encontrado</h2>
      <p class="text-sm text-zinc-400">{{ error }}</p>
      <button @click="navigateTo('/')" class="px-4 py-2 rounded-xl bg-primary text-white text-sm font-bold">
        Ir al Inicio
      </button>
    </div>

    <!-- Profile Content -->
    <div v-else-if="profile" class="space-y-8 animate-in fade-in-50 duration-500">
      
      <!-- Header Actions (Sólo visibles para el propio usuario dueño del perfil) -->
      <div v-if="isOwnProfile" class="flex items-center justify-between gap-3 w-full">
        <button 
          @click="goBack"
          class="inline-flex items-center justify-center gap-1.5 px-3 py-2.5 rounded-xl bg-[hsl(228,15%,9%)] border border-white/5 text-zinc-400 hover:text-white hover:bg-[hsl(228,15%,12%)] transition-all duration-200 text-xs sm:text-sm font-semibold whitespace-nowrap"
        >
          <ArrowLeft class="w-4 h-4 flex-shrink-0" />
          {{ $t('common.back') }}
        </button>
        
        <button 
          @click="exportPDF"
          :disabled="isExporting"
          class="inline-flex items-center justify-center gap-1.5 px-4 py-2.5 rounded-xl bg-primary text-white hover:bg-primary/90 shadow-lg shadow-primary/20 transition-all duration-200 text-xs sm:text-sm font-semibold disabled:opacity-50 disabled:cursor-not-allowed whitespace-nowrap"
        >
          <Download class="w-4 h-4 flex-shrink-0" :class="{'animate-bounce': isExporting}" />
          {{ isExporting ? 'PDF...' : 'PDF' }}
        </button>
      </div>

      <!-- Candidate Read Only Banner -->
      <div 
        v-if="authStore.isAuthenticated && userId !== authStore.user?.id" 
        class="bg-amber-500/10 border border-amber-500/20 text-amber-600 dark:text-amber-400 p-5 rounded-2xl flex items-center justify-between shadow-lg"
      >
        <div class="flex items-center space-x-3.5">
          <div class="p-2.5 bg-amber-500/10 dark:bg-amber-500/20 rounded-xl text-amber-500">
            <Eye class="w-5 h-5" />
          </div>
          <div>
            <h4 class="text-sm font-bold text-zinc-900 dark:text-zinc-100">{{ $t('extraProfile.readOnlyTitle') }}</h4>
            <p class="text-xs text-zinc-500 dark:text-zinc-400 mt-0.5">
              {{ $t('extraProfile.readOnlyDesc') }}
            </p>
          </div>
        </div>
        <div class="hidden sm:block px-3 py-1 bg-amber-500/15 border border-amber-500/25 rounded-lg text-[10px] font-bold uppercase tracking-widest text-amber-500">
          {{ $t('extraProfile.candidateView') }}
        </div>
      </div>

      <!-- Hero Banner -->
      <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-6 sm:p-8 backdrop-blur-xl shadow-2xl relative">
        <div class="flex flex-col md:flex-row gap-6 items-center md:items-start">
          <div class="w-32 h-32 rounded-full bg-[hsl(228,15%,15%)] flex items-center justify-center text-4xl font-bold text-primary border-2 border-white/10 shadow-inner flex-shrink-0">
            <img v-if="profile.photoUrl" :src="profile.photoUrl" alt="Avatar" loading="lazy" class="w-full h-full rounded-full object-cover" />
            <span v-else>{{ profile.name?.charAt(0) }}{{ profile.surname?.charAt(0) }}</span>
          </div>

          <div class="flex-1 text-center md:text-left space-y-2">
            <h1 class="text-3xl font-bold text-white tracking-tight">{{ profile.name }} {{ profile.surname }}</h1>
            <p class="text-lg text-primary font-semibold flex flex-wrap items-center justify-center md:justify-start gap-3">
              <span>{{ profile.jobTitle || $t('sidebar.professionalRole') }}</span>
              <span v-if="profile.totalReferencesCount > 0" class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 shadow-lg select-none">
                <ShieldCheck class="w-3.5 h-3.5 mr-1 text-emerald-400" />
                {{ profile.totalReferencesCount }} {{ profile.totalReferencesCount === 1 ? $t('extraProfile.certifiedReference') : $t('extraProfile.certifiedReferences') }}
              </span>
            </p>
            <p class="text-[hsl(220,10%,75%)] mt-4 max-w-2xl text-balance">
              {{ profile.aboutMe || $t('profile.noBio') }}
            </p>
          </div>
        </div>
      </div>

      <!-- Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        <!-- Left Column: Sticky Radar Chart & Verification Summary -->
        <div class="lg:col-span-5 xl:col-span-4 space-y-6 lg:sticky lg:top-6 self-start">
          
          <!-- Skills (Radar Chart) -->
          <div class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl">
            <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
              <Award class="w-5 h-5 text-primary" />
              {{ $t('profile.softSkillsTitle') }}
            </h2>
            <div v-if="profile.skills" class="h-80 flex items-center justify-center">
              <ClientOnly>
                <LazySkillsRadarChart :metrics="profile.skills" />
              </ClientOnly>
            </div>
            <div v-else class="text-center py-20 text-[hsl(220,10%,40%)]">
              {{ $t('dashboard.noFeedbackYet') }}
            </div>
          </div>

          <!-- Certification Summary Card -->
          <div v-if="profile.skills" class="bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl space-y-5">
            <h3 class="text-lg font-bold text-white border-b border-white/5 pb-3 flex items-center gap-2">
              <ShieldCheck class="w-5 h-5 text-emerald-400" />
              {{ $t('profile.verificationBadge') }}
            </h3>
            <div class="space-y-4">
              <!-- Global Average -->
              <div class="flex items-center justify-between">
                <span class="text-zinc-400 text-sm">{{ $t('dashboard.metrics.trustScore') }}</span>
                <div class="flex items-center text-amber-400 gap-1.5">
                  <span class="text-sm font-bold text-white">{{ profile.skills.averageScore?.toFixed(1) || '0.0' }}</span>
                  <div class="flex items-center">
                    <svg v-for="star in 5" :key="star" class="w-3.5 h-3.5" :class="star <= Math.round(profile.skills.averageScore || 0) ? 'fill-current' : 'text-zinc-700'" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                </div>
              </div>

              <!-- References Count -->
              <div class="flex items-center justify-between">
                <span class="text-zinc-400 text-sm">{{ $t('dashboard.metrics.verifiedSkills') }}</span>
                <span class="text-sm font-bold text-white flex items-center gap-1">
                  <ShieldCheck class="w-4 h-4 text-emerald-400" />
                  {{ profile.totalReferencesCount || 0 }}
                </span>
              </div>

              <!-- Top Skill -->
              <div class="flex items-center justify-between" v-if="topSkill">
                <span class="text-zinc-400 text-sm">Top Skill</span>
                <span class="text-xs font-bold px-2.5 py-1 rounded-lg bg-primary/10 text-primary border border-primary/20">
                  {{ $t('questionnaire.categories.' + topSkill.categoryKey + '.name') }}
                </span>
              </div>
            </div>
          </div>

        </div>

        <!-- Experience Timeline -->
        <div class="lg:col-span-7 xl:col-span-8 bg-[hsl(228,15%,9%)] border border-white/5 rounded-2xl p-4 sm:p-6 backdrop-blur-xl shadow-xl">
          <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <Briefcase class="w-5 h-5 text-primary" />
            {{ $t('experience.title') }}
          </h2>

          <div v-if="profile.experiences?.length > 0" class="space-y-6 relative before:absolute before:inset-y-0 before:left-2.5 before:w-px before:bg-white/5">
            <div v-for="exp in profile.experiences" :key="exp.id" class="relative pl-6 sm:pl-8 group">
              <!-- Timeline dot -->
              <div class="absolute left-2.5 top-2.5 w-3 h-3 rounded-full bg-primary -translate-x-1/2 group-hover:scale-125 transition-transform duration-200"></div>
              
              <div class="space-y-1">
                <h3 class="text-lg font-semibold text-white group-hover:text-primary transition-colors duration-200">{{ exp.position }}</h3>
                <p class="text-[hsl(220,10%,70%)] text-sm font-medium">{{ exp.companyName }} <span v-if="exp.department">· {{ exp.department }}</span></p>
                <div class="flex items-center gap-2 text-xs text-[hsl(220,10%,50%)]">
                  <Calendar class="w-3.5 h-3.5" />
                  <span>{{ formatDate(exp.startDate) }} - {{ exp.finishDate ? formatDate(exp.finishDate) : 'Present' }}</span>
                </div>
                <p class="text-sm text-[hsl(220,10%,60%)] mt-2 leading-relaxed whitespace-pre-wrap">{{ exp.functions || exp.description }}</p>

                <!-- Experience Rating Breakdown (Showable/Hideable) -->
                <div v-if="getMetricsForExperience(exp.id)" class="mt-4 p-4 rounded-xl bg-white/[0.01] border border-white/5 space-y-4">
                  <div class="flex flex-wrap items-center justify-between gap-3">
                    <!-- Rating and Reference Count -->
                    <div class="flex items-center gap-2">
                      <div class="flex items-center text-amber-400">
                        <span class="text-sm font-bold text-white mr-1.5">
                          {{ getMetricsForExperience(exp.id).averageScore?.toFixed(1) }}
                        </span>
                        <svg v-for="star in 5" :key="star" class="w-3.5 h-3.5" :class="star <= Math.round(getMetricsForExperience(exp.id).averageScore || 0) ? 'fill-current' : 'text-zinc-700'" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      </div>
                      <span class="text-xs text-[hsl(220,10%,55%)]">
                        ({{ getMetricsForExperience(exp.id).referencesCount }})
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
                        {{ $t('extraProfile.trustLevelTag', { level: getTrustLevelLabel(getMetricsForExperience(exp.id).averageTrustScore) }) }}
                      </span>
                    </div>

                    <!-- Action Toggle -->
                    <button 
                      @click="toggleExperienceBreakdown(exp.id)"
                      class="text-xs text-primary hover:text-primary-hover font-semibold flex items-center gap-1 transition-all duration-200"
                    >
                      <span>{{ expandedExperiences[exp.id] ? $t('extraProfile.hideBreakdown') : $t('extraProfile.viewBreakdown') }}</span>
                      <Eye class="w-3.5 h-3.5" />
                    </button>
                  </div>

                  <!-- Details Section (Accordion) -->
                  <div v-if="expandedExperiences[exp.id]" class="mt-4 pt-4 border-t border-white/5 space-y-4 animate-in fade-in slide-in-from-top-2 duration-300">
                    <!-- Soft Skills breakdown bars -->
                    <div class="space-y-3">
                      <h4 class="text-[10px] font-bold text-zinc-400 uppercase tracking-widest">{{ $t('extraProfile.softSkillsRole') }}</h4>
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
                    <div v-if="getMetricsForExperience(exp.id).relationshipCounts && Object.keys(getMetricsForExperience(exp.id).relationshipCounts).length > 0" class="pt-3 border-t border-white/5">
                      <h4 class="text-[10px] font-bold text-zinc-400 uppercase tracking-widest mb-2.5">{{ $t('extraProfile.evaluatorsDistribution') }}</h4>
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

                    <!-- Testimonials -->
                    <div v-if="getMetricsForExperience(exp.id).testimonials && getMetricsForExperience(exp.id).testimonials.length > 0" class="pt-4 border-t border-white/5 space-y-3">
                      <h4 class="text-[10px] font-bold text-zinc-400 uppercase tracking-widest">{{ $t('extraProfile.reviewsReceived') }}</h4>
                      <div class="space-y-3">
                        <div 
                          v-for="t in getMetricsForExperience(exp.id).testimonials" 
                          :key="t.createdAt"
                          class="p-4 rounded-xl bg-white/[0.02] border border-white/5 space-y-2 relative"
                        >
                          <div class="flex items-center justify-between text-xs flex-wrap gap-2">
                            <span class="font-bold text-zinc-300">
                              {{ relationshipLabels[t.relationshipCode] || t.relationshipCode }}
                            </span>
                            <span 
                              class="inline-flex items-center text-[10px] font-semibold"
                              :class="{
                                'text-emerald-400': t.trustScore >= 80,
                                'text-amber-400': t.trustScore >= 50 && t.trustScore < 80,
                                'text-orange-400': t.trustScore >= 30 && t.trustScore < 50,
                                'text-rose-400': t.trustScore < 30
                              }"
                            >
                              <ShieldCheck class="w-3.5 h-3.5 mr-1" />
                              {{ $t('extraProfile.trustLevelTag', { level: getTrustLevelLabel(t.trustScore) }) }}
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
          <div v-else class="text-center py-20 text-[hsl(220,10%,40%)]">
            {{ $t('dashboard.noExperiencesYet') }}
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
          {{ $t('extraProfile.generatingPdfTitle') }}
        </h3>
        <p class="text-xs text-zinc-400 max-w-xs leading-relaxed">
          {{ $t('extraProfile.generatingPdfDesc') }}
        </p>
      </div>
    </div>

    <!-- Premium PDF Template -->
    <div v-if="isGeneratingPDF" class="absolute left-0 top-0 z-[9999] bg-white" style="width: 700px; min-width: 700px; overflow: visible;">
      <div 
        id="pdf-profile-template" 
        class="bg-white text-zinc-900 font-sans relative overflow-hidden flex flex-col justify-between" 
        style="width: 700px; min-height: 1000px; box-sizing: border-box; padding: 24px;"
      >
        <!-- Watermark -->
        <div class="absolute inset-0 pointer-events-none select-none z-0 flex flex-col justify-around items-center overflow-hidden">
          <div class="text-6xl font-black uppercase tracking-[0.25em] transform -rotate-45 select-none" style="color: rgba(24, 24, 27, 0.035);">
            Verified by MiCaché
          </div>
          <div class="text-6xl font-black uppercase tracking-[0.25em] transform -rotate-45 select-none" style="color: rgba(24, 24, 27, 0.035);">
            Verified by MiCaché
          </div>
        </div>

        <div class="relative z-10 flex-1 flex flex-col justify-between" style="height: 100%;">
          <div>
            <!-- Header Banner -->
            <div class="flex items-center justify-between border-b pb-6" style="border-color: #e4e4e7; margin-bottom: 30px;">
              <div>
                <h2 class="text-2xl font-black tracking-tight" style="color: #f29727; margin: 0; line-height: 1.1;">MiCaché</h2>
                <p class="text-xs font-semibold text-zinc-500 uppercase tracking-widest" style="margin: 4px 0 0 0;">CERTIFIED PROFESSIONAL REPORT</p>
              </div>
              <div class="text-right">
                <div class="inline-flex items-center px-3 py-1 rounded-full bg-green-50 border border-green-200 text-[10px] font-bold text-green-700 uppercase tracking-wider">
                  ✓ VERIFIED
                </div>
                <p class="text-[10px] text-zinc-400" style="margin: 6px 0 0 0;">Issued: {{ new Date().toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }) }}</p>
              </div>
            </div>

            <!-- Candidate Info block -->
            <div class="flex items-start bg-zinc-50 border rounded-2xl p-6" style="border-color: #e4e4e7; margin-bottom: 30px;">
              <div 
                class="w-20 h-20 rounded-full flex items-center justify-center font-bold text-2xl flex-shrink-0 shadow-inner overflow-hidden"
                style="background-color: rgba(242, 151, 39, 0.1); color: #f29727; border: 1px solid rgba(242, 151, 39, 0.2); margin-right: 24px;"
              >
                <img v-if="profile.photoUrl" :src="profile.photoUrl" alt="Avatar" loading="lazy" class="w-full h-full object-cover" crossorigin="anonymous" />
                <span v-else>{{ profile.name?.charAt(0) }}{{ profile.surname?.charAt(0) }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <h3 class="text-xl font-extrabold text-zinc-950" style="margin: 0 0 4px 0; line-height: 1.2;">{{ profile.name }} {{ profile.surname }}</h3>
                <p class="text-sm font-bold" style="color: #f29727; margin: 0 0 8px 0; line-height: 1.2;">{{ profile.jobTitle }}</p>
                <p class="text-xs text-zinc-600 leading-relaxed" style="margin: 0; line-height: 1.5; word-wrap: break-word;">{{ profile.aboutMe || 'No personal biography provided.' }}</p>
              </div>
            </div>

            <!-- Soft Skills Metrics Section -->
            <div style="margin-bottom: 30px;">
              <h3 class="text-sm font-black uppercase tracking-wider text-zinc-400 border-b pb-2" style="border-color: #e4e4e7; display: flex; align-items: center; margin: 0 0 16px 0;">
                <Award class="w-4 h-4" style="color: #f29727; margin-right: 8px; display: inline-block; vertical-align: middle;" />
                <span style="display: inline-block; vertical-align: middle;">SOFT SKILLS & COMPETENCIES</span>
              </h3>
              
              <div v-if="profile.skills" class="grid grid-cols-2 gap-x-8 gap-y-4">
                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Teamwork</span>
                    <span style="color: #f29727;">{{ (profile.skills.teamwork || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((profile.skills.teamwork || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Proactivity</span>
                    <span style="color: #f29727;">{{ (profile.skills.proactivity || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((profile.skills.proactivity || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Integrity</span>
                    <span style="color: #f29727;">{{ (profile.skills.integrity || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((profile.skills.integrity || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Self-Confidence</span>
                    <span style="color: #f29727;">{{ (profile.skills.selfConfidence || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((profile.skills.selfConfidence || 0) / 5) * 100}%` }"
                      style="background: linear-gradient(90deg, #f29727 0%, #f5712d 100%);"
                    ></div>
                  </div>
                </div>

                <div>
                  <div class="flex justify-between items-center text-xs font-bold text-zinc-800" style="margin-bottom: 4px;">
                    <span>Flexibility</span>
                    <span style="color: #f29727;">{{ (profile.skills.flexibility || 0).toFixed(1) }} / 5.0</span>
                  </div>
                  <div class="w-full bg-zinc-100 rounded-full h-2.5" style="background-color: #f4f4f5; border: 1px solid #e4e4e7; overflow: hidden;">
                    <div 
                      class="h-full rounded-full" 
                      :style="{ width: `${((profile.skills.flexibility || 0) / 5) * 100}%` }"
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
                <span style="display: inline-block; vertical-align: middle;">CERTIFIED WORK HISTORY</span>
              </h3>
              
              <div v-if="profile.experiences && profile.experiences.length > 0">
                <div 
                  v-for="exp in profile.experiences" 
                  :key="exp.id" 
                  class="border-l-2 pl-4 py-1 relative" 
                  style="border-color: #f29727; margin-bottom: 20px; page-break-inside: avoid;"
                >
                  <h4 class="text-sm font-bold text-zinc-900" style="margin: 0 0 2px 0;">{{ exp.position }}</h4>
                  <p class="text-xs font-semibold text-zinc-600" style="margin: 0 0 2px 0;">{{ exp.companyName }} <span v-if="exp.department">· {{ exp.department }}</span></p>
                  <p class="text-[10px] text-zinc-400 font-medium" style="margin: 0 0 6px 0;">
                    {{ formatDate(exp.startDate) }} - {{ exp.finishDate ? formatDate(exp.finishDate) : 'Present' }}
                  </p>
                  <p class="text-xs text-zinc-500 leading-relaxed" style="margin: 0 0 8px 0; word-wrap: break-word; white-space: pre-wrap;">{{ exp.functions || exp.description }}</p>

                  <!-- Métricas Certificadas -->
                  <div v-if="getMetricsForExperience(exp.id)" class="p-3 bg-zinc-50 rounded-xl border border-zinc-200/60" style="margin-top: 8px;">
                    <div class="flex items-center justify-between flex-wrap" style="margin-bottom: 6px;">
                      <div class="flex items-center" style="gap: 8px;">
                        <div class="flex items-center text-amber-500">
                          <strong class="text-xs text-zinc-800" style="margin-right: 6px;">{{ getMetricsForExperience(exp.id).averageScore?.toFixed(1) }} / 5.0</strong>
                          <div class="flex items-center" style="gap: 2px;">
                            <svg v-for="star in 5" :key="star" class="w-3 h-3" :style="{ color: star <= Math.round(getMetricsForExperience(exp.id).averageScore || 0) ? '#f59e0b' : '#e4e4e7' }" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                            </svg>
                          </div>
                        </div>
                        <span class="text-[10px] text-zinc-500 font-semibold" style="margin-left: 6px;">
                          ({{ getMetricsForExperience(exp.id).referencesCount }} {{ getMetricsForExperience(exp.id).referencesCount === 1 ? 'reference' : 'references' }})
                        </span>
                      </div>
                      <div 
                        class="inline-flex items-center px-2 py-0.5 rounded text-[9px] font-bold uppercase tracking-wider border"
                        :class="{
                          'bg-emerald-50 text-emerald-700 border-emerald-200': getMetricsForExperience(exp.id).averageTrustScore >= 80,
                          'bg-amber-50 text-amber-700 border-amber-200': getMetricsForExperience(exp.id).averageTrustScore >= 50 && getMetricsForExperience(exp.id).averageTrustScore < 80,
                          'bg-orange-50 text-orange-700 border-orange-200': getMetricsForExperience(exp.id).averageTrustScore >= 30 && getMetricsForExperience(exp.id).averageTrustScore < 50,
                          'bg-rose-50 text-rose-700 border-rose-200': getMetricsForExperience(exp.id).averageTrustScore < 30
                        }"
                      >
                        ✓ Trust Level: {{ getTrustLevelLabel(getMetricsForExperience(exp.id).averageTrustScore) }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Bottom Footer Certificate -->
            <div class="pt-8 border-t flex justify-between items-end text-[9px] text-zinc-400" style="border-color: #e4e4e7; margin-top: 30px;">
              <div class="space-y-1 max-w-[70%]">
                <p class="font-bold text-zinc-500 uppercase tracking-wide" style="margin: 0 0 2px 0;">MiCaché B2B Authenticity Guarantee</p>
                <p class="leading-relaxed" style="margin: 0;">
                  This report has been certified using MiCaché's secure feedback protocol. The soft skills rating is the result of anonymized evaluations from verified peers and supervisors.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
