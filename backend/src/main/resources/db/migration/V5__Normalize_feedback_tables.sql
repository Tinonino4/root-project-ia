/* =========================================================
   V5: Normalizar tablas de feedback/cuestionario
   - Reemplaza requested_caches (columnas planas) por modelo
     normalizado con catálogo de preguntas
   ========================================================= */

-- 1. Eliminar tablas legacy del V3 que se reemplazan
DROP TABLE IF EXISTS requested_caches;

/* =========================================================
   CATÁLOGO DE SKILL CATEGORIES
   ========================================================= */

CREATE TABLE skill_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    position INT NOT NULL DEFAULT 0
);

/* =========================================================
   CATÁLOGO DE SKILL QUESTIONS
   ========================================================= */

CREATE TABLE skill_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL,
    question_text TEXT NOT NULL,
    position INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_question_category FOREIGN KEY (category_id) REFERENCES skill_categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_skill_questions_category ON skill_questions(category_id);

/* =========================================================
   FEEDBACK RESPONSES (respuestas normalizadas, 1 fila = 1 rating)
   ========================================================= */

CREATE TABLE feedback_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cache_request_id UUID NOT NULL,
    question_id UUID NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_response_request FOREIGN KEY (cache_request_id) REFERENCES cache_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_response_question FOREIGN KEY (question_id) REFERENCES skill_questions(id) ON DELETE CASCADE,
    CONSTRAINT uq_response_per_question UNIQUE (cache_request_id, question_id)
);

CREATE INDEX idx_feedback_responses_request ON feedback_responses(cache_request_id);

/* =========================================================
   EXTRA ANSWERS: columna JSONB en cache_requests
   para preguntas no-rating (relación, tiempo, texto libre, etc.)
   ========================================================= */

ALTER TABLE cache_requests ADD COLUMN extra_answers JSONB;

/* =========================================================
   SEED: Catálogo de categorías y preguntas
   ========================================================= */

-- Categorías
INSERT INTO skill_categories (id, code, name, description, position) VALUES
    ('a1000000-0000-0000-0000-000000000001', 'TEAMWORK', 'Trabajo en equipo',
     'Capacidad de colaborar y trabajar eficazmente con otros miembros del equipo', 1),
    ('a1000000-0000-0000-0000-000000000002', 'PROACTIVITY', 'Proactividad',
     'Identificar un obstáculo u oportunidad y actuar para dar respuesta a los mismos', 2),
    ('a1000000-0000-0000-0000-000000000003', 'INTEGRITY', 'Integridad',
     'Actuar en consonancia con creencias y valores. Hace aquello que considera correcto', 3),
    ('a1000000-0000-0000-0000-000000000004', 'SELF_CONFIDENCE', 'Autoconfianza',
     'Seguridad que alguien tiene en sí mismo para tomar decisiones o afrontar riesgos', 4),
    ('a1000000-0000-0000-0000-000000000005', 'FLEXIBILITY', 'Flexibilidad',
     'Se adapta con facilidad a la opinión o actitud de otros. Susceptible de cambios', 5);

-- Preguntas: TEAMWORK
INSERT INTO skill_questions (category_id, question_text, position) VALUES
    ('a1000000-0000-0000-0000-000000000001', 'Apoya las decisiones del grupo y realiza la parte que le corresponde', 1),
    ('a1000000-0000-0000-0000-000000000001', 'Mantiene a los demás miembros informados sobre temas que puedan afectarles', 2),
    ('a1000000-0000-0000-0000-000000000001', 'Tiene en cuenta las opiniones e ideas de los demás cuando hay que tomar una decisión', 3),
    ('a1000000-0000-0000-0000-000000000001', 'Expresa públicamente el mérito del resto de los componentes del equipo', 4),
    ('a1000000-0000-0000-0000-000000000001', 'Trata de crear un buen ambiente de trabajo e intenta resolver los conflictos de equipo que surjan', 5);

-- Preguntas: PROACTIVITY
INSERT INTO skill_questions (category_id, question_text, position) VALUES
    ('a1000000-0000-0000-0000-000000000002', 'Propone mejoras en la forma de trabajar si lo considera necesario', 1),
    ('a1000000-0000-0000-0000-000000000002', 'En situaciones de crisis actúa rápida y decididamente', 2),
    ('a1000000-0000-0000-0000-000000000002', 'Minimiza los problemas mediante un esfuerzo extra, antes de que ocurra', 3),
    ('a1000000-0000-0000-0000-000000000002', 'Se anticipa a problemas específicos que no son evidentes para otros', 4),
    ('a1000000-0000-0000-0000-000000000002', 'Autoinicia cambios que considera positivos para la organización', 5);

-- Preguntas: INTEGRITY
INSERT INTO skill_questions (category_id, question_text, position) VALUES
    ('a1000000-0000-0000-0000-000000000003', 'Expresa opiniones o sentimientos abiertamente incluso en situaciones difíciles', 1),
    ('a1000000-0000-0000-0000-000000000003', 'Reconoce errores cometidos o sentimientos negativos propios', 2),
    ('a1000000-0000-0000-0000-000000000003', 'Intenta tratar de una forma equitativa a todas las personas con las que trabaja', 3),
    ('a1000000-0000-0000-0000-000000000003', 'Dice las cosas aunque pueda molestar', 4),
    ('a1000000-0000-0000-0000-000000000003', 'Trabaja según sus valores, aunque ello le pueda perjudicar', 5);

-- Preguntas: SELF_CONFIDENCE
INSERT INTO skill_questions (category_id, question_text, position) VALUES
    ('a1000000-0000-0000-0000-000000000004', 'Afronta sus responsabilidades sin necesidad de supervisión', 1),
    ('a1000000-0000-0000-0000-000000000004', 'Expresa seguridad en cuanto a las capacidades que tiene para realizar una tarea', 2),
    ('a1000000-0000-0000-0000-000000000004', 'Busca retos y nuevas metas desafiantes en su día a día', 3),
    ('a1000000-0000-0000-0000-000000000004', 'Expresa su desacuerdo educadamente, en cuanto a decisiones de superiores, compañeros o clientes', 4),
    ('a1000000-0000-0000-0000-000000000004', 'Se ofrece para el desarrollo de proyectos o funciones de mayor complicación', 5);

-- Preguntas: FLEXIBILITY
INSERT INTO skill_questions (category_id, question_text, position) VALUES
    ('a1000000-0000-0000-0000-000000000005', 'Puede dejar de hacer una tarea para cambiar a otra en el momento en el que se requiera', 1),
    ('a1000000-0000-0000-0000-000000000005', 'Es capaz de trabajar en diferentes circunstancias y con personas diversas', 2),
    ('a1000000-0000-0000-0000-000000000005', 'Valora y comprende puntos de vista diferentes al suyo', 3),
    ('a1000000-0000-0000-0000-000000000005', 'Adapta su propio enfoque si la situación lo requiere para no bloquear el ritmo de trabajo', 4),
    ('a1000000-0000-0000-0000-000000000005', 'Acepta cambios que la organización propone tanto en su puesto de trabajo como en la compañía', 5);
