CREATE TABLE IF NOT EXISTS tickets (
    id            VARCHAR(20)  NOT NULL,
    event_id      VARCHAR(20)  NOT NULL,
    user_id       VARCHAR(50)  NOT NULL,
    purchase_date DATETIME     NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME,
    PRIMARY KEY (id)
);
