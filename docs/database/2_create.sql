-- Created by Redgate Data Modeler (https://datamodeler.redgate-platform.com)
-- Last modification date: 2026-05-13 06:40:17.153

-- tables
-- Table: event_tags
CREATE TABLE event_tags (
                            event_id uuid  NOT NULL,
                            tag_id uuid  NOT NULL,
                            CONSTRAINT event_tags_pk PRIMARY KEY (event_id,tag_id)
);

CREATE INDEX idx_event_tags_event on event_tags (event_id ASC);

CREATE INDEX idx_event_tags_tag on event_tags (tag_id ASC);

-- Table: events
CREATE TABLE events (
                        id uuid  NOT NULL DEFAULT gen_random_uuid(),
                        organizer_id uuid  NOT NULL,
                        title varchar(200)  NOT NULL,
                        description text  NULL,
                        event_date date  NOT NULL,
                        start_time time  NOT NULL,
                        end_time time  NOT NULL,
                        location varchar(255)  NULL,
                        city varchar(100)  NULL,
                        image_url varchar(500)  NULL,
                        created_at timestamp  NOT NULL DEFAULT now(),
                        CONSTRAINT events_pk PRIMARY KEY (id)
);

CREATE INDEX idx_events_organizer on events (organizer_id ASC);

CREATE INDEX idx_events_date on events (event_date ASC);

CREATE INDEX idx_events_city on events (city ASC);

-- Table: registrations
CREATE TABLE registrations (
                               id uuid  NOT NULL DEFAULT gen_random_uuid(),
                               user_id uuid  NOT NULL,
                               event_id uuid  NOT NULL,
                               status varchar(20)  NOT NULL DEFAULT 'LAHEB',
                               registered_at timestamp  NOT NULL DEFAULT now(),
                               CONSTRAINT AK_2 UNIQUE (user_id, event_id) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                               CONSTRAINT CHECK_1 CHECK (( status IN ( 'LAHEB' , 'VOIB_OLLA' , 'EI_LAHE' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                               CONSTRAINT registrations_pk PRIMARY KEY (id)
);

CREATE INDEX idx_registrations_user on registrations (user_id ASC);

CREATE INDEX idx_registrations_event on registrations (event_id ASC);

-- Table: tags
CREATE TABLE tags (
                      id uuid  NOT NULL DEFAULT gen_random_uuid(),
                      name varchar(50)  NOT NULL,
                      CONSTRAINT AK_1 UNIQUE (name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                      CONSTRAINT tags_pk PRIMARY KEY (id)
);

-- Table: users
CREATE TABLE users (
                       id uuid  NOT NULL DEFAULT gen_random_uuid(),
                       full_name varchar(100)  NOT NULL,
                       email varchar(255)  NOT NULL,
                       password_hash varchar(255)  NOT NULL,
                       phone varchar(20)  NULL,
                       description text  NULL,
                       role varchar(20)  NOT NULL DEFAULT 'USER',
                       created_at timestamp  NOT NULL DEFAULT now(),
                       CONSTRAINT AK_0 UNIQUE (email) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                       CONSTRAINT CHECK_0 CHECK (( role IN ( 'USER' , 'ADMIN' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                       CONSTRAINT users_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: FK_0 (table: events)
ALTER TABLE events ADD CONSTRAINT FK_0
    FOREIGN KEY (organizer_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_1 (table: event_tags)
ALTER TABLE event_tags ADD CONSTRAINT FK_1
    FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_2 (table: event_tags)
ALTER TABLE event_tags ADD CONSTRAINT FK_2
    FOREIGN KEY (tag_id)
        REFERENCES tags (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_3 (table: registrations)
ALTER TABLE registrations ADD CONSTRAINT FK_3
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_4 (table: registrations)
ALTER TABLE registrations ADD CONSTRAINT FK_4
    FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- End of file.

