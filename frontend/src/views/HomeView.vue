<script setup>
import { RouterLink } from 'vue-router';
import { ShieldCheck, Target, Users, Search, Award, TrendingUp, Briefcase } from 'lucide-vue-next';

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
];

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
];
</script>

<template>
  <main class="pt-16 selection:bg-primary/30">
    
    <!-- ─── HERO SECTION ──────────────────────────────────────────────────── -->
    <section class="relative overflow-hidden pt-20 pb-32">
      <!-- Decoración de fondo -->
      <div class="absolute top-1/4 left-1/2 -translate-x-1/2 w-[800px] h-[800px] rounded-full bg-primary/5 blur-[120px] pointer-events-none"></div>
      <div class="absolute -top-32 right-0 w-[400px] h-[400px] rounded-full bg-secondary/10 blur-[100px] pointer-events-none"></div>

      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center">
        <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-primary/10 border border-primary/20 mb-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
          <span class="w-2 h-2 rounded-full bg-primary animate-pulse"></span>
          <span class="text-sm font-medium text-primary">La evolución del currículum</span>
        </div>

        <h1 class="text-5xl md:text-7xl font-bold font-heading text-white mb-8 tracking-tight leading-tight animate-in fade-in slide-in-from-bottom-6 duration-700 delay-100">
          Tu reputación profesional,<br />
          <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-accent to-secondary">
            tu mayor activo.
          </span>
        </h1>

        <p class="text-lg md:text-xl text-[hsl(220,10%,65%)] max-w-2xl mx-auto mb-12 animate-in fade-in slide-in-from-bottom-8 duration-700 delay-200">
          Caché es la plataforma donde los profesionales construyen confianza. 
          Consigue referencias validadas de tu entorno laboral y demuestra lo que un CV no puede contar.
        </p>

        <div class="flex flex-col sm:flex-row items-center justify-center gap-4 animate-in fade-in slide-in-from-bottom-10 duration-700 delay-300">
          <RouterLink
            to="/register"
            class="w-full sm:w-auto px-8 py-4 rounded-xl font-bold text-white bg-primary hover:bg-primary/90 transition-all duration-300
                   hover:shadow-[0_0_30px_rgba(242,151,39,0.3)] hover:-translate-y-1"
          >
            Empieza a construir tu Caché
          </RouterLink>
          <a
            href="#como-funciona"
            class="w-full sm:w-auto px-8 py-4 rounded-xl font-semibold text-white bg-white/5 border border-white/10 hover:bg-white/10 transition-all duration-300"
          >
            Descubre cómo funciona
          </a>
        </div>
      </div>
    </section>

    <!-- ─── FEATURES SECTION ────────────────────────────────────────────── -->
    <section id="caracteristicas" class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="text-center max-w-3xl mx-auto mb-20">
          <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">Más que recomendaciones en LinkedIn</h2>
          <p class="text-[hsl(220,10%,60%)] text-lg">
            A diferencia de los endorsements genéricos, Caché recopila feedback estructurado y accionable sobre tus verdaderas competencias.
          </p>
        </div>

        <div class="grid md:grid-cols-3 gap-8">
          <div v-for="feat in features" :key="feat.title" class="glass-dark p-8 rounded-2xl border border-white/5 hover:border-primary/30 transition-colors duration-300 group">
            <div class="w-14 h-14 rounded-xl bg-primary/10 flex items-center justify-center text-primary mb-6 group-hover:scale-110 transition-transform duration-300">
              <component :is="feat.icon" class="w-7 h-7" />
            </div>
            <h3 class="text-xl font-bold text-white mb-4">{{ feat.title }}</h3>
            <p class="text-[hsl(220,10%,60%)] leading-relaxed">
              {{ feat.description }}
            </p>
          </div>
        </div>

      </div>
    </section>

    <!-- ─── HOW IT WORKS SECTION ────────────────────────────────────────── -->
    <section id="como-funciona" class="py-32 relative">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div class="flex flex-col md:flex-row gap-16 items-center">
          <!-- Text -->
          <div class="w-full md:w-1/2 space-y-8">
            <h2 class="text-4xl md:text-5xl font-bold text-white leading-tight">
              Diseñado para <br/>
              <span class="text-primary">impulsar tu carrera</span>
            </h2>
            <p class="text-lg text-[hsl(220,10%,60%)]">
              El proceso es simple, transparente y está diseñado para extraer el máximo valor de tu red profesional sin incomodar a tus contactos.
            </p>

            <div class="space-y-6 pt-4">
              <div v-for="step in steps" :key="step.num" class="flex gap-4">
                <div class="w-12 h-12 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-primary font-bold font-heading shrink-0">
                  {{ step.num }}
                </div>
                <div>
                  <h4 class="text-xl font-bold text-white mb-2">{{ step.title }}</h4>
                  <p class="text-[hsl(220,10%,60%)]">{{ step.description }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Mockup visual -->
          <div class="w-full md:w-1/2 relative">
            <div class="aspect-square max-w-md mx-auto relative">
              <div class="absolute inset-0 bg-gradient-to-tr from-primary/20 to-secondary/20 rounded-full blur-3xl"></div>
              <div class="relative h-full w-full glass-dark rounded-3xl border border-white/10 shadow-2xl p-6 flex flex-col">
                <!-- Mockup de UI interna simple -->
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

    <!-- ─── AUDIENCE SECTION ────────────────────────────────────────────── -->
    <section id="para-quien" class="py-24 bg-[hsl(228,15%,9%)] border-y border-white/5">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-3xl md:text-4xl font-bold text-white mb-16">¿A quién ayuda Caché?</h2>
        
        <div class="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
          <div class="p-8 rounded-2xl bg-[hsl(228,16%,7%)] border border-white/5 text-left flex flex-col">
            <Users class="w-10 h-10 text-primary mb-6" />
            <h3 class="text-2xl font-bold text-white mb-4">Profesionales</h3>
            <p class="text-[hsl(220,10%,60%)] mb-6 flex-1">
              Valida tu experiencia real, destaca en entrevistas mostrando referencias previas y construye una marca personal sólida e independiente del CV tradicional.
            </p>
            <ul class="space-y-3 text-sm text-[hsl(220,14%,80%)]">
              <li class="flex gap-2">✓<span>Aumenta tu poder de negociación</span></li>
              <li class="flex gap-2">✓<span>Destaca soft skills comprobadas</span></li>
            </ul>
          </div>

          <div class="p-8 rounded-2xl bg-[hsl(228,16%,7%)] border border-white/5 text-left flex flex-col">
            <Search class="w-10 h-10 text-secondary mb-6" />
            <h3 class="text-2xl font-bold text-white mb-4">Reclutadores & Empresas</h3>
            <p class="text-[hsl(220,10%,60%)] mb-6 flex-1">
              Reduce la fricción en la contratación, verifica referencias de forma estandarizada y asegúrate del encaje cultural antes de contratar.
            </p>
            <ul class="space-y-3 text-sm text-[hsl(220,14%,80%)]">
              <li class="flex gap-2">✓<span>Acorta el ciclo de contratación</span></li>
              <li class="flex gap-2">✓<span>Filtro de calidad transparente</span></li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- ─── BOTTOM CTA ──────────────────────────────────────────────────── -->
    <section class="py-32 relative overflow-hidden">
      <div class="absolute inset-0 bg-primary/5"></div>
      <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-full max-w-4xl h-1/2 bg-gradient-to-t from-primary/20 to-transparent blur-3xl"></div>
      
      <div class="max-w-4xl mx-auto px-4 text-center relative z-10">
        <h2 class="text-4xl md:text-5xl font-bold text-white mb-8">
          Tu próximo empleo valora tu reputación
        </h2>
        <p class="text-xl text-[hsl(220,10%,65%)] mb-12">
          No dejes que tu talento quede oculto tras un PDF. Únete a Caché hoy.
        </p>
        <RouterLink
          to="/register"
          class="inline-block px-10 py-5 rounded-xl font-bold text-white bg-primary hover:bg-primary/90 transition-all duration-300 text-lg
                 hover:shadow-[0_0_40px_rgba(242,151,39,0.4)] hover:-translate-y-1"
        >
          Crear mi perfil gratis
        </RouterLink>
      </div>
    </section>

  </main>
</template>

<style scoped>
/* Las animaciones de entrada están controladas por tailwindcss-animate (animate-in) */
</style>
