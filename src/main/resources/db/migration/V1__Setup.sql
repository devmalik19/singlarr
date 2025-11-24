CREATE TABLE indexes (
    id INTEGER NOT NULL,
    enable BOOLEAN NOT NULL,
    name VARCHAR(255),
    tags TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE settings (
    "key" VARCHAR(255) NOT NULL,
    value VARCHAR(255),
    PRIMARY KEY ("key")
);

CREATE TABLE search (
    "query" TEXT NOT NULL,
    PRIMARY KEY ("query")
);