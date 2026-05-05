ALTER TABLE users
ADD COLUMN name VARCHAR(255),
ADD COLUMN is_active BOOLEAN DEFAULT FALSE NOT NULL;

-- Update existing users to have a default name and be active
UPDATE users SET name = 'Usuario', is_active = TRUE WHERE name IS NULL;

ALTER TABLE users
ALTER COLUMN name SET NOT NULL;

CREATE TABLE user_otps (
    id UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_otps_user_code ON user_otps(user_id, code);
