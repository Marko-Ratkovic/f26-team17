-- GlowUp database changes (shared Neon database)
-- spring.jpa.hibernate.ddl-auto=update adds new tables and columns, but it never drops columns,
-- renames anything, or updates CHECK constraints. Every change it cannot do is written here.
-- Run each block once, in order, in the Neon SQL editor. Newest at the bottom.

-- 2026-10-20  Provider schedule moved to weekday + time block; old table dropped (test data only)
-- DROP TABLE IF EXISTS provider_schedule;

-- 2026-10-20  Customer preferred time ranges removed
-- DROP TABLE IF EXISTS preferred_time_ranges;

-- 2026-10-20  Service status value renamed
-- UPDATE provider_services SET status = 'CHANGES_REQUESTED' WHERE status = 'CHANGE_REQUESTED';

-- 2026-10-21  Service drafts: a new service has no parent, so the column must allow NULL
ALTER TABLE provider_services ALTER COLUMN parent_service_id DROP NOT NULL;

-- 2026-10-21  (run after the schedule-hours change is merged) Provider schedule: one row per weekday with opening and closing times (time blocks removed).
-- The old rows (one per weekday + block) cannot be merged automatically, so the table is recreated;
-- Hibernate creates the new one on the next start. Providers set their hours again (test data only).
DROP TABLE IF EXISTS provider_schedule;
