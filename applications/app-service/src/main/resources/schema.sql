CREATE TABLE IF NOT EXISTS bootcamps (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    duration_in_days BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS bootcamp_capacity (
    id BIGSERIAL PRIMARY KEY,
    bootcamp_id BIGINT NOT NULL,
    capacity_id BIGINT NOT NULL
);