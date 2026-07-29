# 🧠 Contexto de Arquitectura y Rediseño Conceptual: Cuestionarios 360° y Radar Conductual para MiCaché.es

> **Propósito del Documento:** Este archivo contiene el contexto integral, decisiones de producto, modelo conceptual y la lista completa de archivos creados/modificados en el proyecto **MiCaché.es**. Está formateado para ser consumido directamente por un Modelo de Lenguaje (LLM) o por un desarrollador para entender la evolución del sistema.

---

## 1. Visión del Producto y Problema a Resolver

### Contexto Anterior (MVP)
En la versión inicial de **MiCaché.es**, el cuestionario público de referencias solicitaba a los evaluadores valorar al candidato en **5 soft skills** (Trabajo en equipo, Proactividad, Integridad, Autoconfianza, Flexibilidad) mediante una **escala numérica del 1 al 5**.

### Diagnóstico de Fallos del Modelo Antiguo
1. **Sesgo de Indulgencia (*Leniency Bias*):** Los referentes (amigos o excompañeros) tienden a poner 5/5 o 4.8 a todo por compromiso social y empatía, para no perjudicar al candidato.
2. **Efecto Techo (*Ceiling Effect*):** La escala 1-5 se colapsa. Las notas 1, 2 y 3 no se usan; 4 es "normal" y 5 es "lo esperado". Todos los candidatos terminan con promedios entre **4.5 y 4.9**.
3. **Falta de Discriminación B2B:** Para un reclutador pagador, un gráfico donde todos los candidatos parecen pentágonos idénticos de 4.8/5 carece de valor predictivo.

---

## 2. Rediseño Conceptual: El Modelo Conductual Híbrido 360°

Para resolver la inflación del feedback sin aumentar el tiempo de respuesta ni la fricción del evaluador, se implementó una evolución conceptual basada en 4 pilares:

### A. Opciones Moralmente Neutrales (BARS - Behavioral Anchored Rating Scales)
Sustituir las notas 1-5 por **descriptores de estilos de trabajo reales**. Ninguna opción es "buena" o "mala" en abstracto; cada respuesta describe una forma válida de funcionar.
- *Resultado:* El evaluador no siente que está "aprobando o suspendiendo" al candidato, sino **describiendo su estilo de trabajo**. Se elimina la tentación de poner "5 a todo".

### B. Matriz de Perspectiva 360° por Rol Jerárquico
Las preguntas del cuestionario se adaptan según el tipo de relación entre evaluador y evaluado:
- **Jefes / Mánagers:** Evalúan Autonomía, Receptividad al feedback, Fiabilidad en entregables y Gestión de la presión.
- **Compañeros (Peers):** Evalúan Resolución del conflicto horizontal, Apoyo mutuo, Comunicación diaria y Clima laboral.
- **Subordinados (Equipo):** Evalúan Liderazgo, Delegación, Protección bajo presión y Desarrollo de personas.

### C. Elección Forzada (Forced-Choice Quadrads)
Una pregunta donde se presentan 5 atributos positivos y el evaluador debe **seleccionar únicamente las 2 virtudes más representativas**. Esto obliga a priorizar las fortalezas relativas del candidato.

### D. Rediseño UX del Radar con Pestañas (*Tabbed Radar*)
Para evitar la colisión visual de superponer 3 capas de colores en un mismo gráfico radial:
- **Vista por Defecto (`🌐 Global`):** Un radar limpio con la línea consolidada calibrada.
- **Pestañas de Aislamiento (`👔 Jefes`, `🤝 Peers`, `👑 Equipo`):** Permiten aislar con 1 clic la percepción de cada estamento jerárquico.
- **Modo Comparativo (`⚡ Comparar 360°`):** Permite superponer las capas opcionalmente para analizar diferencias.

---

## 3. Catálogo Oficial de Preguntas de 5 Bloques por Rol

El cuestionario consta de **exactamente 5 preguntas por rol** (tiempo estimado de respuesta: **< 90 segundos**):

```markdown
┌────────────────────────────────────────────────────────────────────────┐
│ Q1. Estilo de Resolución / Conflicto      (Conductual Neutral)         │
│ Q2. Estilo de Comunicación / Apoyo        (Conductual Neutral)         │
│ Q3. Estilo ante la Presión / Incertidumbre (Conductual Neutral)        │
│ Q4. Top Virtudes Destacadas               (Elección Forzada - Pick 2)  │
│ Q5. Entorno Ideal de Rendimiento          (Fit Cultural)               │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Inventario Completo de Archivos Creados y Modificados

A continuación se detallan los archivos del proyecto impactados por esta implementación:

### A. Base de Datos (PostgreSQL & Flyway)
- 📄 **[NEW]** [backend/src/main/resources/db/migration/V9__Evolve_questionnaires_to_behavioral_model.sql](file:///home/tino/Projects/root-project-ia/backend/src/main/resources/db/migration/V9__Evolve_questionnaires_to_behavioral_model.sql)
  - *Descripción:* Crea la tabla `user_role_skills_metrics` para almacenar métricas de soft skills desglosadas por rol (`DIRECT_MANAGER`, `COLLEAGUE`, `SUBORDINATE`).

---

### B. Backend (Java 21 / Spring Boot 4.0.5)
- 📄 **[NEW]** [backend/src/main/java/com/ia/root/backend/analytics/MultiLayerSkillsData.java](file:///home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/analytics/MultiLayerSkillsData.java)
  - *Descripción:* Record DTO que empaqueta la estructura de métricas `global`, `managers`, `peers` y `subordinates`.
- 📄 **[NEW]** [backend/src/main/java/com/ia/root/backend/analytics/ArchetypeDataDTO.java](file:///home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/analytics/ArchetypeDataDTO.java)
  - *Descripción:* Record DTO para etiquetas de arquetipo conductual, top fortalezas y encaje de entorno ideal.
- 📄 **[MODIFY]** [backend/src/main/java/com/ia/root/backend/analytics/SkillsMetricsService.java](file:///home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/analytics/SkillsMetricsService.java)
  - *Descripción:* Implementa los métodos `getMultiLayerSkillsData` y `getArchetypeData` consultando la base de datos por tipo de relación.
- 📄 **[MODIFY]** [backend/src/main/java/com/ia/root/backend/professional/internal/infrastructure/web/dto/PublicProfileDTO.java](file:///home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/professional/internal/infrastructure/web/dto/PublicProfileDTO.java)
  - *Descripción:* Actualiza el DTO de respuesta del perfil público para incluir los objetos `skillsMultiLayer` y `archetype`.
- 📄 **[MODIFY]** [backend/src/main/java/com/ia/root/backend/professional/internal/infrastructure/web/PublicProfileController.java](file:///home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/professional/internal/infrastructure/web/PublicProfileController.java)
  - *Descripción:* Inyecta los datos de `MultiLayerSkillsData` y `ArchetypeDataDTO` en la respuesta de la API `/api/public/profile/{userIdOrUsername}`.

---

### C. Frontend (Nuxt 3 / Vue 3)
- 📄 **[MODIFY]** [frontend-nuxt/types/profile.ts](file:///home/tino/Projects/root-project-ia/frontend-nuxt/types/profile.ts)
  - *Descripción:* Define las interfaces TypeScript `RoleSkillsMetrics`, `MultiLayerSkillsMetrics` y `ArchetypeData`.
- 📄 **[NEW]** [frontend-nuxt/components/dashboard/SkillsRadarChart360.vue](file:///home/tino/Projects/root-project-ia/frontend-nuxt/components/dashboard/SkillsRadarChart360.vue)
  - *Descripción:* Componente de radar con `vue-chartjs` y selector interactivo de pestañas por rol (`Global`, `Jefes`, `Peers`, `Equipo`, `Comparar`).
- 📄 **[NEW]** [frontend-nuxt/components/profile/FitCulturalCard.vue](file:///home/tino/Projects/root-project-ia/frontend-nuxt/components/profile/FitCulturalCard.vue)
  - *Descripción:* Tarjeta UI para mostrar arquetipos de trabajo, fortalezas de elección forzada e indicador de fit cultural.
- 📄 **[MODIFY]** [frontend-nuxt/pages/u/[id].vue](file:///home/tino/Projects/root-project-ia/frontend-nuxt/pages/u/[id].vue)
  - *Descripción:* Integra los componentes `SkillsRadarChart360` y `FitCulturalCard` en el perfil público del candidato.
- 📄 **[MODIFY]** [frontend-nuxt/locales/es.json](file:///home/tino/Projects/root-project-ia/frontend-nuxt/locales/es.json)
  - *Descripción:* Claves de internacionalización i18n para pestañas, arquetipos e indicadores.

---

### D. Suites de Pruebas Automatizadas
- 📄 **[NEW]** [backend/src/test/java/com/ia/root/backend/professional/PublicProfile360Test.java](file:///home/tino/Projects/root-project-ia/backend/src/test/java/com/ia/root/backend/professional/PublicProfile360Test.java)
  - *Descripción:* Test unitario en JUnit 5 + Mockito que verifica la integridad de los DTOs y servicios de métricas 360°.
- 📄 **[NEW]** [e2e/tests/profile_360.spec.ts](file:///home/tino/Projects/root-project-ia/e2e/tests/profile_360.spec.ts)
  - *Descripción:* Test End-to-End en Playwright que prueba la interactividad de las pestañas del radar y las tarjetas de arquetipo.

---

### E. Prototipo Interactivo Standalone
- 📄 **[NEW]** [preview_perfil_360.html](file:///home/tino/Projects/root-project-ia/preview_perfil_360.html)
  - *Descripción:* Fichero HTML/JS ejecutable directamente en navegador con la demo completa interactiva de la interfaz.

---

## 🧪 Estado de Verificación y Compilación

1. **Backend Test & Compilation:** `mvn test -Dtest=PublicProfile360Test` ➔ **`BUILD SUCCESS`** (0 errores, 2/2 tests pasados).
2. **Frontend Build:** `cd frontend-nuxt && npm run build` ➔ **`✨ Build complete!`** (Compilación Nitro y TypeScript 100% limpia).
3. **E2E Tests:** `npx playwright test e2e/tests/profile_360.spec.ts` ➔ **`2 passed (1.6s)`** (100% pasados).
