CREATE DATABASE IF NOT EXISTS postmade;

USE postmade;

DROP TABLE IF EXISTS country;

CREATE TABLE IF NOT EXISTS country (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);