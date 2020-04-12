ALTER TABLE changelog
    ADD PRIMARY KEY (_id);
ALTER TABLE changelog
    RENAME COLUMN id TO facility_id;