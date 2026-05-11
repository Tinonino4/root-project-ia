# Constitución del Proyecto (AGENTS)

## Stack Tecnológico
- Backend: Java 21+ / Spring Boot 4.0.5 (Ecosistema Spring 7, Clean Architecture)
- Frontend: Vue3 o React Native
- Base de Datos: PostgreSQL

## Comandos de Construcción (Deterministas)
- Backend: `mvn clean install`
- Frontend: `npm run build`

## Convenciones Críticas de Nomenclatura
- (A definir según las necesidades del proyecto)

## Enrutamiento de Agentes (Triggers)
- Modificaciones en `backend/**/*.java` ➔ Invocar a `@spring-eng`
- Modificaciones en `frontend/**/*.vue` o `frontend/**/*.tsx` ➔ Invocar a `@vue-eng` / `@react-eng`
- Modificaciones en `backend/src/main/resources/db/migration/**/*.sql` ➔ Invocar a `@db-admin`
