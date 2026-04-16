-- Migration: stock_alias -> tag_alias and stock(UUID) -> tag(VARCHAR(25))
BEGIN;

ALTER TABLE stock_alias DROP CONSTRAINT IF EXISTS uq_stock_alias;
ALTER TABLE stock_alias DROP CONSTRAINT IF EXISTS fk_stock_alias_stock;

ALTER TABLE stock_alias RENAME TO tag_alias;
ALTER TABLE tag_alias RENAME COLUMN stock TO stock_id;
ALTER TABLE tag_alias ADD COLUMN tag VARCHAR(25);

UPDATE tag_alias ta
SET tag = s.ticker
FROM stock s
WHERE s.id = ta.stock_id;

ALTER TABLE tag_alias ALTER COLUMN tag SET NOT NULL;
ALTER TABLE tag_alias DROP COLUMN stock_id;

ALTER TABLE tag_alias DROP CONSTRAINT IF EXISTS fk_tag_alias_tag;

ALTER TABLE tag_alias
    ADD CONSTRAINT uq_tag_alias UNIQUE (tag, alias);

COMMIT;
