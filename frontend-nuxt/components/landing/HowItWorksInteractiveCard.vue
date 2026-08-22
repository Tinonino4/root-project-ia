<script setup lang="ts">
import { ref } from 'vue'
import { 
  ShieldCheck, 
  CheckCircle2, 
  Lock, 
  Sparkles, 
  Send, 
  Award, 
  ArrowRight, 
  Share2,
  Mail,
  UserCheck
} from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const activeStep = ref<number>(1)
const selectedAnswer = ref<'A' | 'B'>('A')
</script>

<template>
  <div class="relative rounded-3xl bg-[hsl(228,16%,9%)]/95 border border-white/15 p-5 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.85)] backdrop-blur-2xl space-y-6 transition-all duration-500 hover:border-amber-500/40">
    
    <!-- Top Step Selector Pills -->
    <div class="flex items-center justify-between border-b border-white/10 pb-4">
      <div class="flex items-center gap-1 sm:gap-1.5 p-1 rounded-2xl bg-white/[0.04] border border-white/10 w-full">
        <button 
          @click="activeStep = 1"
          :class="activeStep === 1 ? 'bg-gradient-to-r from-amber-400 to-amber-500 text-zinc-950 font-black shadow-md' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
          class="flex-1 py-2 px-2 text-[11px] sm:text-xs rounded-xl transition-all duration-300 flex items-center justify-center gap-1.5"
        >
          <Send class="w-3.5 h-3.5 shrink-0" />
          <span class="truncate">{{ $t('home.howItWorks.interactive.tab1', '1. Invitación') }}</span>
        </button>
        <button 
          @click="activeStep = 2"
          :class="activeStep === 2 ? 'bg-gradient-to-r from-sky-400 to-sky-500 text-zinc-950 font-black shadow-md' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
          class="flex-1 py-2 px-2 text-[11px] sm:text-xs rounded-xl transition-all duration-300 flex items-center justify-center gap-1.5"
        >
          <Lock class="w-3.5 h-3.5 shrink-0" />
          <span class="truncate">{{ $t('home.howItWorks.interactive.tab2', '2. Elección Forzada') }}</span>
        </button>
        <button 
          @click="activeStep = 3"
          :class="activeStep === 3 ? 'bg-gradient-to-r from-emerald-400 to-emerald-500 text-zinc-950 font-black shadow-md' : 'text-zinc-400 hover:text-white font-medium hover:bg-white/[0.04]'"
          class="flex-1 py-2 px-2 text-[11px] sm:text-xs rounded-xl transition-all duration-300 flex items-center justify-center gap-1.5"
        >
          <Award class="w-3.5 h-3.5 shrink-0" />
          <span class="truncate">{{ $t('home.howItWorks.interactive.tab3', '3. Pasaporte 360°') }}</span>
        </button>
      </div>
    </div>

    <!-- Step 1: Invitación Blindada -->
    <div v-if="activeStep === 1" class="space-y-4 animate-in fade-in zoom-in-95 duration-500">
      <div class="flex items-center justify-between">
        <span class="text-[10px] font-extrabold uppercase tracking-widest text-amber-400">
          {{ $t('home.howItWorks.interactive.step1Badge', 'Paso 01: Activación Criptográfica') }}
        </span>
        <span class="text-[11px] text-emerald-400 font-bold flex items-center gap-1">
          <ShieldCheck class="w-3.5 h-3.5" />
          {{ $t('home.howItWorks.interactive.step1Security', 'Token Único de Solo Uso') }}
        </span>
      </div>

      <div class="p-4 rounded-2xl bg-white/[0.03] border border-white/5 space-y-3">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 shrink-0">
            <Mail class="w-4 h-4" />
          </div>
          <div>
            <h4 class="text-xs sm:text-sm font-bold text-white">marta.director@techgroup.com</h4>
            <p class="text-[11px] text-zinc-400">Ex-Mánager Directo • Relación Laboral Verificada</p>
          </div>
        </div>

        <div class="p-3 rounded-xl bg-black/40 border border-white/5 space-y-1.5 text-xs">
          <div class="flex justify-between text-zinc-400 text-[11px]">
            <span>Enlace cifrado generado:</span>
            <span class="text-amber-300 font-mono">cache.io/verify/tk_9f82...</span>
          </div>
          <p class="text-zinc-300 text-[11px] leading-relaxed">
            "Has sido invitado a certificar el desempeño de Alex. Tu evaluación es <strong class="text-white font-bold">100% anónima</strong> y no se mostrará de forma individual."
          </p>
        </div>
      </div>

      <button 
        @click="activeStep = 2"
        class="w-full py-3 rounded-xl font-bold text-xs sm:text-sm bg-white/[0.06] hover:bg-white/[0.12] border border-white/10 text-white flex items-center justify-center gap-2 transition-all group"
      >
        <span>{{ $t('home.howItWorks.interactive.step1Cta', 'Ver cómo responde el evaluador en el Paso 2') }}</span>
        <ArrowRight class="w-4 h-4 text-amber-400 transition-transform group-hover:translate-x-1" />
      </button>
    </div>

    <!-- Step 2: Cuestionario de Elección Forzada -->
    <div v-else-if="activeStep === 2" class="space-y-4 animate-in fade-in zoom-in-95 duration-500">
      <div class="flex items-center justify-between">
        <span class="text-[10px] font-extrabold uppercase tracking-widest text-sky-400">
          {{ $t('home.howItWorks.interactive.step2Badge', 'Paso 02: Evaluación Blindada Anti-Sesgo') }}
        </span>
        <span class="text-[11px] text-sky-300 font-bold flex items-center gap-1">
          <Sparkles class="w-3.5 h-3.5" />
          {{ $t('home.howItWorks.interactive.step2AntiBias', 'Sin Notas 5/5 Infladas') }}
        </span>
      </div>

      <div class="p-4 rounded-2xl bg-white/[0.03] border border-white/5 space-y-3">
        <div class="flex items-start gap-2.5">
          <span class="w-5 h-5 rounded-md bg-sky-500/20 text-sky-300 font-black text-[10px] flex items-center justify-center shrink-0 mt-0.5">
            Q1
          </span>
          <p class="text-xs sm:text-sm font-bold text-white leading-snug">
            {{ $t('home.howItWorks.interactive.questionTitle', '¿Qué describe con mayor precisión su comportamiento ante un bloqueo técnico imprevisto?') }}
          </p>
        </div>

        <!-- Forced-Choice Options (Interactive) -->
        <div class="space-y-2 pt-1">
          <button 
            @click="selectedAnswer = 'A'"
            :class="selectedAnswer === 'A' ? 'bg-sky-500/15 border-sky-500/40 text-white shadow-lg ring-1 ring-sky-400/30' : 'bg-black/40 border-white/5 text-zinc-400 hover:text-zinc-200'"
            class="w-full p-3 rounded-xl border text-left transition-all duration-300 flex items-start gap-2.5"
          >
            <span 
              :class="selectedAnswer === 'A' ? 'bg-sky-400 text-zinc-950 font-black' : 'border border-white/20 text-zinc-500'"
              class="w-4 h-4 rounded-full flex items-center justify-center text-[10px] shrink-0 mt-0.5 transition-colors"
            >
              A
            </span>
            <span class="text-xs font-semibold leading-relaxed">
              {{ $t('home.howItWorks.interactive.optionA', 'Propone soluciones ágiles y desbloquea rápidamente la entrega.') }}
            </span>
          </button>

          <button 
            @click="selectedAnswer = 'B'"
            :class="selectedAnswer === 'B' ? 'bg-sky-500/15 border-sky-500/40 text-white shadow-lg ring-1 ring-sky-400/30' : 'bg-black/40 border-white/5 text-zinc-400 hover:text-zinc-200'"
            class="w-full p-3 rounded-xl border text-left transition-all duration-300 flex items-start gap-2.5"
          >
            <span 
              :class="selectedAnswer === 'B' ? 'bg-sky-400 text-zinc-950 font-black' : 'border border-white/20 text-zinc-500'"
              class="w-4 h-4 rounded-full flex items-center justify-center text-[10px] shrink-0 mt-0.5 transition-colors"
            >
              B
            </span>
            <span class="text-xs font-semibold leading-relaxed">
              {{ $t('home.howItWorks.interactive.optionB', 'Analiza a fondo la causa raíz y prioriza el rigor arquitectónico.') }}
            </span>
          </button>
        </div>

        <div class="flex items-center gap-1.5 text-[11px] text-zinc-400 pt-1">
          <CheckCircle2 class="w-3.5 h-3.5 text-emerald-400 shrink-0" />
          <span>{{ $t('home.howItWorks.interactive.forcedChoiceNotice', 'La elección forzada garantiza datos conductuales contrastados sin sesgo de cortesía.') }}</span>
        </div>
      </div>

      <button 
        @click="activeStep = 3"
        class="w-full py-3 rounded-xl font-bold text-xs sm:text-sm bg-white/[0.06] hover:bg-white/[0.12] border border-white/10 text-white flex items-center justify-center gap-2 transition-all group"
      >
        <span>{{ $t('home.howItWorks.interactive.step2Cta', 'Ver el Pasaporte y Sello generado en el Paso 3') }}</span>
        <ArrowRight class="w-4 h-4 text-sky-400 transition-transform group-hover:translate-x-1" />
      </button>
    </div>

    <!-- Step 3: Pasaporte 360° Certificado -->
    <div v-else class="space-y-4 animate-in fade-in zoom-in-95 duration-500">
      <div class="flex items-center justify-between">
        <span class="text-[10px] font-extrabold uppercase tracking-widest text-emerald-400">
          {{ $t('home.howItWorks.interactive.step3Badge', 'Paso 03: Pasaporte Certificado Listo') }}
        </span>
        <span class="text-[11px] text-emerald-300 font-bold flex items-center gap-1">
          <UserCheck class="w-3.5 h-3.5" />
          {{ $t('home.howItWorks.interactive.step3Audit', 'Auditado & Verificado') }}
        </span>
      </div>

      <div class="p-4 rounded-2xl bg-gradient-to-br from-emerald-500/10 via-zinc-900 to-black border border-emerald-500/30 space-y-3.5 shadow-xl">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2.5">
            <div class="w-9 h-9 rounded-xl bg-gradient-to-tr from-emerald-400 to-teal-500 text-zinc-950 font-black flex items-center justify-center shadow-md">
              <Award class="w-5 h-5" />
            </div>
            <div>
              <h4 class="text-xs sm:text-sm font-black text-white">Pasaporte Caché 360°</h4>
              <span class="text-[10px] text-emerald-400 font-mono font-bold">CERT-2026-9817X</span>
            </div>
          </div>

          <div class="px-2.5 py-1 rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 font-black text-[11px]">
            98% Trust Score
          </div>
        </div>

        <div class="grid grid-cols-2 gap-2 text-xs">
          <div class="p-2.5 rounded-xl bg-black/50 border border-white/5 space-y-0.5">
            <span class="text-[10px] text-zinc-400 font-bold uppercase">Referencias</span>
            <span class="text-white font-extrabold block text-xs sm:text-sm">8 Verificadas</span>
          </div>
          <div class="p-2.5 rounded-xl bg-black/50 border border-white/5 space-y-0.5">
            <span class="text-[10px] text-zinc-400 font-bold uppercase">Consistencia 360°</span>
            <span class="text-emerald-400 font-extrabold block text-xs sm:text-sm">100% Calibrada</span>
          </div>
        </div>

        <div class="p-2.5 rounded-xl bg-white/[0.02] border border-white/5 flex items-center justify-between text-[11px] text-zinc-300">
          <span>Insignia oficial para CV y LinkedIn:</span>
          <span class="text-emerald-300 font-bold flex items-center gap-1">
            <Share2 class="w-3 h-3" />
            cache.io/u/alex-dev
          </span>
        </div>
      </div>

      <button 
        @click="activeStep = 1"
        class="w-full py-3 rounded-xl font-bold text-xs sm:text-sm bg-white/[0.06] hover:bg-white/[0.12] border border-white/10 text-white flex items-center justify-center gap-2 transition-all group"
      >
        <span>{{ $t('home.howItWorks.interactive.step3Cta', 'Reiniciar demostración del flujo') }}</span>
        <Sparkles class="w-4 h-4 text-amber-400" />
      </button>
    </div>

  </div>
</template>
