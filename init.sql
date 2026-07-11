CREATE SEQUENCE IF NOT EXISTS person_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS person (
    id BIGINT NOT NULL DEFAULT nextval('person_id_sequence'),
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

ALTER SEQUENCE person_id_sequence OWNED BY person.id;