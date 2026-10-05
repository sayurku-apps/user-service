-- Cabang tempat STAFF bekerja (id Branch di product-service, tanpa FK lintas service).
-- IF NOT EXISTS: di database lama kolom ini sudah dibuat ddl-auto=update sebelum Flyway dipasang.
alter table users add column if not exists branch_id uuid;
