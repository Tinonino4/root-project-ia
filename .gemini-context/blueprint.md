# Blueprint: root-project-ia

## Stack Tecnológico Mandatorio
- **Backend**: Java 21, Spring Boot 4.0.5, Spring Modulith 2.0.6, Spring Security, Flyway, PostgreSQL (local/producción).
- **Frontend**: Vue 3 (Composition API), Vite 8.x, Pinia 3.x, Tailwind CSS 3.4.x, Shadcn-vue, Vee-Validate, Zod.
- **Observabilidad**: OpenTelemetry, Micrometer (con otel-collector-config.yaml en Docker Compose).
- **Herramientas de Test**: Testcontainers (PostgreSQL), JUnit 5, Spring Security Test.
- **Estándares de Código (Backend)**:
  - NO usar Lombok (escribir constructores, getters y factory methods explícitos de forma manual).
  - NO usar MapStruct (realizar mapeos manuales si es necesario para evitar acoplamiento de capas).

## Reglas de Arquitectura (Backend - Spring Modulith + Hexagonal Pragmático)
- **Estructura por Módulos (Modulith)**: El backend se divide en módulos en `com.ia.root.backend` (ej. `auth`, `communication`, `feedback`, `professional`).
- **Arquitectura Interna del Módulo**:
  - `internal/domain`: Modelos y puertos (interfaces de repositorio). Por pragmatismo, los modelos de dominio se anotan con JPA (`@Entity`) para evitar mapeos complejos, pero deben encapsular su lógica de negocio mediante factory methods (ej. `createLocal()`) y métodos mutadores semánticos.
  - `internal/application`: Servicios de aplicación que implementan casos de uso.
  - `internal/infrastructure`: Adaptadores externos como controladores REST (en `web`), configuraciones de seguridad (en `security`) y repositorios Spring Data.
- **Comunicación Inter-módulo**: Debe realizarse de manera asíncrona mediante eventos de Spring Modulith (ej. `FeedbackCompletedEvent` o `FeedbackCompletedNotificationEvent`) o llamando a servicios públicos del módulo expuestos fuera del paquete `internal`. Se lee información ligera inter-módulo vía `JdbcTemplate` para evitar acoplar repositorios de persistencia.

## Estándares de Frontend (Vue 3 + Shadcn)
- **Componentes**: Reutilizar componentes de Shadcn-vue, personalizados con Tailwind CSS.
- **Reactividad**: Composition API con `<script setup>` obligatorio.
- **Formularios**: Validación del lado del cliente mediante `vee-validate` y esquemas de `zod`.
- **i18n**: Todo texto en la interfaz debe estar internacionalizado mediante `vue-i18n` (en/es).
