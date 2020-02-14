ALTER TABLE region
    ADD COLUMN  IF NOT EXISTS unemployed int,
    ADD COLUMN IF NOT EXISTS total_labour_force int,
    ADD COLUMN IF NOT EXISTS gdp double precision,
    ADD COLUMN IF NOT EXISTS avg_property_price double precision,
    ADD COLUMN IF NOT EXISTS avg_family_income double precision;
CREATE TYPE utility_type AS ENUM (
    'Available',
    'Under Construction',
    'Unavailable'
);
ALTER TABLE facility
    ADD COLUMN IF NOT EXISTS utility utility_type NOT NULL DEFAULT 'Unavailable',
    ADD COLUMN IF NOT EXISTS employees int,
    ADD COLUMN IF NOT EXISTS investment_size double precision,
    ADD COLUMN IF NOT EXISTS profitability double precision;