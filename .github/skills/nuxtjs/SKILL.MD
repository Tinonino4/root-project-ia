# 🚀 Skill: Migración de Vue 3 a NuxtJS (v3/v4)

## 📌 Contexto y Rol
Actúas como un **Ingeniero Principal Frontend / Full-Stack** especializado en Vue 3 y Nuxt. Tu objetivo es migrar o reescribir aplicaciones desarrolladas en Vue 3 puro (Vite/Webpack, Vue Router, Pinia/Vuex) hacia la arquitectura moderna de **Nuxt (v3/v4)**.

Debes aplicar patrones de renderizado óptimos, garantizar compatibilidad con **Server-Side Rendering (SSR)**, aprovechar las capacidades del motor **Nitro** y eliminar código repetitivo mediante auto-importaciones y composables integrados.

---

## 🏗️ 1. Mapeo de Arquitectura y Estructura

Al transformar una estructura de Vue 3 a Nuxt, aplica el siguiente mapeo de directorios:

| Concepto / Recurso | Vue 3 (Standalone) | Nuxt (v3 / v4) |
| :--- | :--- | :--- |
| **Enrutamiento** | `src/router/index.ts` con `vue-router` | Basado en archivos en `app/pages/` (o `pages/`) |
| **Punto de Entrada** | `src/main.ts` | `app.vue` (o `app/app.vue` en v4) |
| **Layouts** | Componentes contenedores manuales | Directorio `app/layouts/` + `<NuxtLayout>` |
| **Navegación** | `<router-link>` | `<NuxtLink>` (optimizado con prefetching) |
| **Estado Global** | `createPinia()` manual en `main.ts` | Módulo `@pinia/nuxt` o composables `useState`/`useCookie` |
| **Endpoints / APIs** | Servidor Express externo / Axios directo | Motor Nitro en `server/api/` y `server/middleware/` |
| **SEO / Metadatos** | `@vueuse/head` / Configuración manual | Unhead integrado: `useHead()`, `useSeoMeta()` |

---

## ⚡ 2. Principios de Migración y Reglas de Código

### 2.1. Sustitución de Invocaciones de Red (Data Fetching)
* **Regla:** Reemplaza `axios` o `fetch()` dentro de hooks de montaje (`onMounted`) por composables nativos de Nuxt para evitar peticiones duplicadas en cliente y servidor.
* **Composables Recomendados:**
  * `useFetch(url)`: Opción preferida para peticiones reactivas basadas en URLs.
  * `useAsyncData(key, fn)`: Para envolver llamadas complejas o ejecutar múltiples promesas.
  * `$fetch`: Exclusivo para acciones desencadenadas por eventos del usuario (clicks, submit de formularios).

### 2.2. Prevención de Hydration Mismatches (SSR vs CSR)
* **Regla:** Ningún objeto exclusivo del navegador (`window`, `document`, `localStorage`, `navigator`) debe ser invocado en el cuerpo principal del `<script setup>`.
* **Soluciones:**
  1. Envolver la lógica dependiente del navegador en el hook `onMounted()`.
  2. Validar el contexto mediante `import.meta.client` (o `process.client`).
  3. Envolver componentes exclusivamente cliente en la etiqueta `<ClientOnly>`.

### 2.3. Gestión de Estado Global
* **Pinia:** Utiliza `@pinia/nuxt` para habilitar la hidratación automática del estado sin modificar `main.ts`.
* **Estados Ligeros:** Reemplaza variables volátiles compartidas por `useState('key', () => defaultValue)`.
* **Persistencia SSR-Friendly:** Para datos persistentes como tokens o preferencias, usa `useCookie('token_name')` en lugar de `localStorage`.

---

## 🛠️ 3. Backend Integrado y Motor Nitro (`server/`)

Aprovecha el servidor Nitro para migrar controladores, proxies de APIs o lógica de Node.js a endpoints internos.

### 3.1. Creación de Endpoints Integrados
Crea archivos dentro de `server/api/` para exponer rutas automáticas:

```typescript
// server/api/users.ts
export default defineEventHandler(async (event) => {
  // Lógica de servidor protegida
  return { status: 'success', data: [] }
})
```

---

## 🔒 4. Seguridad y Variables de Entorno

**Variables Públicas vs Privadas:** Configura tus variables en `nuxt.config.ts` mediante `runtimeConfig`:
* Propiedades raíz: Solo accesibles en el servidor (Nitro).
* Propiedades en `public`: Accesibles tanto en cliente como en servidor.

```typescript
// nuxt.config.ts
export default defineNuxtConfig({
  runtimeConfig: {
    apiSecret: '', // Privado (solo servidor)
    public: {
      apiBase: '' // Público (cliente y servidor)
    }
  }
})
```

**Cookies Seguras:** Define cookies con opciones de seguridad explícitas:

```typescript
const token = useCookie('auth_token', {
  httpOnly: true,
  secure: true,
  sameSite: 'lax'
})
```

---

## 🚀 5. Rendimiento y Optimizaciones Avanzadas

**Hybrid Rendering (routeRules):** Combina estrategias de renderizado por ruta según las necesidades de la aplicación:

```typescript
// nuxt.config.ts
export default defineNuxtConfig({
  routeRules: {
    '/': { prerender: true },            // SSG / Estático
    '/products/**': { swr: 3600 },        // Stale-While-Revalidate
    '/admin/**': { ssr: false },          // SPA puro (CSR)
    '/api/**': { cors: true }             // Configuración de API
  }
})
```

* **Optimización de Imágenes y Recursos:** Utiliza el componente `<NuxtImg>` o `<NuxtPicture>` mediante `@nuxt/image` para la conversión automática a formatos modernos (WebP/AVIF) y la carga diferida (`loading="lazy"`).
* **Auto-Imports:** Elimina las importaciones explícitas de composables (`ref`, `computed`, `onMounted`) y componentes dentro del directorio `components/`. Nuxt los gestiona bajo demanda con optimización de tree-shaking.

---

## 📋 6. Checklist Paso a Paso durante la Migración
Al convertir un componente o módulo de Vue 3 a Nuxt, sigue esta secuencia:
- [ ] **Paso 1:** Mueve la vista/componente al directorio correspondiente (`app/pages/` o `app/components/`).
- [ ] **Paso 2:** Reemplaza rutas manuales de `vue-router` por la estructura de archivos en `pages/`.
- [ ] **Paso 3:** Elimina las importaciones explícitas de composables de Vue (`ref`, `reactive`, `computed`).
- [ ] **Paso 4:** Sustituye llamadas directas de Axios/Fetch en hooks de montaje por `useFetch` o `useAsyncData`.
- [ ] **Paso 5:** Revisa interacciones con `window` o `document` e impleméntalas dentro de `onMounted()` o mediante `<ClientOnly>`.
- [ ] **Paso 6:** Sustituye etiquetas `<router-link>` por `<NuxtLink>`.
- [ ] **Paso 7:** Refactoriza metadatos e inyecciones de `<head>` mediante `useSeoMeta()`.