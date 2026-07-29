<script setup lang="ts">
import { ref, computed } from 'vue'
import Logo from '~/components/ui/Logo.vue'
import LanguageSwitcher from '~/components/ui/LanguageSwitcher.vue'
import {
  LayoutDashboard,
  User,
  BriefcaseBusiness,
  MessageSquareQuote,
  Search,
  Menu,
  X,
  LogOut
} from 'lucide-vue-next'
import { useAuthStore } from '~/stores/auth.store'

const route = useRoute()
const authStore = useAuthStore()
const { t, locale, setLocale } = useI18n()
const isMobileMenuOpen = ref(false)

const currentLocale = computed(() => locale.value)

const changeLang = (lang: 'es' | 'en') => {
  setLocale(lang)
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const handleLogout = () => {
  authStore.logout()
  navigateTo('/login')
}

const displayRole = computed(() => {
  const role = authStore.user?.role
  if (role === 'ROLE_ADMIN') return t('sidebar.adminRole')
  if (role === 'ROLE_COMPANY') return t('sidebar.companyRole')
  return t('sidebar.professionalRole')
})

const navigation = computed(() => [
  { key: 'sidebar.dashboard', path: '/dashboard', icon: LayoutDashboard },
  { key: 'sidebar.recruiterSearch', path: '/recruiter/search', icon: Search },
  { key: 'sidebar.profile', path: '/profile', icon: User },
  { key: 'sidebar.experiences', path: '/experiences', icon: BriefcaseBusiness },
  { key: 'sidebar.feedbackRequests', path: '/feedback', icon: MessageSquareQuote },
])
</script>

<template>
  <a href="#main-content" class="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-white focus:rounded-lg focus:font-bold focus:shadow-lg focus:outline-none focus:ring-2 focus:ring-primary">
    {{ $t('navbar.skipToContent') }}
  </a>
  <div class="min-h-screen md:h-screen bg-zinc-50 dark:bg-zinc-950 flex flex-col md:flex-row md:overflow-hidden">
    
    <!-- Mobile Header -->
    <header class="md:hidden flex items-center justify-between p-4 bg-white dark:bg-zinc-900 border-b border-zinc-200 dark:border-zinc-800 sticky top-0 z-30">
      <div class="flex items-center gap-2">
        <NuxtLink to="/dashboard">
          <Logo variant="full" size="sm" interactive />
        </NuxtLink>
      </div>
      
      <div class="flex items-center gap-2">
        <LanguageSwitcher theme="light" variant="compact" />

        <button @click="toggleMobileMenu" class="text-zinc-600 dark:text-zinc-300 hover:text-primary transition-colors focus:outline-none">
          <Menu v-if="!isMobileMenuOpen" class="w-6 h-6" />
          <X v-else class="w-6 h-6" />
        </button>
      </div>
    </header>

    <!-- Sidebar -->
    <aside 
      :class="[
        'fixed inset-y-0 left-0 z-40 w-64 bg-white dark:bg-zinc-900 border-r border-zinc-200 dark:border-zinc-800 transform transition-transform duration-300 ease-in-out md:relative md:translate-x-0 md:h-full flex flex-col flex-shrink-0',
        isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <div class="flex flex-col h-full overflow-hidden">
        <div class="hidden md:flex items-center gap-2 p-6 border-b border-zinc-100 dark:border-zinc-800 flex-shrink-0">
          <NuxtLink to="/dashboard">
            <Logo variant="full" size="md" interactive />
          </NuxtLink>
        </div>

        <nav class="flex-1 px-4 py-6 space-y-1.5 overflow-y-auto">
          <NuxtLink 
            v-for="item in navigation" 
            :key="item.key" 
            :to="item.path"
            @click="isMobileMenuOpen = false"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-150 font-medium text-sm group relative"
            :class="[
              route.path === item.path || (route.path.startsWith('/profile') && item.path === '/profile')
                ? 'bg-primary/10 text-primary border-l-[3px] border-primary pl-[10px]' 
                : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800/50 hover:text-zinc-900 dark:hover:text-zinc-100 border-l-[3px] border-transparent pl-[10px]'
            ]"
          >
            <component :is="item.icon" class="w-5 h-5 transition-colors flex-shrink-0"
              :class="[
                route.path === item.path || (route.path.startsWith('/profile') && item.path === '/profile')
                  ? 'text-primary' 
                  : 'text-zinc-400 group-hover:text-zinc-500 dark:group-hover:text-zinc-300'
              ]" 
            />
            {{ $t(item.key) }}
          </NuxtLink>
        </nav>

        <div class="p-4 border-t border-zinc-100 dark:border-zinc-800 flex-shrink-0 bg-white dark:bg-zinc-900 flex flex-col gap-3">
          <div class="flex items-center justify-between px-3 py-2 rounded-lg bg-zinc-50 dark:bg-zinc-900/50 border border-zinc-100 dark:border-zinc-800/80">
            <span class="text-[11px] font-semibold text-zinc-500 dark:text-zinc-400">{{ $t('sidebar.language') }}</span>
            <LanguageSwitcher theme="light" variant="compact" />
          </div>

          <div class="flex items-center gap-3 px-3 py-2.5 rounded-xl bg-zinc-50 dark:bg-zinc-800/40 border border-zinc-100 dark:border-zinc-800/60 transition-all hover:border-zinc-200 dark:hover:border-zinc-700">
            <div class="relative flex-shrink-0">
              <div class="w-9 h-9 rounded-full bg-gradient-to-tr from-primary/30 to-amber-500/20 border border-primary/30 flex items-center justify-center text-primary font-extrabold text-sm shadow-sm">
                {{ authStore.user?.name?.charAt(0)?.toUpperCase() || 'U' }}
              </div>
              <span class="absolute bottom-0 right-0 w-2.5 h-2.5 rounded-full bg-emerald-500 ring-2 ring-white dark:ring-zinc-900" title="Verificado / En línea"></span>
            </div>
            <div class="flex-1 min-w-0 overflow-hidden">
              <p class="text-xs font-bold text-zinc-900 dark:text-white truncate leading-snug">{{ authStore.user?.name || $t('sidebar.userFallback') }}</p>
              <p class="text-[11px] font-medium text-zinc-500 dark:text-zinc-400 truncate">{{ displayRole }}</p>
            </div>
            <button
              @click="handleLogout"
              class="text-zinc-400 hover:text-red-500 hover:bg-red-500/10 transition-all p-1.5 rounded-lg"
              :title="$t('sidebar.logout')"
            >
              <LogOut class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </aside>

    <div 
      v-if="isMobileMenuOpen" 
      @click="toggleMobileMenu"
      class="fixed inset-0 z-30 bg-black/50 backdrop-blur-sm md:hidden"
    ></div>

    <!-- Main Content -->
    <main id="main-content" class="flex-1 flex flex-col min-h-0 overflow-hidden">
      <div class="flex-1 overflow-y-auto px-2 py-4 md:p-6 lg:p-8">
        <div class="mx-auto w-full max-w-7xl relative">
          <slot />
        </div>
      </div>
    </main>

  </div>
</template>
