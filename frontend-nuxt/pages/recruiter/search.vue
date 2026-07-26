<script setup lang="ts">
import { ref } from 'vue'
import { Search, Users, MapPin, Briefcase, ArrowRight, UserCheck } from 'lucide-vue-next'

definePageMeta({
  layout: 'default'
})

const query = ref('')
const candidates = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const searchPerformed = ref(false)

const quickFilters = [
  { label: 'Desarrollador', value: 'Developer' },
  { label: 'Manager', value: 'Manager' },
  { label: 'Diseñador', value: 'Designer' },
  { label: 'Madrid', value: 'Madrid' },
  { label: 'Barcelona', value: 'Barcelona' }
]

const fetchCandidates = async () => {
  const trimmedQuery = query.value.trim()
  if (!trimmedQuery) {
    candidates.value = []
    searchPerformed.value = false
    return
  }

  loading.value = true
  error.value = null
  searchPerformed.value = true

  try {
    const data = await $api('/recruiter/candidates', { query: { query: trimmedQuery } })
    candidates.value = data
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

const clearSearch = () => {
  query.value = ''
  candidates.value = []
  searchPerformed.value = false
  error.value = null
}
</script>

<template>
  <div class="space-y-8 max-w-6xl mx-auto">
    <div class="space-y-2">
      <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight text-zinc-900 dark:text-white">
        {{ $t('recruiterSearch.title') }} <span class="bg-gradient-to-r from-primary to-orange-500 bg-clip-text text-transparent">B2B</span>
      </h1>
      <p class="text-zinc-500 dark:text-zinc-400 text-lg max-w-2xl leading-relaxed">
        {{ $t('recruiterSearch.subtitle') }}
      </p>
    </div>

    <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 md:p-8 shadow-sm relative overflow-hidden backdrop-blur-xl transition-all duration-300">
      <div class="space-y-4 relative z-10">
        <label for="search-input" class="block text-sm font-semibold text-zinc-700 dark:text-zinc-300">
          {{ $t('recruiterSearch.title') }}
        </label>
        
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-zinc-400 dark:text-zinc-500">
            <Search class="w-5 h-5" />
          </div>
          <input
            id="search-input"
            v-model="query"
            type="text"
            :placeholder="$t('recruiterSearch.searchPlaceholder')"
            class="block w-full pl-12 pr-10 py-3.5 bg-zinc-50 dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-xl text-zinc-900 dark:text-white placeholder-zinc-400 dark:placeholder-zinc-600 focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200 text-base"
            @input="onSearchInput"
            @keydown.enter="fetchCandidates"
          />
          <button
            v-if="query"
            @click="clearSearch"
            class="absolute inset-y-0 right-0 pr-4 flex items-center text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200"
          >
            ×
          </button>
        </div>

        <div class="flex flex-wrap items-center gap-2.5 pt-2">
          <span class="text-xs font-semibold uppercase tracking-wider text-zinc-400 dark:text-zinc-500 mr-1.5">
            Quick search:
          </span>
          <button
            v-for="filter in quickFilters"
            :key="filter.label"
            @click="selectFilter(filter.value)"
            class="px-3.5 py-1.5 text-xs font-medium rounded-full border transition-all duration-200"
            :class="[
              query === filter.value
                ? 'bg-primary text-white border-primary shadow-sm shadow-primary/25'
                : 'bg-zinc-50 dark:bg-zinc-800/40 text-zinc-600 dark:text-zinc-300 border-zinc-200 dark:border-zinc-800'
            ]"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>
    </div>

    <div class="space-y-4">
      <div v-if="searchPerformed" class="flex items-center justify-between px-1">
        <h2 class="text-lg font-bold text-zinc-800 dark:text-zinc-200">
          Results
          <span v-if="!loading" class="text-sm font-normal text-zinc-500 dark:text-zinc-500 ml-2">
            ({{ candidates.length }})
          </span>
        </h2>
      </div>

      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="i in 3" 
          :key="i"
          class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 space-y-4 animate-pulse"
        >
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-full bg-zinc-200 dark:bg-zinc-800"></div>
          </div>
        </div>
      </div>

      <div v-else-if="error" class="bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900/50 rounded-xl p-4 text-center">
        <p class="text-sm font-medium text-red-800 dark:text-red-300">{{ error }}</p>
      </div>

      <div 
        v-else-if="!searchPerformed" 
        class="bg-zinc-50 dark:bg-zinc-900/20 border-2 border-dashed border-zinc-200 dark:border-zinc-800/80 rounded-2xl p-12 text-center flex flex-col items-center justify-center space-y-4"
      >
        <div class="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center text-primary">
          <Users class="w-7 h-7" />
        </div>
        <div class="max-w-sm space-y-2">
          <h3 class="text-base font-bold text-zinc-900 dark:text-white">{{ $t('recruiterSearch.title') }}</h3>
          <p class="text-sm text-zinc-500 dark:text-zinc-400">
            {{ $t('recruiterSearch.subtitle') }}
          </p>
        </div>
      </div>

      <div 
        v-else-if="candidates.length === 0" 
        class="bg-zinc-50 dark:bg-zinc-900/20 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-12 text-center flex flex-col items-center justify-center space-y-4"
      >
        <div class="w-14 h-14 rounded-2xl bg-zinc-100 dark:bg-zinc-800/50 flex items-center justify-center text-zinc-400">
          <Search class="w-7 h-7" />
        </div>
        <div class="max-w-sm space-y-2">
          <h3 class="text-base font-bold text-zinc-900 dark:text-white">{{ $t('recruiterSearch.noResults') }}</h3>
        </div>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="candidate in candidates"
          :key="candidate.userId"
          class="group bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col justify-between"
        >
          <div class="space-y-4">
            <div class="flex items-start gap-4">
              <div class="w-14 h-14 rounded-full bg-gradient-to-tr from-primary/10 to-orange-500/10 border border-primary/20 flex items-center justify-center font-bold text-lg text-primary flex-shrink-0 overflow-hidden">
                <img v-if="candidate.photoUrl" :src="candidate.photoUrl" alt="Avatar" class="w-full h-full object-cover" />
                <span v-else>{{ candidate.name?.charAt(0) }}{{ candidate.surname?.charAt(0) }}</span>
              </div>

              <div class="space-y-1 min-w-0">
                <h3 class="font-bold text-zinc-900 dark:text-white text-base truncate group-hover:text-primary transition-colors duration-200">
                  {{ candidate.name }} {{ candidate.surname }}
                </h3>
                
                <div class="flex items-center gap-1.5 text-xs font-semibold text-primary">
                  <Briefcase class="w-3.5 h-3.5 flex-shrink-0" />
                  <span class="truncate">{{ candidate.jobTitle || $t('sidebar.professionalRole') }}</span>
                </div>

                <div v-if="candidate.city" class="flex items-center gap-1.5 text-xs text-zinc-500 dark:text-zinc-400">
                  <MapPin class="w-3.5 h-3.5 flex-shrink-0 text-zinc-400" />
                  <span class="truncate">{{ candidate.city }}</span>
                </div>
              </div>
            </div>

            <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-green-50 dark:bg-green-950/20 border border-green-200/40 text-xs font-medium text-green-700 dark:text-green-400">
              <UserCheck class="w-4 h-4 text-green-500 flex-shrink-0" />
              <span>{{ $t('profile.verificationBadge') }}</span>
            </div>
          </div>

          <div class="mt-6 pt-4 border-t border-zinc-100 dark:border-zinc-800/80">
            <NuxtLink
              :to="`/u/${candidate.userId}`"
              class="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-zinc-50 dark:bg-zinc-800 text-sm font-semibold text-zinc-700 dark:text-zinc-200 hover:bg-primary hover:text-white border border-zinc-200 dark:border-zinc-700 transition-all duration-200"
            >
              {{ $t('recruiterSearch.viewProfile') }}
              <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
