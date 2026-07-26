<script setup lang="ts">
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
  RefreshCw 
} from 'lucide-vue-next'

definePageMeta({
  layout: 'public'
})

const { t } = useI18n()
const requestUrl = useRequestURL()
const logoUrl = computed(() => `${requestUrl.origin}/logo-cache.png`)

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

const features = [
  {
    title: 'Verificación 360°',
    description: 'Referencias cruzadas de ex-compañeros, managers y clientes para crear un perfil inquebrantable.',
    icon: ShieldCheck
  },
  {
    title: 'Match de Cultura',
    description: 'Encuentra empresas que se alineen no solo con tus habilidades, sino con tus valores de trabajo.',
    icon: Target
  },
  {
    title: 'Destaca sobre el CV',
    description: 'Demuestra tus soft-skills reales validadas por profesionales con los que has colaborado.',
    icon: Award
  }
]

const steps = [
  {
    num: '01',
    title: 'Crea tu perfil Caché',
    description: 'Regístrate y completa tu experiencia profesional.'
  },
  {
    num: '02',
    title: 'Solicita Feedback',
    description: 'Envía peticiones estructuradas a tus contactos profesionales.'
  },
  {
    num: '03',
    title: 'Aumenta tu Valor',
    description: 'Usa tu perfil validado para destacar en procesos de selección.'
  }
]

const softSkills = [
  {
    translationKey: 'home.skillsSection.items.teamwork',
    icon: Users,
    iconBg: 'bg-indigo-500/10 text-indigo-400 group-hover:bg-indigo-500/20',
    iconBlurBg: 'bg-indigo-500/40',
    glowBg: 'from-indigo-600/30 to-violet-600/5',
    accentClass: 'group-hover:text-indigo-400',
    borderClass: 'hover:border-indigo-500/30 hover:shadow-[0_0_30px_rgba(99,102,241,0.15)]'
  },
  {
    translationKey: 'home.skillsSection.items.selfConfidence',
    icon: Award,
    iconBg: 'bg-amber-500/10 text-amber-400 group-hover:bg-amber-500/20',
    iconBlurBg: 'bg-amber-500/40',
    glowBg: 'from-amber-600/30 to-yellow-600/5',
    accentClass: 'group-hover:text-amber-400',
    borderClass: 'hover:border-amber-500/30 hover:shadow-[0_0_30px_rgba(245,158,11,0.15)]'
  },
  {
    translationKey: 'home.skillsSection.items.proactivity',
    icon: Zap,
    iconBg: 'bg-rose-500/10 text-rose-400 group-hover:bg-rose-500/20',
    iconBlurBg: 'bg-rose-500/40',
    glowBg: 'from-rose-600/30 to-orange-600/5',
    accentClass: 'group-hover:text-rose-400',
    borderClass: 'hover:border-rose-500/30 hover:shadow-[0_0_30px_rgba(244,63,94,0.15)]'
  },
  {
    translationKey: 'home.skillsSection.items.integrity',
    icon: Fingerprint,
    iconBg: 'bg-emerald-500/10 text-emerald-400 group-hover:bg-emerald-500/20',
    iconBlurBg: 'bg-emerald-500/40',
    glowBg: 'from-emerald-600/30 to-teal-600/5',
    accentClass: 'group-hover:text-emerald-400',
    borderClass: 'hover:border-emerald-500/30 hover:shadow-[0_0_30px_rgba(16,185,129,0.15)]'
  },
  {
    translationKey: 'home.skillsSection.items.flexibility',
    icon: RefreshCw,
    iconBg: 'bg-cyan-500/10 text-cyan-400 group-hover:bg-cyan-500/20',
    iconBlurBg: 'bg-cyan-500/40',
    glowBg: 'from-cyan-600/30 to-sky-600/5',
    accentClass: 'group-hover:text-cyan-400',
    borderClass: 'hover:border-cyan-500/30 hover:shadow-[0_0_30px_rgba(6,182,212,0.15)]'
  }
]
</script>

<template>
  <main class="pt-16 selection:bg-primary/30">
    
    <!-- Hero Section -->
    <section class="relative overflow-hidden pt-20 pb-32">
      <div class="absolute top-1/4 left-1/2 -translate-x-1/2 w-[800px] h-[800px] rounded-full bg-primary/5 blur-[120px] pointer-events-none"></div>
      <div class="absolute -top-32 right-0 w-[400px] h-[400px] rounded-full bg-secondary/10 blur-[100px] pointer-events-none"></div>

      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center">
        <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-primary/10 border border-primary/20 mb-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
          <span class="w-2 h-2 rounded-full bg-primary animate-pulse"></span>
          <span class="text-sm font-medium text-primary">{{ $t('home.hero.evolution') }}</span>
        </div>

        <h1 class="text-5xl md:text-7xl font-bold font-heading text-white mb-8 tracking-tight leading-tight animate-in fade-in slide-in-from-bottom-6 duration-700 delay-100">
          {{ $t('home.hero.headlinePrefix') }}<br />
          <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-accent to-secondary">
            {{ $t('home.hero.headlineHighlight') }}
          </span>
        </h1>

        <p class="text-lg md:text-xl text-[hsl(220,10%,65%)] max-w-2xl mx-auto mb-12 animate-in fade-in slide-in-from-bottom-8 duration-700 delay-200">
          {{ $t('home.hero.subheadline') }}
        </p>

        <div class="flex flex-col sm:flex-row items-center justify-center gap-4 animate-in fade-in slide-in-from-bottom-10 duration-700 delay-300">
          <NuxtLink
            to="/register"
            class="w-full sm:w-auto px-8 py-4 rounded-xl font-bold text-white bg-primary hover:bg-primary/90 transition-all duration-300
                   hover:shadow-[0_0_30px_rgba(242,151,39,0.3)] hover:-translate-y-1"
          >
            {{ $t('home.hero.ctaBuild') }}
          </NuxtLink>
          <a
            href="#como-funciona"
            class="w-full sm:w-auto px-8 py-4 rounded-xl font-semibold text-white bg-white/5 border border-white/10 hover:bg-white/10 transition-all duration-300"
          >
            {{ $t('home.hero.ctaDiscover') }}
          </a>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section id="caracteristicas" class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-20">
          <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">{{ $t('home.features.badge') }}</h2>
          <p class="text-[hsl(220,10%,60%)] text-lg">
            {{ $t('home.features.tagline') }}
          </p>
        </div>

        <div class="grid md:grid-cols-3 gap-8">
          <div v-for="(feat, idx) in features" :key="idx" class="glass-dark p-8 rounded-2xl border border-white/5 hover:border-primary/30 transition-colors duration-300 group">
            <div class="w-14 h-14 rounded-xl bg-primary/10 flex items-center justify-center text-primary mb-6 group-hover:scale-110 transition-transform duration-300">
              <component :is="feat.icon" class="w-7 h-7" />
            </div>
            <h3 class="text-xl font-bold text-white mb-4">{{ $t('home.features.item' + (idx + 1) + '.title') }}</h3>
            <p class="text-[hsl(220,10%,60%)] leading-relaxed">
              {{ $t('home.features.item' + (idx + 1) + '.desc') }}
            </p>
          </div>
        </div>

      </div>
    </section>

    <!-- Skills Section -->
    <section id="habilidades" class="py-24 bg-gradient-to-b from-zinc-950 to-[hsl(228,15%,9%)] relative overflow-hidden">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        <div class="text-center max-w-3xl mx-auto mb-16">
          <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">{{ $t('home.skillsSection.title') }}</h2>
          <p class="text-[hsl(220,10%,60%)] text-lg">
            {{ $t('home.skillsSection.subtitle') }}
          </p>
        </div>

        <div class="grid sm:grid-cols-2 lg:grid-cols-5 gap-6">
          <div 
            v-for="skill in softSkills" 
            :key="skill.translationKey" 
            class="glass-dark p-6 rounded-2xl border border-white/5 space-y-4 transition-all duration-300 group relative overflow-hidden hover:-translate-y-1.5"
            :class="skill.borderClass"
          >
            <div 
              class="absolute -top-12 -left-12 w-28 h-28 rounded-full blur-2xl opacity-0 group-hover:opacity-20 transition-opacity duration-300 pointer-events-none bg-gradient-to-br"
              :class="skill.glowBg"
            ></div>

            <div class="relative w-11 h-11 flex items-center justify-center rounded-xl transition-all duration-300 group-hover:scale-110" :class="skill.iconBg">
              <div 
                class="absolute inset-0 rounded-xl blur-md opacity-0 group-hover:opacity-40 transition-opacity duration-300 pointer-events-none"
                :class="skill.iconBlurBg"
              ></div>
              <component :is="skill.icon" class="w-5 h-5 relative z-10 transition-transform duration-500 group-hover:rotate-12" />
            </div>

            <div class="space-y-2 relative z-10">
              <h4 class="text-lg font-bold text-white transition-colors duration-300" :class="skill.accentClass">
                {{ $t(skill.translationKey + '.title') }}
              </h4>
              <p class="text-xs text-[hsl(220,10%,60%)] leading-relaxed">
                {{ $t(skill.translationKey + '.desc') }}
              </p>
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
                  <h4 class="text-xl font-bold text-white mb-2">{{ $t('home.howItWorks.steps.step' + (idx + 1) + '.title') }}</h4>
                  <p class="text-[hsl(220,10%,60%)]">{{ $t('home.howItWorks.steps.step' + (idx + 1) + '.desc') }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="w-full md:w-1/2 relative">
            <div class="aspect-square max-w-md mx-auto relative">
              <div class="absolute inset-0 bg-gradient-to-tr from-primary/20 to-secondary/20 rounded-full blur-3xl"></div>
              <div class="relative h-full w-full glass-dark rounded-3xl border border-white/10 shadow-2xl p-6 flex flex-col overflow-hidden">
                <div class="flex items-center gap-4 border-b border-white/10 pb-4 mb-4">
                  <div class="w-12 h-12 rounded-full bg-primary/20"></div>
                  <div>
                    <div class="h-4 w-32 bg-white/20 rounded mb-2"></div>
                    <div class="h-3 w-24 bg-white/10 rounded"></div>
                  </div>
                </div>
                <div class="flex-1 space-y-4">
                  <div class="h-8 w-full bg-white/5 rounded-lg border border-white/5 flex items-center px-4"><span class="text-xs text-primary font-bold">Feedback #124</span></div>
                  <div class="h-24 w-full bg-white/5 rounded-lg border border-white/5 p-4">
                     <div class="h-2 w-full bg-white/20 rounded mb-3"></div>
                     <div class="h-2 w-5/6 bg-white/20 rounded mb-3"></div>
                     <div class="h-2 w-4/6 bg-white/20 rounded"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- Trust Protocol Section -->
    <section id="verificacion" class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col lg:flex-row gap-16 items-center">
          
          <div class="w-full lg:w-1/2 space-y-6">
            <div class="glass-dark p-6 rounded-3xl border border-white/10 shadow-2xl relative">
              <div class="absolute -top-4 -left-4 bg-emerald-500 text-zinc-950 font-bold text-[10px] px-3 py-1 rounded-full uppercase tracking-wider shadow-lg flex items-center gap-1">
                <ShieldCheck class="w-3.5 h-3.5" />
                {{ $t('home.trustProtocol.securityBadge') }}
              </div>
              <h3 class="text-xl font-bold text-white mb-4 mt-2">{{ $t('home.trustProtocol.chartTitle') }}</h3>
              
              <div class="space-y-4">
                <div class="space-y-1">
                  <div class="flex justify-between text-xs font-semibold">
                    <span class="text-zinc-300">{{ $t('home.trustProtocol.metric1') }}</span>
                    <span class="text-emerald-400 font-bold">+30%</span>
                  </div>
                  <div class="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div class="h-full bg-emerald-400 rounded-full" style="width: 30%"></div>
                  </div>
                </div>
                
                <div class="space-y-1">
                  <div class="flex justify-between text-xs font-semibold">
                    <span class="text-zinc-300">{{ $t('home.trustProtocol.metric2') }}</span>
                    <span class="text-emerald-400 font-bold">+40%</span>
                  </div>
                  <div class="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div class="h-full bg-emerald-400 rounded-full" style="width: 40%"></div>
                  </div>
                </div>
                
                <div class="space-y-1">
                  <div class="flex justify-between text-xs font-semibold">
                    <span class="text-zinc-300">{{ $t('home.trustProtocol.metric3') }}</span>
                    <span class="text-emerald-400 font-bold">+20%</span>
                  </div>
                  <div class="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div class="h-full bg-emerald-400 rounded-full" style="width: 20%"></div>
                  </div>
                </div>

                <div class="space-y-1">
                  <div class="flex justify-between text-xs font-semibold">
                    <span class="text-zinc-300">{{ $t('home.trustProtocol.metric4') }}</span>
                    <span class="text-emerald-400 font-bold">+10%</span>
                  </div>
                  <div class="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div class="h-full bg-emerald-400 rounded-full" style="width: 10%"></div>
                  </div>
                </div>
              </div>

              <div class="mt-6 pt-4 border-t border-white/5 flex flex-wrap gap-2.5">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">{{ $t('home.trustProtocol.level1') }}</span>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded text-[10px] font-bold bg-amber-500/10 text-amber-400 border border-amber-500/20">{{ $t('home.trustProtocol.level2') }}</span>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded text-[10px] font-bold bg-orange-500/10 text-orange-400 border border-orange-500/20">{{ $t('home.trustProtocol.level3') }}</span>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded text-[10px] font-bold bg-rose-500/10 text-rose-400 border border-rose-500/20">{{ $t('home.trustProtocol.level4') }}</span>
              </div>
            </div>
          </div>

          <div class="w-full lg:w-1/2 space-y-6">
            <h2 class="text-3xl md:text-4xl font-bold text-white leading-tight">
              {{ $t('home.trustProtocol.titlePrefix') }} <br/>
              <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary to-orange-500">
                {{ $t('home.trustProtocol.titleHighlight') }}
              </span>
            </h2>
            <p class="text-lg text-[hsl(220,10%,60%)]">
              {{ $t('home.trustProtocol.desc1') }}
            </p>
            <p class="text-sm text-[hsl(220,10%,50%)] leading-relaxed">
              {{ $t('home.trustProtocol.desc2') }}
            </p>
          </div>

        </div>
      </div>
    </section>

    <!-- Audience Section -->
    <section id="para-quien" class="py-24 bg-gradient-to-b from-[hsl(228,15%,9%)] to-zinc-950">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-3xl md:text-4xl font-bold text-white mb-16">{{ $t('home.audience.title') }}</h2>
        
        <div class="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
          <div class="p-8 rounded-2xl bg-[hsl(228,16%,7%)] border border-white/5 text-left flex flex-col">
            <Users class="w-10 h-10 text-primary mb-6" />
            <h3 class="text-2xl font-bold text-white mb-4">{{ $t('home.audience.professionals.title') }}</h3>
            <p class="text-[hsl(220,10%,60%)] mb-6 flex-1">
              {{ $t('home.audience.professionals.desc') }}
            </p>
            <ul class="space-y-3 text-sm text-[hsl(220,14%,80%)]">
              <li class="flex gap-2">✓<span>{{ $t('home.audience.professionals.bullet1') }}</span></li>
              <li class="flex gap-2">✓<span>{{ $t('home.audience.professionals.bullet2') }}</span></li>
            </ul>
          </div>

          <div class="p-8 rounded-2xl bg-[hsl(228,16%,7%)] border border-white/5 text-left flex flex-col">
            <Search class="w-10 h-10 text-secondary mb-6" />
            <h3 class="text-2xl font-bold text-white mb-4">{{ $t('home.audience.recruiters.title') }}</h3>
            <p class="text-[hsl(220,10%,60%)] mb-6 flex-1">
              {{ $t('home.audience.recruiters.desc') }}
            </p>
            <ul class="space-y-3 text-sm text-[hsl(220,14%,80%)]">
              <li class="flex gap-2">✓<span>{{ $t('home.audience.recruiters.bullet1') }}</span></li>
              <li class="flex gap-2">✓<span>{{ $t('home.audience.recruiters.bullet2') }}</span></li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- Use Cases Section -->
    <section id="casos-de-uso" class="py-24 relative overflow-hidden bg-gradient-to-b from-zinc-950 to-[hsl(228,15%,9%)]">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-20">
          <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">{{ $t('home.useCases.title') }}</h2>
          <p class="text-[hsl(220,10%,60%)] text-lg">
            {{ $t('home.useCases.desc') }}
          </p>
        </div>

        <div class="grid md:grid-cols-4 gap-6">
          <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/5 space-y-4 hover:bg-white/[0.04] transition-all">
            <div class="w-10 h-10 rounded-xl bg-primary/10 text-primary flex items-center justify-center">
              <Users class="w-5 h-5" />
            </div>
            <h4 class="text-lg font-bold text-white">{{ $t('home.useCases.case1.title') }}</h4>
            <p class="text-xs text-[hsl(220,10%,60%)] leading-relaxed">{{ $t('home.useCases.case1.desc') }}</p>
          </div>

          <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/5 space-y-4 hover:bg-white/[0.04] transition-all">
            <div class="w-10 h-10 rounded-xl bg-primary/10 text-primary flex items-center justify-center">
              <TrendingUp class="w-5 h-5" />
            </div>
            <h4 class="text-lg font-bold text-white">{{ $t('home.useCases.case2.title') }}</h4>
            <p class="text-xs text-[hsl(220,10%,60%)] leading-relaxed">{{ $t('home.useCases.case2.desc') }}</p>
          </div>

          <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/5 space-y-4 hover:bg-white/[0.04] transition-all">
            <div class="w-10 h-10 rounded-xl bg-primary/10 text-primary flex items-center justify-center">
              <Search class="w-5 h-5" />
            </div>
            <h4 class="text-lg font-bold text-white">{{ $t('home.useCases.case3.title') }}</h4>
            <p class="text-xs text-[hsl(220,10%,60%)] leading-relaxed">{{ $t('home.useCases.case3.desc') }}</p>
          </div>

          <div class="p-6 rounded-2xl bg-white/[0.02] border border-white/5 space-y-4 hover:bg-white/[0.04] transition-all">
            <div class="w-10 h-10 rounded-xl bg-primary/10 text-primary flex items-center justify-center">
              <Briefcase class="w-5 h-5" />
            </div>
            <h4 class="text-lg font-bold text-white">{{ $t('home.useCases.case4.title') }}</h4>
            <p class="text-xs text-[hsl(220,10%,60%)] leading-relaxed">{{ $t('home.useCases.case4.desc') }}</p>
          </div>
        </div>

      </div>
    </section>

    <!-- Bottom CTA -->
    <section class="py-32 relative overflow-hidden">
      <div class="absolute inset-0 bg-primary/5"></div>
      <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-full max-w-4xl h-1/2 bg-gradient-to-t from-primary/20 to-transparent blur-3xl"></div>
      
      <div class="max-w-4xl mx-auto px-4 text-center relative z-10">
        <h2 class="text-4xl md:text-5xl font-bold text-white mb-8">
          {{ $t('home.bottomCta.title') }}
        </h2>
        <p class="text-xl text-[hsl(220,10%,65%)] mb-12">
          {{ $t('home.bottomCta.desc') }}
        </p>
        <NuxtLink
          to="/register"
          class="inline-block px-10 py-5 rounded-xl font-bold text-white bg-primary hover:bg-primary/90 transition-all duration-300 text-lg
                 hover:shadow-[0_0_40px_rgba(242,151,39,0.4)] hover:-translate-y-1"
        >
          {{ $t('home.bottomCta.cta') }}
        </NuxtLink>
      </div>
    </section>

  </main>
</template>
