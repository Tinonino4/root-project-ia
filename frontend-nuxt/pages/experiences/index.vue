<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useExperienceStore } from '~/stores/experience.store'
import { Button } from '~/components/ui/button'
import { Plus, Building2, Calendar, Briefcase, Trash2, Edit2, AlertTriangle } from 'lucide-vue-next'

definePageMeta({
  layout: 'default'
})

const experienceStore = useExperienceStore()

const showDeleteModal = ref(false)
const experienceToDelete = ref<any>(null)

onMounted(async () => {
  if (import.meta.client) {
    await experienceStore.fetchExperiences()
  }
})

const confirmDelete = (id: any) => {
  experienceToDelete.value = id
  showDeleteModal.value = true
}

const executeDelete = async () => {
  if (experienceToDelete.value) {
    await experienceStore.deleteExperience(experienceToDelete.value)
    showDeleteModal.value = false
    experienceToDelete.value = null
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  experienceToDelete.value = null
}

const formatDate = (dateString?: string | null) => {
  if (!dateString) return 'Presente'
  const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'short' }
  return new Date(dateString).toLocaleDateString('es-ES', options)
}
</script>

<template>
  <div class="space-y-6 max-w-5xl mx-auto pb-12">
    
    <div class="flex items-center justify-between gap-3 w-full">
      <div>
        <h2 class="text-2xl sm:text-3xl font-bold font-heading text-zinc-900 dark:text-white">{{ $t('experience.title') }}</h2>
        <p class="text-zinc-500 dark:text-zinc-400 mt-1 hidden sm:block">{{ $t('experience.subtitle') }}</p>
      </div>
      <Button 
        @click="navigateTo('/experiences/new')" 
        class="bg-gradient-to-r from-primary to-orange-500 hover:scale-[1.02] active:scale-[0.98] transition-all text-white font-semibold rounded-xl px-4 py-2 flex items-center gap-1.5 shadow-lg shadow-primary/20 flex-shrink-0"
      >
        <Plus class="w-4.5 h-4.5" />
        <span class="text-xs sm:text-sm">{{ $t('experience.add') }}</span>
      </Button>
    </div>

    <div v-if="experienceStore.loading && experienceStore.experiences.length === 0" class="space-y-4">
      <div 
        v-for="i in 3" 
        :key="i"
        class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800/80 rounded-2xl p-6 space-y-4 animate-pulse"
      >
        <div class="flex items-start gap-4">
          <div class="w-12 h-12 rounded-xl bg-zinc-200 dark:bg-zinc-800 flex-shrink-0"></div>
          <div class="space-y-2 flex-1">
            <div class="h-5 bg-zinc-200 dark:bg-zinc-800 rounded w-1/3"></div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="experienceStore.error" class="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-4 rounded-lg flex items-center gap-3">
      <p>{{ experienceStore.error }}</p>
      <Button variant="outline" @click="experienceStore.fetchExperiences()" class="ml-auto">Retry</Button>
    </div>

    <div v-else-if="experienceStore.experiences.length === 0" class="bg-white dark:bg-zinc-900 rounded-2xl p-12 border border-dashed border-zinc-300 dark:border-zinc-800 flex flex-col items-center justify-center text-center">
      <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center text-primary mb-4">
        <Briefcase class="w-8 h-8" />
      </div>
      <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-2">{{ $t('experience.noExperiences') }}</h3>
      <Button @click="navigateTo('/experiences/new')" class="bg-primary hover:bg-primary/90 text-white gap-2">
        <Plus class="w-5 h-5" />
        {{ $t('experience.add') }}
      </Button>
    </div>

    <div v-else class="space-y-4">
      <div 
        v-for="exp in experienceStore.sortedByDate" 
        :key="exp.id"
        class="bg-white dark:bg-zinc-900 rounded-2xl p-4 sm:p-6 border border-zinc-200 dark:border-zinc-800 shadow-sm transition-all hover:shadow-md relative group"
      >
        <div class="absolute right-4 top-4 flex items-center gap-1.5 opacity-80 group-hover:opacity-100 transition-opacity">
          <button 
            @click="navigateTo(`/experiences/${exp.id}/edit`)"
            class="p-2 text-zinc-400 hover:text-zinc-900 dark:hover:text-white hover:bg-zinc-100 dark:hover:bg-white/5 rounded-xl transition-all border border-transparent hover:border-zinc-200 dark:hover:border-white/5"
            :title="$t('experience.edit')"
          >
            <Edit2 class="w-4 h-4" />
          </button>
          <button 
            @click="confirmDelete(exp.id)"
            class="p-2 text-zinc-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950/20 rounded-xl transition-all border border-transparent hover:border-red-100 dark:hover:border-red-900/10"
            :title="$t('experience.delete')"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>

        <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
          <div class="flex items-start gap-4 flex-1">
            <div class="w-12 h-12 rounded-xl bg-zinc-100 dark:bg-zinc-800 flex items-center justify-center flex-shrink-0 text-zinc-500 hidden sm:flex">
              <Building2 class="w-6 h-6" />
            </div>
            
            <div class="space-y-1">
              <h3 class="text-xl font-bold font-heading text-zinc-900 dark:text-white pr-16 sm:pr-2">{{ exp.position }}</h3>
              <div class="flex flex-wrap items-center gap-x-4 gap-y-2 text-sm text-zinc-600 dark:text-zinc-400">
                <span class="font-medium text-primary">{{ exp.companyName }}</span>
                <span v-if="exp.department" class="flex items-center gap-1">
                  <span class="w-1 h-1 rounded-full bg-zinc-300 dark:bg-zinc-700"></span>
                  {{ exp.department }}
                </span>
                <span class="flex items-center gap-1">
                  <span class="w-1 h-1 rounded-full bg-zinc-300 dark:bg-zinc-700"></span>
                  <Calendar class="w-4 h-4" />
                  {{ formatDate(exp.startDate) }} - {{ formatDate(exp.finishDate) }}
                </span>
              </div>
              <p v-if="exp.functions" class="text-zinc-600 dark:text-zinc-400 mt-3 text-sm leading-relaxed whitespace-pre-wrap">
                {{ exp.functions }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showDeleteModal" class="fixed inset-0 z-[1000] flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm">
      <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-white/5 rounded-3xl max-w-sm w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-200 text-zinc-900 dark:text-zinc-100">
        <div class="flex items-center gap-3 text-red-500">
          <div class="p-2 bg-red-500/10 dark:bg-red-500/20 rounded-xl">
            <AlertTriangle class="w-6 h-6 text-red-500" />
          </div>
          <h3 class="text-lg font-bold text-zinc-900 dark:text-white">{{ $t('experience.confirmDelete') }}</h3>
        </div>
        <div class="flex justify-end gap-3 pt-2">
          <button 
            class="px-4 py-2 text-sm font-semibold rounded-xl bg-zinc-100 dark:bg-zinc-800 hover:bg-zinc-200 dark:hover:bg-zinc-700 transition-colors text-zinc-700 dark:text-zinc-300"
            @click="cancelDelete"
          >
            {{ $t('profileEdit.cancel') }}
          </button>
          <Button 
            variant="destructive"
            class="px-4 py-2 text-sm font-semibold rounded-xl bg-red-600 hover:bg-red-700 text-white shadow-lg shadow-red-500/20 border-0"
            @click="executeDelete"
          >
            {{ $t('experience.delete') }}
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>
