<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '@/stores/auth.store';
import { 
  LayoutDashboard, 
  User, 
  BriefcaseBusiness, 
  MessageSquareQuote,
  Search,
  Menu,
  X,
  LogOut
} from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import logoUrl from '@/assets/images/logo-cache.png';
import { switchLanguage } from '@/i18n';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const { locale } = useI18n();
const isMobileMenuOpen = ref(false);

const currentLocale = computed(() => locale.value);

const changeLang = (lang) => {
  switchLanguage(lang);
};

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const handleLogout = () => {
  authStore.logout();
  router.push({ name: 'Login' });
};

// Map raw backend roles to friendly, secure titles
const displayRole = computed(() => {
  const role = authStore.user?.role;
  if (!role) return 'Profesional';
  if (role === 'ROLE_USER') return 'Profesional';
  if (role === 'ROLE_ADMIN') return 'Administrador';
  return 'Profesional';
});

const navigation = [
  { name: 'Dashboard', routeName: 'Dashboard', icon: LayoutDashboard },
  { name: 'Buscador de Talento', routeName: 'RecruiterSearch', icon: Search },
  { name: 'Mi Perfil', routeName: 'Profile', icon: User },
  { name: 'Mis Experiencias', routeName: 'ExperienceList', icon: BriefcaseBusiness },
  { name: 'Solicitudes de Feedback', routeName: 'FeedbackList', icon: MessageSquareQuote },
];
</script>

<template>
  <a href="#main-content" class="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-white focus:rounded-lg focus:font-bold focus:shadow-lg focus:outline-none focus:ring-2 focus:ring-primary">
    {{ $t('navbar.skipToContent') }}
  </a>
  <div class="min-h-screen md:h-screen bg-zinc-50 dark:bg-zinc-950 flex flex-col md:flex-row md:overflow-hidden">
    
    <!-- Mobile Header (Navbar) -->
    <header class="md:hidden flex items-center justify-between p-4 bg-white dark:bg-zinc-900 border-b border-zinc-200 dark:border-zinc-800 sticky top-0 z-30">
      <div class="flex items-center gap-2">
        <img :src="logoUrl" alt="Caché Logo" class="h-8 w-auto object-contain" />
      </div>
      
      <!-- Idioma Mini Móvil -->
      <div class="flex items-center gap-2">
        <div class="flex items-center gap-0.5 bg-zinc-100 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 rounded-lg p-0.5">
          <button
            @click="changeLang('es')"
            class="px-1.5 py-0.5 text-[10px] font-bold rounded"
            :class="currentLocale === 'es' ? 'bg-primary text-white' : 'text-zinc-400'"
          >
            ES
          </button>
          <button
            @click="changeLang('en')"
            class="px-1.5 py-0.5 text-[10px] font-bold rounded"
            :class="currentLocale === 'en' ? 'bg-primary text-white' : 'text-zinc-400'"
          >
            EN
          </button>
        </div>

        <button @click="toggleMobileMenu" class="text-zinc-600 dark:text-zinc-300 hover:text-primary transition-colors focus:outline-none">
          <Menu v-if="!isMobileMenuOpen" class="w-6 h-6" />
          <X v-else class="w-6 h-6" />
        </button>
      </div>
    </header>

    <!-- Sidebar (Desktop & Mobile Overlay) -->
    <aside 
      :class="[
        'fixed inset-y-0 left-0 z-40 w-64 bg-white dark:bg-zinc-900 border-r border-zinc-200 dark:border-zinc-800 transform transition-transform duration-300 ease-in-out md:relative md:translate-x-0 md:h-full flex flex-col flex-shrink-0',
        isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <div class="flex flex-col h-full overflow-hidden">
        <!-- Sidebar Logo -->
        <div class="hidden md:flex items-center gap-2 p-6 border-b border-zinc-100 dark:border-zinc-800 flex-shrink-0">
          <img :src="logoUrl" alt="Caché Logo" class="h-8 w-auto object-contain" />
        </div>

        <!-- Navigation Links -->
        <nav class="flex-1 px-4 py-6 space-y-1.5 overflow-y-auto">
          <RouterLink 
            v-for="item in navigation" 
            :key="item.name" 
            :to="{ name: item.routeName }"
            @click="isMobileMenuOpen = false"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-150 font-medium text-sm group relative"
            :class="[
              route.name === item.routeName || (route.name?.startsWith('Profile') && item.routeName === 'Profile')
                ? 'bg-primary/10 text-primary border-l-[3px] border-primary pl-[10px]' 
                : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800/50 hover:text-zinc-900 dark:hover:text-zinc-100 border-l-[3px] border-transparent pl-[10px]'
            ]"
          >
            <component :is="item.icon" class="w-5 h-5 transition-colors flex-shrink-0"
              :class="[
                route.name === item.routeName || (route.name?.startsWith('Profile') && item.routeName === 'Profile')
                  ? 'text-primary' 
                  : 'text-zinc-400 group-hover:text-zinc-500 dark:group-hover:text-zinc-300'
              ]" 
            />
            {{ item.name }}
          </RouterLink>
        </nav>

        <!-- Sidebar Footer / User (Fixed at the bottom, no scroll) -->
        <div class="p-4 border-t border-zinc-100 dark:border-zinc-800 flex-shrink-0 bg-white dark:bg-zinc-900 flex flex-col gap-3">
          <!-- Selector de Idioma Estilizado para Dashboard -->
          <div class="flex items-center justify-between px-3 py-1.5 rounded-lg bg-zinc-50 dark:bg-zinc-900/50 border border-zinc-100 dark:border-zinc-800">
            <span class="text-[11px] font-semibold text-zinc-500 dark:text-zinc-400">Idioma / Language</span>
            <div class="flex items-center gap-0.5 bg-white dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-md p-0.5">
              <button
                @click="changeLang('es')"
                class="px-2 py-0.5 text-[10px] font-bold rounded transition-all duration-200"
                :class="currentLocale === 'es' ? 'bg-primary text-white shadow-sm' : 'text-zinc-400 dark:text-zinc-500 hover:text-zinc-800 dark:hover:text-zinc-200'"
              >
                ES
              </button>
              <button
                @click="changeLang('en')"
                class="px-2 py-0.5 text-[10px] font-bold rounded transition-all duration-200"
                :class="currentLocale === 'en' ? 'bg-primary text-white shadow-sm' : 'text-zinc-400 dark:text-zinc-500 hover:text-zinc-800 dark:hover:text-zinc-200'"
              >
                EN
              </button>
            </div>
          </div>

          <div class="flex items-center gap-3 px-3 py-3 rounded-lg bg-zinc-50 dark:bg-zinc-900/50">
            <div class="w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center flex-shrink-0 text-primary font-bold">
              {{ authStore.user?.name?.charAt(0)?.toUpperCase() || 'U' }}
            </div>
            <div class="flex-1 min-w-0 overflow-hidden">
              <p class="text-sm font-semibold text-zinc-900 dark:text-white truncate">{{ authStore.user?.name || 'Usuario' }}</p>
              <p class="text-xs text-zinc-500 dark:text-zinc-400 truncate">{{ displayRole }}</p>
            </div>
            <button @click="handleLogout" class="text-zinc-400 hover:text-red-500 transition-colors p-1" title="Cerrar sesión">
              <LogOut class="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </aside>

    <!-- Mobile Overlay Backdrop -->
    <div 
      v-if="isMobileMenuOpen" 
      @click="toggleMobileMenu"
      class="fixed inset-0 z-30 bg-black/50 backdrop-blur-sm md:hidden"
    ></div>

    <!-- Main Content Area -->
    <main id="main-content" class="flex-1 flex flex-col min-h-0 overflow-hidden">
      <!-- Content scroll inside container, clean, no redundant header -->
      <div class="flex-1 overflow-y-auto p-4 md:p-6 lg:p-8">
        <div class="mx-auto w-full max-w-7xl relative">
          <router-view v-slot="{ Component, route: viewRoute }">
            <Transition name="route-fade" mode="out-in">
              <component :is="Component" :key="viewRoute.path" />
            </Transition>
          </router-view>
        </div>
      </div>
    </main>

  </div>
</template>
