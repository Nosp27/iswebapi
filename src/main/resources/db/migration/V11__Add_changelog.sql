CREATE TABLE changelog (
    id integer not null references facility(_id),
    type text,
    updated_at timestamp not null default NOW()
);