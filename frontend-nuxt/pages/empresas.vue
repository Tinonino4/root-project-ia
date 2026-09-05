<script setup lang="ts">
import { ref, computed } from 'vue'
import { 
  ShieldCheck, 
  Target, 
  Users, 
  Search, 
  Award, 
  TrendingUp, 
  Zap, 
  Sparkles, 
  CheckCircle2, 
  ArrowRight, 
  Lock, 
  ChevronDown, 
  HelpCircle, 
  Briefcase, 
  Clock, 
  XCircle, 
  Layers, 
  Building2, 
  ExternalLink,
  Star
} from 'lucide-vue-next'

definePageMeta({
  layout: 'public'
})

const { t } = useI18n()
const requestUrl = useRequestURL()
const logoUrl = computed(() => `${requestUrl.origin}/logo.svg`)

useSeoMeta({
  title: () => t('companies.seo.title'),
  ogTitle: () => t('companies.seo.title'),
  twitterTitle: () => t('companies.seo.title'),
  description: () => t('companies.seo.description'),
  ogDescription: () => t('companies.seo.description'),
  twitterDescription: () => t('companies.seo.description'),
  ogImage: () => logoUrl.value,
  twitterImage: () => logoUrl.value,
  ogUrl: () => requestUrl.href,
  ogType: 'website',
  ogSiteName: 'Caché',
  twitterCard: 'summary_large_image',
})

const openFaq = ref<number | null>(0)
function toggleFaq(idx: number) {
  openFaq.value = openFaq.value === idx ? null : idx
}

const faqs = [
  { qKey: 'companies.faq.item1.q', aKey: 'companies.faq.item1.a' },
  { qKey: 'companies.faq.item2.q', aKey: 'companies.faq.item2.a' },
  { qKey: 'companies.faq.item3.q', aKey: 'companies.faq.item3.a' },
  { qKey: 'companies.faq.item4.q', aKey: 'companies.faq.item4.a' }
]

const activeArchetype = ref<'builder' | 'leader' | 'specialist'>('builder')

const sampleProfiles = {
  builder: {
    role: 'Lead Software Engineer',
    archetype: 'Builder Pragmático (0 a 1)',
    desc: 'Alta resiliencia ante la incertidumbre, velocidad de entrega y capacidad de desbloquear dependencias complejas.',
    trustScore: 97,
    autonomyScore: 78,
    speedScore: 72,
    domainCompany: 'Stripe & Cabify',
    superpower: 'Desbloquea cuellos de botella técnicos rápidamente'
  },
  leader: {
    role: 'Engineering Manager',
    archetype: 'Líder Facilitador & Coach',
    desc: 'Foco en desbloqueo del equipo, comunicación sintética y creación de cultura técnica de alto rendimiento.',
    trustScore: 98,
    autonomyScore: 65,
    speedScore: 58,
    domainCompany: 'MercadoLibre & Glovo',
    superpower: 'Genera un clima de confianza y buen humor bajo presión'
  },
  specialist: {
    role: 'Principal Architect',
    archetype: 'Arquitecto de Alto Rigor',
    desc: 'Excelencia metodológica, estándares técnicos elevados y resolución de problemas arquitectónicos de gran escala.',
    trustScore: 95,
    autonomyScore: 84,
    speedScore: 48,
    domainCompany: 'Amazon & Datadog',
    superpower: 'Referencia técnica a la que recurre el equipo ante bloqueos críticos'
  }
}

const currentProfile = computed(() => sampleProfiles[activeArchetype.value])
</script>

<template>
  <div class="selection:bg-primary/30 min-h-screen">
    
    <!-- Hero B2B Section -->
    <section class="relative pt-12 pb-24 lg:pt-20 lg:pb-32 overflow-hidden">
      <!-- Glows de ambientación -->
      <div class="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[650px] h-[400px] bg-gradient-to-tr from-amber-500/15 via-primary/10 to-indigo-600/15 blur-[140px] pointer-events-none rounded-full"></div>
      
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center">
        
        <!-- Badge B2B -->
        <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/[0.04] border border-amber-500/30 backdrop-blur-md shadow-[0_0_20px_rgba(242,151,39,0.15)] mb-8 animate-in fade-in slide-in-from-bottom-3 duration-700">
          <Building2 class="w-3.5 h-3.5 text-amber-400" />
          <span class="text-xs font-bold text-amber-300 uppercase tracking-widest">{{ $t('companies.hero.badge') }}</span>
        </div>

        <!-- Título Principal -->
        <h1 class="text-3xl sm:text-5xl lg:text-6xl font-black text-white tracking-tight leading-[1.1] max-w-4xl mx-auto mb-6">
          {{ $t('companies.hero.headlinePrefix') }}<br />
          <span class="bg-clip-text text-transparent bg-gradient-to-r from-amber-400 via-primary to-orange-400">
            {{ $t('companies.hero.headlineHighlight') }}
          </span>
        </h1>

        <!-- Subtítulo -->
        <p class="text-base sm:text-xl text-[hsl(220,10%,70%)] max-w-3xl mx-auto font-normal leading-relaxed mb-10">
          {{ $t('companies.hero.subheadline') }}
        </p>

        <!-- Botones de Acción B2B -->
        <div class="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16">
          <NuxtLink
            to="/recruiter/search"
            class="w-full sm:w-auto px-8 py-4 rounded-xl font-black text-zinc-950 bg-gradient-to-r from-amber-400 via-primary to-orange-500 hover:brightness-110 transition-all duration-300 shadow-[0_0_35px_rgba(242,151,39,0.4)] flex items-center justify-center gap-2 group text-base"
          >
            <Search class="w-4 h-4" />
            <span>{{ $t('companies.hero.ctaSearch') }}</span>
            <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </NuxtLink>

          <a
            href="#informe-demo"
            class="w-full sm:w-auto px-7 py-4 rounded-xl font-bold text-white bg-white/[0.04] border border-white/10 hover:bg-white/[0.08] hover:border-white/20 transition-all duration-300 flex items-center justify-center gap-2 text-base"
          >
            <span>{{ $t('companies.hero.ctaReportDemo') }}</span>
          </a>
        </div>

        <!-- Barra de Métricas Clave B2B -->
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-6 max-w-3xl mx-auto pt-6 border-t border-white/10">
          <div class="p-4 rounded-2xl bg-white/[0.02] border border-white/5">
            <div class="text-3xl font-black text-emerald-400 mb-1">{{ $t('companies.hero.metric1Val') }}</div>
            <div class="text-xs text-zinc-400 font-medium">{{ $t('companies.hero.metric1Label') }}</div>
          </div>
          <div class="p-4 rounded-2xl bg-white/[0.02] border border-white/5">
            <div class="text-3xl font-black text-amber-400 mb-1">{{ $t('companies.hero.metric2Val') }}</div>
            <div class="text-xs text-zinc-400 font-medium">{{ $t('companies.hero.metric2Label') }}</div>
          </div>
          <div class="p-4 rounded-2xl bg-white/[0.02] border border-white/5">
            <div class="text-3xl font-black text-sky-400 mb-1">{{ $t('companies.hero.metric3Val') }}</div>
            <div class="text-xs text-zinc-400 font-medium">{{ $t('companies.hero.metric3Label') }}</div>
          </div>
        </div>

      </div>
    </section>

    <!-- Interactive Recruiter Report Showcase -->
    <section id="informe-demo" class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-16 space-y-3">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-bold uppercase tracking-wider">
            <Sparkles class="w-3.5 h-3.5" />
            <span>{{ $t('companies.reportPreview.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white tracking-tight">
            {{ $t('companies.reportPreview.title') }}
          </h2>
          <p class="text-[hsl(220,10%,60%)] text-base">
            {{ $t('companies.reportPreview.subtitle') }}
          </p>

          <!-- Switcher de Perfil de Prueba -->
          <div class="pt-4 inline-flex p-1.5 rounded-2xl bg-white/[0.04] border border-white/10 gap-1.5">
            <button 
              @click="activeArchetype = 'builder'"
              :class="activeArchetype === 'builder' ? 'bg-amber-400 text-zinc-950 font-black shadow-md' : 'text-zinc-400 hover:text-white font-medium'"
              class="px-4 py-2 text-xs rounded-xl transition-all"
            >
              🚀 Builder (0 a 1)
            </button>
            <button 
              @click="activeArchetype = 'leader'"
              :class="activeArchetype === 'leader' ? 'bg-sky-400 text-zinc-950 font-black shadow-md' : 'text-zinc-400 hover:text-white font-medium'"
              class="px-4 py-2 text-xs rounded-xl transition-all"
            >
              👑 Líder Coach
            </button>
            <button 
              @click="activeArchetype = 'specialist'"
              :class="activeArchetype === 'specialist' ? 'bg-emerald-400 text-zinc-950 font-black shadow-md' : 'text-zinc-400 hover:text-white font-medium'"
              class="px-4 py-2 text-xs rounded-xl transition-all"
            >
              🎯 Arquitecto Rigor
            </button>
          </div>
        </div>

        <!-- Mockup Visual del Informe para Reclutadores -->
        <div class="max-w-4xl mx-auto rounded-3xl bg-[hsl(228,16%,7%)] border border-white/15 p-6 sm:p-10 shadow-2xl space-y-8 relative overflow-hidden">
          <div class="absolute top-0 right-0 w-80 h-80 bg-primary/10 blur-3xl pointer-events-none"></div>

          <!-- Cabecera del Informe -->
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-white/10 relative z-10">
            <div class="flex items-center gap-4">
              <div class="w-14 h-14 rounded-2xl bg-gradient-to-tr from-amber-400 to-orange-500 flex items-center justify-center text-zinc-950 font-black text-xl shadow-lg">
                {{ currentProfile.role.charAt(0) }}
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <h3 class="text-xl font-bold text-white">{{ currentProfile.role }}</h3>
                  <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 flex items-center gap-1">
                    <CheckCircle2 class="w-3 h-3" /> Auditado
                  </span>
                </div>
                <p class="text-xs text-zinc-400 mt-0.5">{{ $t('companies.reportPreview.verifiedReferences') }} • {{ currentProfile.domainCompany }}</p>
              </div>
            </div>

            <div class="flex items-center gap-3 bg-white/[0.03] border border-white/10 px-4 py-2.5 rounded-2xl">
              <ShieldCheck class="w-6 h-6 text-emerald-400" />
              <div>
                <div class="text-xs text-zinc-400 font-medium">{{ $t('companies.reportPreview.trustScoreLabel') }}</div>
                <div class="text-lg font-black text-white">{{ currentProfile.trustScore }}% Trust Score</div>
              </div>
            </div>
          </div>

          <!-- Cuadrícula de Datos de Desempeño y Conducta -->
          <div class="grid md:grid-cols-2 gap-6 relative z-10">
            
            <!-- Arquetipo Conductual -->
            <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/10 space-y-3">
              <div class="flex items-center justify-between">
                <span class="text-xs font-bold text-amber-400 uppercase tracking-wider">Arquetipo Validado</span>
                <span class="text-[10px] text-zinc-400 font-mono">5/5 Fuentes</span>
              </div>
              <h4 class="text-lg font-bold text-white">{{ currentProfile.archetype }}</h4>
              <p class="text-xs text-zinc-400 leading-relaxed">{{ currentProfile.desc }}</p>
            </div>

            <!-- Ejes de Comportamiento Medidos -->
            <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/10 space-y-4">
              <div class="space-y-1.5">
                <div class="flex justify-between text-xs font-semibold">
                  <span class="text-zinc-300">Autonomía vs Facilitación</span>
                  <span class="text-indigo-400 font-bold">{{ currentProfile.autonomyScore }}% Iniciativa</span>
                </div>
                <div class="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-indigo-500 to-violet-400 rounded-full transition-all duration-500" :style="{ width: `${currentProfile.autonomyScore}%` }"></div>
                </div>
              </div>

              <div class="space-y-1.5">
                <div class="flex justify-between text-xs font-semibold">
                  <span class="text-zinc-300">Velocidad vs Rigor</span>
                  <span class="text-amber-400 font-bold">{{ currentProfile.speedScore }}% Pragmatismo</span>
                </div>
                <div class="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-amber-500 to-orange-400 rounded-full transition-all duration-500" :style="{ width: `${currentProfile.speedScore}%` }"></div>
                </div>
              </div>
            </div>

          </div>

          <!-- Superpotencias y Fit Cultural -->
          <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/10 flex flex-col sm:flex-row sm:items-center justify-between gap-4 relative z-10">
            <div class="space-y-1">
              <div class="text-xs text-zinc-400 font-semibold">{{ $t('companies.reportPreview.superpowersTitle') }}</div>
              <div class="text-sm font-bold text-emerald-300 flex items-center gap-2">
                <Star class="w-4 h-4 text-emerald-400 fill-emerald-400/20 shrink-0" />
                <span>{{ currentProfile.superpower }}</span>
              </div>
            </div>

            <div class="px-3.5 py-1.5 rounded-xl bg-sky-500/10 border border-sky-500/20 text-xs font-bold text-sky-300 shrink-0 self-start sm:self-auto">
              🚀 {{ $t('companies.reportPreview.culturalFitMatch') }}
            </div>
          </div>

          <!-- Pie de Verificación -->
          <div class="pt-4 border-t border-white/10 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-zinc-500">
            <div class="flex items-center gap-2 font-mono">
              <Lock class="w-3.5 h-3.5 text-zinc-400" />
              <span>{{ $t('companies.reportPreview.auditHash') }}</span>
            </div>
            <span class="text-emerald-400 font-semibold flex items-center gap-1">
              <CheckCircle2 class="w-3.5 h-3.5" />
              100% Respuestas Anónimas de Ex-Compañeros
            </span>
          </div>

        </div>

      </div>
    </section>

    <!-- Traditional vs Caché Comparison Matrix -->
    <section class="py-24 bg-gradient-to-b from-[hsl(228,15%,9%)] to-zinc-950 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-16 space-y-3">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs font-bold uppercase tracking-wider">
            <Target class="w-3.5 h-3.5" />
            <span>{{ $t('companies.comparison.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white tracking-tight">
            {{ $t('companies.comparison.title') }}
          </h2>
          <p class="text-[hsl(220,10%,60%)] text-base">
            {{ $t('companies.comparison.subtitle') }}
          </p>
        </div>

        <div class="grid md:grid-cols-2 gap-8 max-w-5xl mx-auto">
          
          <!-- Columna Tradicional -->
          <div class="p-8 rounded-3xl bg-rose-950/10 border border-rose-500/20 space-y-6">
            <div class="flex items-center gap-2.5 pb-4 border-b border-rose-500/20 text-rose-400 font-bold">
              <XCircle class="w-5 h-5" />
              <h3 class="text-lg text-white">{{ $t('companies.comparison.colTraditional') }}</h3>
            </div>

            <div class="space-y-4 text-xs sm:text-sm text-zinc-300">
              <div class="space-y-1">
                <span class="font-bold text-rose-300 block">{{ $t('companies.comparison.row1Title') }}</span>
                <p class="text-zinc-400 leading-relaxed">{{ $t('companies.comparison.row1Traditional') }}</p>
              </div>
              <div class="space-y-1 pt-3 border-t border-white/5">
                <span class="font-bold text-rose-300 block">{{ $t('companies.comparison.row2Title') }}</span>
                <p class="text-zinc-400 leading-relaxed">{{ $t('companies.comparison.row2Traditional') }}</p>
              </div>
              <div class="space-y-1 pt-3 border-t border-white/5">
                <span class="font-bold text-rose-300 block">{{ $t('companies.comparison.row3Title') }}</span>
                <p class="text-zinc-400 leading-relaxed">{{ $t('companies.comparison.row3Traditional') }}</p>
              </div>
              <div class="space-y-1 pt-3 border-t border-white/5">
                <span class="font-bold text-rose-300 block">{{ $t('companies.comparison.row4Title') }}</span>
                <p class="text-zinc-400 leading-relaxed">{{ $t('companies.comparison.row4Traditional') }}</p>
              </div>
            </div>
          </div>

          <!-- Columna Caché Protocol -->
          <div class="p-8 rounded-3xl bg-emerald-950/15 border border-emerald-500/30 space-y-6 shadow-xl relative overflow-hidden">
            <div class="absolute top-0 right-0 w-48 h-48 bg-emerald-500/10 blur-3xl pointer-events-none"></div>

            <div class="flex items-center gap-2.5 pb-4 border-b border-emerald-500/20 text-emerald-400 font-bold">
              <CheckCircle2 class="w-5 h-5" />
              <h3 class="text-lg text-white">{{ $t('companies.comparison.colCache') }}</h3>
            </div>

            <div class="space-y-4 text-xs sm:text-sm text-zinc-300">
              <div class="space-y-1">
                <span class="font-bold text-emerald-300 block">{{ $t('companies.comparison.row1Title') }}</span>
                <p class="text-zinc-300 leading-relaxed">{{ $t('companies.comparison.row1Cache') }}</p>
              </div>
              <div class="space-y-1 pt-3 border-t border-white/5">
                <span class="font-bold text-emerald-300 block">{{ $t('companies.comparison.row2Title') }}</span>
                <p class="text-zinc-300 leading-relaxed">{{ $t('companies.comparison.row2Cache') }}</p>
              </div>
              <div class="space-y-1 pt-3 border-t border-white/5">
                <span class="font-bold text-emerald-300 block">{{ $t('companies.comparison.row3Title') }}</span>
                <p class="text-zinc-300 leading-relaxed">{{ $t('companies.comparison.row3Cache') }}</p>
              </div>
              <div class="space-y-1 pt-3 border-t border-white/5">
                <span class="font-bold text-emerald-300 block">{{ $t('companies.comparison.row4Title') }}</span>
                <p class="text-zinc-300 leading-relaxed">{{ $t('companies.comparison.row4Cache') }}</p>
              </div>
            </div>
          </div>

        </div>

      </div>
    </section>

    <!-- How It Works in 3 Steps -->
    <section class="py-24 bg-zinc-950 border-t border-white/5">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-16 space-y-3">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-sky-500/10 border border-sky-500/30 text-sky-300 text-xs font-bold uppercase tracking-wider">
            <Clock class="w-3.5 h-3.5" />
            <span>{{ $t('companies.howItWorks.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white tracking-tight">
            {{ $t('companies.howItWorks.title') }}
          </h2>
          <p class="text-[hsl(220,10%,60%)] text-base">
            {{ $t('companies.howItWorks.subtitle') }}
          </p>
        </div>

        <div class="grid md:grid-cols-3 gap-8">
          <div class="p-8 rounded-3xl bg-white/[0.02] border border-white/10 hover:border-white/20 transition-all">
            <div class="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/25 flex items-center justify-center text-amber-400 font-black text-xl mb-6">
              01
            </div>
            <h3 class="text-xl font-bold text-white mb-3">{{ $t('companies.howItWorks.step1Title') }}</h3>
            <p class="text-sm text-zinc-400 leading-relaxed">{{ $t('companies.howItWorks.step1Desc') }}</p>
          </div>

          <div class="p-8 rounded-3xl bg-white/[0.02] border border-white/10 hover:border-white/20 transition-all">
            <div class="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/25 flex items-center justify-center text-sky-400 font-black text-xl mb-6">
              02
            </div>
            <h3 class="text-xl font-bold text-white mb-3">{{ $t('companies.howItWorks.step2Title') }}</h3>
            <p class="text-sm text-zinc-400 leading-relaxed">{{ $t('companies.howItWorks.step2Desc') }}</p>
          </div>

          <div class="p-8 rounded-3xl bg-white/[0.02] border border-white/10 hover:border-white/20 transition-all">
            <div class="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/25 flex items-center justify-center text-emerald-400 font-black text-xl mb-6">
              03
            </div>
            <h3 class="text-xl font-bold text-white mb-3">{{ $t('companies.howItWorks.step3Title') }}</h3>
            <p class="text-sm text-zinc-400 leading-relaxed">{{ $t('companies.howItWorks.step3Desc') }}</p>
          </div>
        </div>

      </div>
    </section>

    <!-- Enterprise Security & GDPR Section -->
    <section class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-16 space-y-3">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs font-bold uppercase tracking-wider">
            <ShieldCheck class="w-3.5 h-3.5" />
            <span>{{ $t('companies.security.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white tracking-tight">
            {{ $t('companies.security.title') }}
          </h2>
          <p class="text-[hsl(220,10%,60%)] text-base">
            {{ $t('companies.security.subtitle') }}
          </p>
        </div>

        <div class="grid md:grid-cols-3 gap-8">
          <div class="p-7 rounded-3xl bg-white/[0.02] border border-white/10 space-y-3">
            <Lock class="w-8 h-8 text-emerald-400 mb-2" />
            <h4 class="text-lg font-bold text-white">{{ $t('companies.security.item1Title') }}</h4>
            <p class="text-xs text-zinc-400 leading-relaxed">{{ $t('companies.security.item1Desc') }}</p>
          </div>

          <div class="p-7 rounded-3xl bg-white/[0.02] border border-white/10 space-y-3">
            <Building2 class="w-8 h-8 text-amber-400 mb-2" />
            <h4 class="text-lg font-bold text-white">{{ $t('companies.security.item2Title') }}</h4>
            <p class="text-xs text-zinc-400 leading-relaxed">{{ $t('companies.security.item2Desc') }}</p>
          </div>

          <div class="p-7 rounded-3xl bg-white/[0.02] border border-white/10 space-y-3">
            <ShieldCheck class="w-8 h-8 text-sky-400 mb-2" />
            <h4 class="text-lg font-bold text-white">{{ $t('companies.security.item3Title') }}</h4>
            <p class="text-xs text-zinc-400 leading-relaxed">{{ $t('companies.security.item3Desc') }}</p>
          </div>
        </div>

      </div>
    </section>

    <!-- FAQ Accordion for Companies -->
    <section class="py-24 bg-zinc-950">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-2xl mx-auto mb-16 space-y-3">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-bold uppercase tracking-wider">
            <HelpCircle class="w-3.5 h-3.5" />
            <span>{{ $t('companies.faq.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white tracking-tight">
            {{ $t('companies.faq.title') }}
          </h2>
          <p class="text-[hsl(220,10%,60%)] text-base">
            {{ $t('companies.faq.subtitle') }}
          </p>
        </div>

        <div class="space-y-3">
          <div 
            v-for="(faq, idx) in faqs" 
            :key="idx"
            class="rounded-2xl border transition-all duration-300 overflow-hidden"
            :class="openFaq === idx ? 'bg-white/[0.04] border-amber-500/30 shadow-lg' : 'bg-white/[0.02] border-white/5 hover:border-white/15'"
          >
            <button 
              @click="toggleFaq(idx)"
              class="w-full p-5 text-left flex items-center justify-between gap-4 transition-colors"
            >
              <span class="text-base font-bold text-white">{{ $t(faq.qKey) }}</span>
              <ChevronDown 
                class="w-5 h-5 text-zinc-400 shrink-0 transition-transform duration-300"
                :class="{ 'rotate-180 text-amber-400': openFaq === idx }"
              />
            </button>
            <div 
              v-show="openFaq === idx"
              class="px-5 pb-5 text-sm text-[hsl(220,10%,70%)] leading-relaxed border-t border-white/5 pt-3 animate-in fade-in duration-300"
            >
              {{ $t(faq.aKey) }}
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- Bottom B2B CTA -->
    <section class="py-32 relative overflow-hidden border-t border-white/5 bg-gradient-to-b from-zinc-950 to-[hsl(228,16%,7%)]">
      <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-full max-w-4xl h-1/2 bg-gradient-to-t from-primary/20 to-transparent blur-3xl pointer-events-none"></div>
      
      <div class="max-w-4xl mx-auto px-4 text-center relative z-10 space-y-6">
        <h2 class="text-3xl sm:text-5xl font-black text-white tracking-tight leading-tight">
          {{ $t('companies.bottomCta.title') }}
        </h2>
        <p class="text-base sm:text-lg text-[hsl(220,10%,65%)] max-w-2xl mx-auto leading-relaxed">
          {{ $t('companies.bottomCta.desc') }}
        </p>

        <div class="pt-4 flex flex-col sm:flex-row items-center justify-center gap-4">
          <NuxtLink
            to="/recruiter/search"
            class="w-full sm:w-auto px-10 py-5 rounded-2xl font-black text-zinc-950 bg-gradient-to-r from-amber-400 via-primary to-orange-500 hover:brightness-110 transition-all duration-300 text-lg shadow-[0_0_40px_rgba(242,151,39,0.45)] flex items-center justify-center gap-2.5 group"
          >
            <Search class="w-5 h-5" />
            <span>{{ $t('companies.bottomCta.ctaPrimary') }}</span>
            <ArrowRight class="w-5 h-5 transition-transform group-hover:translate-x-1" />
          </NuxtLink>

          <NuxtLink
            to="/register"
            class="w-full sm:w-auto px-8 py-5 rounded-2xl font-bold text-white bg-white/[0.04] border border-white/10 hover:bg-white/[0.08] hover:border-white/20 transition-all text-lg flex items-center justify-center"
          >
            <span>{{ $t('companies.bottomCta.ctaSecondary') }}</span>
          </NuxtLink>
        </div>

        <p class="text-xs text-zinc-400 font-medium pt-2">
          {{ $t('companies.bottomCta.subtext') }}
        </p>
      </div>
    </section>

  </div>
</template>
