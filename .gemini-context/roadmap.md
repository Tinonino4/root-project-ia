# Roadmap de Desarrollo - root-project-ia

## Hito 1: Setup, Autenticación y Trayectoria Profesional [Completado]
- [x] Configuración inicial multimodular (Maven, Spring Boot 4.x).
- [x] Registro y Login Local con confirmación OTP.
- [x] Integración de OAuth2 / Login Social (Google, GitHub, LinkedIn).
- [x] Gestión de Perfil de Usuario (Cacher) y CRUD de Experiencias Laborales.

## Hito 2: Flujo de Feedback y Motor de Confianza [Completado]
- [x] Flujo de Solicitud de Feedback (generación de url_token público).
- [x] Cuestionario público de 5 competencias blandas (Trabajo en equipo, Proactividad, Integridad, Autoconfianza, Flexibilidad).
- [x] Motor de Confianza del Referente ("Let's Trust Security") en el backend.
- [x] Panel de control con radar de competencias (Chart.js) y visualización en el frontend.
- [x] Pestaña de solicitudes pendientes, envío de recordatorios y cancelación en frontend.
- [x] Envío de notificaciones automáticas por correo electrónico (Brevo) al crearse, recordarse y completarse una referencia.

## Hito 3: URL Amigable y Slugs de Usuario [Pendiente]
- [ ] Añadir columna `username` único en `user_profiles` en base de datos.
- [ ] Permitir modificar el username del perfil desde el frontend.
- [ ] Adaptar router de Vue y backend para servir el perfil público en `/u/{username}`.

## Hito 4: Gating B2B y Registro de Empresas (Reclutadores) [Pendiente]
- [ ] Implementar soporte completo para `ROLE_COMPANY` en Spring Security.
- [ ] Modificar formulario de registro en Vue para seleccionar "Candidato" o "Empresa (Reclutador)", exigiendo nombre de empresa y CIF para esta última.
- [ ] Restringir endpoints de búsqueda en `RecruiterController.java` para que solo sean accesibles por usuarios con `ROLE_COMPANY`.
- [ ] Modificar el listado de búsqueda para mostrar insignias y habilitar descargas en PDF.
