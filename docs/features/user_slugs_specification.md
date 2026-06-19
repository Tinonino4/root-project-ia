# Especificación Funcional: URL Amigable y Slugs de Usuario (User Slugs)

Este documento detalla la especificación funcional y los casos de uso para la implementación de URLs amigables (slugs/usernames) en los perfiles públicos de **MiCaché**. 

---

## 1. Descripción de la Funcionalidad
Actualmente, los perfiles públicos de los usuarios (Cachers) se consultan a través de su identificador UUID (ej. `/u/550e8400-e29b-41d4-a716-446655440000`). Esto resulta impráctico y difícil de compartir en currículums físicos o redes sociales como LinkedIn.

Esta feature permite a los usuarios:
1. Tener un `username` único (slug) asignado por defecto al registrarse.
2. Personalizar su `username` desde la pantalla de edición de perfil, validando el formato y la unicidad.
3. Compartir su perfil público a través de una URL amigable (ej. `/u/nombre-apellido` o `/u/juan-perez`).
4. Mantener la retrocompatibilidad: las URLs antiguas que contienen el UUID del usuario seguirán funcionando de forma transparente.

---

## 2. Historias de Usuario

### Historia 1: Asignación automática en el registro
Como **Candidato recién registrado**, quiero que el sistema me asigne un nombre de usuario (slug) por defecto para que mi perfil sea inmediatamente accesible mediante una URL amigable sin necesidad de configurarlo manualmente.

### Historia 2: Personalización del slug
Como **Candidato registrado (Cacher)**, quiero poder cambiar mi nombre de usuario (slug) por uno de mi preferencia para que mi enlace público sea fácil de recordar y profesional.

### Historia 3: Consulta del perfil amigable (Reclutador)
Como **Reclutador o visitante**, quiero ingresar a la URL amigable de un candidato para visualizar su gráfico de radar, experiencias y referencias certificadas de forma rápida.

---

## 3. Escenarios de Aceptación (Gherkin / Cucumber)

### Escenario 1: Asignación por defecto al registrarse
```gherkin
Given que un nuevo usuario se registra en la plataforma con el nombre "Agustín Hernández"
When el sistema crea su perfil de usuario
Then se genera un slug por defecto "agustin-hernandez"
And si el slug ya existe, se le añade un sufijo numérico único (ej. "agustin-hernandez-1")
And el valor se guarda en la columna "username" de la tabla "user_profiles"
```

### Escenario 2: Edición del username con formato correcto y libre
```gherkin
Given que un usuario autenticado está en la pantalla de edición de su perfil
When introduce un nuevo nombre de usuario "agustin_h"
And hace clic en "Guardar"
Then el sistema valida que "agustin_h" cumple el formato permitido (alfanumérico, guiones y guiones bajos, sin espacios)
And valida que no está siendo utilizado por otro usuario
And actualiza el perfil con éxito
```

### Escenario 3: Edición del username con formato incorrecto
```gherkin
Given que un usuario autenticado está en la pantalla de edición de su perfil
When introduce un nombre de usuario con caracteres inválidos "agustin h!" o "agustín" (con acento)
Then el sistema rechaza la solicitud
And muestra un mensaje de error indicando: "El nombre de usuario solo puede contener letras (sin acentos o eñes), números, guiones y guiones bajos"
```

### Escenario 4: Edición del username duplicado
```gherkin
Given que el nombre de usuario "carla-gonzalez" ya está asignado a la usuaria Carla
And el usuario Agustín intenta cambiar su nombre de usuario a "carla-gonzalez"
When hace clic en "Guardar"
Then el sistema rechaza la solicitud
And muestra un mensaje de error indicando: "Este nombre de usuario ya está en uso"
```

### Escenario 5: Visualización de perfil con URL amigable
```gherkin
Given que el usuario Agustín tiene el slug "agustin-h"
When un visitante accede a la URL "/u/agustin-h"
Then el frontend solicita el perfil mediante el slug "agustin-h"
And el backend resuelve que no es un UUID y busca por "username"
And se muestra el perfil público de Agustín correctamente
```

### Escenario 6: Retrocompatibilidad con URLs basadas en UUID
```gherkin
Given que el usuario Agustín tiene el UUID "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
When un visitante accede a la URL "/u/f81d4fae-7dec-11d0-a765-00a0c91e6bf6"
Then el frontend solicita el perfil mediante el ID
And el backend resuelve que es un UUID y busca por "id" de usuario
And se muestra el perfil público de Agustín correctamente
```

---

## 4. Detalles Técnicos de Implementación

### A. Base de Datos (PostgreSQL via Flyway)
* Nueva migración `V8__Add_username_to_user_profiles.sql` para añadir la columna `username`:
  ```sql
  ALTER TABLE user_profiles ADD COLUMN username VARCHAR(100);
  
  -- Inicializar perfiles existentes con un slug por defecto basado en su ID o nombre
  UPDATE user_profiles 
  SET username = LOWER(REGEXP_REPLACE(name, '[^a-zA-Z0-9]', '-', 'g')) || '-' || SUBSTRING(id::text, 1, 4)
  WHERE username IS NULL;
  
  -- Hacer la columna única y no nula
  ALTER TABLE user_profiles ALTER COLUMN username SET NOT NULL;
  ALTER TABLE user_profiles ADD CONSTRAINT uq_user_profiles_username UNIQUE (username);
  ```

### B. Backend (Spring Boot 4.0.5)
1. **Modelo de Dominio (`UserProfile.java`):**
   * Añadir propiedad `private String username`.
   * Añadir getter, setter y método de actualización `updateUsername(String username)`.
2. **Repositorio (`UserProfileRepository.java`):**
   * Añadir método `Optional<UserProfile> findByUsername(String username)`.
   * Añadir método `boolean existsByUsername(String username)`.
3. **Escucha de Registro (`UserRegisteredListener.java`):**
   * Al recibir `UserRegisteredEvent`, generar un slug por defecto utilizando una utilidad (ej. `Slugify`) y guardarlo.
4. **Controlador Público (`PublicProfileController.java`):**
   * Modificar el endpoint de consulta `/api/public/profile/{userIdOrUsername}`.
   * Detectar si el path param es un UUID válido. Si lo es, buscar por `userId`. Si no lo es, buscar por `username`.
5. **Controlador Privado (`ProfessionalController.java`):**
   * Actualizar el DTO de edición `UserProfileRequest` para aceptar `username`.
   * Validar el formato mediante expresión regular (`^[a-z0-9-_]+$`) y verificar unicidad en la base de datos antes de guardar.

### C. Frontend (Vue 3 + Vite)
1. **Rutas (`public.routes.js`):**
   * Modificar la ruta `/u/:userId` para que sea genérica: `/u/:slug`.
2. **Vista Pública (`PublicProfileView.vue`):**
   * Cambiar la llamada API para pasar `:slug` en lugar de `:userId`.
3. **Edición de Perfil (`ProfileEditView.vue`):**
   * Añadir campo de texto "Nombre de usuario (URL)" en el formulario de edición de perfil.
   * Añadir validaciones reactivas con `zod` y `vee-validate` para el formato del slug.
   * Mostrar errores específicos si el backend devuelve un código 400 (nombre de usuario en uso).
