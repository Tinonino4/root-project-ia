# Guía Paso a Paso: Conexión al VPS, BD y Borrado de Referencias para `ahgalonso@gmail.com`

Este documento contiene las instrucciones detalladas y comandos exactos para conectarte al servidor VPS de producción de **Mi Caché** (`37.27.197.244`), acceder a la base de datos PostgreSQL en Docker y eliminar de forma segura todas las solicitudes de referencias (enviadas) y referencias recibidas asociadas al usuario **`ahgalonso@gmail.com`**.

---

## 1. Conexión al Servidor (SSH)

Abre una terminal en tu equipo local y conéctate al VPS como usuario `root` según lo indicado en `server_guide.md`:

```bash
ssh root@37.27.197.244
```

---

## 2. Acceso a la Base de Datos PostgreSQL

Según la arquitectura de producción (`server_guide.md` y `docker-compose.yml`), la base de datos PostgreSQL se ejecuta dentro de un contenedor Docker gestionado desde `/var/web/micache/observability`.

### Opción A: Acceso interactivo al prompt de PostgreSQL (`psql`)

Conéctate a `psql` dentro del contenedor ejecutando:

```bash
docker exec -it observability-postgres-1 psql -U postgres -d root_ia_db
```
*(Si el nombre del contenedor varía en Docker Compose, puedes verificarlo con `docker ps` o ejecutar desde el directorio de observabilidad: `cd /var/web/micache/observability && docker compose exec postgres psql -U postgres -d root_ia_db`)*

---

## 3. Paso 1: Verificación Previa (Consultar Datos Existentes)

Antes de realizar el borrado, ejecuta estas consultas en el prompt de `psql` (`root_ia_db=#`) para inspeccionar qué datos existen actualmente para este usuario:

### A. Verificar el ID del usuario
```sql
SELECT id, email, created_at FROM users WHERE email = 'ahgalonso@gmail.com';
```

### B. Consultar solicitudes de referencias enviadas por el usuario
```sql
SELECT id, target_name, target_surname, target_email, finished, created_at
FROM cache_requests
WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com');
```

### C. Consultar solicitudes/referencias recibidas hacia su correo (`target_email`)
```sql
SELECT id, user_id, target_name, target_email, finished, created_at
FROM cache_requests
WHERE target_email = 'ahgalonso@gmail.com';
```

### D. Consultar respuestas de feedback asociadas (`feedback_responses`)
```sql
SELECT fr.id, fr.cache_request_id, fr.question_id, fr.rating, fr.created_at
FROM feedback_responses fr
JOIN cache_requests cr ON fr.cache_request_id = cr.id
WHERE cr.user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com')
   OR cr.target_email = 'ahgalonso@gmail.com';
```

### E. Consultar métricas de habilidades del usuario (`user_skills_metrics`)
```sql
SELECT * FROM user_skills_metrics
WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com');
```

---

## 4. Paso 2: Borrado Seguro mediante Transacción (`BEGIN` / `COMMIT`)

El uso de una transacción garantiza que las eliminaciones se apliquen en bloque o se reviertan totalmente si se requiere.

Ejecuta el siguiente bloque en `psql`:

```sql
-- 1. Iniciar transacción
BEGIN;

-- 2. Eliminar todas las solicitudes enviadas por el usuario Y recibidas hacia su email.
-- Nota: Las respuestas en `feedback_responses` se eliminan automáticamente mediante la clave foránea ON DELETE CASCADE.
DELETE FROM cache_requests
WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com')
   OR target_email = 'ahgalonso@gmail.com';

-- 3. Eliminar métricas acumuladas de habilidades del usuario
DELETE FROM user_skills_metrics
WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com');

-- 4. Confirmar cambios en la base de datos
COMMIT;
```

> ⚠️ **Nota:** Si deseas deshacer los cambios antes de ejecutar `COMMIT;`, escribe:
> ```sql
> ROLLBACK;
> ```

---

## 5. Paso 3: Verificación Posterior

Comprueba que ya no existan registros para dicho usuario (debe devolver 0 en ambos casos):

```sql
SELECT COUNT(*) FROM cache_requests
WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com')
   OR target_email = 'ahgalonso@gmail.com';

SELECT COUNT(*) FROM user_skills_metrics
WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com');
```

---

## 6. Comando Ejecutable en Una Sola Línea (Desde el VPS)

Si prefieres no entrar interactivamente a `psql`, puedes ejecutar el borrado completo en un solo comando SSH o dentro del VPS:

```bash
docker exec -i $(docker ps -q -f name=postgres) psql -U postgres -d root_ia_db -c "
BEGIN;
DELETE FROM cache_requests WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com') OR target_email = 'ahgalonso@gmail.com';
DELETE FROM user_skills_metrics WHERE user_id = (SELECT id FROM users WHERE email = 'ahgalonso@gmail.com');
COMMIT;
"
```
