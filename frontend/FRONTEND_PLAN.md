# Plan de Arquitectura Frontend — TinoIA (Vue 3)

> **Requisitos transversales:** Responsive (mobile-first) + Accesibilidad (WCAG 2.1 AA)

## 1. Resumen del Análisis del Backend

### 1.1 Módulos de dominio identificados (Clean Architecture)

| Módulo | Base Path | Auth | Descripción |
|---|---|---|---|
| **Auth** | `/api/auth` | Público | Registro, OTP, login, password reset, OAuth2 |
| **Professional** | `/api/v1/professional` | JWT | Perfil de usuario y experiencias laborales |
| **Feedback** | `/api/feedback` | JWT | Solicitudes de feedback (cache requests) |
| **Questionnaire** | `/api/questionnaire` | Público | Cuestionario de evaluación por token URL |
| **Analytics (Skills)** | `/api/skills` | JWT | Métricas agregadas de soft skills |

### 1.2 Endpoints completos

#### Auth (público)
| Método | Path | Request | Response | Descripción |
|---|---|---|---|---|
| POST | `/auth/register` | `RegisterRequest(name, email, password, role)` | `MessageResponse` | Registro + envío OTP |
| POST | `/auth/confirm` | `OtpVerificationRequest(email, code)` | `MessageResponse` | Confirmar cuenta por OTP (6 dígitos) |
| POST | `/auth/login` | `LoginRequest(email, password)` | `AuthResponse(token, id, name, role)` | Login → JWT |
| POST | `/auth/forgot-password` | `ForgotPasswordRequest(email)` | `MessageResponse` | Solicitar reset password |
| POST | `/auth/reset-password` | `ResetPasswordRequest(email, code, newPassword)` | `MessageResponse` | Reset con OTP |

#### Professional (autenticado)
| Método | Path | Request | Response | Descripción |
|---|---|---|---|---|
| GET | `/v1/professional/profile` | — | `UserProfile` | Obtener perfil |
| PUT | `/v1/professional/profile` | `UserProfileRequest` | `UserProfile` | Actualizar perfil |
| GET | `/v1/professional/experiences` | — | `Experience[]` | Listar experiencias |
| POST | `/v1/professional/experiences` | `ExperienceRequest` | `Experience` | Crear experiencia |
| PUT | `/v1/professional/experiences/:id` | `ExperienceRequest` | `Experience` | Actualizar experiencia |
| DELETE | `/v1/professional/experiences/:id` | — | `204` | Eliminar experiencia |

#### Feedback (autenticado)
| Método | Path | Request | Response | Descripción |
|---|---|---|---|---|
| GET | `/feedback/categories` | — | `SkillCategory[]` | Catálogo de categorías+preguntas |
| GET | `/feedback/relationships` | — | `RelationshipType[]` | Catálogo de relaciones |
| POST | `/feedback/requests` | `CreateCacheRequestDTO` | `CacheRequestViewDTO` | Crear solicitud |
| GET | `/feedback/requests` | — | `CacheRequestViewDTO[]` | Listar solicitudes del usuario |
| GET | `/feedback/requests/experience/:id` | — | `CacheRequestViewDTO[]` | Solicitudes por experiencia |
| GET | `/feedback/requests/experience/:id/count` | — | `number` | Feedbacks completados por experiencia |

#### Questionnaire (público)
| Método | Path | Request | Response | Descripción |
|---|---|---|---|---|
| GET | `/questionnaire/:urlToken` | — | `QuestionnaireViewDTO` | Obtener cuestionario |
| POST | `/questionnaire/:urlToken` | `SubmitQuestionnaireDTO` | `{message}` | Enviar respuestas |

#### Skills Metrics (autenticado)
| Método | Path | Request | Response | Descripción |
|---|---|---|---|---|
| GET | `/skills/metrics` | — | `UserSkillsMetrics \| 204` | Métricas de soft skills |

---

## 2. Estructura de Proyecto Propuesta

```
frontend/src/
├── api/                          # Capa de comunicación HTTP
│   ├── client.js                 # Instancia Axios + interceptors (request/response)
│   ├── auth.api.js               # Endpoints de autenticación
│   ├── professional.api.js       # Endpoints de perfil y experiencias
│   ├── feedback.api.js           # Endpoints de feedback requests
│   ├── questionnaire.api.js      # Endpoints del cuestionario público
│   └── skills.api.js             # Endpoints de métricas
│
├── composables/                  # Composables reutilizables (lógica compartida)
│   ├── useAuth.js                # Lógica de autenticación para componentes
│   ├── useFormValidation.js      # Validación genérica de formularios
│   ├── useNotification.js        # Toast/snackbar notifications
│   └── useLoading.js             # Estado de loading genérico
│
├── stores/                       # Pinia stores (estado global)
│   ├── auth.store.js             # Token, user, login/logout
│   ├── profile.store.js          # Perfil del usuario
│   ├── experience.store.js       # Experiencias laborales
│   ├── feedback.store.js         # Solicitudes de feedback + catálogos
│   └── skills.store.js           # Métricas de skills
│
├── router/
│   ├── index.js                  # Configuración del router
│   ├── guards.js                 # Navigation guards (auth, guest)
│   └── routes/
│       ├── auth.routes.js        # /login, /register, /confirm, /forgot, /reset
│       ├── dashboard.routes.js   # / (home/dashboard)
│       ├── profile.routes.js     # /profile, /profile/edit
│       ├── experience.routes.js  # /experiences, /experiences/:id
│       ├── feedback.routes.js    # /feedback, /feedback/new
│       └── questionnaire.routes.js # /q/:token (público)
│
├── layouts/                      # Layouts de página
│   ├── DefaultLayout.vue         # Navbar + sidebar + main content (autenticado)
│   ├── AuthLayout.vue            # Layout limpio para login/register
│   └── PublicLayout.vue          # Layout para cuestionario público
│
├── views/                        # Vistas de página (1 por ruta)
│   ├── auth/
│   │   ├── LoginView.vue
│   │   ├── RegisterView.vue
│   │   ├── ConfirmAccountView.vue
│   │   ├── ForgotPasswordView.vue
│   │   └── ResetPasswordView.vue
│   ├── dashboard/
│   │   └── DashboardView.vue     # Vista principal con resumen skills + experiencias
│   ├── profile/
│   │   ├── ProfileView.vue       # Vista de perfil (lectura)
│   │   └── ProfileEditView.vue   # Edición de perfil
│   ├── experience/
│   │   ├── ExperienceListView.vue
│   │   └── ExperienceFormView.vue  # Crear/Editar (mismo componente)
│   ├── feedback/
│   │   ├── FeedbackListView.vue    # Lista de solicitudes enviadas
│   │   └── FeedbackCreateView.vue  # Formulario para solicitar feedback
│   └── questionnaire/
│       └── QuestionnaireView.vue   # Cuestionario público (/q/:token)
│
├── components/                   # Componentes reutilizables
│   ├── ui/                       # Componentes shadcn-vue (copiados, customizables)
│   │   ├── button/               # Button.vue + variants
│   │   ├── input/                # Input.vue
│   │   ├── card/                 # Card.vue, CardHeader, CardContent, CardFooter
│   │   ├── dialog/               # Dialog (modal)
│   │   ├── badge/                # Badge.vue
│   │   ├── alert/                # Alert.vue
│   │   ├── form/                 # Form, FormField, FormItem (vee-validate)
│   │   ├── select/               # Select dropdown
│   │   ├── separator/            # Separator
│   │   ├── skeleton/             # Skeleton loader
│   │   ├── toast/                # Toast notifications (sonner)
│   │   ├── tooltip/              # Tooltip
│   │   └── avatar/               # Avatar.vue
│   ├── layout/
│   │   ├── AppNavbar.vue
│   │   ├── AppSidebar.vue
│   │   └── AppFooter.vue
│   ├── profile/
│   │   ├── ProfileCard.vue       # Tarjeta resumen del perfil
│   │   └── ProfileForm.vue       # Formulario reutilizable
│   ├── experience/
│   │   ├── ExperienceCard.vue    # Tarjeta de una experiencia
│   │   └── ExperienceForm.vue    # Formulario crear/editar
│   ├── feedback/
│   │   ├── FeedbackRequestCard.vue
│   │   └── FeedbackRequestForm.vue
│   ├── questionnaire/
│   │   ├── CategorySection.vue   # Bloque de categoría con preguntas
│   │   └── QuestionRating.vue    # Pregunta individual con rating
│   └── skills/
│       ├── SkillsRadarChart.vue  # Gráfico radar de 5 categorías
│       └── SkillsSummary.vue     # Resumen numérico de métricas
│
├── types/                        # JSDoc typedefs (o TypeScript si se migra)
│   └── models.js                 # Definiciones de tipos/interfaces
│
├── utils/                        # Utilidades puras
│   ├── constants.js              # Constantes de la app
│   ├── validators.js             # Funciones de validación
│   └── formatters.js             # Formateo de fechas, números, etc.
│
├── assets/
│   ├── styles/
│   │   ├── main.css              # Estilos globales
│   │   ├── variables.css         # Variables CSS / Design tokens
│   │   └── transitions.css       # Transiciones de Vue
│   └── images/
│
├── App.vue
└── main.js
```

---

## 3. Mapa de Rutas

| Ruta | Vista | Layout | Guard | Descripción |
|---|---|---|---|---|
| `/login` | `LoginView` | Auth | guest | Inicio de sesión |
| `/register` | `RegisterView` | Auth | guest | Registro |
| `/confirm` | `ConfirmAccountView` | Auth | guest | Confirmación OTP |
| `/forgot-password` | `ForgotPasswordView` | Auth | guest | Solicitar reset |
| `/reset-password` | `ResetPasswordView` | Auth | guest | Resetear contraseña |
| `/` | `DashboardView` | Default | auth | Dashboard principal |
| `/profile` | `ProfileView` | Default | auth | Ver perfil |
| `/profile/edit` | `ProfileEditView` | Default | auth | Editar perfil |
| `/experiences` | `ExperienceListView` | Default | auth | Lista experiencias |
| `/experiences/new` | `ExperienceFormView` | Default | auth | Nueva experiencia |
| `/experiences/:id/edit` | `ExperienceFormView` | Default | auth | Editar experiencia |
| `/feedback` | `FeedbackListView` | Default | auth | Lista de solicitudes |
| `/feedback/new` | `FeedbackCreateView` | Default | auth | Crear solicitud |
| `/q/:token` | `QuestionnaireView` | Public | — | Cuestionario público |

---

## 4. Stores (Pinia) — Responsabilidades

### `auth.store.js`
- **Estado:** `token`, `user { id, name, role }`, `isAuthenticated`
- **Acciones:** `login()`, `register()`, `confirmAccount()`, `forgotPassword()`, `resetPassword()`, `logout()`, `loadFromStorage()`
- **Getters:** `isAuthenticated`, `userName`, `userRole`

### `profile.store.js`
- **Estado:** `profile`, `loading`, `error`
- **Acciones:** `fetchProfile()`, `updateProfile(data)`

### `experience.store.js`
- **Estado:** `experiences[]`, `loading`, `error`
- **Acciones:** `fetchExperiences()`, `addExperience(data)`, `updateExperience(id, data)`, `deleteExperience(id)`
- **Getters:** `getExperienceById(id)`, `sortedByDate`

### `feedback.store.js`
- **Estado:** `requests[]`, `categories[]`, `relationships[]`, `loading`, `error`
- **Acciones:** `fetchCategories()`, `fetchRelationships()`, `fetchRequests()`, `fetchRequestsByExperience(id)`, `createRequest(data)`, `getCompletedCount(expId)`

### `skills.store.js`
- **Estado:** `metrics`, `hasMetrics`, `loading`, `error`
- **Acciones:** `fetchMetrics()`
- **Getters:** `categoryScores` (array para radar chart), `averageScore`

---

## 5. Capa API — Diseño

### `client.js` (refactor de `api.js`)
- Instancia Axios con `baseURL` desde variable de entorno `VITE_API_BASE_URL`
- **Request interceptor:** inyectar JWT desde auth store
- **Response interceptor:** manejar 401 → logout automático, extraer errores de validación

### Módulos API (un archivo por dominio)
Cada módulo exporta funciones puras que devuelven la Promise de Axios:

```js
// ejemplo: auth.api.js
export const authApi = {
  login: (data) => client.post('/auth/login', data),
  register: (data) => client.post('/auth/register', data),
  confirm: (data) => client.post('/auth/confirm', data),
  forgotPassword: (data) => client.post('/auth/forgot-password', data),
  resetPassword: (data) => client.post('/auth/reset-password', data),
}
```

---

## 6. Flujos de Usuario Clave

### 6.1 Registro → Confirmación → Login
```
RegisterView → POST /auth/register → ConfirmAccountView (OTP 6 dígitos)
  → POST /auth/confirm → LoginView → POST /auth/login → Dashboard
```

### 6.2 Recuperar contraseña
```
LoginView → "¿Olvidaste tu contraseña?" → ForgotPasswordView
  → POST /auth/forgot-password → ResetPasswordView (OTP + nueva contraseña)
  → POST /auth/reset-password → LoginView
```

### 6.3 Gestión de perfil profesional
```
Dashboard → ProfileView (GET /professional/profile)
  → ProfileEditView → PUT /professional/profile → ProfileView
```

### 6.4 Gestión de experiencias
```
ExperienceListView (GET /professional/experiences)
  → ExperienceFormView (POST o PUT) → ExperienceListView
  → DELETE → Confirmación modal → ExperienceListView
```

### 6.5 Solicitar feedback
```
FeedbackListView (GET /feedback/requests)
  → FeedbackCreateView:
      1. Seleccionar experiencia (del store)
      2. Seleccionar relación (GET /feedback/relationships)
      3. Datos del referente
      → POST /feedback/requests → email automático al referente
```

### 6.6 Cuestionario público (referente)
```
Email con link → /q/:token → QuestionnaireView
  → GET /questionnaire/:token → Render categorías + preguntas
  → Usuario responde 1-5 por pregunta
  → POST /questionnaire/:token → Éxito / Gracias
```

### 6.7 Dashboard con métricas
```
DashboardView:
  → GET /skills/metrics → SkillsRadarChart + SkillsSummary
  → GET /professional/profile → ProfileCard resumen
  → GET /feedback/requests → últimas solicitudes
```

---

## 7. Plan de Implementación (Fases)

### Fase 1: Infraestructura base
1. Reestructurar carpetas (`api/`, `composables/`, `layouts/`, `types/`, `utils/`)
2. Configurar `client.js` con interceptors mejorados y variable de entorno
3. Crear módulos API (`auth.api.js`, `professional.api.js`, etc.)
4. Crear layouts (`AuthLayout`, `DefaultLayout`, `PublicLayout`)
5. Refactorizar router con rutas modulares y guards
6. Componentes UI base (`AppButton`, `AppInput`, `AppCard`, `AppSpinner`, `AppAlert`)

### Fase 2: Auth completo
7. Refactorizar `auth.store.js` con flujo OTP
8. `ConfirmAccountView` (nueva)
9. `ForgotPasswordView` (nueva)
10. `ResetPasswordView` (nueva)
11. Mejorar `LoginView` y `RegisterView` existentes

### Fase 3: Perfil profesional
12. `profile.store.js`
13. `ProfileView` + `ProfileCard`
14. `ProfileEditView` + `ProfileForm`

### Fase 4: Experiencias laborales
15. `experience.store.js`
16. `ExperienceListView` + `ExperienceCard`
17. `ExperienceFormView` + `ExperienceForm` (crear/editar)

### Fase 5: Feedback
18. `feedback.store.js`
19. `FeedbackListView` + `FeedbackRequestCard`
20. `FeedbackCreateView` + `FeedbackRequestForm`

### Fase 6: Cuestionario público
21. `QuestionnaireView` con `CategorySection` + `QuestionRating` + `AppRating`

### Fase 7: Dashboard y métricas
22. `skills.store.js`
23. `SkillsRadarChart` (chart.js o similar)
24. `SkillsSummary`
25. `DashboardView` integrando perfil + skills + feedback

### Fase 8: Polish
26. Transiciones de ruta
27. Manejo global de errores (toast notifications)
28. Loading skeletons
29. Auditoría de accesibilidad (axe-core / Lighthouse) y ajustes finales
30. Tests unitarios (Vitest) para stores y composables

---

## 8. Dependencias Recomendadas a Añadir

| Paquete | Justificación |
|---|---|
| `tailwindcss` + `@tailwindcss/vite` | Framework CSS utility-first |
| `shadcn-vue` | Componentes UI copiables basados en Radix Vue + Tailwind |
| `radix-vue` | Primitivos accesibles headless (dependencia de shadcn-vue) |
| `class-variance-authority` | Variantes de estilos para componentes (cn helper) |
| `clsx` + `tailwind-merge` | Utilidades para merging de clases CSS |
| `lucide-vue-next` | Iconos SVG consistentes (recomendado por shadcn-vue) |
| `@vueuse/core` | Composables utilitarios (useStorage, useDebounceFn, etc.) |
| `chart.js` + `vue-chartjs` | Gráfico radar para métricas de skills |
| `vee-validate` + `zod` | Validación declarativa de formularios con schema |
| `vitest` + `@vue/test-utils` | Testing unitario |
| `unplugin-auto-import` | Auto-import de Vue/Router/Pinia APIs |

---

## 9. Convenciones

- **Naming:** PascalCase para componentes, camelCase para archivos JS, kebab-case para rutas URL
- **Stores:** sufijo `.store.js`, composición API (`setup` syntax)
- **API modules:** sufijo `.api.js`
- **Componentes UI:** shadcn-vue copiados en `components/ui/` (Button, Input, Card, Dialog, Badge, etc.)
- **Componentes de dominio:** prefijo de dominio (e.g., `ProfileCard`, `ExperienceForm`)
- **Views:** sufijo `View` (e.g., `LoginView.vue`)
- **Props/Emits:** documentados con `defineProps`/`defineEmits` tipados
- **CSS:** Tailwind utility classes, CSS variables para design tokens en `globals.css`, scoped solo cuando necesario
- **Variables de entorno:** prefijo `VITE_` en `.env`
- **Iconos:** Lucide icons via `lucide-vue-next`
- **Tema:** shadcn-vue CSS variables para colores (HSL), modo oscuro con clase `dark`
- **Responsive:** mobile-first, breakpoints Tailwind (`sm`, `md`, `lg`, `xl`), layout adaptativo desde Fase 1
- **Accesibilidad:** WCAG 2.1 AA como estándar mínimo en cada componente

---

## 10. Responsive Design (Mobile-First)

### 10.1 Estrategia
- **Mobile-first:** estilos base para móvil, progresivamente mejorados con `sm:`, `md:`, `lg:`, `xl:`
- **Breakpoints Tailwind:** `sm(640)` → `md(768)` → `lg(1024)` → `xl(1280)`
- **No es una fase separada:** cada componente y vista se diseña responsive desde su creación

### 10.2 Patrones por zona

| Zona | Móvil (<640px) | Tablet (640-1024px) | Desktop (>1024px) |
|---|---|---|---|
| **Navbar** | Hamburger menu + drawer | Hamburger o tabs | Barra horizontal completa |
| **Sidebar** | Oculta (drawer overlay) | Colapsable (iconos) | Expandida fija |
| **Dashboard** | Cards apiladas verticalmente | Grid 2 columnas | Grid 3 columnas |
| **Formularios** | Full-width, campos apilados | 2 columnas parcial | 2-3 columnas |
| **Tablas/listas** | Cards apiladas (no tabla) | Tabla responsiva con scroll | Tabla completa |
| **Cuestionario** | 1 categoría visible, stepper | 1 categoría con más espacio | Todas visibles con scroll |

### 10.3 Composable `useBreakpoint`
- Usa `@vueuse/core` `useBreakpoints` para lógica condicional en JS
- Ejemplo: sidebar drawer en móvil vs fija en desktop

---

## 11. Accesibilidad (WCAG 2.1 AA)

### 11.1 Fundamentos (garantías de Radix Vue / shadcn-vue)
Los componentes de shadcn-vue están construidos sobre **Radix Vue**, que proporciona de serie:
- Roles ARIA correctos (`role="dialog"`, `role="alert"`, etc.)
- Gestión de focus trap en modals/drawers
- Navegación por teclado (Tab, Escape, Enter, Arrow keys)
- `aria-expanded`, `aria-selected`, `aria-disabled` automáticos

### 11.2 Reglas obligatorias en desarrollo

| Criterio | Implementación |
|---|---|
| **Semántica HTML** | `<main>`, `<nav>`, `<header>`, `<section>`, `<form>`, `<h1>`-`<h6>` correctamente anidados |
| **Labels en formularios** | Todo `<input>` con `<label>` asociado (via `for`/`id` o FormField de shadcn) |
| **Contraste de color** | Ratio mínimo 4.5:1 texto normal, 3:1 texto grande (asegurado por tokens HSL del tema) |
| **Focus visible** | Anillo de focus visible en todos los elementos interactivos (`focus-visible:ring-2`) |
| **Textos alternativos** | Imágenes con `alt`, iconos decorativos con `aria-hidden="true"`, iconos funcionales con `aria-label` |
| **Errores de formulario** | `aria-invalid="true"` + `aria-describedby` apuntando al mensaje de error |
| **Skip to content** | Link oculto "Ir al contenido" como primer elemento focusable |
| **Navegación por teclado** | Tab order lógico, Escape cierra modals/drawers, Enter activa botones |
| **Live regions** | `aria-live="polite"` para toasts/notificaciones, `aria-live="assertive"` para errores críticos |
| **Responsive text** | `rem`/`em` para tipografía (nunca `px` fijo), respeta zoom 200% sin pérdida de contenido |
| **Targets táctiles** | Mínimo 44x44px para elementos interactivos en móvil |

### 11.3 Rating del cuestionario (componente custom)
El componente de rating 1-5 debe implementar:
- `role="radiogroup"` con `aria-label="Puntuación"`
- Cada estrella/punto: `role="radio"` + `aria-checked` + `aria-label="1 de 5"`
- Navegación con flechas izquierda/derecha

### 11.4 Radar Chart (accesibilidad de gráficos)
- Tabla de datos alternativa oculta con `sr-only` para lectores de pantalla
- `aria-label` descriptivo en el `<canvas>` del chart
- Resumen textual visible debajo del gráfico (SkillsSummary)

### 11.5 Herramientas de auditoría
- **Desarrollo:** extensión axe DevTools en navegador
- **CI (futuro):** `@axe-core/playwright` o `vitest-axe` para tests automatizados
- **Manual:** Lighthouse Accessibility score ≥ 90 como objetivo
