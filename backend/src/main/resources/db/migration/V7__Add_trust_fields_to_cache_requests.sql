-- V7: Añadir campos de confianza y nivel de verificación a las solicitudes de feedback
ALTER TABLE cache_requests ADD COLUMN trust_score INT DEFAULT 0 NOT NULL;
ALTER TABLE cache_requests ADD COLUMN trust_level VARCHAR(20) DEFAULT 'BASICO' NOT NULL;
