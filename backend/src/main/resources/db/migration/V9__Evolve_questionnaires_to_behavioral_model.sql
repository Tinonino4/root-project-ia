/* =========================================================
   MODULE: ANALYTICS / 360 ROLE METRICS
   ========================================================= */

CREATE TABLE IF NOT EXISTS user_role_skills_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    teamwork FLOAT DEFAULT 0,
    self_confidence FLOAT DEFAULT 0,
    proactivity FLOAT DEFAULT 0,
    integrity FLOAT DEFAULT 0,
    flexibility FLOAT DEFAULT 0,
    average_score FLOAT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_role_metrics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_role_metrics UNIQUE (user_id, role_code)
);

CREATE INDEX IF NOT EXISTS idx_user_role_metrics_user ON user_role_skills_metrics(user_id);
