CREATE KEYSPACE IF NOT EXISTS hibp
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE hibp;

CREATE TABLE IF NOT EXISTS pwned_hashes (
    prefix text,
    sha1 text,
    count int,
    PRIMARY KEY (prefix, sha1)
);