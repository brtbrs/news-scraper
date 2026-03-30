-- how to query enumeration in postgresql
-- SELECT enum_range(NULL::stock_status); -- Result: {LISTED, DELISTED}
-- SELECT unnest(enum_range(NULL::stock_status))::text AS stock_status; -- Result: two rows of the values above

-- CREATE TYPE stock_status AS ENUM ('LISTED', 'DELISTED');
-- CREATE TYPE news_status AS ENUM ('PENDING', 'DUPLICATE', 'ERROR', 'ACTIVE', 'INACTIVE');
-- CREATE TYPE audio_status AS ENUM ('PENDING', 'ERROR', 'ACTIVE', 'INACTIVE');
-- CREATE TYPE sentiment_status AS ENUM ('POSITIVE', 'NEUTRAL', 'NEGATIVE');
-- CREATE TYPE corporate_event AS ENUM ('IPO', 'RUPS', 'DIVIDEND', 'EARNINGS', 'RIGHTSISSUE');
-- CREATE TYPE activity_type AS ENUM ('REGISTER', 'LOGIN', 'LOGOUT', 'READ', 'LISTEN', 'SUBSCRIBE');

CREATE TABLE attribute (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	type VARCHAR NOT NULL,
	code VARCHAR NOT NULL,
	str_value VARCHAR,
	num_value INTEGER,
	dec_value FLOAT,
	date1_value TIMESTAMP,
	date2_value TIMESTAMP,
	status VARCHAR NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE / INACTIVE
    CONSTRAINT nq_type_code UNIQUE (type, code)
);

CREATE TABLE sector_industry (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	sector VARCHAR NOT NULL,
	industry VARCHAR NOT NULL,
	status VARCHAR NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE / INACTIVE
    CONSTRAINT nq_sector_industry UNIQUE (sector, industry)
);

CREATE TABLE source (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	name VARCHAR NOT NULL,
	url VARCHAR NOT NULL UNIQUE,
	last_scrapped_at TIMESTAMP NULL,
	active BOOLEAN DEFAULT FALSE NOT NULL,      -- TRUE / FALSE
	created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE stock (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	ticker VARCHAR NOT NULL UNIQUE,
	name VARCHAR NOT NULL UNIQUE,
	industry UUID NOT NULL,
	status UUID NOT NULL,
	created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_stock_industry FOREIGN KEY (industry) REFERENCES sector_industry (id) ON DELETE RESTRICT,
    CONSTRAINT fk_stock_status FOREIGN KEY (status) REFERENCES attribute (id) ON DELETE RESTRICT
);

-- 1 stock N stock_alias
CREATE TABLE stock_alias (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	stock UUID NOT NULL,
	alias VARCHAR NOT NULL,
    CONSTRAINT fk_stock_alias_stock FOREIGN KEY (stock) REFERENCES stock (id) ON DELETE CASCADE,
    CONSTRAINT nq_stock_alias UNIQUE (stock, alias)
);

-- 1 stock N corporate_event
CREATE TABLE corporate_event (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	stock UUID NOT NULL,
	title VARCHAR NOT NULL,
	event_type UUID NOT NULL,
    event_date TIMESTAMP NOT NULL,
	created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_corporate_event_stock FOREIGN KEY (stock) REFERENCES stock (id) ON DELETE CASCADE,
    CONSTRAINT fk_corporate_event_type FOREIGN KEY (event_type) REFERENCES attribute (id) ON DELETE RESTRICT
);

CREATE TABLE news (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	source UUID NOT NULL,
    url VARCHAR NOT NULL UNIQUE,
	original_title VARCHAR NOT NULL,
    original_content VARCHAR NOT NULL,
    original_language VARCHAR NOT NULL,
    original_publish_date TIMESTAMP NOT NULL,
    title_id VARCHAR,
    title_en VARCHAR,
    content_id VARCHAR,
    content_en VARCHAR,
	sentiment UUID,
    duplicate UUID,
    publish_date TIMESTAMP,
    status UUID NOT NULL,
	created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_news_source FOREIGN KEY (source) REFERENCES source (id) ON DELETE CASCADE,
    CONSTRAINT fk_news_sentiment FOREIGN KEY (sentiment) REFERENCES attribute (id) ON DELETE RESTRICT,
    CONSTRAINT fk_news_duplicate FOREIGN KEY (duplicate) REFERENCES news (id) ON DELETE CASCADE,
    CONSTRAINT fk_news_status FOREIGN KEY (status) REFERENCES attribute (id) ON DELETE RESTRICT
);

-- 1 news N stock
CREATE TABLE news_stock (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	news UUID NOT NULL,
	stock UUID NOT NULL,
    CONSTRAINT fk_news_stock_news FOREIGN KEY (news) REFERENCES news (id) ON DELETE CASCADE,
    CONSTRAINT fk_news_stock_stock FOREIGN KEY (stock) REFERENCES stock (id) ON DELETE CASCADE,
    CONSTRAINT nq_news_stock UNIQUE (news, stock)
);

-- 1 news 1 audio
CREATE TABLE audio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    news UUID NOT NULL UNIQUE,
    url_id VARCHAR,
    url_en VARCHAR,
    duration_id smallint,                       -- in seconds
    duration_en smallint,                       -- in seconds
	status VARCHAR NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE / INACTIVE
	created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_audio_news FOREIGN KEY (news) REFERENCES news (id)  ON DELETE CASCADE
);

CREATE TABLE user (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	email VARCHAR NOT NULL UNIQUE,
	name VARCHAR NOT NULL,
    provider VARCHAR NOT NULL,
	status VARCHAR NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE / INACTIVE
	created_at TIMESTAMP DEFAULT NOW() NOT NULL,
);

-- 1 user 1 user_profiles
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user UUID NOT NULL UNIQUE,
    theme VARCHAR NOT NULL DEFAULT 'LIGHT',     -- DARK / LIGHT
    language VARCHAR NOT NULL DEFAULT 'ID',     -- ID / EN
    playback_speed FLOAT NOT NULL DEFAULT 1.0,  -- 1.0 / 1.25 / 1.5 / 1.75 / 2.0
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user) REFERENCES user (id)  ON DELETE CASCADE
);

-- 1 user N watchlist
CREATE TABLE watchlist (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	user UUID NOT NULL,
	stock UUID NOT NULL,
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user) REFERENCES user (id) ON DELETE CASCADE,
    CONSTRAINT fk_watchlist_stock FOREIGN KEY (stock) REFERENCES stock (id) ON DELETE CASCADE,
    CONSTRAINT nq_user_stock UNIQUE (user, stock)
);

CREATE TABLE activity_log (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	user UUID NOT NULL,
	activity_type UUID NOT NULL,
	news UUID,
	audio UUID,
    activity_start TIMESTAMP DEFAULT NOW() NOT NULL,
    activity_end TIMESTAMP,
    CONSTRAINT fk_activity_log_user FOREIGN KEY (user) REFERENCES user (id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_log_activity_type FOREIGN KEY (activity_type) REFERENCES attribute (id) ON DELETE RESTRICT,
    CONSTRAINT fk_activity_log_news FOREIGN KEY (news) REFERENCES news (id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_log_audio FOREIGN KEY (audio) REFERENCES audio (id) ON DELETE CASCADE
);

CREATE TABLE pipeline_log (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	source UUID NOT NULL,
    total_found smallint,
    total_saved smallint,
	start_at TIMESTAMP DEFAULT NOW() NOT NULL,
	end_at TIMESTAMP,
    CONSTRAINT fk_pipeline_log_source FOREIGN KEY (source) REFERENCES source (id) ON DELETE CASCADE
);
