---
description: Gestión y ejecución de migraciones en PostgreSQL
---

# Habilidad: Ejecutar Migración de Base de Datos PostgreSQL

**Objetivo:** Crear y aplicar migraciones de esquema de base de datos de forma segura.

## Reglas Imperativas
1. Si necesitas cambiar el esquema, entonces crea un nuevo script SQL en `scripts/`.
2. Nunca modifiques un script de migración que ya haya sido ejecutado en producción.
3. Utiliza comandos deterministas para probar las migraciones localmente antes de confirmar.

*Instrucciones adicionales a definir...*
