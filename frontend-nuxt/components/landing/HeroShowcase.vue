<script setup lang="ts">
import { ref, computed } from 'vue'
import { 
  Dna, 
  ShieldCheck, 
  ArrowRight, 
  Sparkles, 
  Star, 
  CheckCircle2, 
  Search, 
  Layers, 
  Zap,
  Target
} from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

type RoleKey = 'builder' | 'leader' | 'specialist'
const selectedRole = ref<RoleKey>('builder')

interface RoleProfile {
  titleKey: string
  taglineKey: string
  coordX: number
  coordY: number
  trustScore: number
  topStrengthKey: string
  validatorsCount: number
  badgeColor: string
  quadrantId: string
  dotClass: string
  crosshairClass: string
  tooltipClass: string
}

const roleProfiles: Record<RoleKey, RoleProfile> = {
  builder: {
    titleKey: 'home.hero.roles.builder.title',
    taglineKey: 'home.hero.roles.builder.tagline',
    coordX: 76,
    coordY: 26,
    trustScore: 96,
    topStrengthKey: 'home.hero.roles.builder.strength',
    validatorsCount: 5,
    badgeColor: 'text-amber-400 bg-amber-500/10 border-amber-500/30',
    quadrantId: 'builder',
    dotClass: 'bg-gradient-to-tr from-amber-400 to-orange-500 shadow-[0_0_25px_rgba(245,158,11,1)]',
    crosshairClass: 'border-amber-400/40',
    tooltipClass: 'border-amber-500/40 text-amber-300'
  },
  leader: {
    titleKey: 'home.hero.roles.leader.title',
    taglineKey: 'home.hero.roles.leader.tagline',
    coordX: 38,
    coordY: 22,
    trustScore: 98,
    topStrengthKey: 'home.hero.roles.leader.strength',
    validatorsCount: 8,
    badgeColor: 'text-sky-400 bg-sky-500/10 border-sky-500/30',
    quadrantId: 'leader',
    dotClass: 'bg-gradient-to-tr from-sky-400 to-blue-500 shadow-[0_0_25px_rgba(56,189,248,1)]',
    crosshairClass: 'border-sky-400/40',
    tooltipClass: 'border-sky-500/40 text-sky-300'
  },
  specialist: {
    titleKey: 'home.hero.roles.specialist.title',
    taglineKey: 'home.hero.roles.specialist.tagline',
    coordX: 84,
    coordY: 68,
    trustScore: 95,
    topStrengthKey: 'home.hero.roles.specialist.strength',
    validatorsCount: 6,
    badgeColor: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30',
    quadrantId: 'specialist',
    dotClass: 'bg-gradient-to-tr from-emerald-400 to-teal-500 shadow-[0_0_25px_rgba(16,185,129,1)]',
    crosshairClass: 'border-emerald-400/40',
    tooltipClass: 'border-emerald-500/40 text-emerald-300'
  }
}

const activeProfile = computed(() => roleProfiles[selectedRole.value])
</script>

<template>
  <div class="relative w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6 pb-16 lg:pt-16 lg:pb-28">
    <!-- Halos de luz ambiental con efecto Glassmorphic Glow -->
    <div class="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[700px] h-[450px] bg-gradient-to-tr from-amber-500/15 via-primary/10 to-indigo-600/15 blur-[140px] pointer-events-none rounded-full"></div>
    <div class="absolute -top-16 right-10 w-[350px] h-[350px] bg-secondary/10 blur-[100px] pointer-events-none rounded-full"></div>

    <div class="grid lg:grid-cols-12 gap-8 lg:gap-8 items-center relative z-10">
      
      <!-- Columna Izquierda: Mensaje Principal, Selector de Demo (Desktop) y CTAs (Desktop) -->
      <div class="lg:col-span-7 space-y-6 lg:space-y-8 text-center lg:text-left">
        
        <!-- Badge de Protocolo -->
        <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/[0.04] border border-amber-500/30 backdrop-blur-md shadow-[0_0_20px_rgba(242,151,39,0.15)] animate-in fade-in slide-in-from-bottom-3 duration-700">
          <span class="flex h-2 w-2 rounded-full bg-amber-400 animate-pulse"></span>
          <span class="text-xs font-bold text-amber-300 uppercase tracking-widest">{{ $t('home.hero.evolution', 'Protocolo de Certificación 360°') }}</span>
        </div>

        <!-- Título Principal con Alto Impacto Visual -->
        <h1 class="text-3xl sm:text-5xl lg:text-6xl xl:text-7xl font-black text-white tracking-tight leading-[1.08] font-heading animate-in fade-in slide-in-from-bottom-4 duration-700 delay-100">
          {{ $t('home.hero.headlinePrefix', 'Tu talento real no cabe en un CV.') }}<br />
          <span class="bg-clip-text text-transparent bg-gradient-to-r from-amber-400 via-primary to-orange-400">
            {{ $t('home.hero.headlineHighlight', 'Demuéstralo con tu Caché.') }}
          </span>
        </h1>

        <!-- Subtítulo Prescriptivo -->
        <p class="text-base sm:text-xl text-[hsl(220,10%,70%)] max-w-2xl mx-auto lg:mx-0 font-normal leading-relaxed animate-in fade-in slide-in-from-bottom-5 duration-700 delay-200">
          {{ $t('home.hero.subheadline', 'Referencias profesionales 360° blindadas con preguntas de elección forzada. Sin cartas de recomendación vacías: datos conductuales verificados por quienes han colaborado contigo.') }}
        </p>

        <!-- Selector Rápido de Demostración en Tiempo Real (Solo visible en Desktop) -->
        <div class="hidden lg:block space-y-3 pt-2 animate-in fade-in slide-in-from-bottom-6 duration-700 delay-300">
          <div class="flex items-center gap-2">
            <Sparkles class="w-3.5 h-3.5 text-amber-400" />
            <span class="text-xs font-semibold text-zinc-300 uppercase tracking-wider">
              {{ $t('home.hero.demoLabel', 'Prueba cómo se ve tu huella profesional:') }}
            </span>
          </div>
          
          <div class="inline-flex flex-wrap items-center justify-start p-1.5 rounded-2xl bg-white/[0.04] border border-white/10 gap-1.5 shadow-lg backdrop-blur-xl">
            <button 
              @click="selectedRole = 'builder'"
              :class="selectedRole === 'builder' ? 'bg-gradient-to-r from-amber-400 to-amber-500 text-zinc-950 font-black shadow-lg shadow-amber-500/25 scale-[1.02]' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
              class="px-4 py-2.5 text-xs sm:text-sm rounded-xl transition-all duration-300 flex items-center gap-1.5"
            >
              <Zap class="w-3.5 h-3.5" />
              <span>{{ $t('home.hero.roles.builder.btn', '🚀 Ejecutor / Builder') }}</span>
            </button>
            <button 
              @click="selectedRole = 'leader'"
              :class="selectedRole === 'leader' ? 'bg-gradient-to-r from-sky-400 to-sky-500 text-zinc-950 font-black shadow-lg shadow-sky-500/25 scale-[1.02]' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
              class="px-4 py-2.5 text-xs sm:text-sm rounded-xl transition-all duration-300 flex items-center gap-1.5"
            >
              <Target class="w-3.5 h-3.5" />
              <span>{{ $t('home.hero.roles.leader.btn', '👑 Líder / Coach') }}</span>
            </button>
            <button 
              @click="selectedRole = 'specialist'"
              :class="selectedRole === 'specialist' ? 'bg-gradient-to-r from-emerald-400 to-emerald-500 text-zinc-950 font-black shadow-lg shadow-emerald-500/25 scale-[1.02]' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
              class="px-4 py-2.5 text-xs sm:text-sm rounded-xl transition-all duration-300 flex items-center gap-1.5"
            >
              <Layers class="w-3.5 h-3.5" />
              <span>{{ $t('home.hero.roles.specialist.btn', '🎯 Especialista Técnico') }}</span>
            </button>
          </div>
        </div>

        <!-- Botones de Acción Primaria (CTAs en Desktop) -->
        <div class="hidden lg:flex flex-row items-center justify-start gap-4 pt-2 animate-in fade-in slide-in-from-bottom-7 duration-700 delay-400">
          <NuxtLink
            to="/register"
            class="px-8 py-4 rounded-xl font-black text-zinc-950 bg-gradient-to-r from-amber-400 via-primary to-orange-500 hover:brightness-110 transition-all duration-300 shadow-[0_0_35px_rgba(242,151,39,0.4)] hover:-translate-y-1 active:translate-y-0 flex items-center justify-center gap-2 group text-base"
          >
            <span>{{ $t('home.hero.ctaBuild', 'Crear mi Caché Gratis') }}</span>
            <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </NuxtLink>

          <NuxtLink
            to="/recruiter/search"
            class="px-7 py-4 rounded-xl font-bold text-white bg-white/[0.04] border border-white/10 hover:bg-white/[0.08] hover:border-white/20 transition-all duration-300 flex items-center justify-center gap-2 text-base"
          >
            <Search class="w-4 h-4 text-zinc-400" />
            <span>{{ $t('home.hero.ctaRecruiters', 'Buscar Talento Verificado') }}</span>
          </NuxtLink>
        </div>

        <!-- Indicadores de Confianza y Privacidad (Desktop) -->
        <div class="hidden lg:flex flex-wrap items-center justify-start gap-6 pt-4 text-zinc-400 text-xs font-medium border-t border-white/5">
          <div class="flex items-center gap-1.5">
            <ShieldCheck class="w-4 h-4 text-emerald-400 shrink-0" />
            <span>{{ $t('home.hero.interactiveCard.anonymousBadge', '100% Anónimo para el evaluador') }}</span>
          </div>
          <div class="flex items-center gap-1.5">
            <CheckCircle2 class="w-4 h-4 text-amber-400 shrink-0" />
            <span>{{ $t('home.hero.interactiveCard.antiFraudBadge', 'Anti-fraude de IP y relación') }}</span>
          </div>
        </div>

      </div>

      <!-- Columna Derecha: Selector de Demo (Mobile), Tarjeta Huella Conductual y CTAs (Mobile) -->
      <div class="lg:col-span-5 space-y-6 relative animate-in fade-in slide-in-from-right-8 duration-1000 delay-200">
        
        <!-- Selector Rápido de Demostración en Tiempo Real (Solo visible en Mobile - Justo encima del mapa) -->
        <div class="block lg:hidden space-y-2.5">
          <div class="flex items-center justify-center gap-1.5">
            <Sparkles class="w-3.5 h-3.5 text-amber-400" />
            <span class="text-xs font-semibold text-zinc-300 uppercase tracking-wider">
              {{ $t('home.hero.demoLabel', 'Prueba cómo se ve tu huella profesional:') }}
            </span>
          </div>
          
          <div class="grid grid-cols-3 p-1 rounded-2xl bg-white/[0.04] border border-white/10 gap-1 shadow-lg backdrop-blur-xl">
            <button 
              @click="selectedRole = 'builder'"
              :class="selectedRole === 'builder' ? 'bg-gradient-to-r from-amber-400 to-amber-500 text-zinc-950 font-black shadow-lg shadow-amber-500/25 scale-[1.02]' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
              class="py-2.5 px-1 sm:px-3 text-xs rounded-xl transition-all duration-300 flex items-center justify-center gap-1 text-center"
            >
              <Zap class="w-3 h-3 shrink-0" />
              <span class="truncate">{{ $t('home.hero.roles.builder.btn', 'Builder') }}</span>
            </button>
            <button 
              @click="selectedRole = 'leader'"
              :class="selectedRole === 'leader' ? 'bg-gradient-to-r from-sky-400 to-sky-500 text-zinc-950 font-black shadow-lg shadow-sky-500/25 scale-[1.02]' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
              class="py-2.5 px-1 sm:px-3 text-xs rounded-xl transition-all duration-300 flex items-center justify-center gap-1 text-center"
            >
              <Target class="w-3 h-3 shrink-0" />
              <span class="truncate">{{ $t('home.hero.roles.leader.btn', 'Leader') }}</span>
            </button>
            <button 
              @click="selectedRole = 'specialist'"
              :class="selectedRole === 'specialist' ? 'bg-gradient-to-r from-emerald-400 to-emerald-500 text-zinc-950 font-black shadow-lg shadow-emerald-500/25 scale-[1.02]' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
              class="py-2.5 px-1 sm:px-3 text-xs rounded-xl transition-all duration-300 flex items-center justify-center gap-1 text-center"
            >
              <Layers class="w-3 h-3 shrink-0" />
              <span class="truncate">{{ $t('home.hero.roles.specialist.btn', 'Specialist') }}</span>
            </button>
          </div>
        </div>

        <!-- Tarjeta de Huella Conductual Interactiva (Live WOW Preview) -->
        <div class="relative rounded-2xl sm:rounded-3xl bg-[hsl(228,16%,9%)]/90 border border-white/15 p-4 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.85)] backdrop-blur-2xl space-y-4 sm:space-y-5 transition-all duration-500 hover:border-amber-500/40">
          
          <!-- Header de la Card -->
          <div class="flex items-center justify-between border-b border-white/10 pb-3 sm:pb-4">
            <div class="flex items-center gap-2.5 sm:gap-3">
              <div class="w-9 h-9 sm:w-10 sm:h-10 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-600 flex items-center justify-center text-zinc-950 font-black shadow-md shrink-0">
                <Dna class="w-4 h-4 sm:w-5 sm:h-5" />
              </div>
              <div>
                <h3 class="text-xs sm:text-sm font-bold text-white tracking-tight">
                  {{ $t('home.hero.interactiveCard.title', 'Huella de Talento 360°') }}
                </h3>
                <span class="text-[10px] sm:text-[11px] text-zinc-400 font-medium">
                  {{ $t('home.hero.interactiveCard.subtitle', 'Verificación Multicapa Calibrada') }}
                </span>
              </div>
            </div>

            <div class="flex items-center gap-1 sm:gap-1.5 px-2.5 sm:px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 font-extrabold text-[10px] sm:text-[11px] shadow-sm shrink-0">
              <ShieldCheck class="w-3 h-3 sm:w-3.5 sm:h-3.5" />
              <span>{{ activeProfile.trustScore }}% Trust Score</span>
            </div>
          </div>

          <!-- Arquetipo Dinámico Activo -->
          <div class="space-y-1 sm:space-y-1.5 p-3 sm:p-4 rounded-xl sm:rounded-2xl bg-white/[0.03] border border-white/5 transition-all duration-300">
            <div class="flex items-center justify-between">
              <span class="text-[9px] sm:text-[10px] font-extrabold uppercase tracking-widest" :class="activeProfile.badgeColor.split(' ')[0]">
                {{ $t('home.hero.interactiveCard.dominantArchetype', 'Arquetipo Dominante') }}
              </span>
              <span class="text-[9px] sm:text-[10px] font-semibold text-zinc-400">
                {{ activeProfile.validatorsCount }} {{ $t('home.trustProtocol.validators', 'referencias verificadas') }}
              </span>
            </div>
            <h4 class="text-sm sm:text-lg font-black text-white tracking-tight">
              {{ $t(activeProfile.titleKey) }}
            </h4>
            <p class="text-[11px] sm:text-xs text-zinc-300 leading-relaxed">
              {{ $t(activeProfile.taglineKey) }}
            </p>
          </div>

          <!-- Plano Cartesiano 2D Reactivo con 4 Cuadrantes Iluminados y Crosshair Láser -->
          <div class="relative w-full h-56 sm:h-64 rounded-xl sm:rounded-2xl bg-black/70 border border-white/15 p-2 sm:p-3 overflow-hidden shadow-inner flex flex-col justify-between select-none">
            <!-- Rejilla de Fondo -->
            <div class="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:1.25rem_1.25rem] pointer-events-none"></div>

            <!-- Ejes Divisorios Centrales (Cruz en 50%) -->
            <div class="absolute left-1/2 top-0 bottom-0 w-[1px] bg-white/10 pointer-events-none -translate-x-1/2"></div>
            <div class="absolute top-1/2 left-0 right-0 h-[1px] bg-white/10 pointer-events-none -translate-y-1/2"></div>

            <!-- 4 Cuadrantes de Talento con Iluminación Reactiva -->
            <div class="absolute inset-2 sm:inset-3 grid grid-cols-2 grid-rows-2 gap-1.5 pointer-events-none z-0">
              <!-- Cuadrante 2 (Top-Left): Líder & Facilitador -->
              <div 
                :class="selectedRole === 'leader' ? 'bg-sky-500/15 border-sky-500/40 shadow-[inset_0_0_20px_rgba(56,189,248,0.15)] ring-1 ring-sky-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
                class="rounded-xl border p-2 flex flex-col justify-between transition-all duration-500"
              >
                <div class="flex items-center gap-1">
                  <span class="text-xs">👑</span>
                  <span 
                    :class="selectedRole === 'leader' ? 'text-sky-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                    class="text-[9px] sm:text-[11px] tracking-tight uppercase"
                  >
                    {{ $t('home.hero.quadrants.leader', 'Líder Facilitador') }}
                  </span>
                </div>
                <span class="text-[8px] sm:text-[9px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.leaderSub', 'Autonomía + Equipo') }}</span>
              </div>

              <!-- Cuadrante 1 (Top-Right): Builder / Innovador -->
              <div 
                :class="selectedRole === 'builder' ? 'bg-amber-500/15 border-amber-500/40 shadow-[inset_0_0_20px_rgba(245,158,11,0.15)] ring-1 ring-amber-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
                class="rounded-xl border p-2 flex flex-col justify-between items-end text-right transition-all duration-500"
              >
                <div class="flex items-center gap-1">
                  <span 
                    :class="selectedRole === 'builder' ? 'text-amber-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                    class="text-[9px] sm:text-[11px] tracking-tight uppercase"
                  >
                    {{ $t('home.hero.quadrants.builder', 'Builder (0 a 1)') }}
                  </span>
                  <span class="text-xs">🚀</span>
                </div>
                <span class="text-[8px] sm:text-[9px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.builderSub', 'Autonomía + Rigor') }}</span>
              </div>

              <!-- Cuadrante 3 (Bottom-Left): Colaborador Ágil -->
              <div 
                class="rounded-xl border bg-white/[0.015] border-white/5 opacity-40 p-2 flex flex-col justify-between transition-all duration-500"
              >
                <span class="text-[8px] sm:text-[9px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.collaboratorSub', 'Consenso + Ejecución') }}</span>
                <div class="flex items-center gap-1">
                  <span class="text-xs">🤝</span>
                  <span class="text-zinc-400 font-semibold text-[9px] sm:text-[11px] tracking-tight uppercase">
                    {{ $t('home.hero.quadrants.collaborator', 'Colaborador Ágil') }}
                  </span>
                </div>
              </div>

              <!-- Cuadrante 4 (Bottom-Right): Especialista Técnico -->
              <div 
                :class="selectedRole === 'specialist' ? 'bg-emerald-500/15 border-emerald-500/40 shadow-[inset_0_0_20px_rgba(16,185,129,0.15)] ring-1 ring-emerald-400/30' : 'bg-white/[0.015] border-white/5 opacity-40'"
                class="rounded-xl border p-2 flex flex-col justify-between items-end text-right transition-all duration-500"
              >
                <span class="text-[8px] sm:text-[9px] text-zinc-400 font-mono">{{ $t('home.hero.quadrants.specialistSub', 'Rigor + Foco Técnico') }}</span>
                <div class="flex items-center gap-1">
                  <span 
                    :class="selectedRole === 'specialist' ? 'text-emerald-300 font-extrabold' : 'text-zinc-400 font-semibold'"
                    class="text-[9px] sm:text-[11px] tracking-tight uppercase"
                  >
                    {{ $t('home.hero.quadrants.specialist', 'Especialista Técnico') }}
                  </span>
                  <span class="text-xs">🎯</span>
                </div>
              </div>
            </div>

            <!-- Eje Y Superior -->
            <div class="text-center text-[9px] sm:text-[10px] font-black text-amber-400/90 uppercase tracking-widest relative z-10 pointer-events-none">
              ▲ {{ $t('home.hero.interactiveCard.axisYTop', 'Alta Autonomía / Proactividad') }}
            </div>

            <!-- Eje X Central Minimalista -->
            <div class="flex items-center justify-between text-[8px] sm:text-[10px] font-bold text-zinc-300 relative z-10 px-1 pointer-events-none">
              <span class="bg-black/85 backdrop-blur-md px-1.5 sm:px-2 py-0.5 rounded border border-white/10 shadow-sm">
                ◄ {{ $t('home.hero.interactiveCard.axisXLeft', 'Consenso & Equipo') }}
              </span>
              <span class="bg-black/85 backdrop-blur-md px-1.5 sm:px-2 py-0.5 rounded border border-white/10 shadow-sm">
                {{ $t('home.hero.interactiveCard.axisXRight', 'Rigor & Estándares') }} ►
              </span>
            </div>

            <!-- Eje Y Inferior -->
            <div class="text-center text-[9px] sm:text-[10px] font-black text-sky-400/90 uppercase tracking-widest relative z-10 pointer-events-none">
              ▼ {{ $t('home.hero.interactiveCard.axisYBottom', 'Ejecución Guiada') }}
            </div>

            <!-- Líneas Láser Proyectadas (Crosshair) -->
            <div 
              class="absolute top-0 bottom-0 w-[1px] border-l border-dashed transition-all duration-700 ease-out z-10 pointer-events-none"
              :class="activeProfile.crosshairClass"
              :style="{ left: `${activeProfile.coordX}%` }"
            ></div>
            <div 
              class="absolute left-0 right-0 h-[1px] border-t border-dashed transition-all duration-700 ease-out z-10 pointer-events-none"
              :class="activeProfile.crosshairClass"
              :style="{ top: `${activeProfile.coordY}%` }"
            ></div>

            <!-- Punto Neón Reactivo con Badge Flotante de Coordenadas -->
            <div 
              class="absolute z-30 -translate-x-1/2 -translate-y-1/2 transition-all duration-700 ease-out flex flex-col items-center pointer-events-none"
              :style="{ left: `${activeProfile.coordX}%`, top: `${activeProfile.coordY}%` }"
            >
              <!-- Tooltip / Badge de Coordenadas -->
              <div 
                class="mb-1.5 px-2 py-0.5 rounded-md bg-zinc-950/95 border text-[9px] font-mono font-bold shadow-2xl whitespace-nowrap backdrop-blur-md flex items-center gap-1.5 transition-colors duration-500"
                :class="activeProfile.tooltipClass"
              >
                <span class="w-1.5 h-1.5 rounded-full animate-pulse bg-current"></span>
                <span>X: {{ activeProfile.coordX }}%</span>
                <span class="text-zinc-600">•</span>
                <span>Y: {{ 100 - activeProfile.coordY }}%</span>
              </div>

              <!-- Punto Pulsante -->
              <div 
                class="relative w-4 h-4 sm:w-5 sm:h-5 rounded-full border-2 border-white transition-all duration-500"
                :class="activeProfile.dotClass"
              >
                <span class="absolute inset-0 rounded-full animate-ping opacity-75 bg-current"></span>
              </div>
            </div>

          </div>

          <!-- Superpotencia Destacada -->
          <div class="space-y-1.5 sm:space-y-2 pt-0.5 sm:pt-1">
            <div class="flex items-center justify-between text-[11px] sm:text-xs">
              <span class="text-zinc-400 font-semibold">{{ $t('home.hero.interactiveCard.recognizedVirtue', 'Superpotencia Más Reconocida:') }}</span>
              <span class="text-emerald-400 font-bold text-[10px] sm:text-[11px] flex items-center gap-1">
                <CheckCircle2 class="w-3 h-3 sm:w-3.5 sm:h-3.5" />
                100% Consistencia 360°
              </span>
            </div>
            <div class="p-2.5 sm:p-3.5 rounded-xl bg-amber-500/10 border border-amber-500/25 text-[11px] sm:text-xs font-bold text-amber-200 flex items-start gap-2 sm:gap-2.5 shadow-sm">
              <Star class="w-3.5 h-3.5 sm:w-4 sm:h-4 text-amber-400 shrink-0 mt-0.5 fill-amber-400/20" />
              <span class="leading-relaxed">{{ $t(activeProfile.topStrengthKey) }}</span>
            </div>
          </div>

        </div>

        <!-- Botones de Acción Primaria (CTAs en Mobile - Justo debajo de la tarjeta interactiva) -->
        <div class="flex lg:hidden flex-col sm:flex-row items-center justify-center gap-3.5 pt-2">
          <NuxtLink
            to="/register"
            class="w-full sm:w-auto px-8 py-3.5 sm:py-4 rounded-xl font-black text-zinc-950 bg-gradient-to-r from-amber-400 via-primary to-orange-500 hover:brightness-110 transition-all duration-300 shadow-[0_0_35px_rgba(242,151,39,0.4)] flex items-center justify-center gap-2 group text-base"
          >
            <span>{{ $t('home.hero.ctaBuild', 'Crear mi Caché Gratis') }}</span>
            <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </NuxtLink>

          <NuxtLink
            to="/recruiter/search"
            class="w-full sm:w-auto px-7 py-3.5 sm:py-4 rounded-xl font-bold text-white bg-white/[0.04] border border-white/10 hover:bg-white/[0.08] hover:border-white/20 transition-all duration-300 flex items-center justify-center gap-2 text-base"
          >
            <Search class="w-4 h-4 text-zinc-400" />
            <span>{{ $t('home.hero.ctaRecruiters', 'Buscar Talento Verificado') }}</span>
          </NuxtLink>
        </div>

        <!-- Indicadores de Confianza y Privacidad (Mobile) -->
        <div class="flex lg:hidden flex-wrap items-center justify-center gap-4 pt-2 text-zinc-400 text-xs font-medium border-t border-white/5">
          <div class="flex items-center gap-1.5">
            <ShieldCheck class="w-4 h-4 text-emerald-400 shrink-0" />
            <span>{{ $t('home.hero.interactiveCard.anonymousBadge', '100% Anónimo para el evaluador') }}</span>
          </div>
          <div class="flex items-center gap-1.5">
            <CheckCircle2 class="w-4 h-4 text-amber-400 shrink-0" />
            <span>{{ $t('home.hero.interactiveCard.antiFraudBadge', 'Anti-fraude de IP y relación') }}</span>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>
