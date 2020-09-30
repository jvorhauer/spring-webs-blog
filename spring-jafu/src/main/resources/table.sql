CREATE TABLE IF NOT EXISTS to_do_item
(
    id IDENTITY PRIMARY KEY,
    ts BIGINT,
    title VARCHAR(255),
    priority VARCHAR(7),
    status VARCHAR(5)
);
