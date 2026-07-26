# Constitución del Proyecto (AGENTS)

## Stack Tecnológico
- Backend: Java 21+ / Spring Boot 4.0.5 (Ecosistema Spring 7, Clean Architecture)
- Frontend: Nuxt 3 (Vue 3 SSR)
- Base de Datos: PostgreSQL

## Base de Datos (Entorno Local)
Para consultar la base de datos local según los requerimientos del prompt, utiliza la herramienta de ejecución de comandos (`run_command`) con `psql` usando las siguientes credenciales:
- **URL JDBC:** `jdbc:postgresql://localhost:5432/root_ia_db`
- **Host:** `localhost`
- **Puerto:** `5432`
- **Base de datos:** `root_ia_db`
- **Usuario:** `postgres`
- **Contraseña:** `postgrespassword`

*Ejemplo de acceso por terminal:*
```bash
PGPASSWORD="postgrespassword" psql -U postgres -h localhost -p 5432 -d root_ia_db -c "TU_CONSULTA_SQL;"
```
## Comandos de Construcción (Deterministas)
- Backend: `mvn clean install`
- Frontend: `cd frontend-nuxt && npm run build`

## Convenciones Críticas de Nomenclatura
- (A definir según las necesidades del proyecto)

## Enrutamiento de Agentes (Triggers)
- Modificaciones en `backend/**/*.java` ➔ Invocar a `@spring-eng`
- Modificaciones en `frontend-nuxt/**/*.vue` o `frontend-nuxt/**/*.ts` ➔ Invocar a `@vue-eng`
- Modificaciones en `backend/src/main/resources/db/migration/**/*.sql` ➔ Invocar a `@db-admin`
