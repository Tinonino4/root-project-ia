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
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_request_experience FOREIGN KEY (experience_id) REFERENCES experiences(id) ON DELETE CASCADE
);

CREATE TABLE requested_caches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    experience_id UUID,
    cache_request_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    flexibility FLOAT,
    integrity FLOAT,
    proactivity FLOAT,
    self_confidence FLOAT,
    teamwork FLOAT,

    question1 INT,
    question2 INT,
    question3 INT,
    question4 VARCHAR(255),
    question5 VARCHAR(255),
    question6 INT,

    CONSTRAINT fk_rc_request FOREIGN KEY (cache_request_id) REFERENCES cache_requests(id) ON DELETE SET NULL
);

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
