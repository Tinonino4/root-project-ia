/* =========================================================
   MODULE: USER PROFILE (Desacoplado de Auth)
   ========================================================= */

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    name VARCHAR(100),
    surname VARCHAR(100),
    contact_email VARCHAR(100),
    about_me TEXT,
    city VARCHAR(100),
    birthday DATE,
    zipcode VARCHAR(10),
    phone_number VARCHAR(20),
    photo_url TEXT,
    job_title VARCHAR(100),
    education VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

/* =========================================================
   MODULE: CAREER (Experiences)
   ========================================================= */

CREATE TABLE experiences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    company_name VARCHAR(250) NOT NULL,
    department VARCHAR(250),
    position VARCHAR(250) NOT NULL,
    start_date DATE NOT NULL,
    finish_date DATE,
    functions TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_experience_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

/* =========================================================
   CATÁLOGO DE TIPOS DE RELACIÓN
   ========================================================= */

CREATE TABLE relationship_types (
    id INT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    position INT NOT NULL DEFAULT 0
);

INSERT INTO relationship_types (id, code, name, description, position) VALUES
    (0, 'DIRECT_MANAGER', 'Jefe directo', 'Superior jerárquico directo del usuario', 1),
    (1, 'COLLEAGUE', 'Compañero/a', 'Compañero/a del mismo nivel o equipo', 2),
    (2, 'SUBORDINATE', 'Subordinado/a', 'Persona a cargo del usuario', 3),
    (3, 'CLIENT', 'Cliente', 'Cliente interno o externo', 4),
    (4, 'OTHER', 'Otro', 'Otra relación profesional no especificada', 5);

/* =========================================================
   MODULE: FEEDBACK (Cache Requests)
   ========================================================= */

CREATE TABLE cache_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    experience_id UUID NOT NULL,
    relationship_id INT NOT NULL,
    still_works_there BOOLEAN NOT NULL,
    target_name VARCHAR(100) NOT NULL,
    target_surname VARCHAR(100) NOT NULL,
    target_email VARCHAR(100) NOT NULL,
    url_token VARCHAR(100) NOT NULL,
    finished BOOLEAN NOT NULL DEFAULT FALSE,
    target_phone VARCHAR(20),
    extra_answers JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_request_experience FOREIGN KEY (experience_id) REFERENCES experiences(id) ON DELETE CASCADE,
    CONSTRAINT fk_request_relationship FOREIGN KEY (relationship_id) REFERENCES relationship_types(id)
);

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
   MODULE: ANALYTICS / STATS
   ========================================================= */

CREATE TABLE user_skills_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    teamwork FLOAT DEFAULT 0,
    self_confidence FLOAT DEFAULT 0,
    proactivity FLOAT DEFAULT 0,
    integrity FLOAT DEFAULT 0,
    flexibility FLOAT DEFAULT 0,
    average_score FLOAT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_metrics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

/* =========================================================
   SEED: Catálogo de categorías y preguntas
   ========================================================= */

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
