<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  Search, 
  Users, 
  MapPin, 
  Briefcase, 
  ArrowRight, 
  ShieldCheck, 
  Sparkles, 
  Zap, 
  Target, 
  Layers, 
  Award,
  Filter
} from 'lucide-vue-next'

definePageMeta({
  layout: 'default'
})

const query = ref('')
const selectedArchetype = ref('all')
const candidates = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const searchPerformed = ref(false)

const archetypeFilters = [
  { id: 'all', label: 'Todos los Arquetipos', icon: Layers },
  { id: 'builder', label: '🚀 Builder (0 a 1)', icon: Zap },
  { id: 'leader', label: '👑 Líder Coach', icon: Target },
  { id: 'specialist', label: '🎯 Alto Rigor', icon: Sparkles },
  { id: 'high_trust', label: '🛡️ Trust Score > 80%', icon: ShieldCheck }
]

const quickKeywords = [
  { label: 'Engineering Manager', value: 'Engineering Manager' },
  { label: 'Frontend / Vue', value: 'Frontend' },
  { label: 'Backend / Java', value: 'Java' },
  { label: 'Product Designer', value: 'Product' },
  { label: 'Madrid / Remoto', value: 'Madrid' }
]

// Showcase featured candidates for instant recruiter value
const featuredCandidates = [
  {
    userId: 'demo-1',
    name: 'Agustin',
    surname: 'Hernandez-Gil',
    jobTitle: 'Program Engineer Manager',
    city: 'Madrid (Remoto)',
    archetypeTitle: 'El Ejecutor Pragmático',
    trustScore: 84,
    skillsCount: 1,
    topStrengths: ['Entrega bajo incertidumbre', 'Arquitectura Hexagonal', 'Liderazgo de Equipos']
  },
  {
    userId: 'demo-2',
    name: 'Elena',
    surname: 'Vázquez',
    jobTitle: 'Lead Product Designer',
    city: 'Barcelona',
    archetypeTitle: 'Diseñadora de Sistemas',
    trustScore: 92,
    skillsCount: 4,
    topStrengths: ['Design Systems', 'Investigación Heurística', 'Prototipado Rápido']
  },
  {
    userId: 'demo-3',
    name: 'Carlos',
    surname: 'Mendoza',
    jobTitle: 'Staff Backend Architect',
    city: 'Valencia',
    archetypeTitle: 'Arquitecto de Rigor',
    trustScore: 88,
    skillsCount: 3,
    topStrengths: ['Sistemas Distribuidos', 'Clean Code', 'Seguridad y Concurrencia']
  }
]

onMounted(async () => {
  if (import.meta.client && !query.value) {
    try {
      const data = await $api('/recruiter/candidates', { query: { query: 'a' } })
      if (Array.isArray(data) && data.length > 0) {
        candidates.value = data
      }
    } catch (e) {
      console.log('Using featured showcase for recruiter search preview')
    }
  }
})

const fetchCandidates = async () => {
  const trimmedQuery = query.value.trim()
  if (!trimmedQuery && selectedArchetype.value === 'all') {
    searchPerformed.value = false
    return
  }

  loading.value = true
  error.value = null
  searchPerformed.value = true

  try {
    const data = await $api('/recruiter/candidates', { 
      query: { 
        query: trimmedQuery || selectedArchetype.value 
      } 
    })
    candidates.value = data as any[]
  } catch (err) {
    console.error('Error searching candidates:', err)
    error.value = 'Ocurrió un error al realizar la búsqueda. Por favor, inténtelo de nuevo.'
  } finally {
    loading.value = false
  }
}

let debounceTimeout: any = null
const onSearchInput = () => {
  if (debounceTimeout) clearTimeout(debounceTimeout)
  debounceTimeout = setTimeout(() => {
    fetchCandidates()
  }, 350)
}

const selectFilter = (filterValue: string) => {
  query.value = filterValue
  fetchCandidates()
}

const selectArchetype = (archetypeId: string) => {
  selectedArchetype.value = archetypeId
  if (archetypeId === 'all') {
    if (!query.value) searchPerformed.value = false
    else fetchCandidates()
  } else {
    fetchCandidates()
  }
}

const clearSearch = () => {
  query.value = ''
  selectedArchetype.value = 'all'
  searchPerformed.value = false
  error.value = null
}
</script>

<template>
  <div class="space-y-8 max-w-6xl mx-auto pb-12">
    
    <!-- Hero Header -->
    <div class="space-y-2">
      <div class="flex items-center gap-2.5">
        <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight text-zinc-900 dark:text-white font-heading">
          {{ $t('recruiterSearch.title') }}
        </h1>
        <span class="px-2.5 py-1 rounded-xl bg-gradient-to-r from-amber-400 via-primary to-orange-500 text-zinc-950 text-xs font-black shadow-lg shadow-primary/20">
          B2B TALENT
        </span>
      </div>
      <p class="text-zinc-500 dark:text-zinc-400 text-sm sm:text-base max-w-2xl leading-relaxed">
        {{ $t('recruiterSearch.subtitle') }}
      </p>
    </div>

    <!-- Search & Faceted Filter Card -->
    <div class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/60 dark:border-white/10 rounded-3xl p-6 md:p-8 shadow-xl relative overflow-hidden backdrop-blur-xl space-y-6">
      <div class="space-y-3 relative z-10">
        <label for="search-input" class="block text-xs font-bold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider">
          Búsqueda de Talento Certificado
        </label>
        
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-zinc-400 dark:text-zinc-500">
            <Search class="w-5 h-5" />
          </div>
          <input
            id="search-input"
            v-model="query"
            type="text"
            :placeholder="$t('recruiterSearch.searchPlaceholder', 'Buscar por nombre, puesto, competencias o tecnología...')"
            class="block w-full pl-12 pr-10 py-3.5 bg-zinc-50 dark:bg-zinc-950/80 border border-zinc-200 dark:border-white/10 rounded-2xl text-zinc-900 dark:text-white placeholder-zinc-400 dark:placeholder-zinc-600 focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all text-sm sm:text-base font-medium shadow-inner"
            @input="onSearchInput"
            @keydown.enter="fetchCandidates"
          />
          <button
            v-if="query"
            @click="clearSearch"
            class="absolute inset-y-0 right-0 pr-4 flex items-center text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 text-xl font-bold"
          >
            ×
          </button>
        </div>

        <!-- Archetype Facets -->
        <div class="space-y-2 pt-2">
          <div class="flex items-center gap-1.5 text-xs font-bold text-zinc-400">
            <Filter class="w-3.5 h-3.5 text-primary" />
            <span>Filtrar por Arquetipo & Nivel de Confianza:</span>
          </div>
          <div class="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
            <button
              v-for="item in archetypeFilters"
              :key="item.id"
              @click="selectArchetype(item.id)"
              class="px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 shrink-0 border"
              :class="selectedArchetype === item.id 
                ? 'bg-amber-500/20 border-amber-500/50 text-amber-300 shadow-sm' 
                : 'bg-zinc-100 dark:bg-white/[0.03] border-zinc-200 dark:border-white/5 text-zinc-600 dark:text-zinc-400 hover:text-white hover:border-white/15'"
            >
              <component :is="item.icon" class="w-3.5 h-3.5" />
              <span>{{ item.label }}</span>
            </button>
          </div>
        </div>

        <!-- Quick Keywords -->
        <div class="flex flex-wrap items-center gap-2 pt-2">
          <span class="text-[11px] font-bold uppercase tracking-wider text-zinc-400 dark:text-zinc-500 mr-1">
            Keywords:
          </span>
          <button
            v-for="filter in quickKeywords"
            :key="filter.label"
            @click="selectFilter(filter.value)"
            class="px-3 py-1 text-xs font-semibold rounded-lg border transition-all"
            :class="[
              query === filter.value
                ? 'bg-primary text-white border-primary shadow-sm'
                : 'bg-zinc-50 dark:bg-white/[0.02] text-zinc-600 dark:text-zinc-400 border-zinc-200 dark:border-white/5 hover:border-white/20'
            ]"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- Results Section -->
    <div class="space-y-6">
      
      <div v-if="searchPerformed" class="flex items-center justify-between px-1">
        <h2 class="text-lg font-extrabold text-zinc-900 dark:text-white font-heading">
          Candidatos Certificados
          <span v-if="!loading" class="text-xs font-bold text-amber-400 ml-2 px-2.5 py-0.5 rounded-full bg-amber-500/10 border border-amber-500/20">
            {{ candidates.length }} encontrados
          </span>
        </h2>
      </div>

      <div v-else class="flex items-center justify-between px-1">
        <div class="flex items-center gap-2">
          <Sparkles class="w-4 h-4 text-amber-400" />
          <h2 class="text-base font-extrabold text-zinc-900 dark:text-white font-heading">
            Talento Verificado Destacado de la Semana
          </h2>
        </div>
        <span class="text-xs text-zinc-500 dark:text-zinc-400">Verificados con Protocolo Let's Trust</span>
      </div>

      <!-- Loading skeleton -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="i in 3" 
          :key="i"
          class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200 dark:border-white/5 rounded-3xl p-6 space-y-4 animate-pulse"
        >
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-2xl bg-zinc-200 dark:bg-zinc-800"></div>
            <div class="space-y-2 flex-1">
              <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-2/3"></div>
              <div class="h-3 bg-zinc-200 dark:bg-zinc-800 rounded w-1/2"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-red-500/10 border border-red-500/20 rounded-2xl p-6 text-center">
        <p class="text-sm font-medium text-red-400">{{ error }}</p>
      </div>

      <!-- No Search Results -->
      <div 
        v-else-if="searchPerformed && candidates.length === 0" 
        class="bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200 dark:border-white/5 rounded-3xl p-12 text-center flex flex-col items-center justify-center space-y-3 shadow-xl"
      >
        <div class="w-14 h-14 rounded-2xl bg-zinc-100 dark:bg-zinc-800/80 flex items-center justify-center text-zinc-400">
          <Search class="w-7 h-7" />
        </div>
        <h3 class="text-lg font-bold text-zinc-900 dark:text-white">{{ $t('recruiterSearch.noResults', 'No se encontraron candidatos para esta búsqueda') }}</h3>
        <p class="text-xs text-zinc-500 max-w-sm">Prueba ajustando los filtros de arquetipo o utilizando términos más generales.</p>
      </div>

      <!-- Candidates Grid (Search results or featured discovery) -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="candidate in (searchPerformed ? candidates : (candidates.length > 0 ? candidates : featuredCandidates))"
          :key="candidate.userId"
          class="group bg-white dark:bg-[hsl(228,15%,9%)] border border-zinc-200/70 dark:border-white/10 rounded-3xl p-6 shadow-xl hover:border-primary/40 hover:shadow-2xl transition-all duration-300 flex flex-col justify-between"
        >
          <div class="space-y-4">
            <!-- Candidate Header -->
            <div class="flex items-start gap-4">
              <div class="relative w-14 h-14 rounded-2xl bg-gradient-to-tr from-amber-500/20 to-orange-500/10 border border-primary/30 flex items-center justify-center font-extrabold text-xl text-primary flex-shrink-0 overflow-hidden shadow-md">
                <img v-if="candidate.photoUrl" :src="candidate.photoUrl" alt="Avatar" loading="lazy" class="w-full h-full object-cover" />
                <span v-else>{{ candidate.name?.charAt(0) }}{{ candidate.surname?.charAt(0) }}</span>
              </div>

              <div class="space-y-0.5 min-w-0 flex-1">
                <h3 class="font-extrabold text-zinc-900 dark:text-white text-base truncate group-hover:text-primary transition-colors">
                  {{ candidate.name }} {{ candidate.surname }}
                </h3>
                
                <div class="flex items-center gap-1 text-xs font-semibold text-primary truncate">
                  <Briefcase class="w-3.5 h-3.5 flex-shrink-0" />
                  <span class="truncate">{{ candidate.jobTitle || $t('sidebar.professionalRole') }}</span>
                </div>

                <div v-if="candidate.city" class="flex items-center gap-1 text-[11px] text-zinc-500 dark:text-zinc-400">
                  <MapPin class="w-3 h-3 flex-shrink-0 text-zinc-400" />
                  <span class="truncate">{{ candidate.city }}</span>
                </div>
              </div>
            </div>

            <!-- Trust Badge & Archetype -->
            <div class="space-y-2 pt-2 border-t border-zinc-100 dark:border-white/5">
              <div class="flex items-center justify-between">
                <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-extrabold">
                  <ShieldCheck class="w-3.5 h-3.5" />
                  <span>{{ candidate.trustScore ? `${candidate.trustScore}% Trust Score` : 'Nivel Oro Verificado' }}</span>
                </span>
                <span class="text-[10px] font-bold text-amber-300 bg-amber-500/10 px-2 py-0.5 rounded-md border border-amber-500/20">
                  360° Certified
                </span>
              </div>

              <!-- Top Strengths Tags -->
              <div v-if="candidate.topStrengths && candidate.topStrengths.length > 0" class="flex flex-wrap gap-1.5 pt-1">
                <span 
                  v-for="st in candidate.topStrengths.slice(0, 2)" 
                  :key="st"
                  class="text-[10px] font-semibold px-2 py-0.5 rounded-md bg-white/[0.04] border border-white/5 text-zinc-300 truncate"
                >
                  {{ st }}
                </span>
              </div>
            </div>
          </div>

          <!-- Action Button -->
          <div class="mt-6 pt-4 border-t border-zinc-100 dark:border-white/5">
            <NuxtLink
              :to="`/u/${candidate.userId}`"
              class="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-zinc-100 dark:bg-white/[0.04] text-xs font-bold text-zinc-800 dark:text-zinc-200 hover:bg-gradient-to-r hover:from-amber-400 hover:to-orange-500 hover:text-zinc-950 border border-zinc-200 dark:border-white/10 transition-all duration-200 shadow-sm"
            >
              <span>{{ $t('recruiterSearch.viewProfile', 'Ver Caché 360°') }}</span>
              <ArrowRight class="w-3.5 h-3.5 transition-transform group-hover:translate-x-1" />
            </NuxtLink>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>
