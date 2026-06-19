-- V8: Añadir columna username única a user_profiles para URLs amigables
ALTER TABLE user_profiles ADD COLUMN username VARCHAR(100);

-- Inicializar perfiles existentes con un slug único y limpio basado en su nombre e ID
UPDATE user_profiles
SET username = LOWER(REGEXP_REPLACE(name, '[^a-zA-Z0-9]', '-', 'g')) || '-' || SUBSTRING(id::text, 1, 8)
WHERE username IS NULL;

-- Manejo de fallbacks si el slug resultante quedó vacío o es inválido
UPDATE user_profiles
SET username = 'cacher-' || SUBSTRING(id::text, 1, 8)
WHERE username IS NULL OR username = '-' OR username = '';

-- Hacer la columna NOT NULL y agregar la restricción UNIQUE
ALTER TABLE user_profiles ALTER COLUMN username SET NOT NULL;
ALTER TABLE user_profiles ADD CONSTRAINT uq_user_profiles_username UNIQUE (username);
