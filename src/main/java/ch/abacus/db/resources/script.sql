/*
 * script.sql
 *
 * Creator:
 * 05.04.2024 15:57 josia.schweizer
 *
 * Maintainer:
 * 05.04.2024 15:57 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */

DROP DATABASE IF EXISTS persons;
CREATE database persons;

use persons;

CREATE TABLE address (
    id_address INT AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(64) NOT NULL,
    streetnumber VARCHAR(8) NOT NULL,
    postalcode INT NOT NULL,
    city VARCHAR(32) NOT NULL,
    country VARCHAR(32) NOT NULL
);

-- Create the person table
CREATE TABLE person (
    id_person INT AUTO_INCREMENT PRIMARY KEY,
    fk_address INT NOT NULL,
    firstname VARCHAR(128) NOT NULL,
    lastname VARCHAR(128) NOT NULL,
    birthdate DATE NOT NULL,
    gender ENUM("male", "female") NOT NULL,
    FOREIGN KEY (fk_address) REFERENCES address(id_address)
);

select * from person;