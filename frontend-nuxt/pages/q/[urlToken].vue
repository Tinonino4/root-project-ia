<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Check, AlertTriangle, ArrowLeft, ArrowRight, MessageSquare } from 'lucide-vue-next'
import { Button } from '~/components/ui/button'
import BehavioralQuestionCard from '~/components/questionnaire/BehavioralQuestionCard.vue'
import ForcedChoicePickTwo from '~/components/questionnaire/ForcedChoicePickTwo.vue'
import CulturalFitSelector from '~/components/questionnaire/CulturalFitSelector.vue'
import type { QuestionnaireData } from '~/types'

definePageMeta({
  layout: 'public',
  alias: ['/questionnaire/:urlToken', '/f/:urlToken']
})

const route = useRoute()
const urlToken = computed(() => route.params.urlToken as string)
const { t } = useI18n()

const { data: qData, error: fetchError, status } = await useAsyncData(
  `questionnaire-${urlToken.value}`,
  () => $api<QuestionnaireData>(`/questionnaire/${urlToken.value}`),
  { watch: [urlToken] }
)

const questionnaire = computed(() => qData.value)
const questions = computed(() => qData.value?.questions || [])
const loading = computed(() => status.value === 'pending')
const fetchErrorMsg = computed(() => fetchError.value ? (fetchError.value.message || t('questionnaire.errorMsg')) : null)
const submitError = ref<string | null>(null)
const error = computed(() => submitError.value || fetchErrorMsg.value)
const isSubmitting = ref(false)
const submitted = ref(false)

const answers = ref<Record<string, any>>({})
const currentStep = ref(0)
const additionalComments = ref('')

const candidateName = computed(() => questionnaire.value?.candidateName || 'Profesional')
const companyName = computed(() => questionnaire.value?.companyName || '')
const requestUrl = useRequestURL()
const logoUrl = computed(() => `${requestUrl.origin}/logo.svg`)

useSeoMeta({
  title: () => questionnaire.value
    ? t('seo.questionnaireTitle', { name: candidateName.value })
    : t('seo.defaultTitle'),
  ogTitle: () => questionnaire.value
    ? t('seo.questionnaireTitle', { name: candidateName.value })
    : t('seo.defaultTitle'),
  twitterTitle: () => questionnaire.value
    ? t('seo.questionnaireTitle', { name: candidateName.value })
    : t('seo.defaultTitle'),
  description: () => t('seo.questionnaireDesc'),
  ogDescription: () => t('seo.questionnaireDesc'),
  twitterDescription: () => t('seo.questionnaireDesc'),
  ogImage: () => logoUrl.value,
  twitterImage: () => logoUrl.value,
  ogUrl: () => requestUrl.href,
  ogSiteName: 'Caché',
  twitterCard: 'summary',
})

const totalSteps = computed(() => (questions.value.length || 0) + 1)

const isCurrentStepValid = computed(() => {
  if (!questionnaire.value) return false
  if (currentStep.value === questions.value.length) {
    return true
  }
  const currentQ = questions.value[currentStep.value]
  if (!currentQ) return false

  const ans = answers.value[currentQ.id]
  if (currentQ.type === 'FORCED_CHOICE') {
    return Array.isArray(ans) && ans.length === 2
  }
  return typeof ans === 'string' && ans.trim().length > 0
})

const isFormValid = computed(() => {
  if (!questionnaire.value || questions.value.length === 0) return false
  for (const q of questions.value) {
    const ans = answers.value[q.id]
    if (q.type === 'FORCED_CHOICE') {
      if (!Array.isArray(ans) || ans.length !== 2) return false
    } else {
      if (!ans || typeof ans !== 'string') return false
    }
  }
  return true
})

const nextStep = () => {
  if (currentStep.value < questions.value.length) {
    currentStep.value++
    if (import.meta.client) window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
    if (import.meta.client) window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const handleSubmit = async () => {
  if (!isFormValid.value) return
  isSubmitting.value = true
  submitError.value = null

  try {
    const formattedAnswers = questions.value.map(q => {
      const val = answers.value[q.id]
      const selectedOptionIds = Array.isArray(val) ? val : (val ? [val] : [])
      return {
        questionId: q.id,
        selectedOptionIds
      }
    })

    const payload = {
      answers: formattedAnswers,
      comments: additionalComments.value.trim() || undefined
    }

    await $api(`/questionnaire/${urlToken.value}`, {
      method: 'POST',
      body: payload
    })
    submitted.value = true
    if (import.meta.client) window.scrollTo({ top: 0, behavior: 'smooth' })
  } catch (e: unknown) {
    const err = e as { message?: string }
    submitError.value = err.message || t('questionnaire.submitError')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-[hsl(228,16%,7%)] font-sans relative pb-24 transition-colors duration-300">

    <div class="h-48 w-full bg-gradient-to-tr from-primary/90 via-primary/80 to-primary/60 dark:from-primary/60 dark:via-primary/40 dark:to-primary/20 relative overflow-hidden">
      <div class="max-w-3xl mx-auto px-6 h-full flex flex-col justify-center relative z-10 text-white">
        <h1 class="text-3xl font-bold tracking-tight">
          {{ $t('questionnaire.title') }}
        </h1>
        <p class="text-white/80 text-sm mt-1">
          Valoración conductual 360° para <strong class="text-white font-semibold">{{ candidateName }}</strong>
          <span v-if="companyName"> en {{ companyName }}</span>
        </p>
      </div>
    </div>

    <div class="max-w-3xl mx-auto px-6 -mt-12 relative z-20">

      <div v-if="loading" class="space-y-6 animate-pulse">
        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 shadow-sm flex items-center justify-between">
          <div class="w-32 h-6 bg-zinc-200 dark:bg-zinc-800 rounded-lg"></div>
          <div class="w-24 h-4 bg-zinc-200 dark:bg-zinc-800 rounded-lg"></div>
        </div>
      </div>

      <div v-else-if="error" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-red-200 dark:border-red-500/20 rounded-3xl p-8 text-center">
        <AlertTriangle class="w-12 h-12 text-red-500 mx-auto mb-4" />
        <h2 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">{{ $t('questionnaire.errorTitle') }}</h2>
        <p class="text-zinc-600 dark:text-zinc-400 text-sm">{{ error }}</p>
      </div>

      <div v-else-if="submitted" class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-12 text-center shadow-xl">
        <div class="w-16 h-16 bg-emerald-500/10 rounded-full flex items-center justify-center mx-auto mb-6">
          <Check class="w-8 h-8 text-emerald-500" />
        </div>
        <h2 class="text-2xl font-bold text-zinc-900 dark:text-white mb-2">{{ $t('questionnaire.successTitle') }}</h2>
        <p class="text-zinc-600 dark:text-zinc-400 text-sm max-w-md mx-auto">{{ $t('questionnaire.successMsg') }}</p>

        <div class="mt-8 flex flex-col sm:flex-row justify-center gap-4">
          <NuxtLink
            to="/login"
            class="inline-flex items-center justify-center px-6 py-3 rounded-xl bg-primary text-white font-semibold hover:bg-primary/95 shadow-lg shadow-primary/20"
          >
            {{ $t('questionnaire.goToLogin') }}
          </NuxtLink>
          <NuxtLink
            to="/"
            class="inline-flex items-center justify-center px-6 py-3 rounded-xl bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-700 dark:text-zinc-300 font-semibold"
          >
            {{ $t('questionnaire.goHome') }}
          </NuxtLink>
        </div>
      </div>

      <div v-else class="space-y-6">

        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-2xl p-6 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-primary/10 rounded-xl flex items-center justify-center text-primary font-bold">
              {{ currentStep + 1 }}
            </div>
            <div>
              <p class="text-xs text-zinc-400 dark:text-zinc-500 uppercase tracking-wider font-semibold">{{ $t('questionnaire.progress') }}</p>
              <h2 class="text-sm font-bold text-zinc-800 dark:text-zinc-200">
                Pregunta {{ Math.min(currentStep + 1, totalSteps) }} de {{ totalSteps }}
              </h2>
            </div>
          </div>

          <div class="flex items-center space-x-2">
            <button
              v-for="(stepIdx) in totalSteps"
              :key="stepIdx"
              @click="currentStep = stepIdx - 1"
              class="w-3.5 h-3.5 rounded-full transition-all duration-300"
              :class="[
                (stepIdx - 1) === currentStep
                  ? 'bg-primary scale-110 shadow-lg shadow-primary/20'
                  : (stepIdx - 1) < currentStep
                    ? 'bg-emerald-500/80 dark:bg-emerald-500/60'
                    : 'bg-zinc-200 dark:bg-zinc-800'
              ]"
            ></button>
          </div>
        </div>

        <div class="relative overflow-hidden min-h-[380px]">

          <div
            v-for="(q, qIdx) in questions"
            :key="q.id"
            v-show="qIdx === currentStep"
            class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl shadow-sm p-8"
          >
            <ForcedChoicePickTwo
              v-if="q.type === 'FORCED_CHOICE'"
              :question="q"
              v-model="answers[q.id]"
            />
            <CulturalFitSelector
              v-else-if="q.type === 'CULTURAL_FIT'"
              :question="q"
              v-model="answers[q.id]"
            />
            <BehavioralQuestionCard
              v-else
              :question="q"
              v-model="answers[q.id]"
            />
          </div>

          <div
            v-show="currentStep === questions.length"
            class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-8 shadow-sm"
          >
            <div class="mb-8 border-b border-zinc-100 dark:border-zinc-800/80 pb-4">
              <div class="flex items-center gap-2">
                <MessageSquare class="w-6 h-6 text-primary" />
                <h2 class="text-2xl font-bold text-zinc-900 dark:text-white">{{ $t('questionnaire.finishTitle') }}</h2>
              </div>
              <p class="text-zinc-500 dark:text-zinc-400 text-sm mt-2 leading-relaxed">
                {{ $t('questionnaire.finishDesc') }}
              </p>
            </div>

            <div class="space-y-4">
              <label for="additional-comments" class="text-sm font-semibold text-zinc-700 dark:text-zinc-300 leading-normal block">
                {{ $t('questionnaire.optionalComments') }}
              </label>
              <p class="text-zinc-400 dark:text-zinc-500 text-xs">
                {{ $t('questionnaire.commentsDesc') }}
              </p>
              <textarea
                id="additional-comments"
                v-model="additionalComments"
                rows="5"
                :placeholder="$t('questionnaire.commentsPlaceholder')"
                class="w-full p-4 bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700 rounded-2xl text-sm text-zinc-900 dark:text-white placeholder-zinc-400 dark:placeholder-zinc-500 focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200 resize-none"
              ></textarea>
            </div>
          </div>

        </div>

        <div class="backdrop-blur-xl bg-white/80 dark:bg-zinc-900/80 border border-zinc-200/50 dark:border-white/5 rounded-3xl p-6 flex flex-row items-center justify-between gap-4 shadow-sm">
          <div :class="currentStep > 0 ? 'flex-1 sm:flex-initial' : 'hidden sm:block text-xs text-zinc-400 dark:text-zinc-500'">
            <Button
              v-if="currentStep > 0"
              @click="prevStep"
              variant="outline"
              class="w-full sm:w-auto h-11 border-zinc-200 dark:border-zinc-800 rounded-xl px-6 flex items-center justify-center gap-2 hover:bg-zinc-100 dark:hover:bg-zinc-800 text-zinc-700 dark:text-zinc-300 font-medium"
            >
              <ArrowLeft class="w-4 h-4" />
              <span>{{ $t('questionnaire.back') }}</span>
            </Button>
            <div v-else class="text-xs text-zinc-400 dark:text-zinc-500 font-medium select-none">
              {{ $t('questionnaire.instruction') }}
            </div>
          </div>

          <div :class="currentStep > 0 ? 'flex-1 sm:flex-initial text-right' : 'w-full sm:w-auto text-right'">
            <Button
              v-if="currentStep < questions.length"
              @click="nextStep"
              :disabled="!isCurrentStepValid"
              class="w-full sm:w-auto h-11 bg-primary hover:bg-primary/90 text-white rounded-xl px-8 flex items-center justify-center gap-2 shadow-lg shadow-primary/10 disabled:opacity-50"
            >
              <span>{{ $t('questionnaire.next') }}</span>
              <ArrowRight class="w-4 h-4" />
            </Button>

            <Button
              v-else
              @click="handleSubmit"
              :disabled="!isFormValid || isSubmitting"
              class="w-full sm:w-auto h-12 bg-primary hover:bg-primary/95 text-white font-bold rounded-xl px-10 flex items-center justify-center space-x-2 shadow-xl shadow-primary/30"
            >
              <span v-if="isSubmitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></span>
              <template v-else>
                <Check class="w-5 h-5" />
                <span>{{ $t('questionnaire.submit') }}</span>
              </template>
            </Button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>
