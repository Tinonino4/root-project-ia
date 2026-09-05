<script setup lang="ts">
import { ref, computed } from 'vue'
import { 
  ShieldCheck, 
  Target, 
  Users, 
  Search, 
  Award, 
  TrendingUp, 
  Briefcase, 
  Zap, 
  Fingerprint, 
  ChevronDown, 
  HelpCircle, 
  Sparkles, 
  CheckCircle2,
  Star,
  ArrowRight,
  Lock,
  Quote,
  ExternalLink
} from 'lucide-vue-next'

import HeroShowcase from '~/components/landing/HeroShowcase.vue'
import HowItWorksInteractiveCard from '~/components/landing/HowItWorksInteractiveCard.vue'

definePageMeta({
  layout: 'public'
})

const { t } = useI18n()
const requestUrl = useRequestURL()
const logoUrl = computed(() => `${requestUrl.origin}/logo.svg`)

useSeoMeta({
  title: () => t('seo.defaultTitle'),
  ogTitle: () => t('seo.defaultTitle'),
  twitterTitle: () => t('seo.defaultTitle'),
  description: () => t('seo.defaultDescription'),
  ogDescription: () => t('seo.defaultDescription'),
  twitterDescription: () => t('seo.defaultDescription'),
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
  { qKey: 'home.faq.item1.q', aKey: 'home.faq.item1.a' },
  { qKey: 'home.faq.item2.q', aKey: 'home.faq.item2.a' },
  { qKey: 'home.faq.item3.q', aKey: 'home.faq.item3.a' },
  { qKey: 'home.faq.item4.q', aKey: 'home.faq.item4.a' },
  { qKey: 'home.faq.item5.q', aKey: 'home.faq.item5.a' }
]

const steps = [
  {
    num: '01',
    titleKey: 'home.howItWorks.steps.step1.title',
    descKey: 'home.howItWorks.steps.step1.desc'
  },
  {
    num: '02',
    titleKey: 'home.howItWorks.steps.step2.title',
    descKey: 'home.howItWorks.steps.step2.desc'
  },
  {
    num: '03',
    titleKey: 'home.howItWorks.steps.step3.title',
    descKey: 'home.howItWorks.steps.step3.desc'
  }
]

const testimonials = [
  {
    nameKey: 'home.socialProof.case1.name',
    roleKey: 'home.socialProof.case1.role',
    metricKey: 'home.socialProof.case1.metric',
    quoteKey: 'home.socialProof.case1.quote',
    avatar: 'EG',
    color: 'from-amber-500 to-orange-500'
  },
  {
    nameKey: 'home.socialProof.case2.name',
    roleKey: 'home.socialProof.case2.role',
    metricKey: 'home.socialProof.case2.metric',
    quoteKey: 'home.socialProof.case2.quote',
    avatar: 'CM',
    color: 'from-sky-500 to-blue-600'
  },
  {
    nameKey: 'home.socialProof.case3.name',
    roleKey: 'home.socialProof.case3.role',
    metricKey: 'home.socialProof.case3.metric',
    quoteKey: 'home.socialProof.case3.quote',
    avatar: 'LB',
    color: 'from-emerald-500 to-teal-600'
  }
]
</script>

<template>
  <div class="selection:bg-primary/30">
    
    <!-- Hero Showcase Section (Interactivo con Efecto WOW) -->
    <section class="relative overflow-hidden">
      <HeroShowcase />
    </section>

    <!-- Skills Bento Grid Section (Elimina la redundancia previa de "features" y va directo al grano) -->
    <section id="habilidades" class="py-24 bg-gradient-to-b from-zinc-950 to-[hsl(228,15%,9%)] relative overflow-hidden border-t border-white/5">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        <div class="text-center max-w-3xl mx-auto mb-16">
          <div class="inline-flex items-center gap-1.5 px-3.5 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-bold uppercase tracking-wider mb-4">
            <Sparkles class="w-3.5 h-3.5" />
            <span>{{ $t('home.skillsSection.badge', 'Evaluación Conductual Objetiva') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">{{ $t('home.skillsSection.title') }}</h2>
          <p class="text-[hsl(220,10%,60%)] text-lg">
            {{ $t('home.skillsSection.subtitle') }}
          </p>
        </div>

        <div class="space-y-6">
          <!-- Fila Superior: 2 Cards Destacadas (Preguntas Clave de Rendimiento en Equipo) -->
          <div class="grid md:grid-cols-2 gap-6">
            
            <!-- Card 1: Autonomía vs Facilitación -->
            <div class="glass-dark p-7 md:p-8 rounded-3xl border border-indigo-500/20 hover:border-indigo-500/40 transition-all duration-300 group relative overflow-hidden flex flex-col justify-between hover:shadow-[0_0_30px_rgba(99,102,241,0.15)] hover:-translate-y-1">
              <div class="absolute -top-12 -left-12 w-36 h-36 rounded-full blur-3xl opacity-20 group-hover:opacity-40 transition-opacity bg-gradient-to-br from-indigo-600 to-violet-600 pointer-events-none"></div>
              
              <div class="space-y-4 relative z-10">
                <div class="flex items-center justify-between">
                  <div class="w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400 group-hover:scale-105 transition-transform">
                    <Users class="w-6 h-6" />
                  </div>
                  <span class="text-xs font-bold uppercase tracking-wider px-3 py-1 rounded-full bg-indigo-500/10 text-indigo-300 border border-indigo-500/20">
                    {{ $t('home.skillsSection.items.autonomyVsTeam.badge', 'Iniciativa vs Facilitación') }}
                  </span>
                </div>
                
                <h3 class="text-xl md:text-2xl font-bold text-white group-hover:text-indigo-300 transition-colors">
                  {{ $t('home.skillsSection.items.autonomyVsTeam.title') }}
                </h3>
                <p class="text-sm text-[hsl(220,10%,65%)] leading-relaxed">
                  {{ $t('home.skillsSection.items.autonomyVsTeam.desc') }}
                </p>
              </div>

              <!-- Micro-Visualizador de Balance -->
              <div class="mt-6 pt-6 border-t border-white/5 space-y-2 relative z-10">
                <div class="flex justify-between text-xs font-medium text-zinc-400">
                  <span class="text-indigo-300 font-semibold">{{ $t('home.skillsSection.items.autonomyVsTeam.left', 'Iniciativa y Autonomía') }}</span>
                  <span>{{ $t('home.skillsSection.items.autonomyVsTeam.right', 'Facilitación y Equipo') }}</span>
                </div>
                <div class="h-2 w-full bg-zinc-800/80 rounded-full overflow-hidden p-0.5 border border-white/5">
                  <div class="h-full bg-gradient-to-r from-indigo-500 to-violet-400 rounded-full" style="width: 68%"></div>
                </div>
              </div>
            </div>

            <!-- Card 2: Pragmatismo vs Rigor -->
            <div class="glass-dark p-7 md:p-8 rounded-3xl border border-amber-500/20 hover:border-amber-500/40 transition-all duration-300 group relative overflow-hidden flex flex-col justify-between hover:shadow-[0_0_30px_rgba(245,158,11,0.15)] hover:-translate-y-1">
              <div class="absolute -top-12 -left-12 w-36 h-36 rounded-full blur-3xl opacity-20 group-hover:opacity-40 transition-opacity bg-gradient-to-br from-amber-600 to-orange-600 pointer-events-none"></div>
              
              <div class="space-y-4 relative z-10">
                <div class="flex items-center justify-between">
                  <div class="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 group-hover:scale-105 transition-transform">
                    <Zap class="w-6 h-6" />
                  </div>
                  <span class="text-xs font-bold uppercase tracking-wider px-3 py-1 rounded-full bg-amber-500/10 text-amber-300 border border-amber-500/20">
                    {{ $t('home.skillsSection.items.executionVsAnalysis.badge', 'Pragmatismo vs Rigor') }}
                  </span>
                </div>
                
                <h3 class="text-xl md:text-2xl font-bold text-white group-hover:text-amber-300 transition-colors">
                  {{ $t('home.skillsSection.items.executionVsAnalysis.title') }}
                </h3>
                <p class="text-sm text-[hsl(220,10%,65%)] leading-relaxed">
                  {{ $t('home.skillsSection.items.executionVsAnalysis.desc') }}
                </p>
              </div>

              <!-- Micro-Visualizador de Balance -->
              <div class="mt-6 pt-6 border-t border-white/5 space-y-2 relative z-10">
                <div class="flex justify-between text-xs font-medium text-zinc-400">
                  <span class="text-amber-300 font-semibold">{{ $t('home.skillsSection.items.executionVsAnalysis.left', 'Velocidad y Entrega') }}</span>
                  <span>{{ $t('home.skillsSection.items.executionVsAnalysis.right', 'Rigor y Arquitectura') }}</span>
                </div>
                <div class="h-2 w-full bg-zinc-800/80 rounded-full overflow-hidden p-0.5 border border-white/5">
                  <div class="h-full bg-gradient-to-r from-amber-500 to-orange-400 rounded-full" style="width: 74%"></div>
                </div>
              </div>
            </div>

          </div>

          <!-- Fila Inferior: 3 Cards Complementarias -->
          <div class="grid md:grid-cols-3 gap-6">
            
            <!-- Card 3: Elección Forzada Anti-Sesgo -->
            <div class="glass-dark p-6 rounded-3xl border border-rose-500/20 hover:border-rose-500/40 transition-all duration-300 group relative overflow-hidden flex flex-col justify-between hover:shadow-[0_0_30px_rgba(244,63,94,0.15)] hover:-translate-y-1">
              <div class="space-y-4 relative z-10">
                <div class="w-11 h-11 rounded-2xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center text-rose-400 group-hover:scale-105 transition-transform">
                  <Target class="w-5 h-5" />
                </div>
                <h4 class="text-lg font-bold text-white group-hover:text-rose-300 transition-colors">
                  {{ $t('home.skillsSection.items.forcedChoice.title') }}
                </h4>
                <p class="text-xs text-[hsl(220,10%,65%)] leading-relaxed">
                  {{ $t('home.skillsSection.items.forcedChoice.desc') }}
                </p>
              </div>

              <div class="mt-5 pt-4 border-t border-white/5 flex flex-wrap gap-2 text-[11px] font-bold text-rose-300">
                <span class="px-2.5 py-1 rounded-lg bg-rose-500/10 border border-rose-500/20">⚡ 5 Preguntas Ágiles</span>
                <span class="px-2.5 py-1 rounded-lg bg-rose-500/10 border border-rose-500/20">🚫 Sin Notas 5/5 Falsas</span>
              </div>
            </div>

            <!-- Card 4: Tus 3 Superpoderes Reales -->
            <div class="glass-dark p-6 rounded-3xl border border-emerald-500/20 hover:border-emerald-500/40 transition-all duration-300 group relative overflow-hidden flex flex-col justify-between hover:shadow-[0_0_30px_rgba(16,185,129,0.15)] hover:-translate-y-1">
              <div class="space-y-4 relative z-10">
                <div class="w-11 h-11 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 group-hover:scale-105 transition-transform">
                  <Award class="w-5 h-5" />
                </div>
                <h4 class="text-lg font-bold text-white group-hover:text-emerald-300 transition-colors">
                  {{ $t('home.skillsSection.items.superpowers.title') }}
                </h4>
                <p class="text-xs text-[hsl(220,10%,65%)] leading-relaxed">
                  {{ $t('home.skillsSection.items.superpowers.desc') }}
                </p>
              </div>

              <div class="mt-5 pt-4 border-t border-white/5 flex flex-wrap gap-2 text-[11px] font-bold text-emerald-300">
                <span class="px-2.5 py-1 rounded-lg bg-emerald-500/10 border border-emerald-500/20">⭐ Desbloqueo Rápido</span>
                <span class="px-2.5 py-1 rounded-lg bg-emerald-500/10 border border-emerald-500/20">🤝 Clima Positivo</span>
              </div>
            </div>

            <!-- Card 5: Afinidad de Entorno Laboral -->
            <div class="glass-dark p-6 rounded-3xl border border-cyan-500/20 hover:border-cyan-500/40 transition-all duration-300 group relative overflow-hidden flex flex-col justify-between hover:shadow-[0_0_30px_rgba(6,182,212,0.15)] hover:-translate-y-1">
              <div class="space-y-4 relative z-10">
                <div class="w-11 h-11 rounded-2xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 group-hover:scale-105 transition-transform">
                  <Fingerprint class="w-5 h-5" />
                </div>
                <h4 class="text-lg font-bold text-white group-hover:text-cyan-300 transition-colors">
                  {{ $t('home.skillsSection.items.culturalFit.title') }}
                </h4>
                <p class="text-xs text-[hsl(220,10%,65%)] leading-relaxed">
                  {{ $t('home.skillsSection.items.culturalFit.desc') }}
                </p>
              </div>

              <div class="mt-5 pt-4 border-t border-white/5 flex flex-wrap gap-2 text-[11px] font-bold text-cyan-300">
                <span class="px-2.5 py-1 rounded-lg bg-cyan-500/10 border border-cyan-500/20">🚀 Startups 0 a 1</span>
                <span class="px-2.5 py-1 rounded-lg bg-cyan-500/10 border border-cyan-500/20">📈 Scaleups</span>
              </div>
            </div>

          </div>
        </div>
      </div>
    </section>

    <!-- How It Works Section -->
    <section id="como-funciona" class="py-32 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="flex flex-col md:flex-row gap-16 items-center">
          <div class="w-full md:w-1/2 space-y-8">
            <h2 class="text-4xl md:text-5xl font-bold text-white leading-tight">
              {{ $t('home.howItWorks.titlePrefix') }} <br/>
              <span class="text-primary">{{ $t('home.howItWorks.titleHighlight') }}</span>
            </h2>
            <p class="text-lg text-[hsl(220,10%,60%)]">
              {{ $t('home.howItWorks.desc') }}
            </p>

            <div class="space-y-6 pt-4">
              <div v-for="(step, idx) in steps" :key="idx" class="flex gap-4">
                <div class="w-12 h-12 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-primary font-bold font-heading shrink-0">
                  {{ step.num }}
                </div>
                <div>
                  <h4 class="text-xl font-bold text-white mb-2">{{ $t(step.titleKey) }}</h4>
                  <p class="text-[hsl(220,10%,60%)]">{{ $t(step.descKey) }}</p>
                </div>
              </div>
            </div>

            <!-- Badges de Fricción Cero en Cómo Funciona -->
            <div class="flex flex-wrap items-center gap-4 pt-6 border-t border-white/10 text-xs font-medium text-zinc-400">
              <div class="flex items-center gap-1.5">
                <ShieldCheck class="w-4 h-4 text-emerald-400 shrink-0" />
                <span>{{ $t('home.howItWorks.trustBadge1') }}</span>
              </div>
              <div class="flex items-center gap-1.5">
                <CheckCircle2 class="w-4 h-4 text-amber-400 shrink-0" />
                <span>{{ $t('home.howItWorks.trustBadge2') }}</span>
              </div>
              <div class="flex items-center gap-1.5">
                <Sparkles class="w-4 h-4 text-sky-400 shrink-0" />
                <span>{{ $t('home.howItWorks.trustBadge3') }}</span>
              </div>
            </div>
          </div>

          <div class="w-full md:w-1/2 relative">
            <div class="w-full max-w-lg mx-auto relative">
              <div class="absolute inset-0 bg-gradient-to-tr from-amber-500/20 via-primary/20 to-violet-600/20 rounded-3xl blur-3xl pointer-events-none"></div>
              <div class="relative z-10">
                <HowItWorksInteractiveCard />
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- Trust Protocol Section (Mockup Tangible del Pasaporte y Sello Verificado) -->
    <section id="verificacion" class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col lg:flex-row gap-16 items-center">
          
          <!-- Mockup Visual del Pasaporte y Sello Certificado (Reemplaza las barras porcentuales abstractas) -->
          <div class="w-full lg:w-1/2 space-y-6">
            <div class="glass-dark p-6 sm:p-7 rounded-3xl border border-white/10 shadow-2xl relative overflow-hidden group hover:border-emerald-500/30 transition-all duration-500">
              
              <!-- Badge Flotante de Certificación -->
              <div class="flex items-center justify-between border-b border-white/10 pb-4 mb-5">
                <div class="flex items-center gap-2.5">
                  <div class="w-9 h-9 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
                    <ShieldCheck class="w-5 h-5" />
                  </div>
                  <div>
                    <h4 class="text-sm font-bold text-white tracking-tight">{{ $t('home.trustProtocol.cardTitle') }}</h4>
                    <span class="text-[11px] text-zinc-400 font-mono">{{ $t('home.trustProtocol.cardSubtitle') }}</span>
                  </div>
                </div>

                <div class="flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 font-extrabold text-[11px]">
                  <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                  <span>{{ $t('home.trustProtocol.statusActive') }}</span>
                </div>
              </div>

              <!-- Snapshot del Perfil Auditado -->
              <div class="space-y-3.5">
                
                <!-- Credencial 1: Email Corporativo -->
                <div class="p-3.5 rounded-2xl bg-white/[0.03] border border-white/5 flex items-start gap-3 hover:bg-white/[0.05] transition-colors">
                  <div class="w-8 h-8 rounded-lg bg-emerald-500/10 flex items-center justify-center text-emerald-400 shrink-0 mt-0.5">
                    <CheckCircle2 class="w-4 h-4" />
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center justify-between">
                      <span class="text-xs font-bold text-white">{{ $t('home.trustProtocol.corporateValidation') }}</span>
                      <span class="text-[10px] font-mono text-emerald-400 font-bold">100% Match</span>
                    </div>
                    <p class="text-[11px] text-zinc-400 mt-0.5">{{ $t('home.trustProtocol.corporateDomain') }}</p>
                  </div>
                </div>

                <!-- Credencial 2: Consistencia Multicapa sin Outliers -->
                <div class="p-3.5 rounded-2xl bg-white/[0.03] border border-white/5 flex items-start gap-3 hover:bg-white/[0.05] transition-colors">
                  <div class="w-8 h-8 rounded-lg bg-amber-500/10 flex items-center justify-center text-amber-400 shrink-0 mt-0.5">
                    <Target class="w-4 h-4" />
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center justify-between">
                      <span class="text-xs font-bold text-white">{{ $t('home.trustProtocol.consistency360') }}</span>
                      <span class="text-[10px] font-mono text-amber-300 font-bold">5 Fuentes</span>
                    </div>
                    <p class="text-[11px] text-zinc-400 mt-0.5">{{ $t('home.trustProtocol.consistencyDesc') }}</p>
                  </div>
                </div>

                <!-- Credencial 3: Firma Criptográfica Inmutable -->
                <div class="p-3.5 rounded-2xl bg-white/[0.03] border border-white/5 flex items-start gap-3 hover:bg-white/[0.05] transition-colors">
                  <div class="w-8 h-8 rounded-lg bg-sky-500/10 flex items-center justify-center text-sky-400 shrink-0 mt-0.5">
                    <Lock class="w-4 h-4" />
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center justify-between">
                      <span class="text-xs font-bold text-white">{{ $t('home.trustProtocol.hashId') }}</span>
                      <span class="text-[10px] font-mono text-zinc-400">hash: #8f4a-e12b</span>
                    </div>
                    <p class="text-[11px] text-zinc-400 mt-0.5">Token anti-suplantación de único uso para cada referente</p>
                  </div>
                </div>

              </div>

              <!-- Barra de Enlace Compartible -->
              <div class="mt-5 pt-4 border-t border-white/5 flex items-center justify-between text-xs text-zinc-400 bg-white/[0.02] p-3 rounded-xl border border-white/5">
                <div class="flex items-center gap-2 font-mono text-zinc-300">
                  <span class="text-amber-400 font-bold">micache.es/u/tu-perfil</span>
                </div>
                <span class="text-[10px] text-emerald-400 font-bold flex items-center gap-1">
                  <CheckCircle2 class="w-3.5 h-3.5" />
                  Listo para CV y LinkedIn
                </span>
              </div>

            </div>
          </div>

          <!-- Columna Derecha: Copy Explicativo Enfocado en el Dolor de Selección -->
          <div class="w-full lg:w-1/2 space-y-6">
            <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs font-bold uppercase tracking-wider">
              <ShieldCheck class="w-3.5 h-3.5" />
              <span>{{ $t('home.trustProtocol.securityBadge') }}</span>
            </div>
            <h2 class="text-3xl md:text-4xl font-bold text-white leading-tight">
              {{ $t('home.trustProtocol.titlePrefix') }} <br/>
              <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary to-orange-500">
                {{ $t('home.trustProtocol.titleHighlight') }}
              </span>
            </h2>
            <p class="text-lg text-[hsl(220,10%,65%)] leading-relaxed">
              {{ $t('home.trustProtocol.desc1') }}
            </p>
            <p class="text-sm text-[hsl(220,10%,55%)] leading-relaxed">
              {{ $t('home.trustProtocol.desc2') }}
            </p>

            <div class="pt-2">
              <NuxtLink 
                to="/register"
                class="inline-flex items-center gap-2 text-sm font-bold text-amber-400 hover:text-amber-300 transition-colors group"
              >
                <span>{{ $t('home.trustProtocol.viewExampleCta', 'Crear mi Pasaporte de Talento ahora') }}</span>
                <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
              </NuxtLink>
            </div>
          </div>

        </div>
      </div>
    </section>

    <!-- Audience Section (100% enfocada en el Candidato) -->
    <section id="para-quien" class="py-24 bg-gradient-to-b from-[hsl(228,15%,9%)] to-zinc-950">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        
        <div class="inline-flex items-center gap-1.5 px-3.5 py-1 rounded-full bg-primary/10 border border-primary/30 text-amber-300 text-xs font-bold uppercase tracking-wider mb-4">
          <Award class="w-3.5 h-3.5" />
          <span>{{ $t('home.audience.badge') }}</span>
        </div>
        <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">{{ $t('home.audience.title') }}</h2>
        <p class="text-lg text-[hsl(220,10%,60%)] max-w-2xl mx-auto mb-16">{{ $t('home.audience.subtitle') }}</p>
        
        <div class="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
          
          <!-- Card 1: Poder de Negociación -->
          <div class="p-8 rounded-3xl bg-[hsl(228,16%,7%)] border border-white/10 text-left flex flex-col hover:border-primary/40 transition-all duration-300 group hover:-translate-y-1 shadow-xl">
            <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/25 flex items-center justify-center text-primary mb-6 group-hover:scale-105 transition-transform">
              <TrendingUp class="w-6 h-6" />
            </div>
            <h3 class="text-2xl font-bold text-white mb-4">{{ $t('home.audience.card1.title') }}</h3>
            <p class="text-[hsl(220,10%,60%)] mb-6 flex-1 text-sm leading-relaxed">
              {{ $t('home.audience.card1.desc') }}
            </p>
            <ul class="space-y-3 text-sm text-[hsl(220,14%,80%)] pt-4 border-t border-white/5">
              <li class="flex gap-2.5 items-center">
                <CheckCircle2 class="w-4 h-4 text-emerald-400 shrink-0" />
                <span>{{ $t('home.audience.card1.bullet1') }}</span>
              </li>
              <li class="flex gap-2.5 items-center">
                <CheckCircle2 class="w-4 h-4 text-emerald-400 shrink-0" />
                <span>{{ $t('home.audience.card1.bullet2') }}</span>
              </li>
            </ul>
          </div>

          <!-- Card 2: Marca Personal Portable -->
          <div class="p-8 rounded-3xl bg-[hsl(228,16%,7%)] border border-white/10 text-left flex flex-col hover:border-sky-500/40 transition-all duration-300 group hover:-translate-y-1 shadow-xl">
            <div class="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/25 flex items-center justify-center text-sky-400 mb-6 group-hover:scale-105 transition-transform">
              <Fingerprint class="w-6 h-6" />
            </div>
            <h3 class="text-2xl font-bold text-white mb-4">{{ $t('home.audience.card2.title') }}</h3>
            <p class="text-[hsl(220,10%,60%)] mb-6 flex-1 text-sm leading-relaxed">
              {{ $t('home.audience.card2.desc') }}
            </p>
            <ul class="space-y-3 text-sm text-[hsl(220,14%,80%)] pt-4 border-t border-white/5">
              <li class="flex gap-2.5 items-center">
                <CheckCircle2 class="w-4 h-4 text-sky-400 shrink-0" />
                <span>{{ $t('home.audience.card2.bullet1') }}</span>
              </li>
              <li class="flex gap-2.5 items-center">
                <CheckCircle2 class="w-4 h-4 text-sky-400 shrink-0" />
                <span>{{ $t('home.audience.card2.bullet2') }}</span>
              </li>
            </ul>
          </div>

        </div>

        <!-- Banner Discreto para Reclutadores (Preserva la vía B2B sin romper el modelo mental del candidato) -->
        <div class="mt-14 inline-flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-white/[0.03] border border-white/10 text-xs text-zinc-400 hover:border-white/20 transition-colors">
          <span>{{ $t('home.audience.recruiterNotice') }}</span>
          <NuxtLink to="/empresas" class="text-amber-400 hover:text-amber-300 font-bold hover:underline">
            {{ $t('home.audience.recruiterLink') }}
          </NuxtLink>
        </div>

      </div>
    </section>

    <!-- Social Proof / Historias de Impacto (Reemplaza los casos de uso genéricos) -->
    <section id="casos-de-uso" class="py-24 relative overflow-hidden bg-gradient-to-b from-zinc-950 to-[hsl(228,15%,9%)] border-t border-white/5">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-16">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs font-bold uppercase tracking-wider mb-4">
            <Star class="w-3.5 h-3.5 fill-current" />
            <span>{{ $t('home.socialProof.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">{{ $t('home.socialProof.title') }}</h2>
          <p class="text-[hsl(220,10%,60%)] text-lg">
            {{ $t('home.socialProof.subtitle') }}
          </p>
        </div>

        <div class="grid md:grid-cols-3 gap-6">
          <div 
            v-for="(item, idx) in testimonials" 
            :key="idx" 
            class="p-7 rounded-3xl bg-white/[0.02] border border-white/10 hover:border-white/20 transition-all duration-300 flex flex-col justify-between hover:-translate-y-1 group"
          >
            <div class="space-y-4">
              <div class="flex items-center justify-between">
                <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-[11px] font-bold">
                  <CheckCircle2 class="w-3 h-3" />
                  {{ $t(item.metricKey) }}
                </span>
                <Quote class="w-5 h-5 text-zinc-600 group-hover:text-zinc-400 transition-colors" />
              </div>
              <p class="text-sm text-[hsl(220,10%,75%)] leading-relaxed italic">
                "{{ $t(item.quoteKey) }}"
              </p>
            </div>

            <div class="mt-6 pt-5 border-t border-white/5 flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-tr flex items-center justify-center text-xs font-black text-white shrink-0" :class="item.color">
                {{ item.avatar }}
              </div>
              <div>
                <h4 class="text-sm font-bold text-white">{{ $t(item.nameKey) }}</h4>
                <p class="text-xs text-zinc-400">{{ $t(item.roleKey) }}</p>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- FAQ Section -->
    <section id="faq" class="py-24 bg-[hsl(228,15%,9%)] border-t border-white/5 relative">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-2xl mx-auto mb-16 space-y-3">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-bold uppercase tracking-wider">
            <HelpCircle class="w-3.5 h-3.5" />
            <span>{{ $t('home.faq.badge') }}</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-white tracking-tight">
            {{ $t('home.faq.title') }}
          </h2>
          <p class="text-[hsl(220,10%,60%)] text-base">
            {{ $t('home.faq.subtitle') }}
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

    <!-- Bottom CTA (Alta Urgencia y Fricción Cero) -->
    <section class="py-32 relative overflow-hidden border-t border-white/5">
      <div class="absolute inset-0 bg-primary/5"></div>
      <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-full max-w-4xl h-1/2 bg-gradient-to-t from-primary/20 to-transparent blur-3xl"></div>
      
      <div class="max-w-4xl mx-auto px-4 text-center relative z-10 space-y-6">
        <h2 class="text-3xl sm:text-5xl font-black text-white tracking-tight leading-tight">
          {{ $t('home.bottomCta.title') }}
        </h2>
        <p class="text-base sm:text-lg text-[hsl(220,10%,65%)] max-w-2xl mx-auto leading-relaxed">
          {{ $t('home.bottomCta.desc') }}
        </p>

        <div class="pt-4 flex flex-col items-center gap-3">
          <NuxtLink
            to="/register"
            class="px-10 py-5 rounded-2xl font-black text-zinc-950 bg-gradient-to-r from-amber-400 via-primary to-orange-500 hover:brightness-110 transition-all duration-300 text-lg
                   shadow-[0_0_40px_rgba(242,151,39,0.45)] hover:-translate-y-1 flex items-center gap-2.5 group"
          >
            <span>{{ $t('home.bottomCta.cta') }}</span>
            <ArrowRight class="w-5 h-5 transition-transform group-hover:translate-x-1" />
          </NuxtLink>
          <p class="text-xs text-zinc-400 font-medium">
            {{ $t('home.bottomCta.subtext') }}
          </p>
        </div>
      </div>
    </section>

  </div>
</template>
