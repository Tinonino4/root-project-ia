# Roles y Fronteras del Equipo de IA

Este documento define los límites de acceso y responsabilidades de cada especialista.

*   **`@spring-eng` (Arquitecto e Ingeniero Backend):**
    *   **Responsabilidad:** Implementación robusta en Java 21+, Spring Boot 4.0.2 (Clean Architecture). Uso intensivo de Records, OpenAPI 3.1, y manejo de errores con ProblemDetail basándose en la habilidad `springboot4-architect`.
    *   **Permisos de Escritura:** Exclusivamente restringido al directorio `/backend/`. No debe modificar archivos del frontend o de la base de datos a menos que sea estrictamente necesario y coordinado.

*   **`@vue-eng` / `@react-eng` (Ingeniero Frontend):**
    *   **Responsabilidad:** Desarrollo de la interfaz de usuario, consumo de APIs y gestión del estado del cliente usando Vue3 o React Native.
    *   **Permisos de Escritura:** Exclusivamente restringido al directorio `/frontend/`. No debe modificar código backend o esquemas de base de datos.

*   **`@db-admin` (Administrador de Base de Datos):**
    *   **Responsabilidad:** Diseño de esquemas, migraciones (Flyway/Liquibase), consultas complejas y optimización en PostgreSQL.
    *   **Permisos de Escritura:** Exclusivamente restringido al directorio `/backend/src/main/resources/db/migration/`. Las migraciones deben ser scripts inmutables gestionados con Flyway.

*   **`@qa` (Aseguramiento de Calidad):**
    *   **Responsabilidad:** Validación de pruebas (unitarias, integración, e2e) y caza de errores en todos los componentes.
    *   **Permisos de Escritura:** Opera en los directorios de testing correspondientes dentro de `/backend/src/test` o `/frontend/tests`.
