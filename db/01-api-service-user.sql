-- Creates a least-privilege role for api-service.
-- Runs only when Postgres initializes a fresh data directory.
DO
$$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'api_service_user') THEN
        CREATE ROLE api_service_user LOGIN PASSWORD 'change_me_api_service';
    END IF;
END
$$;

DO $$
BEGIN
    EXECUTE format('GRANT CONNECT ON DATABASE %I TO api_service_user', current_database());
END
$$;
GRANT USAGE ON SCHEMA public TO api_service_user;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO api_service_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO api_service_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO api_service_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO api_service_user;
