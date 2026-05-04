-- PostgreSQL DDL (improved)
-- Improvements:
-- 1) use TIMESTAMPTZ for timezone-safe timestamps
-- 2) avoid reserved identifiers (user -> app_user)
-- 3) use TEXT for long article content
-- 4) add CHECK constraints for positive durations / playback speed
-- 5) index commonly queried FK columns and timestamps
-- 6) add NOT BLANK-like checks for critical text columns

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS vector;


CREATE TABLE attribute (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    str_value VARCHAR(255),
    num_value INTEGER,
    dec_value DOUBLE PRECISION,
    date1_value TIMESTAMPTZ,
    date2_value TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_attribute_type_code UNIQUE (type, code)
);

CREATE TABLE sector (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE industry (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    sector VARCHAR(20) NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_industry_sector FOREIGN KEY (sector) REFERENCES sector (id) ON DELETE RESTRICT
);

CREATE TABLE sub_industry (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    industry VARCHAR(20) NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_sub_industry_industry FOREIGN KEY (industry) REFERENCES industry (id) ON DELETE RESTRICT
);

CREATE TABLE source (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL UNIQUE,
    url TEXT NOT NULL UNIQUE,
    CONSTRAINT ck_source_name_not_blank CHECK (length(trim(name)) > 0),
    CONSTRAINT ck_source_url_not_blank CHECK (length(trim(url)) > 0),
    last_scraped_at TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE stock (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticker VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    pure_name VARCHAR(255) UNIQUE,
    sub_industry VARCHAR(20) NOT NULL,
    industry VARCHAR(20) NOT NULL,
    sector VARCHAR(20) NOT NULL,
    listing_date TIMESTAMPTZ NOT NULL,
    delisted_at TIMESTAMPTZ,
    status UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_stock_sub_industry FOREIGN KEY (sub_industry) REFERENCES sub_industry (id) ON DELETE RESTRICT,
    CONSTRAINT fk_stock_industry FOREIGN KEY (industry) REFERENCES industry (id) ON DELETE RESTRICT,
    CONSTRAINT fk_stock_sector FOREIGN KEY (sector) REFERENCES sector (id) ON DELETE RESTRICT,
    CONSTRAINT fk_stock_status FOREIGN KEY (status) REFERENCES attribute (id) ON DELETE RESTRICT
);

CREATE TABLE tag_alias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tag VARCHAR(25) NOT NULL,
    alias VARCHAR(255) NOT NULL,
    CONSTRAINT fk_tag_alias_tag FOREIGN KEY (tag) REFERENCES stock (ticker) ON DELETE CASCADE,
    CONSTRAINT uq_tag_alias UNIQUE (tag, alias)
);

CREATE TABLE corporate_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    event_type UUID NOT NULL,
    event_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_corporate_event_stock FOREIGN KEY (stock) REFERENCES stock (id) ON DELETE CASCADE,
    CONSTRAINT fk_corporate_event_type FOREIGN KEY (event_type) REFERENCES attribute (id) ON DELETE RESTRICT
);

CREATE TABLE content (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type UUID NOT NULL,
    source UUID NOT NULL,
    url TEXT NOT NULL UNIQUE,
    original_title VARCHAR(512) NOT NULL,
    original_content TEXT NOT NULL,
    original_language VARCHAR(10) NOT NULL,
    original_publish_date TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_content_url_not_blank CHECK (length(trim(url)) > 0),
    CONSTRAINT ck_content_original_title_not_blank CHECK (length(trim(original_title)) > 0),
    CONSTRAINT ck_content_original_content_not_blank CHECK (length(trim(original_content)) > 0),
    CONSTRAINT ck_content_original_language_not_blank CHECK (length(trim(original_language)) > 0),
    status UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_content_type FOREIGN KEY (type) REFERENCES attribute (id) ON DELETE RESTRICT,
    CONSTRAINT fk_content_source FOREIGN KEY (source) REFERENCES source (id) ON DELETE CASCADE,
    CONSTRAINT fk_content_status FOREIGN KEY (status) REFERENCES attribute (id) ON DELETE RESTRICT
);

CREATE TABLE content_ai (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content UUID NOT NULL UNIQUE,
    title_id TEXT,
    title_en TEXT,
    content_id TEXT,
    content_en TEXT,
    sentiment UUID,
    duplicate UUID,
    publish_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_content_ai_content FOREIGN KEY (content) REFERENCES content (id) ON DELETE CASCADE,
    CONSTRAINT fk_content_ai_sentiment FOREIGN KEY (sentiment) REFERENCES attribute (id) ON DELETE RESTRICT,
    CONSTRAINT fk_content_ai_duplicate FOREIGN KEY (duplicate) REFERENCES content (id) ON DELETE SET NULL
);

CREATE TABLE content_tag (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content UUID NOT NULL,
    tag VARCHAR(25) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_content_tag_content FOREIGN KEY (content) REFERENCES content (id) ON DELETE CASCADE,
    CONSTRAINT uq_content_tag UNIQUE (content, tag)
);

CREATE TABLE audio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content UUID NOT NULL UNIQUE,
    url_id TEXT,
    url_en TEXT,
    duration_id SMALLINT,
    duration_en SMALLINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_audio_content FOREIGN KEY (content) REFERENCES content (id) ON DELETE CASCADE,
    CONSTRAINT ck_audio_duration_id_positive CHECK (duration_id IS NULL OR duration_id >= 0),
    CONSTRAINT ck_audio_duration_en_positive CHECK (duration_en IS NULL OR duration_en >= 0)
);

CREATE TABLE app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    theme VARCHAR(20) NOT NULL DEFAULT 'LIGHT',
    language VARCHAR(10) NOT NULL DEFAULT 'ID',
    playback_speed DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    CONSTRAINT ck_user_profile_playback_speed CHECK (playback_speed > 0)
);

CREATE TABLE watchlist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    stock UUID NOT NULL,
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_watchlist_stock FOREIGN KEY (stock) REFERENCES stock (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_stock UNIQUE (user_id, stock)
);

CREATE TABLE activity_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    activity_type UUID NOT NULL,
    content UUID,
    audio UUID,
    activity_start TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    activity_end TIMESTAMPTZ,
    CONSTRAINT fk_activity_log_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_log_activity_type FOREIGN KEY (activity_type) REFERENCES attribute (id) ON DELETE RESTRICT,
    CONSTRAINT fk_activity_log_content FOREIGN KEY (content) REFERENCES content (id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_log_audio FOREIGN KEY (audio) REFERENCES audio (id) ON DELETE CASCADE
);

CREATE TABLE pipeline_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source UUID NOT NULL,
    pipeline VARCHAR(20),
    total_found INTEGER,
    total_saved INTEGER,
    total_tagged INTEGER,
    total_untagged INTEGER,
    total_multiple INTEGER,
    start_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    end_at TIMESTAMPTZ,
    CONSTRAINT fk_pipeline_log_source FOREIGN KEY (source) REFERENCES source (id) ON DELETE CASCADE
);

CREATE INDEX idx_stock_industry ON stock (industry);
CREATE INDEX idx_stock_status ON stock (status);
CREATE INDEX idx_content_url ON content (url);
CREATE INDEX idx_content_type ON content (type);
CREATE INDEX idx_content_source ON content (source);
CREATE INDEX idx_content_status ON content (status);
CREATE INDEX idx_content_original_publish_date ON content (original_publish_date);
CREATE INDEX idx_content_original_title_trgm ON content USING gin (original_title gin_trgm_ops);
CREATE INDEX idx_content_original_content_trgm ON content USING gin (original_content gin_trgm_ops);
CREATE INDEX idx_content_updated_at ON content (updated_at);
CREATE INDEX idx_tag_alias_alias_trgm ON tag_alias USING gin (alias gin_trgm_ops);
CREATE INDEX idx_content_ai_sentiment ON content_ai (sentiment);
CREATE INDEX idx_content_ai_publish_date ON content_ai (publish_date);
CREATE INDEX idx_corporate_event_stock ON corporate_event (stock);
CREATE INDEX idx_content_tag_content ON content_tag (content);
CREATE INDEX idx_content_tag_tag ON content_tag (tag);
CREATE INDEX idx_watchlist_user ON watchlist (user_id);
CREATE INDEX idx_watchlist_stock ON watchlist (stock);
CREATE INDEX idx_activity_log_user ON activity_log (user_id);
CREATE INDEX idx_activity_log_content ON activity_log (content);
CREATE INDEX idx_activity_log_audio ON activity_log (audio);
CREATE INDEX idx_pipeline_log_source ON pipeline_log (source);
CREATE INDEX idx_pipeline_log_pipeline ON pipeline_log (pipeline);

CREATE OR REPLACE FUNCTION get_tagged_content_by_ticker_and_window(
    p_tag VARCHAR(25),
    p_start TIMESTAMPTZ,
    p_end TIMESTAMPTZ
)
RETURNS TABLE (
    content_id UUID,
    stock_ticker VARCHAR(25),
    url TEXT,
    original_publish_date TIMESTAMPTZ,
    source_name VARCHAR(150),
    original_title VARCHAR(512),
    original_content TEXT
)
LANGUAGE SQL
AS $$
    SELECT c.id,
           ct.tag AS stock_ticker,
           c.url,
           c.original_publish_date,
           s.name AS source_name,
           c.original_title,
           c.original_content
    FROM content c
    JOIN content_tag ct ON c.id = ct.content
    JOIN attribute a ON c.status = a.id
    JOIN source s ON c.source = s.id
    WHERE c.original_publish_date >= p_start
      AND c.original_publish_date <= p_end
      AND ct.tag = p_tag
      AND a.type = 'CONTENT_STATUS'
      AND a.code = 'TAGGED'
    ORDER BY c.original_publish_date ASC, c.id ASC;
$$;

-- ALTER TABLE content DROP COLUMN title_id;
-- ALTER TABLE content DROP COLUMN title_en;
-- ALTER TABLE content DROP COLUMN content_id;
-- ALTER TABLE content DROP COLUMN content_en;
-- ALTER TABLE content DROP COLUMN sentiment;
-- ALTER TABLE content DROP COLUMN duplicate;
-- ALTER TABLE content DROP COLUMN publish_date;
-- ALTER TABLE content DROP CONSTRAINT fk_content_sentiment;
-- ALTER TABLE content DROP CONSTRAINT fk_content_duplicate;

-- ALTER TABLE content ALTER COLUMN original_content TYPE TEXT;
-- ALTER TABLE content ALTER COLUMN original_title TYPE VARCHAR(512);
-- ALTER TABLE content ALTER COLUMN original_language TYPE VARCHAR(10);
-- ALTER TABLE content ALTER COLUMN url TYPE TEXT;

-- GRANT CONNECT ON DATABASE n8ndibi TO nusern;
-- GRANT ALL PRIVILEGES ON DATABASE n8ndibi TO nusern;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO nusern;


-- Service DB roles (scraper-service and tagger-service)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'scraper') THEN
        CREATE ROLE scraper LOGIN PASSWORD '';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'tagger') THEN
        CREATE ROLE tagger LOGIN PASSWORD '';
    END IF;
END
$$;

GRANT CONNECT ON DATABASE newsdibi TO scraper;
GRANT CONNECT ON DATABASE newsdibi TO tagger;
GRANT USAGE ON SCHEMA public TO scraper;
GRANT USAGE ON SCHEMA public TO tagger;

GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO scraper;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO tagger;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO scraper;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO tagger;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE ON TABLES TO scraper;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE ON TABLES TO tagger;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO scraper;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO tagger;
