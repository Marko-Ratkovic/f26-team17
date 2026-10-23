-- GlowUp database changes (shared Neon database)
-- This file contains SQL updates needed to keep the database in sync with the app.
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


-- 2026-10-22  New admin action SERVICE_ARCHIVED (admin takes down a live service). Hibernate made a CHECK
-- constraint listing the allowed actions when it created the table, and ddl-auto=update never changes it.
ALTER TABLE admin_actions DROP CONSTRAINT IF EXISTS admin_actions_action_check;
ALTER TABLE admin_actions ADD CONSTRAINT admin_actions_action_check CHECK (action IN (
    'PROVIDER_APPROVED', 'ACCOUNT_SUSPENDED', 'ACCOUNT_REINSTATED',
    'SERVICE_APPROVED', 'SERVICE_CHANGES_REQUESTED', 'SERVICE_REJECTED', 'SERVICE_ARCHIVED',
    'CONTENT_KEPT_VISIBLE', 'CONTENT_HIDDEN', 'CONTENT_RESTORED'));

-- 2026-10-23  lastLoginAt removed from user accounts (login comes next milestone).
-- The new review_reports table is created by Hibernate on the next start.
ALTER TABLE user_accounts DROP COLUMN IF EXISTS last_login_at;
