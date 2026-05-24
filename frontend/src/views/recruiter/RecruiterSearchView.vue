<script setup>
import { ref, watch, onMounted } from 'vue';
import { recruiterApi } from '@/api/recruiter.api';
import { Search, Users, MapPin, Briefcase, ArrowRight, UserCheck } from 'lucide-vue-next';

const query = ref('');
const candidates = ref([]);
const loading = ref(false);
const error = ref(null);
const searchPerformed = ref(false);

const quickFilters = [
  { label: 'Desarrollador', value: 'Developer' },
  { label: 'Manager', value: 'Manager' },
  { label: 'Diseñador', value: 'Designer' },
  { label: 'Madrid', value: 'Madrid' },
  { label: 'Barcelona', value: 'Barcelona' }
];

const fetchCandidates = async () => {
  const trimmedQuery = query.value.trim();
  if (!trimmedQuery) {
    candidates.value = [];
    searchPerformed.value = false;
    return;
  }

  loading.value = true;
  error.value = null;
  searchPerformed.value = true;

  try {
    const response = await recruiterApi.searchCandidates(trimmedQuery);
    candidates.value = response.data;
  } catch (err) {
    console.error('Error searching candidates:', err);
    error.value = 'Ocurrió un error al realizar la búsqueda. Por favor, inténtelo de nuevo.';
  } finally {
    loading.value = false;
  }
};

let debounceTimeout = null;
const onSearchInput = () => {
  if (debounceTimeout) clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(() => {
    fetchCandidates();
  }, 350);
};

const selectFilter = (filterValue) => {
  query.value = filterValue;
  fetchCandidates();
};

const clearSearch = () => {
  query.value = '';
  candidates.value = [];
  searchPerformed.value = false;
  error.value = null;
};
</script>

<template>
  <div class="space-y-8 max-w-6xl mx-auto">
    <!-- Header -->
    <div class="space-y-2">
      <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight text-zinc-900 dark:text-white">
        Buscador de Talento <span class="bg-gradient-to-r from-primary to-orange-500 bg-clip-text text-transparent">B2B</span>
      </h1>
      <p class="text-zinc-500 dark:text-zinc-400 text-lg max-w-2xl leading-relaxed">
        Encuentra candidatos de primer nivel, analiza sus soft-skills validadas por compañeros y exporta sus informes certificados.
      </p>
    </div>

    <!-- Search Section -->
    <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 md:p-8 shadow-sm dark:shadow-2xl/20 relative overflow-hidden backdrop-blur-xl transition-all duration-300">
      <div class="absolute -right-16 -top-16 w-36 h-36 rounded-full bg-primary/5 blur-3xl"></div>
      <div class="absolute -left-16 -bottom-16 w-36 h-36 rounded-full bg-orange-500/5 blur-3xl"></div>

      <div class="space-y-4 relative z-10">
        <label for="search-input" class="block text-sm font-semibold text-zinc-700 dark:text-zinc-300">
          ¿A quién estás buscando hoy?
        </label>
        
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-zinc-400 dark:text-zinc-500">
            <Search class="w-5 h-5" />
          </div>
          <input
            id="search-input"
            v-model="query"
            type="text"
            placeholder="Escribe un nombre, puesto laboral (ej. Frontend), palabra clave o ciudad..."
            class="block w-full pl-12 pr-10 py-3.5 bg-zinc-50 dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-xl text-zinc-900 dark:text-white placeholder-zinc-400 dark:placeholder-zinc-600 focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200 text-base"
            @input="onSearchInput"
            @keydown.enter="fetchCandidates"
          />
          <button
            v-if="query"
            @click="clearSearch"
            class="absolute inset-y-0 right-0 pr-4 flex items-center text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 transition-colors"
          >
            ×
          </button>
        </div>

        <!-- Quick Filters -->
        <div class="flex flex-wrap items-center gap-2.5 pt-2">
          <span class="text-xs font-semibold uppercase tracking-wider text-zinc-400 dark:text-zinc-500 mr-1.5">
            Búsquedas rápidas:
          </span>
          <button
            v-for="filter in quickFilters"
            :key="filter.label"
            @click="selectFilter(filter.value)"
            class="px-3.5 py-1.5 text-xs font-medium rounded-full border transition-all duration-200"
            :class="[
              query === filter.value
                ? 'bg-primary text-white border-primary shadow-sm shadow-primary/25'
                : 'bg-zinc-50 dark:bg-zinc-800/40 text-zinc-600 dark:text-zinc-300 border-zinc-200 dark:border-zinc-800 hover:bg-zinc-100 dark:hover:bg-zinc-800'
            ]"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- Results Section -->
    <div class="space-y-4">
      <div v-if="searchPerformed" class="flex items-center justify-between px-1">
        <h2 class="text-lg font-bold text-zinc-800 dark:text-zinc-200">
          Resultados de búsqueda
          <span v-if="!loading" class="text-sm font-normal text-zinc-500 dark:text-zinc-500 ml-2">
            ({{ candidates.length }} {{ candidates.length === 1 ? 'candidato encontrado' : 'candidatos encontrados' }})
          </span>
        </h2>
      </div>

      <!-- Loading skeleton -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="i in 3" 
          :key="i"
          class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 space-y-4 animate-pulse"
        >
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-full bg-zinc-200 dark:bg-zinc-800"></div>
            <div class="space-y-2 flex-1">
              <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-2/3"></div>
              <div class="h-3 bg-zinc-200 dark:bg-zinc-800 rounded w-1/2"></div>
            </div>
          </div>
          <div class="h-4 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3"></div>
          <div class="h-10 bg-zinc-200 dark:bg-zinc-800 rounded-xl w-full"></div>
        </div>
      </div>

      <!-- Error view -->
      <div v-else-if="error" class="bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900/50 rounded-xl p-4 text-center">
        <p class="text-sm font-medium text-red-800 dark:text-red-300">{{ error }}</p>
      </div>

      <!-- Empty/Welcome state -->
      <div 
        v-else-if="!searchPerformed" 
        class="bg-zinc-50 dark:bg-zinc-900/20 border-2 border-dashed border-zinc-200 dark:border-zinc-800/80 rounded-2xl p-12 text-center flex flex-col items-center justify-center space-y-4"
      >
        <div class="w-14 h-14 rounded-2xl bg-primary/10 dark:bg-primary/5 flex items-center justify-center text-primary">
          <Users class="w-7 h-7" />
        </div>
        <div class="max-w-sm space-y-2">
          <h3 class="text-base font-bold text-zinc-900 dark:text-white">Empieza a explorar</h3>
          <p class="text-sm text-zinc-500 dark:text-zinc-400">
            Introduce términos de búsqueda arriba o selecciona una de las etiquetas de acceso rápido para explorar el talento disponible en la plataforma.
          </p>
        </div>
      </div>

      <!-- No candidates matching -->
      <div 
        v-else-if="candidates.length === 0" 
        class="bg-zinc-50 dark:bg-zinc-900/20 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-12 text-center flex flex-col items-center justify-center space-y-4"
      >
        <div class="w-14 h-14 rounded-2xl bg-zinc-100 dark:bg-zinc-800/50 flex items-center justify-center text-zinc-400">
          <Search class="w-7 h-7" />
        </div>
        <div class="max-w-sm space-y-2">
          <h3 class="text-base font-bold text-zinc-900 dark:text-white">Sin resultados</h3>
          <p class="text-sm text-zinc-500 dark:text-zinc-400">
            No pudimos encontrar ningún candidato que coincida con "{{ query }}". Prueba a simplificar el término de búsqueda.
          </p>
        </div>
      </div>

      <!-- Candidates Grid -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="candidate in candidates"
          :key="candidate.userId"
          class="group bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 shadow-sm hover:shadow-xl dark:hover:shadow-primary/5 hover:-translate-y-1 transition-all duration-300 flex flex-col justify-between"
        >
          <div class="space-y-4">
            <!-- Profile Info Header -->
            <div class="flex items-start gap-4">
              <!-- Avatar -->
              <div class="w-14 h-14 rounded-full bg-gradient-to-tr from-primary/10 to-orange-500/10 dark:from-primary/20 dark:to-orange-500/20 border border-primary/20 flex items-center justify-center font-bold text-lg text-primary flex-shrink-0 shadow-inner overflow-hidden">
                <img v-if="candidate.photoUrl" :src="candidate.photoUrl" alt="Avatar" class="w-full h-full object-cover" />
                <span v-else>{{ candidate.name?.charAt(0) }}{{ candidate.surname?.charAt(0) }}</span>
              </div>

              <!-- Text Info -->
              <div class="space-y-1 min-w-0">
                <h3 class="font-bold text-zinc-900 dark:text-white text-base truncate group-hover:text-primary transition-colors duration-200">
                  {{ candidate.name }} {{ candidate.surname }}
                </h3>
                
                <div class="flex items-center gap-1.5 text-xs font-semibold text-primary">
                  <Briefcase class="w-3.5 h-3.5 flex-shrink-0" />
                  <span class="truncate">{{ candidate.jobTitle || 'Profesional' }}</span>
                </div>

                <div v-if="candidate.city" class="flex items-center gap-1.5 text-xs text-zinc-500 dark:text-zinc-400">
                  <MapPin class="w-3.5 h-3.5 flex-shrink-0 text-zinc-400" />
                  <span class="truncate">{{ candidate.city }}</span>
                </div>
              </div>
            </div>

            <!-- Subtle verification check -->
            <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-green-50 dark:bg-green-950/20 border border-green-200/40 dark:border-green-900/30 text-xs font-medium text-green-700 dark:text-green-400">
              <UserCheck class="w-4 h-4 text-green-500 flex-shrink-0" />
              <span>Referencias Verificadas por MiCaché</span>
            </div>
          </div>

          <!-- Action Button -->
          <div class="mt-6 pt-4 border-t border-zinc-100 dark:border-zinc-800/80">
            <RouterLink
              :to="`/u/${candidate.userId}`"
              class="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-zinc-50 dark:bg-zinc-800 text-sm font-semibold text-zinc-700 dark:text-zinc-200 hover:bg-primary hover:text-white dark:hover:bg-primary dark:hover:text-white border border-zinc-200 dark:border-zinc-700 hover:border-primary dark:hover:border-primary transition-all duration-200 group-hover:shadow-md"
            >
              Ver Perfil Completo
              <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
