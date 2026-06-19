# Estado de la Sesión Actual

## Tareas Completadas
- [x] Configuración inicial de la memoria persistente del repositorio en `.gemini-context/`.
- [x] Implementación completa del Hito 3: URL Amigable y Slugs de Usuario (base de datos, backend, frontend y suite de tests automáticos).
- [x] Rediseño de la UI en la vista de perfil privado y público (layout asimétrico 4/12 vs 8/12, barra lateral sticky flotante y tarjeta de resumen de certificación).
- [x] Solución de usabilidad y formato en la gestión de experiencias (mayor altura para el textarea de funciones y preservación de saltos de línea `whitespace-pre-wrap` en perfil privado, perfil público, listados y exportación a PDF).

## Próximas Tareas (Hito 4: Gating B2B y Registro de Empresas)
- [ ] Implementar soporte completo para `ROLE_COMPANY` en Spring Security.
- [ ] Modificar formulario de registro en Vue para seleccionar "Candidato" o "Empresa (Reclutador)", exigiendo nombre de empresa y CIF para esta última.
- [ ] Restringir endpoints de búsqueda en `RecruiterController.java` para que solo sean accesibles por usuarios con `ROLE_COMPANY`.
- [ ] Modificar el listado de búsqueda para mostrar insignias y habilitar descargas en PDF.

## Estado del Entorno Local
- **Backend**: Compila y pasa tests con `mvn clean test`.
- **Frontend**: Compila y genera bundle de producción con `npm run build`.
