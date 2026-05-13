<script setup>
import { ref } from 'vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import { 
  LayoutDashboard, 
  User, 
  BriefcaseBusiness, 
  MessageSquareQuote,
  Menu,
  X,
  LogOut
} from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import logoUrl from '@/assets/images/PNGs/Logo e Iconos/Caché-02.png';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const isMobileMenuOpen = ref(false);

const navigation = [
  { name: 'Dashboard', routeName: 'Dashboard', icon: LayoutDashboard },
  { name: 'Mi Perfil', routeName: 'Profile', icon: User },
  { name: 'Mis Experiencias', routeName: 'ExperienceList', icon: BriefcaseBusiness },
  { name: 'Solicitudes de Feedback', routeName: 'FeedbackList', icon: MessageSquareQuote },
];

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const handleLogout = () => {
  authStore.logout();
  router.push({ name: 'Login' });
};
</script>

<template>
  <div class="min-h-screen bg-zinc-50 dark:bg-zinc-950 flex flex-col md:flex-row">
    
    <!-- Mobile Header (Navbar) -->
    <header class="md:hidden flex items-center justify-between p-4 bg-white dark:bg-zinc-900 border-b border-zinc-200 dark:border-zinc-800 sticky top-0 z-30">
      <div class="flex items-center gap-2">
        <img :src="logoUrl" alt="Caché Logo" class="h-8 w-auto object-contain" />
      </div>
      <button @click="toggleMobileMenu" class="text-zinc-600 dark:text-zinc-300 hover:text-primary transition-colors focus:outline-none">
        <Menu v-if="!isMobileMenuOpen" class="w-6 h-6" />
        <X v-else class="w-6 h-6" />
      </button>
    </header>

    <!-- Sidebar (Desktop & Mobile Overlay) -->
    <aside 
      :class="[
        'fixed inset-y-0 left-0 z-40 w-64 bg-white dark:bg-zinc-900 border-r border-zinc-200 dark:border-zinc-800 transform transition-transform duration-300 ease-in-out md:relative md:translate-x-0',
        isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <div class="flex flex-col h-full">
        <!-- Sidebar Logo -->
        <div class="hidden md:flex items-center gap-2 p-6 border-b border-zinc-100 dark:border-zinc-800">
          <img :src="logoUrl" alt="Caché Logo" class="h-8 w-auto object-contain" />
        </div>

        <!-- Navigation Links -->
        <nav class="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
          <RouterLink 
            v-for="item in navigation" 
            :key="item.name" 
            :to="{ name: item.routeName }"
            @click="isMobileMenuOpen = false"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors font-medium text-sm group"
            :class="[
              route.name === item.routeName || (route.name?.startsWith('Profile') && item.routeName === 'Profile')
                ? 'bg-primary/10 text-primary' 
                : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800/50 hover:text-zinc-900 dark:hover:text-zinc-100'
            ]"
          >
            <component :is="item.icon" class="w-5 h-5 transition-colors" 
              :class="[
                route.name === item.routeName || (route.name?.startsWith('Profile') && item.routeName === 'Profile')
                  ? 'text-primary' 
                  : 'text-zinc-400 group-hover:text-zinc-500 dark:group-hover:text-zinc-300'
              ]" 
            />
            {{ item.name }}
          </RouterLink>
        </nav>

        <!-- Sidebar Footer / User -->
        <div class="p-4 border-t border-zinc-100 dark:border-zinc-800">
          <div class="flex items-center gap-3 px-3 py-3 rounded-lg bg-zinc-50 dark:bg-zinc-900/50">
            <div class="w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center flex-shrink-0 text-primary font-bold">
              {{ authStore.user?.name?.charAt(0)?.toUpperCase() || 'U' }}
            </div>
            <div class="flex-1 min-w-0 overflow-hidden">
              <p class="text-sm font-medium text-zinc-900 dark:text-white truncate">{{ authStore.user?.name || 'Usuario' }}</p>
              <p class="text-xs text-zinc-500 dark:text-zinc-400 truncate">{{ authStore.user?.role || 'Professional' }}</p>
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
    <main class="flex-1 flex flex-col min-h-0 overflow-hidden">
      <!-- Desktop Header -->
      <header class="hidden md:flex items-center justify-between p-6 bg-transparent">
        <h1 class="text-2xl font-bold font-heading text-zinc-900 dark:text-white capitalize">
          {{ route.meta?.title || route.name }}
        </h1>
        <div class="flex items-center gap-4">
          <!-- Additional top nav items can go here -->
        </div>
      </header>
      
      <!-- Mobile Title (optional if you want it visible on mobile below navbar) -->
      <div class="md:hidden px-4 pt-6 pb-2">
        <h1 class="text-2xl font-bold font-heading text-zinc-900 dark:text-white capitalize">
          {{ route.meta?.title || route.name }}
        </h1>
      </div>

      <div class="flex-1 overflow-y-auto p-4 md:p-6 lg:p-8">
        <div class="mx-auto max-w-6xl animate-in fade-in slide-in-from-bottom-4 duration-500">
          <router-view />
        </div>
      </div>
    </main>

  </div>
</template>
