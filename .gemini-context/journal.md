# Diario Técnico y Registro de Decisiones

## Decisiones Técnicas Tomadas (Inferidas del Estado Actual del Código)
- **2026-06-19 - Arquitectura Hexagonal Pragmática**: Se acoplan anotaciones JPA (`@Entity`, `@Table`) directamente sobre las entidades de dominio (como `User.java` o `CacheRequest.java`) dentro de los paquetes de dominio de cada módulo. Esto evita código de mapeo excesivo, favoreciendo la velocidad del MVP, manteniendo métodos de negocio semánticos dentro del modelo.
- **2026-06-19 - Spring Modulith**: Se implementó Spring Modulith 2.0.6 en lugar de una modularización maven física tradicional. Se utiliza `ApplicationModuleListener` para gestionar eventos inter-módulo de forma desacoplada y asíncrona (ej. envío de notificaciones de registro y completado de feedback en `EmailNotificationListener`).
- **2026-06-19 - Exclusión de Lombok y MapStruct**: No se utilizan Lombok ni MapStruct en la base de código actual para favorecer código Java explícito y evitar dependencias de compilación propietarias.
- **2026-06-19 - Lecturas ligeras entre módulos vía JDBC**: En `FeedbackService`, para consultar datos básicos de otros módulos (nombre de usuario, email, empresa de la experiencia laboral) se utiliza `JdbcTemplate` con consultas SQL crudas en lugar de acoplar y consultar repositorios externos de otros módulos, lo cual violaría las fronteras lógicas de Spring Modulith.
- **2026-06-19 - Script local de gestión de datos**: Se mantiene `manage_user_data.py` en la raíz como utilidad para realizar backups, borrados y restauraciones de usuarios conectándose por SSH al VPS de producción.
- **2026-06-19 - Configuración manual de OAuth2 (LinkedIn)**: En `SecurityConfig`, se personaliza el resolvedor de solicitudes OAuth2 para LinkedIn eliminando parámetros de PKCE y `nonce` que no son compatibles con su API.

## Deuda Técnica Identificada
- [ ] Restringir seguridad en el backend para `/api/recruiter/**` para que solo acepte usuarios con rol `ROLE_COMPANY`.
- [ ] Validar que los listener de eventos del módulo de comunicación (`EmailNotificationListener`) procesen los correos de forma puramente asíncrona para no bloquear el hilo de ejecución principal.
- [ ] Crear tests de integración automatizados con Testcontainers para verificar las transacciones y la persistencia de base de datos.
